CREATE TABLE `entitytype` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `entitytype` VARCHAR(45) NOT NULL,
  `tenant` VARCHAR(45) NOT NULL);

CREATE TABLE `relation` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `commonid` VARCHAR(45) NOT NULL,
  `entitytype_id` INT NOT NULL);

CREATE TABLE `reference` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `relation_id` INT NOT NULL,
  `endpoint` VARCHAR(45) NOT NULL,
  `endpointid` VARCHAR(45) NOT NULL);
