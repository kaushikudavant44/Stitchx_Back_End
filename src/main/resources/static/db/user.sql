REM INSERTING into EXPORT_TABLE
SET DEFINE OFF;
insert into users ("created_at","updated_at","email","password","status","username") values (to_timestamp('2020-01-01 00:00:00.0','null'),to_timestamp('2020-01-01 00:00:00.0','null'),'admin@gmail.com','$2a$10$zXLRvGA1qZj2K4RoAhfbrOGdCAnhDNYJq7yiKWgxubPJLSgIG/mu6','A','admin');
Insert into users ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (2,to_timestamp('2022-06-28 02:22:03.0','null'),to_timestamp('2022-06-28 02:22:03.0','null'),null,'rohitudavant44@gmail.com','Rohit','Udavant','$2a$10$SKnFzlZSfe6FBjjG7/YZwea82KXIlPBZeIVaYzr6wa.SJN5PqXTQG','A','999999999');
Insert into users ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (3,to_timestamp('2022-07-09 19:32:09.0','null'),to_timestamp('2022-07-09 19:32:09.0','null'),null,'kaushikudavant44@gmail.com',null,null,'$2a$10$Ez0O2n1/mwVfLFZdfdUuVe.O/4ps4uS/RxxHLz4atcz7UecdmViLC','A','9823875548');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (4,to_timestamp('2022-07-09 23:37:21.0','null'),to_timestamp('2022-07-09 23:37:21.0','null'),null,'maheshudavant44@gmail.com',null,null,'$2a$10$4dqfpakHZz53jZcFnzrAbu7NggMrUyAmagkQrj429o/NLcqYSCtNS','A','9595443418');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (5,to_timestamp('2022-07-16 18:46:21.0','null'),to_timestamp('2022-07-16 18:46:21.0','null'),null,'kfor@gmail.com',null,null,'$2a$10$0gxTg5SGLZi91VNdgb97wumsVXjMJpWwuo7QGch2VnTGm0a0Pzx4K','A','7447305548');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (6,to_timestamp('2022-07-16 18:53:16.0','null'),to_timestamp('2022-07-16 18:53:16.0','null'),null,'kfor1@gmail.com',null,null,'$2a$10$watQ1nxqU./Bb6X8r8y4vOYGroa46VXk0juFNMj2nf7HVJWPazM4q','0','7447305549');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (7,to_timestamp('2022-07-16 18:59:36.0','null'),to_timestamp('2022-07-16 18:59:36.0','null'),null,'kaushik@gmail.com',null,null,'$2a$10$5pLPfXjRcz6P/8CxFpw8seCYVCt5CNuPrzYuPOmYO0tupjvLlH1fm','0','8888888888');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (8,to_timestamp('2022-07-16 19:01:41.0','null'),to_timestamp('2022-07-16 19:01:41.0','null'),null,'kaushik1@gmail.com',null,null,'$2a$10$IUJITjrVnVmRia8Vd.s10.fBtW7uzvL7G1lxxzxh0PmBbm2FCH/Hm','0','9090909090');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (9,to_timestamp('2022-07-16 19:04:52.0','null'),to_timestamp('2022-07-16 19:04:55.0','null'),null,'kaushik12@gmail.com',null,null,'$2a$10$yKhQmhVYFNeaLZqkuLPtp..lFFcP1AlC5en7FWTxVDNV9fb3gqWvG','1','9090909091');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (10,to_timestamp('2022-07-16 20:14:37.0','null'),to_timestamp('2022-07-16 20:15:21.0','null'),null,'kaushik13@gmail.com',null,null,'$2a$10$EDCeqdGzaj5vJWqz8.dFI.kzcWYVc9Umzy3.2cJXSopUWF6VtGj2C','0','9090909099');
Insert into EXPORT_TABLE ("user_id","created_at","updated_at","contact_num","email","first_name","last_name","password","status","username") values (11,to_timestamp('2022-07-16 20:16:06.0','null'),to_timestamp('2022-07-16 20:16:25.0','null'),null,'kaushik134@gmail.com',null,null,'$2a$10$4cXdcexAfxrF3vDFWcSqLOB7qDi9gJd2ahkciloOHRZOq1727fKiC','0','9090909100');


GRANT ALL ON *.* to stitchx@68.178.162.123 identified by Rugved@44; flush privileges;

GRANT ALL PRIVILEGES ON *.* TO 'stitchx'@'localhost' WITH GRANT OPTION;

CREATE USER 'stitchx'@'%' IDENTIFIED BY 'Rugved@44';

