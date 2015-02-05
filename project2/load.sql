-- @author: Prithviraj S Patil
-- load.sql: for loading all auction data files into database tables --
-- Tables Loaded: Seller, Bidder, AuctionItem, AutionItemCategory, Bid --

-- Loading Seller table --
	LOAD DATA LOCAL INFILE 'Sellers.dat' INTO TABLE Seller FIELDS TERMINATED BY '|*|';

-- Loading Bidder table --
	LOAD DATA LOCAL INFILE 'Bidders.dat' INTO TABLE Bidder FIELDS TERMINATED BY '|*|';

-- Loading AuctionItem table --
	LOAD DATA LOCAL INFILE 'AuctionItems.dat' INTO TABLE AuctionItem FIELDS TERMINATED BY '|*|';

-- Loading AutionItemCategory table --
	LOAD DATA LOCAL INFILE 'AuctionItemCategories.dat' INTO TABLE AuctionItemCategory FIELDS TERMINATED BY '|*|';
	
-- Loading Bid table --
	LOAD DATA LOCAL INFILE 'Bids.dat' INTO TABLE Bid FIELDS TERMINATED BY '|*|';
	
-- End SQL --