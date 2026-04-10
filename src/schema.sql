DROP DATABASE IF EXISTS coaching_centre;
CREATE DATABASE coaching_centre;
USE coaching_centre;

CREATE TABLE batches (
    batch_id INT PRIMARY KEY AUTO_INCREMENT,
    subject VARCHAR(100),
    timeslot VARCHAR(50)
);

CREATE TABLE students (
    stu_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    phone VARCHAR(15),
    batch_id INT,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
);

CREATE TABLE fee_payments (
    pay_id INT PRIMARY KEY AUTO_INCREMENT,
    stu_id INT,
    pay_month VARCHAR(7),
    paid_on DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (stu_id) REFERENCES students(stu_id)
);

INSERT INTO batches (subject, timeslot) VALUES ('Math', '7AM'), ('English', '5PM'), ('Physics', '10AM');


