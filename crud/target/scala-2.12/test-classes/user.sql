-- -----------------------------------------------------
-- schema `user_details`
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS user;

CREATE TABLE IF NOT EXISTS user.user_details (
user_id VARCHAR(100) PRIMARY KEY,
user_type VARCHAR(100),
name VARCHAR(100),
username VARCHAR(100));

