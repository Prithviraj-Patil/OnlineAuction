-- @author: Prithviraj S Patil
-- queries.sql: for executing all 7 Test Queries --

-- Executing 1st Query --
	SELECT COUNT(*) FROM (SELECT UserID from Seller UNION SELECT UserID FROM Bidder) AS COUNTER;

-- Executing 2nd Query --
	SELECT COUNT(DISTINCT ItemID) FROM AuctionItem WHERE BINARY ItemLocation='New York';	
		
-- Executing 3rd Query --
	SELECT COUNT(*) FROM (SELECT * FROM AuctionItemCategory GROUP BY ItemID HAVING COUNT(Category) = 4) AS X;		
	
-- Executing 4th Query --
	SELECT Bid.ItemID FROM Bid INNER JOIN AuctionItem ON AuctionItem.ItemID = Bid.ItemID WHERE BidAmount = (SELECT MAX(BidAmount) FROM Bid) AND BidEnds > '2001-12-20 00:00:01';
		
-- Executing 5th Query --
	SELECT COUNT(*) FROM Seller WHERE SellerRating > 1000;		

-- Executing 6th Query --
	SELECT COUNT(*) FROM Seller INNER JOIN Bidder ON Seller.UserID = Bidder.UserID;			
	
-- Executing 7th Query --
	SELECT COUNT(*) FROM (SELECT DISTINCT Category FROM AuctionItemCategory WHERE ItemID IN (SELECT DISTINCT ItemID FROM Bid WHERE BidAmount > 100)) AS X;		
		
-- End SQL --