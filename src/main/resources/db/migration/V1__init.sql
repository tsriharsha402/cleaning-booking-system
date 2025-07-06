CREATE TABLE vehicle (
  id BIGINT PRIMARY KEY,
  label VARCHAR(50) NOT NULL
);

CREATE TABLE cleaner (
  id BIGINT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  vehicle_id BIGINT NOT NULL,
  CONSTRAINT fk_cleaner_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE TABLE booking (
  id BIGSERIAL PRIMARY KEY,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  duration_h INTEGER NOT NULL,
  customer VARCHAR(100),
  vehicle_id BIGINT NOT NULL,
  CONSTRAINT fk_booking_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE TABLE booking_cleaner (
  booking_id BIGINT NOT NULL,
  cleaner_id BIGINT NOT NULL,
  PRIMARY KEY (booking_id, cleaner_id),
  CONSTRAINT fk_bc_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
  CONSTRAINT fk_bc_cleaner FOREIGN KEY (cleaner_id) REFERENCES cleaner(id)
);

INSERT INTO vehicle (id, label) VALUES
  (1, 'Van A'), (2, 'Van B'), (3, 'Van C'), (4, 'Van D'), (5, 'Van E');

INSERT INTO cleaner (id, name, vehicle_id) VALUES
  (101, 'Aarav',     1), (102, 'Bharat',    1), (103, 'Chirag',    1), (104, 'Divya',    1), (105, 'Eshan',    1),
  (201, 'Farah',     2), (202, 'Gaurav',    2), (203, 'Harsha',    2), (204, 'Ishita',   2), (205, 'Jayant',   2),
  (301, 'Kavya',     3), (302, 'Lakshya',   3), (303, 'Meera',     3), (304, 'Nikhil',   3), (305, 'Omkar',   3),
  (401, 'Pranav',    4), (402, 'Ritika',    4), (403, 'Sneha',     4), (404, 'Tanmay',   4), (405, 'Utkarsh',  4),
  (501, 'Vaishali',  5), (502, 'Yash',      5), (503, 'Zoya',      5), (504, 'Aditya',   5), (505, 'Bhavya',   5);