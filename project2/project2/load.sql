-- @author: Prithviraj S Patil
-- load.sql: for loading all auction data files into database tables --
-- Tables Loaded: AuctionItem, AutionItemCategory, Seller, Bidder, Bid --

-- Loading AuctionItem table --
	LOAD DATA LOCAL 'AuctionItems.dat' INTO TABLE AuctionItem FIELDS TERMINATED BY '|*|' OPTIONALLY ENCLOSED BY '"';

-- Loading AutionItemCategory table --
	LOAD DATA LOCAL INFILE 'AuctionItemCategories.dat' INTO TABLE AutionItemCategory FIELDS TERMINATED BY '|*|' OPTIONALLY ENCLOSED BY '"';

-- Loading Seller table --
	LOAD DATA LOCAL INFILE 'Sellers.dat' INTO TABLE Seller FIELDS TERMINATED BY '|*|' OPTIONALLY ENCLOSED BY '"';

-- Loading Bidder table --
	LOAD DATA LOCAL INFILE 'Bidders.dat' INTO TABLE Bidder FIELDS TERMINATED BY '|*|' OPTIONALLY ENCLOSED BY '"';
	
-- Loading Bid table --
	LOAD DATA LOCAL INFILE 'Bids.dat' INTO TABLE Bid FIELDS TERMINATED BY '|*|' OPTIONALLY ENCLOSED BY '"';
	
-- End SQL --