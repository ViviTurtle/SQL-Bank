USE Bank;

DROP TRIGGER IF EXISTS INIT_ACCOUNT;
DELIMITER //
CREATE TRIGGER INIT_ACCOUNT
AFTER INSERT ON T_ACCOUNT
FOR EACH ROW
BEGIN
INSERT INTO T_CASH_AMOUNT (ACCOUNT_ID, ACCOUNT_TYPE, AMOUNT) VALUES (NEW.ACCOUNT_ID, 1, 0);
INSERT INTO T_CASH_AMOUNT (ACCOUNT_ID, ACCOUNT_TYPE, AMOUNT) VALUES (NEW.ACCOUNT_ID, 2, 0);
END//
DELIMITER ;

DROP TRIGGER IF EXISTS WITHDRAW_TR;
DELIMITER //
CREATE TRIGGER WITHDRAW_TR
AFTER INSERT ON T_CASH_AMOUNT
FOR EACH ROW
BEGIN
INSERT INTO T_TRANSANCTION_HISTORY SELECT NEW.ACCOUNT_ID, 7 ,NEW.ACCOUNT_TYPE, NEW.AMOUNT, CURRENT_TIMESTAMP;
END//
DELIMITER ;

#DROP TRIGGER IF EXISTS DEPOSIT_TR;
#DELIMITER //
#CREATE TRIGGER DEPOSIT_TR
#AFTER INSERT ON T_CASH_AMOUNT
#FOR EACH ROW
#BEGIN
#INSERT INTO T_TRANSANCTION_HISTORY SELECT NEW.ACCOUNT_ID, 7 ,NEW.ACCOUNT_TYPE, NEW.AMOUNT, CURRENT_TIMESTAMP;
#END//
#DELIMITER ;


DROP PROCEDURE IF EXISTS SP_VIEW_BALANCE;
DELIMITER //
CREATE PROCEDURE SP_VIEW_BALANCE(IN
P_ACCOUNT_ID INT,
P_ACCOUNT_TYPE INT)
BEGIN
SELECT AMOUNT
FROM T_CASH_AMOUNT
WHERE ACCOUNT_ID = P_ACCOUNT_ID AND ACCOUNT_TYPE = P_ACCOUNT_TYPE;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS SP_WITHDRAW_AMOUNT;
DELIMITER //
CREATE PROCEDURE SP_WITHDRAW_AMOUNT(IN
P_ACCOUNT_ID INT,
P_ACCOUNT_TYPE INT,
P_NEW_BALANCE DOUBLE)
BEGIN
IF EXISTS(SELECT * FROM T_CASH_AMOUNT WHERE ACCOUNT_ID = P_ACCOUNT_ID AND ACCOUNT_TYPE = P_ACCOUNT_TYPE AND AMOUNT < P_NEW_BALANCE)
THEN SELECT -1 AS AMOUNT;
ELSE UPDATE T_CASH_AMOUNT 
	SET AMOUNT = AMOUNT - P_NEW_BALANCE 
	WHERE ACCOUNT_ID = P_ACCOUNT_ID 
		AND ACCOUNT_TYPE = P_ACCOUNT_TYPE 
		AND AMOUNT > P_NEW_BALANCE;
SELECT AMOUNT FROM T_CASH_AMOUNT WHERE ACCOUNT_ID = P_ACCOUNT_ID AND ACCOUNT_TYPE = P_ACCOUNT_TYPE;
END IF;
END//
DELIMITER ;


DROP PROCEDURE IF EXISTS SP_DEPOSIT_AMOUNT;
DELIMITER //
CREATE PROCEDURE SP_DEPOSIT_AMOUNT(IN
P_ACCOUNT_ID INT,
P_ACCOUNT_TYPE INT,
P_NEW_BALANCE DOUBLE)
BEGIN
UPDATE T_CASH_AMOUNT
SET AMOUNT = AMOUNT + P_NEW_BALANCE
WHERE ACCOUNT_ID = P_ACCOUNT_ID AND ACCOUNT_TYPE = P_ACCOUNT_TYPE;
SELECT AMOUNT FROM T_CASH_AMOUNT WHERE ACCOUNT_ID = P_ACCOUNT_ID AND ACCOUNT_TYPE = P_ACCOUNT_TYPE;
END//
DELIMITER ;




DROP PROCEDURE IF EXISTS SP_DELETE_ACCOUNT;
DELIMITER //
CREATE PROCEDURE SP_DELETE_ACCOUNT(IN
P_USERNAME VARCHAR(50),
P_PASSWORD VARCHAR(50))
BEGIN
UPDATE T_ACCOUNT SET ACTIVE = 0 WHERE USERNAME = P_USERNAME AND PASS = P_PASSWORD;

IF EXISTS(SELECT ACTIVE FROM T_ACCOUNT WHERE USERNAME = P_USERNAME AND PASS = P_PASSWORD AND ACTIVE = 0) THEN 
SELECT 1 AS result;
ELSE SELECT 0 AS result;
END IF;
END//
DELIMITER  ;







DROP PROCEDURE IF EXISTS SP_CREATE_ACCOUNT;
DELIMITER //
CREATE PROCEDURE SP_CREATE_ACCOUNT(IN
P_F_NAME VARCHAR(50),
P_L_NAME VARCHAR(50),
P_EMAIL VARCHAR(50), 
P_USERNAME VARCHAR(50),
P_PASS VARCHAR(50))
BEGIN
INSERT INTO T_ACCOUNT (EMAIL, F_NAME, L_NAME, USERNAME, PASS)
VALUES (P_F_NAME, P_L_NAME, P_EMAIL, P_USERNAME, P_PASS);

SELECT ACCOUNT_ID
FROM T_ACCOUNT
WHERE USERNAME = P_USERNAME ;
END//
DELIMITER ;



DROP PROCEDURE IF EXISTS SP_LOGIN;
DELIMITER //
CREATE PROCEDURE SP_LOGIN(IN
P_USERNAME VARCHAR(50),
P_PASSWORD VARCHAR(50))
BEGIN
	IF EXISTS(SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE USERNAME = P_USERNAME AND PASS = P_PASSWORD AND ACTIVE = 0)
		THEN SELECT -1 AS ACCOUNT_ID;
	ELSEIF EXISTS(SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE USERNAME = P_USERNAME AND PASS = P_PASSWORD AND ACTIVE = 1)
		THEN SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE USERNAME = P_USERNAME AND PASS = P_PASSWORD AND ACTIVE = 1;
	ELSE SELECT -2 AS ACCOUNT_ID;
	END IF;
	UPDATE T_ACCOUNT
	SET LAST_LOGIN_DATE = CURRENT_TIMESTAMP WHERE USERNAME = P_USERNAME;
END//
DELIMITER  ;

DROP PROCEDURE IF EXISTS SP_CHECK_USERNAME;
DELIMITER //
CREATE PROCEDURE SP_CHECK_USERNAME(IN
P_USERNAME VARCHAR(50))
BEGIN
IF EXISTS(SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE USERNAME = P_USERNAME) THEN SELECT 1 AS result;
ELSE SELECT 0 AS result;
END IF;
END//
DELIMITER  ;



