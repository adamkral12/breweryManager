INSERT INTO KEG (CAPACITY,PRICE,DEPOSIT,BRAND) VALUES (50,1500,500,'Plzeň');
INSERT INTO KEG (CAPACITY,PRICE,DEPOSIT,BRAND) VALUES (30,1000,500,'Radegast');
INSERT INTO KEG (CAPACITY,PRICE,DEPOSIT,BRAND) VALUES (15,750,500,'Kozel');


INSERT INTO EMPLOYEE(NAME,EMAIL,PHONENUMBER,SALARY, POSITION) VALUES ('Adam Král','kral.adam95@gmail.com', '7771473892', 50000, 'MANAGER' );
INSERT INTO EMPLOYEE(NAME,EMAIL,PHONENUMBER,SALARY, POSITION) VALUES ('Honza Novák','honza.novak95@gmail.com', '777183892', 20000, 'BEER_TASTER' );
INSERT INTO EMPLOYEE(NAME,EMAIL,PHONENUMBER,SALARY, POSITION) VALUES ('Vašek Kondula','vkondula95@gmail.com', '789456132', 50000, 'CLEANING_LADY' );


INSERT INTO JOB (employee_id, keg_id, start_date, start_time) values (1,1,'2016-04-20', '20:00:00');
INSERT INTO JOB (employee_id, keg_id, start_date, start_time, end_date, end_time) values (1,2,'2016-04-20', '19:00:00', '2016-04-20', '23:00:00');
INSERT INTO JOB (employee_id, keg_id, start_date, start_time, end_date, end_time) values (2,3,'2016-04-19', '15:30:00', '2016-04-20', '23:00:00');