-- Delete all data and reset the ID counter in the "orders" table
TRUNCATE TABLE orders;


-- Insert sample data into the "orders" table
INSERT INTO orders (status,description) VALUES
    ('PENDING','DUMMY ORDER'),
    ('PENDING','DUMMY ORDER');