#!/bin/bash

#runLoad.sh: Main Execution Script
#Author: Prithviraj S. Patil (UID 604 402 279)

#Execute drop.sql : Drops all Pre-existing DB Tables
mysql CS144 < drop.sql

#Execute create.sql : Creates all DB Tables
mysql CS144 < create.sql

#Execute ant Build File : Compile and Execute Java Code
ant run-all

#Remove Duplicate Entries
sort -u TmpAuctionItems.dat > AuctionItems.dat
sort -u TmpAuctionItemCategories.dat > AuctionItemCategories.dat
sort -u TmpSellers.dat > Sellers.dat
sort -u TmpBidders.dat > Bidders.dat
sort -u TmpBids.dat > Bids.dat

#Execute load.sql : Loads data into all DB Tables
mysql CS144 < load.sql

#Execute queries.sql : Runs 7 SQL Queries that test the Database
mysql CS144 < queries.sql

#Remove all data files
rm *.dat

#End SH

 
