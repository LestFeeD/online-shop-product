INSERT INTO product_category(name) VALUES ('Электроника') RETURNING id_product_category;
INSERT INTO characteristic(name) VALUES ( 'Мгц') RETURNING id_characteristic;
INSERT INTO warehouse(warehouse_number) VALUES ( 323) RETURNING id_warehouse;
