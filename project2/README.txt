@Author: Prithviraj Patil (UID: 604 402 279)

1) Relations and Keys: 

Seller(UserID, SellerRating)
UserID is the Primary Key.

Bidder(UserID, UserLocation, UserCountry, BidderRating)
UserID is the Primary Key.

AuctionItem(ItemID, ItemName, CurrentHigh, BuyPrice, FirstBid, NumberOfBids, ItemLocation, ItemLatitude, ItemLongitude, ItemCountry, BidStarted, BidEnds, ItemDescription, SellerID)
ItemID is Primary Key
SellerID is Foreign Key

AuctionItemCategory(ItemID, Category)
ItemID and Category are Primary Key As CategoryKey
ItemID is Foreign Key

Bid(ItemID, BidderID, BidTime, BidAmount)
ItemID, BidderID and BidTime are Primary Key As BidKey
ItemID, BidderID are Foreign Keys

2) All Non-trivial funtional dependenices specifiy keys in the above relations.

3) All the relations are in Boyce–Codd normal form (BCNF).

4) All the relations are in Fourth Normal Form (4NF).