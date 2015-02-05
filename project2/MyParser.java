/*@author: Prithviraj Patil*/

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

class MyParser {

	static final String columnSeparator = "|*|";
	static DocumentBuilder builder;

	static final String[] typeName = { "none", "Element", "Attr", "Text", "CDATA", "EntityRef",
			"Entity", "ProcInstr", "Comment", "Document", "DocType", "DocFragment", "Notation", };

	private static BufferedWriter AuctionItemFileWriter, CategoryFileWriter, SellerFileWriter,
			BidderFileWriter, BidFileWriter;

	static class MyErrorHandler implements ErrorHandler {

		public void warning(SAXParseException exception) throws SAXException {
			fatalError(exception);
		}

		public void error(SAXParseException exception) throws SAXException {
			fatalError(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			exception.printStackTrace();
			System.out.println("There should be no errors " + "in the supplied XML files.");
			System.exit(3);
		}

	}

	/*
	 * Non-recursive (NR) version of Node.getElementsByTagName(...)
	 */
	static Element[] getElementsByTagNameNR(Element e, String tagName) {
		Vector<Element> elements = new Vector<Element>();
		Node child = e.getFirstChild();
		while (child != null) {
			if (child instanceof Element && child.getNodeName().equals(tagName)) {
				elements.add((Element) child);
			}
			child = child.getNextSibling();
		}
		Element[] result = new Element[elements.size()];
		elements.copyInto(result);
		return result;
	}

	/*
	 * Returns the first subelement of e matching the given tagName, or null if
	 * one does not exist. NR means Non-Recursive.
	 */
	static Element getElementByTagNameNR(Element e, String tagName) {
		Node child = e.getFirstChild();
		while (child != null) {
			if (child instanceof Element && child.getNodeName().equals(tagName))
				return (Element) child;
			child = child.getNextSibling();
		}
		return null;
	}

	/*
	 * Returns the text associated with the given element (which must have type
	 * #PCDATA) as child, or "" if it contains no text.
	 */
	static String getElementText(Element e) {
		if (e.getChildNodes().getLength() == 1) {
			Text elementText = (Text) e.getFirstChild();
			return elementText.getNodeValue();
		} else
			return "";
	}

	/*
	 * Returns the text (#PCDATA) associated with the first subelement X of e
	 * with the given tagName. If no such X exists or X contains no text, "" is
	 * returned. NR means Non-Recursive.
	 */
	static String getElementTextByTagNameNR(Element e, String tagName) {
		Element elem = getElementByTagNameNR(e, tagName);
		if (elem != null)
			return getElementText(elem);
		else
			return "";
	}

	/*
	 * Returns the amount (in XXXXX.xx format) denoted by a money-string like
	 * $3,453.23. Returns the input if the input is an empty string.
	 */
	static String strip(String money) {
		if (money.equals(""))
			return money;
		else {
			double am = 0.0;
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
			try {
				am = nf.parse(money).doubleValue();
			} catch (ParseException e) {
				System.out.println("This method should work for all "
						+ "money values you find in our data.");
				System.exit(20);
			}
			nf.setGroupingUsed(false);
			return nf.format(am).substring(1);
		}
	}

