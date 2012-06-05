DROP DATABASE IF EXISTS warehouse_test;
CREATE DATABASE warehouse_test;

/*
* each record for one variant of cluster configuration
* mode (single, pseudo-distributed, distributed etc)
*
*/
DROP TABLE IF EXISTS warehouse_test.cluster;
CREATE TABLE IF NOT EXISTS warehouse_test.cluster (
  mode VARCHAR(32) NOT NULL,
  description VARCHAR (128),
  nodes_cnt INT NOT NULL,
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);


/*
* describes testing warehouse (datastorage):
* type may be hive, pig and etc
*/
DROP TABLE IF EXISTS warehouse_test.warehouse;
CREATE TABLE IF NOT EXISTS warehouse_test.warehouse (
  type VARCHAR(32) NOT NULL,
  description VARCHAR (128),
  name VARCHAR (64) NOT NULL,
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);


/*
* This table describes tasks
* e.g title may be LoadData, SimpleTask, JoinTask and etc
*/
DROP TABLE IF EXISTS warehouse_test.task;
CREATE TABLE IF NOT EXISTS warehouse_test.task (
  name VARCHAR(32) NOT NULL,
  description VARCHAR(256) NULL,
  type VARCHAR (32) NOT NULL,
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);

/*
* This table describes measures of tasks
* e.g name may be import, aggregation, join and etc
*/
DROP TABLE IF EXISTS warehouse_test.measure;
CREATE TABLE IF NOT EXISTS warehouse_test.measure (
  name VARCHAR(32) NOT NULL,
  description VARCHAR(256) NULL,
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);

/*
* This table describes links between measures and tasks
*/
DROP TABLE IF EXISTS warehouse_test.task_measure;
CREATE TABLE IF NOT EXISTS warehouse_test.task_measure (
  task_id INT NOT NULL,
  measure_id INT NULL,
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY
);


/*
* launch may consists of several tests
* technology (hive, pig, basejava etc)
* mode (mode for particular technology, e.g. bucketing and partitioning for hive technology)
*/
DROP TABLE IF EXISTS warehouse_test.launch;
CREATE TABLE IF NOT EXISTS warehouse_test.launch (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cluster_id  INT NOT NULL,
  warehouse_id INT NOT NULL,
  description VARCHAR(128) NOT NULL,
  mode VARCHAR (128) NOT NULL,
  dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


/*
* binds particular launch with a launch
*/
DROP TABLE IF EXISTS warehouse_test.test;
CREATE TABLE IF NOT EXISTS warehouse_test.test (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  launch_id INT NOT NULL,
  mode VARCHAR (128) NULL,
  description VARCHAR (128) NULL
);


/*
* binds particular task with a test
*/
DROP TABLE IF EXISTS warehouse_test.test_task;
CREATE TABLE IF NOT EXISTS warehouse_test.test_task (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_id INT NOT NULL,
  task_id INT NOT NULL,
  mode VARCHAR (128) NULL,
  task_order INT DEFAULT 0
);


/*
* This table wares information about all tables in particular warehouse
*/
DROP TABLE IF EXISTS warehouse_test.warehouse_table;
CREATE TABLE IF NOT EXISTS warehouse_test.warehouse_table (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  warehouse_id INT NOT NULL,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(128) NULL
);


/*
* This table wares information about all table states in particular launch
*/
DROP TABLE IF EXISTS warehouse_test.table_state;
CREATE TABLE IF NOT EXISTS warehouse_test.table_state (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  table_id INT NOT NULL,
  launch_id INT NOT NULL,
  dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  lines_cnt BIGINT NULL,
  bytes_cnt BIGINT NULL
);

/*
* This table wares measures in millis for the actions of tasks
*/
DROP TABLE IF EXISTS warehouse_test.task_operation;
CREATE TABLE IF NOT EXISTS warehouse_test.task_operation (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  task_measure_id INT NOT NULL,
  test_task_id INT NOT NULL,
  data_size BIGINT NOT NULL,
  millis BIGINT NOT NULL
);