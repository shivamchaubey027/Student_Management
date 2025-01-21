# Student Management System

A simple Java-based student management system with MySQL database and web interface.

## Setting Up MySQL

1. Install MySQL if not already installed:
```bash
sudo apt update
sudo apt install mysql-server
```

2. Start MySQL service:
```bash
sudo service mysql start
```

3. Secure MySQL installation:
```bash
sudo mysql_secure_installation
```
   - Set a root password when prompted
   - Answer 'Y' to all remaining questions

4. Access MySQL:
```bash
sudo mysql -u root -p
```
Enter the password you set during secure installation.

5. View the database (after running the application):
```sql
-- Show all databases
SHOW DATABASES;

-- Use our database
USE student_db;

-- Show all tables
SHOW TABLES;

-- View all students
SELECT * FROM students;
```

## Project Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/student-management-system.git
cd student-management-system
```


3. Update MySQL credentials in config.properties with your database username and password.

## Commands to Run the project

javac -cp mysql-connector-java-8.0.27.jar *.java
java -cp .:mysql-connector-java-8.0.27.jar Main


## Using the Application

1. Add a Student:
   - Fill in the name, age (18-70), and marks (0-100)
   - Click "Save"

2. View Students:
   - All students are displayed in the table below the form

3. Update a Student:
   - Click "Edit" on any student row
   - Modify the details in the form
   - Click "Save"

4. Delete a Student:
   - Click "Delete" on any student row
   - Confirm the deletion

## Monitoring the Database

1. Connect to MySQL:
```bash
sudo mysql -u root -p
```

2. View data:
```sql
-- List all databases
SHOW DATABASES;

-- Select our database
USE student_db;

-- View all students
SELECT * FROM students;

-- View specific student
SELECT * FROM students WHERE id = 1;

-- View students sorted by name
SELECT * FROM students ORDER BY name;
```

## schema.sql
```sql
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    marks DOUBLE NOT NULL,
    CONSTRAINT age_check CHECK (age >= 18 AND age <= 70),
    CONSTRAINT marks_check CHECK (marks >= 0 AND marks <= 100)
);
```
