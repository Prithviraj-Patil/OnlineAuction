-- @author: Prithviraj S Patil
-- create.sql: for creating all default database tables --
-- Tables created: AuctionItem, AutionItemCategory, Seller, Bidder, Bid --

-- Creates Seller table to store all user information--
-- Primary Key: UserID --
	
	CREATE TABLE IF NOT EXISTS Seller (
	
	UserID VARCHAR(50) NOT NULL,
	SellerRating INT(10) NOT NULL,
	
	PRIMARY KEY (UserID)
	
	);
	
-- Creates Bidder table to store all user information--
-- Primary Key: UserID --
	
	CREATE TABLE IF NOT EXISTS Bidder (
	
	UserID VARCHAR(50) NOT NULL,
	UserLocation VARCHAR(100) default NULL,
	UserCountry VARCHAR(100) default NULL,
	BidderRating INT(10) NOT NULL,
	
	PRIMARY KEY (UserID)
	
	);

-- Creates AuctionItem table to store all item information--
-- Primary Key: AuctionItemID--

	CREATE TABLE IF NOT EXISTS AuctionItem(

	ItemID INT(10) NOT NULL,
	ItemName VARCHAR(100) NOT NULL,
    CurrentHigh DECIMAL(8,2) NOT NULL,	
    BuyPrice DECIMAL(8,2) default NULL,   
    FirstBid DECIMAL(8,2) NOT NULL,	  
    NumberOfBids INT(10) NOT NULL,
	
	ItemLocation VARCHAR(100) NOT NULL,
	ItemLatitude DECIMAL(10,6) default NULL,
	ItemLongitude DECIMAL(10,6) default NULL,
	ItemCountry VARCHAR(100) NOT NULL,
	
	BidStarted TIMESTAMP NOT NULL,
    BidEnds TIMESTAMP NOT NULL,
	
	ItemDescription VARCHAR(4000) NOT NULL,	
	
	SellerID VARCHAR(50) NOT NULL,	
	FOREIGN KEY (SellerID) REFERENCES Seller (UserID),
	PRIMARY KEY (ItemID)
	
	);
	
-- Creates AutionItemCategory table to store all item category information--
-- Primary Key: CategoryKey --
	
	CREATE TABLE IF NOT EXISTS AuctionItemCategory(
	
	ItemID INT(10) NOT NULL,
	Category VARCHAR(100) NOT NULL,

	CONSTRAINT CategoryKey PRIMARY KEY (ItemID, Category),
	FOREIGN KEY (ItemID) REFERENCES AuctionItem (ItemID)
	
	);
	
-- Creates Bid table to store all bid information--
-- Primary Key: BidKey --
	
	CREATE TABLE IF NOT EXISTS Bid (
	
	ItemID INT(10) NOT NULL,
	BidderID VARCHAR(50) NOT NULL,
	BidTime TIMESTAMP NOT NULL,
	BidAmount DECIMAL(8,2) NOT NULL,
	
	CONSTRAINT BidKey PRIMARY KEY (ItemID, BidderID, BidTime),
	FOREIGN KEY (ItemID) REFERENCES AuctionItem (ItemID)
	
	);

-- End SQL --