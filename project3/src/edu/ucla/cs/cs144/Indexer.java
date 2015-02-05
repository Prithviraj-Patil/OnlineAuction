package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	/** Creates a new instance of Indexer */
	public Indexer() {
	}

	private IndexWriter indexWriter = null;
	private Connection conn = null;

	public IndexWriter createIndexWriter() throws IOException {

		if (indexWriter == null) {
			// deleteDir("/var/lib/lucene/index-directory");
			// Directory indexDir = FSDirectory.open(new
			// File("/var/lib/lucene/index-directory"));
			deleteDir("index-directory");
			Directory indexDir = FSDirectory.open(new File("index-directory"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);
		}

		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {

		if (indexWriter != null) {
			indexWriter.close();
		}

	}

	public String getItemCategories(String ItemID) {

		String categories = "";

		try {

			Statement sqlStatement = conn.createStatement();
			ResultSet sqlResult = sqlStatement
					.executeQuery("SELECT * FROM AuctionItemCategory WHERE ItemID='" + ItemID + "'");

			while (sqlResult.next()) {

				categories = categories + sqlResult.getString("Category") + " ";
				categories = categories.replace("\n", "").replace("\r", "");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return categories;

	}

	public void rebuildIndexes() {

		// create a connection to the database to retrieve Items from MySQL
		try {

			conn = DbManager.getConnection(true);
			createIndexWriter();
			Statement sqlStatement = conn.createStatement();
			ResultSet sqlResult = sqlStatement.executeQuery("SELECT * FROM AuctionItem");

			while (sqlResult.next()) {

				Document document = new Document();

				String ItemID = sqlResult.getString("ItemID");
				document.add(new StringField("ItemID", ItemID, Field.Store.YES));

				String ItemName = sqlResult.getString("ItemName");
				document.add(new StringField("ItemName", ItemName, Field.Store.YES));

				String ItemCategories = getItemCategories(ItemID);
				document.add(new StringField("ItemCategories", ItemCategories, Field.Store.YES));

				String ItemDescription = sqlResult.getString("ItemDescription");
				document.add(new StringField("ItemDescription", ItemDescription, Field.Store.YES));

				String SearchText = ItemName + " " + ItemCategories + ItemDescription;
				document.add(new TextField("content", SearchText, Field.Store.NO));

				indexWriter.addDocument(document);
			}

			closeIndexWriter();

		} catch (SQLException | IOException ex) {
			System.out.println(ex);
		}

		/*
		 * Add your code here to retrieve Items using the connection and add
		 * corresponding entries to your Lucene inverted indexes.
		 * 
		 * You will have to use JDBC API to retrieve MySQL data from Java. Read
		 * our tutorial on JDBC if you do not know how to use JDBC.
		 * 
		 * You will also have to use Lucene IndexWriter and Document classes to
		 * create an index and populate it with Items data. Read our tutorial on
		 * Lucene as well if you don't know how.
		 * 
		 * As part of this development, you may want to add new methods and
		 * create additional Java classes. If you create new classes, make sure
		 * that the classes become part of "edu.ucla.cs.cs144" package and place
		 * your class source files at src/edu/ucla/cs/cs144/.
		 */

		// close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}

	public static void deleteDir(String direc) {
		Path dir = Paths.get(direc);
		File file = new File(direc);
		if (file.exists()) {
			try {
				Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
							throws IOException {

						Files.delete(file);
						return CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc)
							throws IOException {

						if (exc == null) {
							Files.delete(dir);
							return CONTINUE;
						} else {
							throw exc;
						}
					}

				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {

		Indexer idx = new Indexer();
		idx.rebuildIndexes();

		AuctionSearch as = new AuctionSearch();

		String query = "superman";
		SearchResult[] basicResults = as.basicSearch(query, 0, 20000);
		System.out.println("Basic Search Query: " + query);
		System.out.println("Received " + basicResults.length + " results");
		for (SearchResult result : basicResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}

		SearchRegion region = new SearchRegion(33.774, -118.63, 34.201, -117.38);
		SearchResult[] spatialResults = as.spatialSearch("camera", region, 0, 20000);
		System.out.println("\nSpatial Search");
		System.out.println("Received " + spatialResults.length + " results");
		for (SearchResult result : spatialResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}

	}
}
