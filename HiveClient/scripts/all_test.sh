#!/bin/bash

./ordinary_test.sh $1 
./partition_test.sh $1
./bucketing_ord_test.sh $1
./bucketing_part_test.sh $1
./bucketing_sort_test.sh $1


