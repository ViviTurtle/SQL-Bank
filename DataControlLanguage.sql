CREATE USER 'Bank_User'@'localhost' IDENTIFIED BY 'bank';

GRANT ALL PRIVILEGES ON * . * TO 'Bank_User'@'localhost';

FLUSH PRIVILEGES;

SET SQL_SAFE_UPDATES = 0;