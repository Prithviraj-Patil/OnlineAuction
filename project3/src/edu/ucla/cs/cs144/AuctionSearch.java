package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/*
	 * You will probably have to use JDBC to access MySQL data Lucene
	 * IndexSearcher class to lookup Lucene index. Read the corresponding
	 * tutorial to learn about how to use these.
	 * 
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public, so that
	 * they are not exposed to outside of this class.
	 * 
	 * Any new classes that you create should be part of edu.ucla.cs.cs144
	 * package and their source files should be placed at src/edu/ucla/cs/cs144.
	 */

	public SearchResult[] basicSearch(String query, int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!

		int incIterator = 0;
		SearchResult[] searchResults = null;
		try {

			// IndexSearcher searcher = new
			// IndexSearcher(DirectoryReader.open(FSDirectory
			// .open(new File("/var/lib/lucene/index-directory"))));
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory
					.open(new File("index-directory"))));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());

			Query qry = null;
			try {
				qry = parser.parse(query);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TopDocs topDocs = searcher.search(qry, numResultsToReturn + numResultsToSkip);
			ScoreDoc[] topHits = topDocs.scoreDocs;
			if (numResultsToReturn < topDocs.totalHits) {
				searchResults = new SearchResult[numResultsToReturn];
			} else {
				searchResults = new SearchResult[topDocs.totalHits];
			}
			for (int docIterator = 0; docIterator < topDocs.totalHits; docIterator++) {

				if (docIterator > numResultsToSkip - 1) {

					if (docIterator > numResultsToSkip + numResultsToReturn - 1) {
						break;
					}

					ScoreDoc scoreDoc = topHits[docIterator];
					Document document = searcher.doc(scoreDoc.doc);
					String ItemID = document.get("ItemID");
					String ItemName = document.get("ItemName");
					SearchResult sResult = new SearchResult();
					sResult.setItemId(ItemID);
					sResult.setName(ItemName);
					searchResults[incIterator] = sResult;

					incIterator++;

				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResults;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region, int numResultsToSkip,
			int numResultsToReturn) {

		Connection conn;
		Statement sqlStatement = null;

		try {

			conn = DbManager.getConnection(true);
			sqlStatement = conn.createStatement();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SearchResult[] searchResults = basicSearch(query, numResultsToSkip, numResultsToReturn);
		ArrayList<SearchResult> fsResults = new ArrayList<SearchResult>();

		for (int resultIterator = 0; resultIterator < searchResults.length - 1; resultIterator++) {

			SearchResult sResult = searchResults[resultIterator];
			String ItemID = sResult.getItemId();
			String ItemName = sResult.getName();

			String fetchedItemID = "";

			try {
				String Query = " SELECT * FROM ItemLocation WHERE MBRContains(LineString(Point("
						+ region.getLx() + "," + region.getLy() + "), Point(" + region.getRx()
						+ "," + region.getRy() + ")),Location) AND ItemID='" + ItemID + "'";
				ResultSet sqlResult = sqlStatement.executeQuery(Query);
				while (sqlResult.next()) {
					fetchedItemID = sqlResult.getString("ItemID");

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!fetchedItemID.equals("")) {
				SearchResult soResult = new SearchResult();
				soResult.setItemId(ItemID);
				soResult.setName(ItemName);
				fsResults.add(soResult);
			}
		}

		SearchResult[] finalSearchResults = new SearchResult[fsResults.size()];

		for (int resultIterator = 0; resultIterator < fsResults.size(); resultIterator++) {
			finalSearchResults[resultIterator] = fsResults.get(resultIterator);
		}

		// TODO: Your code here!
		return finalSearchResults;
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}

	public String echo(String message) {
		return message;
	}

}
