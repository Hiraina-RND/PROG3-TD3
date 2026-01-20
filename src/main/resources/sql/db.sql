create database "mini_dish_db";

create user "mini_dish_db_manager" with password '123456';

\c "mini_dish_db"

GRANT CONNECT ON DATABASE "mini_dish_db" TO "mini_dish_db_manager";

GRANT USAGE, CREATE ON SCHEMA public TO "mini_dish_db_manager";

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public TO "mini_dish_db_manager";

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO "mini_dish_db_manager";