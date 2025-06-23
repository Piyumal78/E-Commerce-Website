-- Insert sample categories
INSERT INTO categories (name, description, active, created_at, updated_at) VALUES
('Electronics', 'Electronic devices and gadgets', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Clothing', 'Fashion and apparel', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Books', 'Books and literature', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Home & Garden', 'Home improvement and gardening supplies', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample products
INSERT INTO products (name, description, price, image_url, stock_quantity, active, category_id, created_at, updated_at) VALUES
('Smartphone', 'Latest Android smartphone with 128GB storage', 599.99, 'https://example.com/smartphone.jpg', 50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Laptop', 'High-performance laptop for gaming and work', 1299.99, 'https://example.com/laptop.jpg', 25, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('T-Shirt', 'Comfortable cotton t-shirt', 19.99, 'https://example.com/tshirt.jpg', 100, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jeans', 'Classic blue denim jeans', 49.99, 'https://example.com/jeans.jpg', 75, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Programming Book', 'Learn Spring Boot development', 39.99, 'https://example.com/book.jpg', 30, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
