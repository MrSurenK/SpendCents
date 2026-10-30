-- Insert Dummy user account --
-- INSERT INTO user_account (email,username,first_name,last_name,date_of_birth,occupation,password) VALUES ('dummyUser01@email.com',
-- 'dummyUser01','Sam','Smith','2026-04-11','Cleaner','$2a$12$wOv.h5eRmnQY0pKhnP9GNegVt0xJRV0KgsD7w2yQKliX/qQA7wY9O');
-- INSERT INTO user_account (email,username,first_name,last_name,date_of_birth,occupation,password) VALUES ('dummyUser02@email.com',
-- 'dummyUser02','Tony','Stark','2026-04-21','SWE','$2a$12$IrP.YECyun.ruDsuy2xeu.LIoAV6kWbaRdM493P5fAItUe09m.foa');

-- Default categories for prod db initialization--
--Expense Categories --
INSERT INTO category (name,transaction_type,is_system) VALUES('Rent','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Groceries','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Utilities','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Transportation','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Insurance','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Food','EXPENSE',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Subscription','EXPENSE',True);

-- Income Categories
INSERT INTO category (name,transaction_type,is_system) VALUES('Salary','INCOME',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Dividends','INCOME',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Freelancing','INCOME',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Bonus','INCOME',True);
INSERT INTO category (name,transaction_type,is_system) VALUES('Allowance','INCOME',True);


--Insert Dummy Transactions for Sam Smith--
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),125.50,'Groceries','Groceries from supermarket',false,'2026-04-09',NOW(),1,2);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),4000.00, 'Salary',"pay day",true,'2026-04-21','2026-04-21',NOW(),1,8);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),5000.00, 'Bonus','Performance Bonus',false,'2026-04-27',NOW(),1,11);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),5000.00, 'Bonus','Annual Bonus',false,'2026-04-22',NOW(),1,11);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),80.00, 'Phone bill','Circles life bill',true,'2026-04-05','2026-04-05',NOW(),1,3);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),5, 'MRT','Transportation to work and back',false,'2026-04-09',NOW(),1,4);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),5,'MRT','Transportation to work and back',false,'2026-04-10',NOW(),1,4);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),7.50,'Dinner','Hawker food below',false,'2026-04-09',NOW(),1,6);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),100.00, 'PruShield','Prudential Hospital Plan',true,'2026-04-09','2026-04-09',NOW(),1,5);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),40.00, 'Grab',"Grab to friend's place",false,'2026-04-11',NOW(),1,4);
--
---- Subscriptions --
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),35.00,'Netflix','Netflix subscription',true,'2026-04-09','2026-04-09',NOW(),1,7);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),40.00,'Disney','Disney Subscription',true,'2026-04-09','2026-04-09',NOW(),1,7);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),19.00,'HBO Max','HBO subscription',true,'2026-04-09','2026-04-09',NOW(),1,7);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),110.00,'Microsoft Office','Microsoft Office subscription',true,'2026-04-09','2026-04-09',NOW(),1,7);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),20.00,'iCloud','Cloud Storage',true,'2026-04-09','2026-04-09',NOW(),1,7);
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES (
--UUID_TO_BIN(UUID()),13.00,'GoodNotes','Note taking application subscription',true,'2026-04-09','2026-04-09',NOW(),1,7);
--
---- Additional historical transactions for chart testing (dummyUser01 / user_account_id = 1)
---- 2025 full year + 2026 Jan-Mar so yearly line chart has trend data
--
---- Monthly salary history (recurring income)
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES
--(UUID_TO_BIN(UUID()),3900.00,'Salary','Monthly salary',true,'2025-01-21','2025-02-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),3920.00,'Salary','Monthly salary',true,'2025-02-21','2025-03-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),3950.00,'Salary','Monthly salary',true,'2025-03-21','2025-04-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),3950.00,'Salary','Monthly salary',true,'2025-04-21','2025-05-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),3980.00,'Salary','Monthly salary',true,'2025-05-21','2025-06-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4000.00,'Salary','Monthly salary',true,'2025-06-21','2025-07-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4000.00,'Salary','Monthly salary',true,'2025-07-21','2025-08-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4050.00,'Salary','Monthly salary',true,'2025-08-21','2025-09-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4050.00,'Salary','Monthly salary',true,'2025-09-21','2025-10-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4100.00,'Salary','Monthly salary',true,'2025-10-21','2025-11-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4100.00,'Salary','Monthly salary',true,'2025-11-21','2025-12-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4150.00,'Salary','Monthly salary',true,'2025-12-21','2026-01-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4180.00,'Salary','Monthly salary',true,'2026-01-21','2026-02-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4200.00,'Salary','Monthly salary',true,'2026-02-21','2026-03-21',NOW(),1,8),
--(UUID_TO_BIN(UUID()),4200.00,'Salary','Monthly salary',true,'2026-03-21','2026-04-21',NOW(),1,8);
--
---- Monthly rent history (recurring expense)
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,next_due_date,last_updated,user_account_id,category_id) VALUES
--(UUID_TO_BIN(UUID()),1150.00,'Rent','Monthly room rental',true,'2025-01-01','2025-02-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1150.00,'Rent','Monthly room rental',true,'2025-02-01','2025-03-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1160.00,'Rent','Monthly room rental',true,'2025-03-01','2025-04-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1160.00,'Rent','Monthly room rental',true,'2025-04-01','2025-05-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1170.00,'Rent','Monthly room rental',true,'2025-05-01','2025-06-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1170.00,'Rent','Monthly room rental',true,'2025-06-01','2025-07-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1180.00,'Rent','Monthly room rental',true,'2025-07-01','2025-08-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1180.00,'Rent','Monthly room rental',true,'2025-08-01','2025-09-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1190.00,'Rent','Monthly room rental',true,'2025-09-01','2025-10-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1190.00,'Rent','Monthly room rental',true,'2025-10-01','2025-11-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1200.00,'Rent','Monthly room rental',true,'2025-11-01','2025-12-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1200.00,'Rent','Monthly room rental',true,'2025-12-01','2026-01-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1220.00,'Rent','Monthly room rental',true,'2026-01-01','2026-02-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1220.00,'Rent','Monthly room rental',true,'2026-02-01','2026-03-01',NOW(),1,1),
--(UUID_TO_BIN(UUID()),1230.00,'Rent','Monthly room rental',true,'2026-03-01','2026-04-01',NOW(),1,1);
--
---- Variable monthly spend history (mostly groceries + transport + food)
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES
--(UUID_TO_BIN(UUID()),260.40,'Groceries','Monthly groceries top-up',false,'2025-01-10',NOW(),1,2),
--(UUID_TO_BIN(UUID()),48.50,'Transport card','MRT bus top-up',false,'2025-01-15',NOW(),1,4),
--(UUID_TO_BIN(UUID()),320.80,'Groceries','Monthly groceries top-up',false,'2025-02-11',NOW(),1,2),
--(UUID_TO_BIN(UUID()),61.20,'Dining out','Weekend meals',false,'2025-02-20',NOW(),1,6),
--(UUID_TO_BIN(UUID()),285.30,'Groceries','Monthly groceries top-up',false,'2025-03-09',NOW(),1,2),
--(UUID_TO_BIN(UUID()),52.00,'Transport card','MRT bus top-up',false,'2025-03-17',NOW(),1,4),
--(UUID_TO_BIN(UUID()),340.00,'Groceries','Monthly groceries top-up',false,'2025-04-10',NOW(),1,2),
--(UUID_TO_BIN(UUID()),75.40,'Dining out','Family dinner',false,'2025-04-22',NOW(),1,6),
--(UUID_TO_BIN(UUID()),298.70,'Groceries','Monthly groceries top-up',false,'2025-05-12',NOW(),1,2),
--(UUID_TO_BIN(UUID()),55.80,'Transport card','MRT bus top-up',false,'2025-06-14',NOW(),1,4),
--(UUID_TO_BIN(UUID()),332.40,'Groceries','Monthly groceries top-up',false,'2025-07-10',NOW(),1,2),
--(UUID_TO_BIN(UUID()),82.60,'Dining out','Dinner with friends',false,'2025-07-25',NOW(),1,6),
--(UUID_TO_BIN(UUID()),305.20,'Groceries','Monthly groceries top-up',false,'2025-08-13',NOW(),1,2),
--(UUID_TO_BIN(UUID()),58.90,'Transport card','MRT bus top-up',false,'2025-09-12',NOW(),1,4),
--(UUID_TO_BIN(UUID()),349.10,'Groceries','Monthly groceries top-up',false,'2025-10-08',NOW(),1,2),
--(UUID_TO_BIN(UUID()),90.00,'Dining out','Birthday dinner',false,'2025-10-28',NOW(),1,6),
--(UUID_TO_BIN(UUID()),315.75,'Groceries','Monthly groceries top-up',false,'2025-11-11',NOW(),1,2),
--(UUID_TO_BIN(UUID()),64.40,'Transport card','MRT bus top-up',false,'2025-12-06',NOW(),1,4),
--(UUID_TO_BIN(UUID()),338.90,'Groceries','Monthly groceries top-up',false,'2026-01-09',NOW(),1,2),
--(UUID_TO_BIN(UUID()),72.20,'Dining out','CNY gatherings',false,'2026-01-30',NOW(),1,6),
--(UUID_TO_BIN(UUID()),326.15,'Groceries','Monthly groceries top-up',false,'2026-02-10',NOW(),1,2),
--(UUID_TO_BIN(UUID()),57.35,'Transport card','MRT bus top-up',false,'2026-03-11',NOW(),1,4);
--
---- Periodic extra income for visible peaks in line chart
--INSERT INTO user_transactions (id,amount,title,remarks,recurring,transaction_date,last_updated,user_account_id,category_id) VALUES
--(UUID_TO_BIN(UUID()),1200.00,'Bonus','Performance bonus Q1',false,'2025-03-28',NOW(),1,11),
--(UUID_TO_BIN(UUID()),900.00,'Freelancing','Weekend project',false,'2025-06-20',NOW(),1,10),
--(UUID_TO_BIN(UUID()),1400.00,'Bonus','Performance bonus Q3',false,'2025-09-27',NOW(),1,11),
--(UUID_TO_BIN(UUID()),700.00,'Dividends','Quarterly dividend payout',false,'2025-12-29',NOW(),1,9),
--(UUID_TO_BIN(UUID()),1000.00,'Bonus','Performance bonus Q1',false,'2026-03-26',NOW(),1,11);
--
