insert into dish (id, name, dish_type)
values (1, 'Salaide fraîche', 'STARTER'),
       (2, 'Poulet grillé', 'MAIN'),
       (3, 'Riz aux légumes', 'MAIN'),
       (4, 'Gâteau au chocolat ', 'DESSERT'),
       (5, 'Salade de fruits', 'DESSERT');

SELECT setval('dish_id_seq', (SELECT COALESCE(MAX(id), 0) FROM dish));

insert into ingredient (id, name, category, price)
values (1, 'Laitue', 'VEGETABLE', 800.0),
       (2, 'Tomate', 'VEGETABLE', 600.0),
       (3, 'Poulet', 'ANIMAL', 4500.0),
       (4, 'Chocolat ', 'OTHER', 3000.0),
       (5, 'Beurre', 'DAIRY', 2500.0);

SELECT setval('ingredient_id_seq', (SELECT COALESCE(MAX(id), 0) FROM ingredient));


update dish
set price = 2000.0
where id = 1;

update dish
set price = 6000.0
where id = 2;


-- data for dish_ingredient table
insert into dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit)
values (1, 1, 1, 0.20, 'KG'),
       (2, 1, 2, 0.15, 'KG'),
       (3, 2, 3, 1.00, 'KG'),
       (4, 4, 4, 0.30, 'KG'),
       (5, 4, 5, 0.20, 'KG');

select setval('dish_ingredient_id_seq', (SELECT COALESCE(MAX(id), 0) FROM dish_ingredient));

-- new data for dish_table
insert into dish (id, name, dish_type, selling_price)
values (1, 'Salade fraîche', 'STARTER', 3500.00),
       (2, 'Poulet grillé', 'MAIN', 12000.00),
       (3, 'Riz aux légumes', 'MAIN', null),
       (4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
       (5, 'Salade de fruits', 'DESSERT', null);

select setval('dish_id_seq', (SELECT COALESCE(MAX(id), 0) FROM dish));