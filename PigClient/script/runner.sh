#!/bin/bash

export HADOOPDIR=$HADOOP_INSTALL/conf
javac -cp ../lib/pig-0.9.2.jar ../src/ordinary.Test.java ../src/ordinary.JoinTaskExecutor.java ../src/ordinary.SimpleTaskExecutor.java
java -cp ../lib/pig-0.9.2.jar:.:$HADOOPDIR ordinary.Test -log=/user/svemarch/reqans-log-tsv.txt \
    -join_log=/user/svemarch/redir-log-tsv.txt

