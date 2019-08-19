-- Meant for PostgreSQL 9.5  --
-- DO NOT FORGET TO EXECUTE ALSO sql_functions/register_employee.sql


-- (re)create tables first

DROP TABLE if exists ProjectEmployee;
DROP TABLE if exists Project;
DROP TABLE if exists Employee;
DROP TABLE if exists Department;
DROP TABLE if exists Company;

CREATE TABLE Company (
  pid SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  address TEXT
);

CREATE TABLE Department (
  pid SERIAL PRIMARY KEY,
  company_pid INTEGER NOT NULL REFERENCES Company (pid) ON DELETE NO ACTION,
  name TEXT NOT NULL
);

CREATE TABLE Employee (
  pid            SERIAL PRIMARY KEY,
  department_pid INTEGER NOT NULL REFERENCES Department (pid) ON DELETE NO ACTION,
  name           TEXT    NOT NULL,
  surname        TEXT    NOT NULL,
  email          TEXT,
  salary         NUMERIC(10, 2) CHECK (salary > 0),
  CONSTRAINT emloyee_name_surname_unique UNIQUE (name, surname)
);


CREATE TABLE Project (
  pid SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  dateStarted date
);

CREATE TABLE ProjectEmployee (
  project_pid INTEGER REFERENCES Project ON DELETE CASCADE,
  employee_pid INTEGER REFERENCES Employee ON DELETE CASCADE,
  PRIMARY KEY (project_pid, employee_pid)
);

-- now insertProject some test data

INSERT INTO Company (name, address) VALUES ('CleverGang', 'Prague, Czech Republic');
INSERT INTO Company (name, address) VALUES ('Supersoft', 'Berlin, Germany');
INSERT INTO Company (name, address) VALUES ('Pear', 'Cupertino, USA');

INSERT INTO Department (company_pid, name) VALUES (1, 'Back office');
INSERT INTO Department (company_pid, name) VALUES (1, 'IT Department');
INSERT INTO Department (company_pid, name) VALUES (1, 'Software Development');
INSERT INTO Department (company_pid, name) VALUES (2, 'Help desk');
INSERT INTO Department (company_pid, name) VALUES (2, 'Sales');
INSERT INTO Department (company_pid, name) VALUES (3, 'Hardware Development');
INSERT INTO Department (company_pid, name) VALUES (1, 'Lazy Department');

-- names generated using listofrandomnames.com ;)
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (1, 'Curt', 'Odegaard', 'curt.odegaard@clevergang.com', 10000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (2, 'Rupert', 'Spradling', 'rupert.spradling@clevergang.com', 11000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (3, 'Carita', 'Ladouceur', 'carita.ladouceur@clevergang.com', 12000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (4, 'Abbie', 'Waring', 'abbie.waring@supersoft.com', 13000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (5, 'Cecily', 'Devaughn', 'cecily.devaughn@supersoft.com', 15000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (6, 'Yulanda', 'Grado', 'yulanda.grado@pear.com', 17000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (1, 'Chia', 'Kuder', 'chia.kuder@clevergang.com', 20000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (2, 'Alica', 'Iannotti', 'alica.iannotti@clevergang.com', 30000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (3, 'Estrella', 'Heroux', 'estrella.heroux@clevergang.com', 51000);
INSERT INTO Employee (department_pid, name, surname, email, salary)
  VALUES (4, 'Myrta', 'Lirette', 'myrta.lirette@supersoft.com', 54250);


INSERT INTO Project (name, dateStarted) VALUES ('Awesome app', CURRENT_DATE);
INSERT INTO Project (name, dateStarted) VALUES ('Desktop app', CURRENT_DATE);


INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (1, 1);
INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (1, 2);
INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (1, 9);
INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (2, 10);
INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (2, 5);
INSERT INTO ProjectEmployee (project_pid, employee_pid) VALUES (1, 4);

