-- -----------------------------------------------------
-- schema `user_details`
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS user;

CREATE TABLE IF NOT EXISTS user.user_details (
user_id VARCHAR(50) PRIMARY KEY,
user_type VARCHAR(50),
name VARCHAR(50),
username VARCHAR(50));