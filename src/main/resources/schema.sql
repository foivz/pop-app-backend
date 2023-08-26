CREATE SCHEMA IF NOT EXISTS `popapp_db`;
SET SCHEMA `popapp_db`;

CREATE TABLE IF NOT EXISTS `popapp_db`.`roles` (
  `id_role` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id_role`));

CREATE TABLE IF NOT EXISTS `popapp_db`.`events` (
  `id_event` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `date_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_event`));

CREATE TABLE IF NOT EXISTS `popapp_db`.`stores` (
    `id_store` INT NOT NULL AUTO_INCREMENT,
    `events_id_event` INT NOT NULL,
    `store_name` VARCHAR(255) NOT NULL,
    `balance` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id_store`),
    CONSTRAINT `fk_stores_events1`
      FOREIGN KEY (`events_id_event`)
      REFERENCES `popapp_db`.`events` (`id_event`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION);

--CREATE INDEX `fk_stores_events1_idx` ON `popapp_db`.`stores`(events_id_event);


CREATE TABLE IF NOT EXISTS `popapp_db`.`users` (
  `id_user` INT NOT NULL AUTO_INCREMENT,
  `roles_id_role` INT NOT NULL,
  `events_id_event` INT NULL,
  `stores_id_store` INT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password_hash` NCHAR(60) NOT NULL,
  `date_registered` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `balance` INT NOT NULL DEFAULT 0,
  `is_accepted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_user`),
  CONSTRAINT `fk_users_stores1`
    FOREIGN KEY (`stores_id_store`)
    REFERENCES `popapp_db`.`stores` (`id_store`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_events1`
    FOREIGN KEY (`events_id_event`)
    REFERENCES `popapp_db`.`events` (`id_event`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_roles1`
    FOREIGN KEY (`roles_id_role`)
    REFERENCES `popapp_db`.`roles` (`id_role`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_users_stores1_idx` ON `popapp_db`.`users`(stores_id_store);
--CREATE INDEX `fk_users_events1_idx` ON `popapp_db`.`users`(events_id_event);
--CREATE INDEX `fk_users_roles1_idx` ON `popapp_db`.`users`(roles_id_role);

CREATE TABLE IF NOT EXISTS `popapp_db`.`invoices` (
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
    REFERENCES `popapp_db`.`stores` (`id_store`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_users1`
    FOREIGN KEY (`users_id_user`)
    REFERENCES `popapp_db`.`users` (`id_user`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_stores1_idx` ON `popapp_db`.`invoices`(stores_id_store);
--CREATE INDEX `fk_invoices_users1_idx` ON `popapp_db`.`invoices`(users_id_user);

CREATE TABLE IF NOT EXISTS `popapp_db`.`products` (
  `id_product` INT NOT NULL AUTO_INCREMENT,
  `stores_id_store` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `image` VARCHAR(255) NULL,
  `price` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_product`),
    CONSTRAINT `fk_products_stores1`
    FOREIGN KEY (`stores_id_store`)
    REFERENCES `popapp_db`.`stores` (`id_store`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);

CREATE TABLE IF NOT EXISTS `popapp_db`.`packages` (
  `id_package` INT NOT NULL AUTO_INCREMENT,
  `stores_id_store` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `image` VARCHAR(255) NULL,
  `discount_on_items` INT NOT NULL,
  `amount` INT NULL,
  PRIMARY KEY (`id_package`),
  CONSTRAINT `fk_packages_stores1`
    FOREIGN KEY (`stores_id_store`)
    REFERENCES `popapp_db`.`stores` (`id_store`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);

CREATE TABLE IF NOT EXISTS `popapp_db`.`packages_has_products` (
  `packages_id_package` INT NOT NULL,
  `products_id_product` INT NOT NULL,
  PRIMARY KEY (`packages_id_package`, `products_id_product`),
  CONSTRAINT `fk_packages_has_products_packages1`
    FOREIGN KEY (`packages_id_package`)
    REFERENCES `popapp_db`.`packages` (`id_package`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_packages_has_products_products1`
    FOREIGN KEY (`products_id_product`)
    REFERENCES `popapp_db`.`products` (`id_product`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_packages_has_products_products1_idx` ON `popapp_db`.`packages_has_products`(products_id_product);
--CREATE INDEX `fk_packages_has_products_packages1_idx` ON `popapp_db`.`packages_has_products`(packages_id_package);

CREATE TABLE IF NOT EXISTS `popapp_db`.`invoices_has_packages` (
  `invoices_id_invoice` INT NOT NULL,
  `packages_id_package` INT NOT NULL,
  `amount` INT NOT NULL,
  PRIMARY KEY (`invoices_id_invoice`, `packages_id_package`),
  CONSTRAINT `fk_invoices_has_packages_invoices1`
    FOREIGN KEY (`invoices_id_invoice`)
    REFERENCES `popapp_db`.`invoices` (`id_invoice`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_has_packages_packages1`
    FOREIGN KEY (`packages_id_package`)
    REFERENCES `popapp_db`.`packages` (`id_package`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_has_packages_invoices1_idx` ON `popapp_db`.`invoices_has_packages`(invoices_id_invoice);
--CREATE INDEX `fk_invoices_has_packages_packages1_idx` ON `popapp_db`.`invoices_has_packages`(packages_id_package);

CREATE TABLE IF NOT EXISTS `popapp_db`.`invoices_has_products` (
  `invoices_id_invoice` INT NOT NULL,
  `products_id_product` INT NOT NULL,
  `amount` INT NOT NULL,
  PRIMARY KEY (`invoices_id_invoice`, `products_id_product`),
  CONSTRAINT `fk_invoices_has_products_invoices1`
    FOREIGN KEY (`invoices_id_invoice`)
    REFERENCES `popapp_db`.`invoices` (`id_invoice`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invoices_has_products_products1`
    FOREIGN KEY (`products_id_product`)
    REFERENCES `popapp_db`.`products` (`id_product`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--CREATE INDEX `fk_invoices_has_products_products1_idx` ON `popapp_db`.`invoices_has_products`(products_id_product);
--CREATE INDEX `fk_invoices_has_products_invoices1_idx` ON `popapp_db`.`invoices_has_products`(invoices_id_invoice);

CREATE TABLE IF NOT EXISTS `popapp_db`.`refresh_tokens` (
   `id_refresh_token` INT NOT NULL AUTO_INCREMENT,
   `users_id_user` INT NOT NULL,
   `token` NCHAR(64) NOT NULL,
    `date_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expiration_date` DATETIME NOT NULL,
    PRIMARY KEY (`id_refresh_token`, `users_id_user`),
  CONSTRAINT `fk_refresh_tokens_users1`
    FOREIGN KEY (`users_id_user`)
    REFERENCES `popapp_db`.`users` (`id_user`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
