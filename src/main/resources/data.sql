INSERT INTO address (id, address_line, city, post_code, country)
VALUES
(1, '60 Gloucester Road', 'CLAPTON', 'TA18 4DQ', 'United Kingdom'),
(2, '98 Crescent Avenue', 'DRUMLEMBLE', 'PA28 1NE', 'United Kingdom'),
(3, '68 Harrogate Road', 'RUNNINGTON', 'TA21 8UE', 'United Kingdom'),
(4, '48 Fore St', 'TRIMDON COLLIERY', 'TS29 1YS', 'United Kingdom');


INSERT INTO shopper (id, first_name, last_name, email, date_of_birth, balance, address_id)
VALUES
(1, 'Charlotte', 'Hobbs', 'w58ga7w8bqa@temporary-mail.net', '1955-05-29', 20.3409, 1),
(2, 'Alfie', 'Wright', '9vqylz0fqov@temporary-mail.net', '1951-02-27', 0.0, 2),
(3, 'Jamie', 'Stone', '215bj0mnvcb@temporary-mail.net', '1982-06-24', 200, 3),
(4, 'Chloe', 'Akhtar', 'pnvy2nhlnz9@temporary-mail.net', '1984-11-12', 55.207, 4);


INSERT INTO point_transaction (point_transaction_id, point_amount, status, created_at, shopper_id)
VALUES
(1, 30, 0, '2019-02-11 12:14:45', 1),
(2, 13.3456, 0, '2020-02-15 05:14:23', 1),
(3, 120.36, 0, '2020-07-23 16:35:54', 1),
(4, 200, 0, '2021-02-03 19:32:45', 2),
(5, 12.2906, 0, '2021-06-11 23:14:11', 2),
(6, 150, 0, '2020-12-23 13:14:35', 3),
(7, 48.45, 0, '2021-02-24 17:34:45', 3),
(8, 89.346, 0, '2021-04-11 11:36:22', 3),
(9, 23.345, 0, '2021-08-16 19:39:45', 3),
(10, 68.396, 0, '2019-09-26 07:00:00', 4),
(11, 73.56, 0, '2020-03-17 09:14:33', 4),
(12, 91.2957, 0, '2021-05-25 06:37:58', 4);