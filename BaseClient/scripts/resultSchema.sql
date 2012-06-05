DROP DATABASE IF EXISTS preprocessing_test;
CREATE DATABASE preprocessing_test;


DROP TABLE IF EXISTS preprocessing_test.join_task;
CREATE TABLE IF NOT EXISTS preprocessing_test.join_task (
  dt VARCHAR(32) NOT NULL,
  company_id VARCHAR(32) NOT NULL,
  base VARCHAR(32) NOT NULL,
  region_id INT NOT NULL,
  path VARCHAR(32) NOT NULL,
  clicks_count INT NOT NULL
);

DROP TABLE IF EXISTS preprocessing_test.simple_task;
CREATE TABLE IF NOT EXISTS preprocessing_test.simple_task (
  dt VARCHAR(32) NOT NULL,
  region_id INT NOT NULL,
  company_id VARCHAR(32) NOT NULL,
  base VARCHAR(32) NOT NULL,
  shows_count INT NOT NULL
);
