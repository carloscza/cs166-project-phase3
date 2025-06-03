DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    userid   SERIAL PRIMARY KEY, 
    username TEXT UNIQUE NOT NULL, 
    password TEXT NOT NULL, 
    role     TEXT NOT NULL, 
    CHECK (role IN ('Customer', 'Pilot', 'Technician', 'Manager'))
);