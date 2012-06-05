#!/bin/bash
HADOOP_HOME=/home/svemarch/soft/hadoop/hadoop-1.0.0
HIVE_HOME=/home/svemarch/soft/hadoop/hive-0.8.1

HADOOP_CORE=$HADOOP_HOME/hadoop-core-1.0.0.jar
CLASSPATH=.:$HADOOP_CORE:$HIVE_HOME/conf
CLASSPATH=$CLASSPATH:lib/*.jar

for i in ${HIVE_HOME}/lib/*.jar ; do
    CLASSPATH=$CLASSPATH:$i
done

java -cp $CLASSPATH ordinary.Test