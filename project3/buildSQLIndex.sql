-- @author: Prithviraj S Patil
-- buildSQLIndex.sql: for creating the spatially indexed table ItemLocation --
-- Tables created: ItemLocation --

-- Creates ItemLocation table to store all latitude longitude information of Item --
-- Primary Key: ItemID --
	
	CREATE TABLE IF NOT EXISTS ItemLocation (
	
	ItemID VARCHAR(50) NOT NULL, 
	Location POINT NOT NULL, 
	SPATIAL INDEX(Location),
	
	PRIMARY KEY (ItemID)
	
	) ENGINE=MyISAM;

-- Inserts into ItemLocation table the ItemID, ItemLatitude and ItemLongitude information from AuctionItem --
	
	INSERT INTO ItemLocation (ItemID, Location) 
	SELECT ItemID, POINT(ItemLatitude, ItemLongitude) FROM AuctionItem;

-- End SQL --