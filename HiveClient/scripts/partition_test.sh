#!/bin/bash

echo "ADD 100.000 records in the first log and 100.000 records in the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_1.txt -join_log=$1/redir/redir_1.txt -create_log_tables=true
echo "APPEND 300.000 records into the first log and 300.000 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_2.txt -join_log=$1/redir/redir_2.txt -create_log_tables=false
echo "APPEND 500.000 records into the first log and 500.000 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_3.txt -join_log=$1/redir/redir_3.txt -create_log_tables=false
echo "APPEND 700.000 records into the first log and 700.000 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_4.txt -join_log=$1/redir/redir_4.txt -create_log_tables=false
echo "APPEND 1.000.000 records into the first log and 1.000.000 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_5.txt -join_log=$1/redir/redir_5.txt -create_log_tables=false
echo "APPEND 3.000.000 records into the first log and 3.000.000 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_6.txt -join_log=$1/redir/redir_6.txt -create_log_tables=false
echo "APPEND 5.000.000 records into the first log and 5.168.139 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_7.txt -join_log=$1/redir/redir_7.txt -create_log_tables=false
echo "APPEND 7.000.000 records into the first log and 7.752.034 records into the second" >> result
java -jar HiveClient.jar Runner -optimizationMode=partitioning -log=$1/reqans/reqans_8.txt -join_log=$1/redir/redir_8.txt -create_log_tables=false

