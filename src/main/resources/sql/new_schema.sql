create type dish_type as enum ('STARTER', 'MAIN', 'DESSERT');


create table dish
(
    id                serial primary key,
    name              varchar(255) unique,
    dish_type         dish_type,
    selling_price     numeric(10,2)
);

create type ingredient_category as enum ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

create table ingredient
(
    id       serial primary key,
    name     varchar(255) unique,
    price    numeric(10, 2),
    category ingredient_category
);

create type unit_type as enum ('PCS', 'KG', 'L');

create table dish_ingredient
(
    id                  serial primary key,
    id_dish             int references dish(id),
    id_ingredient       int references ingredient(id),
    quantity_required   numeric(6,3),
    unit                unit_type,
    constraint unique_dish_ingredient unique (id_dish, id_ingredient)
);