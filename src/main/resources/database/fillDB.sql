INSERT INTO tableSales ("rc_id", "ar_id", "wh_id", "sales" ) VALUES
(300, 100, 1, NULL),
(300, 100, 2, 0.674),
(300, 100, 3, 0.711),
(300, 100, 4, 0.676),
(300, 100, 5, 0.319),
(400, 200, 1, 0.436),
(500, 200, 2, 2.468),
(500, 200, 3, 0.414),
(500, 200, 4, 0.483),
(500, 200, 5, 1.294);

INSERT INTO tableValue ("rc_id", "ar_id", "wh_id", "type_id", "qnty", "perc" ) VALUES
(300, 100, 1, 1, 5.4, 1),
(300, 100, 1, 2, 0, 1.1),
(300, 100, 2, 1, 5.4, 1),
(300, 100, 2, 2, 0, 1.1),
(300, 100, 3, 1, 5.4, 1),
(300, 100, 3, 2, 0, 1.3),
(300, 100, 4, 1, 5.4, 1),
(300, 100, 4, 2, 0, 1.1),
(300, 100, 5, 1, 5.4, 1),
(300, 100, 5, 2, 0, 1.1),
(400, 200, 1, 1, 1.234, 1),
(400, 200, 1, 2, 0, 1.178),
(300, 200, 2, 1, 1.234, 1),
(300, 200, 2, 2, 0, 1.178),
(500, 200, 3, 1, 1.234, 1),
(500, 200, 3, 2, 0, 1.178),
(300, 200, 4, 1, 1.233, 1),
(300, 200, 4, 2, 0, 1.178),
(500, 200, 5, 1, 1.82, 1),
(500, 200, 5, 2, 0, 1.178);

INSERT INTO tableType ("ar_id", "type_id")VALUES
(100, 1),
(200, 2);


