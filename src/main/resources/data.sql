-- INSERT VALUES INTO DATABASE --
INSERT INTO `popapp_db`.`events` (`name`, `date_created`, `is_active`)
VALUES ('Event 1', CURRENT_TIMESTAMP(), 1);
INSERT INTO `popapp_db`.`events` (`name`, `date_created`, `is_active`)
VALUES ('Event 2', CURRENT_TIMESTAMP(), 0);;

INSERT INTO `popapp_db`.`stores` (`events_id_event`, `store_name`, `balance`)
VALUES (1, 'Store 1', 0),
       (1, 'Store 2', 0),
       (2, 'Store 3', 0);

INSERT INTO `popapp_db`.`roles` (`role_name`)
VALUES ('buyer'),
       ('seller'),
       ('admin');

INSERT INTO `popapp_db`.`users` (`roles_id_role`, `events_id_event`, `stores_id_store`, `name`, `surname`, `email`,
                                 `username`, `password_salt`, `password_hash`, `date_registered`, `balance`,
                                 `is_accepted`)
VALUES (3, 1, 1, 'Catherine', 'Velasquez', 'cvelasquez@pop.app', 'cvelasquez', '55f312f84e7785aa1efa552acbf251db',
        'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', CURRENT_TIMESTAMP(), 1000, 1),
       (1, 1, 1, 'Dayton', 'Huff', 'dhuff@pop.app', 'dhuff', 'd125a874318684c7d491a20acbd3b879',
        '937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244', CURRENT_TIMESTAMP(), 300, 1),
       (1, 1, 1, 'Naomi', 'Morse', 'nmorse@pop.app', 'nmorse', 'a394664f9ae7066d4cafba639423de48',
        '6fec2a9601d5b3581c94f2150fc07fa3d6e45808079428354b868e412b76e6bb', CURRENT_TIMESTAMP(), 300, 1),
       (1, 1, 2, 'Bruno', 'Hartman', 'bhartman@pop.app', 'bhartman', 'e1b1934332d9c1a1d4b9879a0ca0bf59',
        '85777f270ad7cf2a790981bbae3c4e484a1dc55e24a77390d692fbf1cffa12fa', CURRENT_TIMESTAMP(), 300, 0),
       (1, 1, 2, 'Steven', 'Zhang', 'szhang@pop.app', 'szhang', '55f312f84e7785aa1efa552acbf251db',
        '3dbd94bfa4791006d765fa98d3e65150f3548cb4c15a968413a7eeefeeaa4738', CURRENT_TIMESTAMP(), 300, 1),
       (1, 1, 2, 'Reina', 'Crosby', 'rcrosby@pop.app', 'rcrosby', 'd125a874318684c7d491a20acbd3b879',
        '8da0e2f2312433952b8418122290e0efa938d1f9e5fb20f15e3c93d1f4a520f9', CURRENT_TIMESTAMP(), 300, 1),
       (1, 1, 2, 'Emmanuel', 'Blake', 'eblake@pop.app', 'eblake', 'a394664f9ae7066d4cafba639423de48',
        '5fce29b00e80081daecfb5c273966310513d1d56257e90fa27becd3c71522e42', CURRENT_TIMESTAMP(), 300, 1),
       (2, 1, 2, 'Sean', 'Barry', 'sbarry@pop.app', 'sbarry', 'e1b1934332d9c1a1d4b9879a0ca0bf59',
        'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', CURRENT_TIMESTAMP(), 0, 1),
       (2, 2, 3, 'Olivia', 'Preston', 'opreston@pop.app', 'opreston', '55f312f84e7785aa1efa552acbf251db',
        '937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244', CURRENT_TIMESTAMP(), 0, 1),
       (2, 2, 3, 'Nora', 'Alvarez', 'nalvarez@pop.app', 'nalvarez', 'd125a874318684c7d491a20acbd3b879',
        '6fec2a9601d5b3581c94f2150fc07fa3d6e45808079428354b868e412b76e6bb', CURRENT_TIMESTAMP(), 0,
        1),
       (1, 2, 3, 'Josh', 'Cross', 'jcross@pop.app', 'jcross', 'a394664f9ae7066d4cafba639423de48',
        '85777f270ad7cf2a790981bbae3c4e484a1dc55e24a77390d692fbf1cffa12fa', CURRENT_TIMESTAMP(), 50,
        0),
       (2, 2, 3, 'Dashawn', 'Cervantes', 'dcervantes@pop.app', 'dcervantes', 'e1b1934332d9c1a1d4b9879a0ca0bf59',
        '3dbd94bfa4791006d765fa98d3e65150f3548cb4c15a968413a7eeefeeaa4738', CURRENT_TIMESTAMP(), 0,
        1),
       (2, 2, 3, 'Jada', 'Padilla', 'jpadilla@pop.app', 'jpadilla', '55f312f84e7785aa1efa552acbf251db',
        '8da0e2f2312433952b8418122290e0efa938d1f9e5fb20f15e3c93d1f4a520f9', CURRENT_TIMESTAMP(), 0,
        1),
       (1, 2, 3, 'Connor', 'Kaufman', 'ckaufman@pop.app', 'ckaufman', 'd125a874318684c7d491a20acbd3b879',
        'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', CURRENT_TIMESTAMP(), 75,
        0),
       (2, 2, 3, 'Haven', 'Arroyo', 'harroyo@pop.app', 'harroyo', 'a394664f9ae7066d4cafba639423de48',
        '937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244', CURRENT_TIMESTAMP(), 0,
        0);

INSERT INTO `popapp_db`.`products` (`name`, `description`, `image`, `price`, `quantity`)
VALUES ('Product 1', 'Description 1', 'image1.jpg', 10, 100),
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

INSERT INTO `popapp_db`.`packages` (`name`, `description`, `image`, `discount_on_items`, `amount`)
VALUES ('Package 1', 'Description 1', 'package1.jpg', 5, 100),
       ('Package 2', 'Description 2', 'package2.jpg', 10, 200),
       ('Package 3', 'Description 3', 'package3.jpg', 15, 300),
       ('Package 4', 'Description 4', 'package4.jpg', 20, 400),
       ('Package 5', 'Description 5', 'package5.jpg', 25, 500);

INSERT INTO `popapp_db`.`packages_has_products` (`packages_id_package`, `products_id_product`)
VALUES (1, 1),
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

INSERT INTO `popapp_db`.`invoices` (`stores_id_store`, `users_id_user`, `code`, `date_issued`, `discount`, `total`)
VALUES (1, 1, 'INV001', CURRENT_TIMESTAMP(), 0, 150),
       (2, 2, 'INV002', CURRENT_TIMESTAMP(), 10, 250),
       (1, 3, 'INV003', CURRENT_TIMESTAMP(), 20, 350),
       (1, 4, 'INV004', CURRENT_TIMESTAMP(), 0, 450),
       (2, 5, 'INV005', CURRENT_TIMESTAMP(), 5, 550);

INSERT INTO `popapp_db`.`invoices_has_products` (`invoices_id_invoice`, `products_id_product`, `amount`)
VALUES (1, 1, 2),
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

INSERT INTO `popapp_db`.`invoices_has_packages` (`invoices_id_invoice`, `packages_id_package`, `amount`)
VALUES (1, 1, 1),
       (1, 2, 1),
       (2, 2, 2),
       (2, 3, 2),
       (3, 3, 3),
       (3, 4, 3);