	/*
	 * Process one items-???.xml file.
	 */
	static void processFile(File xmlFile) throws IOException {
		Document doc = null;
		try {
			doc = builder.parse(xmlFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (SAXException e) {
			System.out.println("Parsing error on file " + xmlFile);
			System.out.println("  (not supposed to happen with supplied XML files)");
			e.printStackTrace();
			System.exit(3);
		}

		/*
		 * At this point 'doc' contains a DOM representation of an 'Items' XML
		 * file. Use doc.getDocumentElement() to get the root Element.
		 */
		System.out.println("Successfully parsed - " + xmlFile);

		/**************************************************************/

		Element[] AuctionItems = getElementsByTagNameNR(doc.getDocumentElement(), "Item");
		for (Element AuctionItem : AuctionItems) {

			ParseAuctionItem(AuctionItem);
			ParseItemCategories(AuctionItem);
			ParseAuctionUsersAndBids(AuctionItem);

		}

		/**************************************************************/
	}

	public static void ParseAuctionItem(Element AuctionItem) throws IOException {

		/*
		 * ItemID ItemName CurrentHigh BuyPrice FirstBid NumberOfBids
		 * ItemLocation ItemLatitude ItemLongitude BidStarted BidEnds
		 * ItemDescription SellerID
		 */

		String ItemID, ItemName, CurrentHigh, BuyPrice, FirstBid, NumberOfBids, ItemLocation, ItemLatitude;
		String ItemLongitude, ItemCountry, BidStarted, BidEnds, ItemDescription, SellerID;

		ItemID = AuctionItem.getAttribute("ItemID");
		ItemName = getElementTextByTagNameNR(AuctionItem, "Name");

		CurrentHigh = strip(getElementTextByTagNameNR(AuctionItem, "Currently"));
		BuyPrice = strip(getElementTextByTagNameNR(AuctionItem, "Buy_Price"));
		FirstBid = strip(getElementTextByTagNameNR(AuctionItem, "First_Bid"));
		NumberOfBids = getElementTextByTagNameNR(AuctionItem, "Number_of_Bids");

		ItemLocation = getElementTextByTagNameNR(AuctionItem, "Location");
		ItemCountry = getElementTextByTagNameNR(AuctionItem, "Country");
		ItemLatitude = getElementByTagNameNR(AuctionItem, "Location").getAttribute("Latitude");
		ItemLongitude = getElementByTagNameNR(AuctionItem, "Location").getAttribute("Longitude");

		BidStarted = ConvertToDBTime(getElementTextByTagNameNR(AuctionItem, "Started"));
		BidEnds = ConvertToDBTime(getElementTextByTagNameNR(AuctionItem, "Ends"));

		ItemDescription = getElementTextByTagNameNR(AuctionItem, "Description");
		if (ItemDescription.length() > 4000) {
			ItemDescription.substring(0, 4000);
		}

		SellerID = getElementByTagNameNR(AuctionItem, "Seller").getAttribute("UserID");

		String DataRow = CreateDataRow(ItemID, ItemName, CurrentHigh, BuyPrice, FirstBid,
				NumberOfBids, ItemLocation, ItemLatitude, ItemLongitude, ItemCountry, BidStarted,
				BidEnds, ItemDescription, SellerID);

		AuctionItemFileWriter.write(DataRow + "\n");
	}

	public static void ParseItemCategories(Element AuctionItem) throws IOException {

		String ItemID, Category;

		ItemID = AuctionItem.getAttribute("ItemID");
		Element[] Categories = getElementsByTagNameNR(AuctionItem, "Category");

		for (int CategoryIterator = 0; CategoryIterator < Categories.length; CategoryIterator++) {
			Category = getElementText(Categories[CategoryIterator]);
			String DataRow = CreateDataRow(ItemID, Category);
			CategoryFileWriter.write(DataRow + "\n");
		}

	}

	public static void ParseAuctionUsersAndBids(Element AuctionItem) throws IOException {

		String ItemID, SellerId, BidderID, BidderLocation, BidderCountry, SellerRating, BidderRating, BidTime, BidAmount;

		ItemID = AuctionItem.getAttribute("ItemID");

		Element seller = getElementByTagNameNR(AuctionItem, "Seller");
		SellerId = seller.getAttribute("UserID");
		SellerRating = seller.getAttribute("Rating");
		String DataRow = CreateDataRow(SellerId, SellerRating);
		SellerFileWriter.write(DataRow + "\n");

		Element[] ItemBids = getElementsByTagNameNR(getElementByTagNameNR(AuctionItem, "Bids"),
				"Bid");

		for (Element Bid : ItemBids) {

			Element Bidder = getElementByTagNameNR(Bid, "Bidder");
			BidderRating = Bidder.getAttribute("Rating");
			BidderID = Bidder.getAttribute("UserID");

			BidderLocation = "";
			if (getElementByTagNameNR(Bidder, "Location") != null) {
				BidderLocation = getElementText(getElementByTagNameNR(Bidder, "Location"));
			}

			BidderCountry = "";
			if (getElementByTagNameNR(Bidder, "Country") != null) {
				BidderCountry = getElementText(getElementByTagNameNR(Bidder, "Country"));
			}

			String BidderDataRow = CreateDataRow(BidderID, BidderLocation, BidderCountry,
					BidderRating);
			BidderFileWriter.write(BidderDataRow + "\n");

			BidTime = ConvertToDBTime(getElementText(getElementByTagNameNR(Bid, "Time")));
			BidAmount = strip(getElementText(getElementByTagNameNR(Bid, "Amount")));

			String BidDataRow = CreateDataRow(ItemID, BidderID, BidTime, BidAmount);
			BidFileWriter.write(BidDataRow + "\n");
		}

	}

	public static String ConvertToDBTime(String time) {

		SimpleDateFormat XMLformat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
		SimpleDateFormat DBformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			time = DBformat.format(XMLformat.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;

	}

	public static String CreateDataRow(String... args) {

		String DataRow = "";
		DataRow = DataRow + args[0];
		for (int dataIterator = 1; dataIterator < args.length; dataIterator++) {
			DataRow = DataRow + columnSeparator + args[dataIterator];
		}

		return DataRow;
	}

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();

		File ItemFile = new File("AuctionItems.dat");
		if (ItemFile.exists()) {
			ItemFile.delete();
		}
		ItemFile.createNewFile();
		AuctionItemFileWriter = new BufferedWriter(new FileWriter(ItemFile, true));

		File CategoryFile = new File("AutionItemCategories.dat");
		if (CategoryFile.exists()) {
			CategoryFile.delete();
		}
		CategoryFile.createNewFile();
		CategoryFileWriter = new BufferedWriter(new FileWriter(CategoryFile, true));

		File SellerFile = new File("Sellers.dat");
		if (SellerFile.exists()) {
			SellerFile.delete();
		}
		SellerFile.createNewFile();
		SellerFileWriter = new BufferedWriter(new FileWriter(SellerFile, true));

		File BidderFile = new File("Bidders.dat");
		if (BidderFile.exists()) {
			BidderFile.delete();
		}
		BidderFile.createNewFile();
		BidderFileWriter = new BufferedWriter(new FileWriter(BidderFile, true));

		File BidFile = new File("Bids.dat");
		if (BidFile.exists()) {
			BidFile.delete();
		}
		BidFile.createNewFile();
		BidFileWriter = new BufferedWriter(new FileWriter(BidFile, true));

		if (args.length == 0) {
			System.out.println("Usage: java MyParser [file] [file] ...");
			System.exit(1);
		}

		/* Initialize parser. */
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new MyErrorHandler());
		} catch (FactoryConfigurationError e) {
			System.out.println("unable to get a document builder factory");
			System.exit(2);
		} catch (ParserConfigurationException e) {
			System.out.println("parser was unable to be configured");
			System.exit(2);
		}

		/* Process all files listed on command line. */
		for (int i = 0; i < args.length; i++) {
			File currentFile = new File(args[i]);
			processFile(currentFile);

		}

		AuctionItemFileWriter.close();
		CategoryFileWriter.close();
		SellerFileWriter.close();
		BidderFileWriter.close();
		BidFileWriter.close();

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);

	}
}
