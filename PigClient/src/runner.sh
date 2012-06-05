#!/bin/bash

export HADOOPDIR=$HADOOP_INSTALL/conf
javac -cp ../lib/pig-0.9.2.jar ../src/ordinary.SimpleTaskExecutor.java ../src/ordinary.JoinTaskExecutor.java ../src/Runner.java
java -cp ../lib/pig-0.9.2.jar:.:$HADOOPDIR Runner -log=/user/svemarch/reqans-log-tsv.txt \
    -join_log=/user/svemarch/redir-log-tsv.txt

