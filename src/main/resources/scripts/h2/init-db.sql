CREATE SCHEMA IF NOT EXISTS `mydb`;
SET SCHEMA `mydb`;

CREATE TABLE IF NOT EXISTS `mydb`.`roles` (
  `id_role` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id_role`));

CREATE TABLE IF NOT EXISTS `mydb`.`events` (
  `id_event` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `date_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_event`));

CREATE TABLE IF NOT EXISTS `mydb`.`stores` (
    `id_store` INT NOT NULL AUTO_INCREMENT,
    `events_id_event` INT NOT NULL,
    `store_name` VARCHAR(255) NOT NULL,
    `balance` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id_store`),
    CONSTRAINT `fk_stores_events1`
      FOREIGN KEY (`events_id_event`)
      REFERENCES `mydb`.`events` (`id_event`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION);

--CREATE INDEX `fk_stores_events1_idx` ON `mydb`.`stores`(events_id_event);


CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `id_user` INT NOT NULL AUTO_INCREMENT,
  `roles_id_role` INT NOT NULL,
  `events_id_event` INT NULL,
  `stores_id_store` INT NULL,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password_salt` NCHAR(32) NOT NULL,
  `password_hash` NCHAR(64) NOT NULL,
  `date_registered` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `balance` INT NOT NULL DEFAULT 0,
  `is_accepted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_user`),
  CONSTRAINT `fk_users_stores1`
    FOREIGN KEY (`stores_id_store`)
    REFERENCES `mydb`.`stores` (`id_store`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_events1`
    FOREIGN KEY (`events_id_event`)
    REFERENCES `mydb`.`events` (`id_event`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_roles1`
    FOREIGN KEY (`roles_id_role`)
    REFERENCES `mydb`.`roles` (`id_role`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_users_stores1_idx` ON `mydb`.`users`(stores_id_store);
--CREATE INDEX `fk_users_events1_idx` ON `mydb`.`users`(events_id_event);
--CREATE INDEX `fk_users_roles1_idx` ON `mydb`.`users`(roles_id_role);

CREATE TABLE IF NOT EXISTS `mydb`.`invoices` (
  `id_invoice` INT NOT NULL AUTO_INCREMENT,
  `stores_id_store` INT NOT NULL,
  `users_id_user` INT NOT NULL,
  `code` VARCHAR(8) NOT NULL,
  `date_issued` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `discount` INT NULL,
  `total` INT NOT NULL,
  PRIMARY KEY (`id_invoice`),
  CONSTRAINT `fk_invoices_stores1`
    FOREIGN KEY (`stores_id_store`)
    REFERENCES `mydb`.`stores` (`id_store`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_users1`
    FOREIGN KEY (`users_id_user`)
    REFERENCES `mydb`.`users` (`id_user`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_stores1_idx` ON `mydb`.`invoices`(stores_id_store);
--CREATE INDEX `fk_invoices_users1_idx` ON `mydb`.`invoices`(users_id_user);

CREATE TABLE IF NOT EXISTS `mydb`.`products` (
  `id_product` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `image` VARCHAR(255) NULL,
  `price` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_product`));

CREATE TABLE IF NOT EXISTS `mydb`.`packages` (
  `id_package` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `image` VARCHAR(255) NULL,
  `discount_on_items` INT NOT NULL,
  `amount` INT NULL,
  PRIMARY KEY (`id_package`));

CREATE TABLE IF NOT EXISTS `mydb`.`packages_has_products` (
  `packages_id_package` INT NOT NULL,
  `products_id_product` INT NOT NULL,
  PRIMARY KEY (`packages_id_package`, `products_id_product`),
  CONSTRAINT `fk_packages_has_products_packages1`
    FOREIGN KEY (`packages_id_package`)
    REFERENCES `mydb`.`packages` (`id_package`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_packages_has_products_products1`
    FOREIGN KEY (`products_id_product`)
    REFERENCES `mydb`.`products` (`id_product`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_packages_has_products_products1_idx` ON `mydb`.`packages_has_products`(products_id_product);
--CREATE INDEX `fk_packages_has_products_packages1_idx` ON `mydb`.`packages_has_products`(packages_id_package);

CREATE TABLE IF NOT EXISTS `mydb`.`invoices_has_packages` (
  `invoices_id_invoice` INT NOT NULL,
  `packages_id_package` INT NOT NULL,
  `amount` INT NOT NULL,
  PRIMARY KEY (`invoices_id_invoice`, `packages_id_package`),
  CONSTRAINT `fk_invoices_has_packages_invoices1`
    FOREIGN KEY (`invoices_id_invoice`)
    REFERENCES `mydb`.`invoices` (`id_invoice`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_has_packages_packages1`
    FOREIGN KEY (`packages_id_package`)
    REFERENCES `mydb`.`packages` (`id_package`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_has_packages_invoices1_idx` ON `mydb`.`invoices_has_packages`(invoices_id_invoice);
--CREATE INDEX `fk_invoices_has_packages_packages1_idx` ON `mydb`.`invoices_has_packages`(packages_id_package);

CREATE TABLE IF NOT EXISTS `mydb`.`invoices_has_products` (
  `invoices_id_invoice` INT NOT NULL,
  `products_id_product` INT NOT NULL,
  `amount` INT NOT NULL,
  PRIMARY KEY (`invoices_id_invoice`, `products_id_product`),
  CONSTRAINT `fk_invoices_has_products_invoices1`
    FOREIGN KEY (`invoices_id_invoice`)
    REFERENCES `mydb`.`invoices` (`id_invoice`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_has_products_products1`
    FOREIGN KEY (`products_id_product`)
    REFERENCES `mydb`.`products` (`id_product`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_has_products_products1_idx` ON `mydb`.`invoices_has_products`(products_id_product);
--CREATE INDEX `fk_invoices_has_products_invoices1_idx` ON `mydb`.`invoices_has_products`(invoices_id_invoice);

-- INSERT VALUES INTO DATABASE --
INSERT INTO `mydb`.`events` (`name`, `date_created`, `is_active`) VALUES ('Event 1', CURRENT_TIMESTAMP(), 1);
INSERT INTO `mydb`.`events` (`name`, `date_created`, `is_active`) VALUES ('Event 2', CURRENT_TIMESTAMP(), 1);;

INSERT INTO `mydb`.`stores` (`events_id_event`, `store_name`, `balance`) VALUES
  (1, 'Store 1', 0),
  (1, 'Store 2', 0),
  (2, 'Store 3', 0);

INSERT INTO `mydb`.`roles` (`role_name`) VALUES
  ('buyer'),
  ('seller'),
  ('admin');

INSERT INTO `mydb`.`users` (`roles_id_role`, `events_id_event`, `stores_id_store`, `name`, `surname`, `email`, `username`, `password_salt`, `password_hash`, `date_registered`, `balance`, `is_accepted`) VALUES
  (1, 1, 1, 'User 1', 'Surname 1', 'user1@example.com', 'user1', 'salt1', 'hash1', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 1, 'User 2', 'Surname 2', 'user2@example.com', 'user2', 'salt2', 'hash2', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 1, 'User 3', 'Surname 3', 'user3@example.com', 'user3', 'salt3', 'hash3', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 2, 'User 4', 'Surname 4', 'user4@example.com', 'user4', 'salt4', 'hash4', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 2, 'User 5', 'Surname 5', 'user5@example.com', 'user5', 'salt5', 'hash5', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 2, 'User 6', 'Surname 6', 'user6@example.com', 'user6', 'salt6', 'hash6', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 2, 'User 7', 'Surname 7', 'user7@example.com', 'user7', 'salt7', 'hash7', CURRENT_TIMESTAMP(), 0, 0),
  (1, 1, 2, 'User 8', 'Surname 8', 'user8@example.com', 'user8', 'salt8', 'hash8', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 9', 'Surname 9', 'user9@example.com', 'user9', 'salt9', 'hash9', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 10', 'Surname 10', 'user10@example.com', 'user10', 'salt10', 'hash10', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 11', 'Surname 11', 'user11@example.com', 'user11', 'salt11', 'hash11', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 12', 'Surname 12', 'user12@example.com', 'user12', 'salt12', 'hash12', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 13', 'Surname 13', 'user13@example.com', 'user13', 'salt13', 'hash13', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 14', 'Surname 14', 'user14@example.com', 'user14', 'salt14', 'hash14', CURRENT_TIMESTAMP(), 0, 0),
  (1, 2, 3, 'User 15', 'Surname 15', 'user15@example.com', 'user15', 'salt15', 'hash15', CURRENT_TIMESTAMP(), 0, 0);

INSERT INTO `mydb`.`products` (`name`, `description`, `image`, `price`, `quantity`) VALUES
  ('Product 1', 'Description 1', 'image1.jpg', 10, 100),
  ('Product 2', 'Description 2', 'image2.jpg', 20, 200),
  ('Product 3', 'Description 3', 'image3.jpg', 30, 300),
  ('Product 4', 'Description 4', 'image4.jpg', 40, 400),
  ('Product 5', 'Description 5', 'image5.jpg', 50, 500),
  ('Product 6', 'Description 6', 'image6.jpg', 60, 600),
  ('Product 7', 'Description 7', 'image7.jpg', 70, 700),
  ('Product 8', 'Description 8', 'image8.jpg', 80, 800),
  ('Product 9', 'Description 9', 'image9.jpg', 90, 900),
  ('Product 10', 'Description 10', 'image10.jpg', 100, 1000),
  ('Product 11', 'Description 11', 'image11.jpg', 110, 1100),
  ('Product 12', 'Description 12', 'image12.jpg', 120, 1200),
  ('Product 13', 'Description 13', 'image13.jpg', 130, 1300),
  ('Product 14', 'Description 14', 'image14.jpg', 140, 1400),
  ('Product 15', 'Description 15', 'image15.jpg', 150, 1500),
  ('Product 16', 'Description 16', 'image16.jpg', 160, 1600),
  ('Product 17', 'Description 17', 'image17.jpg', 170, 1700),
  ('Product 18', 'Description 18', 'image18.jpg', 180, 1800),
  ('Product 19', 'Description 19', 'image19.jpg', 190, 1900),
  ('Product 20', 'Description 20', 'image20.jpg', 200, 2000);

INSERT INTO `mydb`.`packages` (`name`, `description`, `image`, `discount_on_items`, `amount`) VALUES
  ('Package 1', 'Description 1', 'package1.jpg', 5, 100),
  ('Package 2', 'Description 2', 'package2.jpg', 10, 200),
  ('Package 3', 'Description 3', 'package3.jpg', 15, 300),
  ('Package 4', 'Description 4', 'package4.jpg', 20, 400),
  ('Package 5', 'Description 5', 'package5.jpg', 25, 500);

INSERT INTO `mydb`.`packages_has_products` (`packages_id_package`, `products_id_product`) VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (2, 4),
  (2, 5),
  (2, 6),
  (3, 7),
  (3, 8),
  (3, 9),
  (4, 10),
  (4, 11),
  (4, 12),
  (5, 13),
  (5, 14),
  (5, 15);

INSERT INTO `mydb`.`invoices` (`stores_id_store`, `users_id_user`, `code`, `date_issued`, `discount`, `total`) VALUES
  (1, 1, 'INV001', CURRENT_TIMESTAMP(), 0, 150),
  (2, 2, 'INV002', CURRENT_TIMESTAMP(), 10, 250),
  (3, 3, 'INV003', CURRENT_TIMESTAMP(), 20, 350),
  (1, 4, 'INV004', CURRENT_TIMESTAMP(), 0, 450),
  (2, 5, 'INV005', CURRENT_TIMESTAMP(), 5, 550);

INSERT INTO `mydb`.`invoices_has_products` (`invoices_id_invoice`, `products_id_product`, `amount`) VALUES
  (1, 1, 2),
  (1, 2, 3),
  (1, 3, 4),
  (2, 4, 5),
  (2, 5, 6),
  (2, 6, 7),
  (3, 7, 8),
  (3, 8, 9),
  (3, 9, 10),
  (4, 10, 11),
  (4, 11, 12),
  (4, 12, 13),
  (5, 13, 14),
  (5, 14, 15),
  (5, 15, 16);

INSERT INTO `mydb`.`invoices_has_packages` (`invoices_id_invoice`, `packages_id_package`, `amount`) VALUES
  (1, 1, 1),
  (1, 2, 1),
  (2, 2, 2),
  (2, 3, 2),
  (3, 3, 3),
  (3, 4, 3);
