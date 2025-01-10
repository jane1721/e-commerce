-- 유저 데이터
INSERT INTO users (id, name, balance) VALUES (1, 'John', 100000), (2, 'Jane', 1000);

-- 주문 데이터
INSERT INTO orders (id, status, final_amount) VALUES (1, 'PENDING', 50000), (2, 'PENDING', 2000);

-- 상품 데이터
INSERT INTO items (id, name, stock, price) VALUES (1, 'Test Item', 10, 1000);

-- 주문 상품 데이터
INSERT INTO order_items (id, order_id, item_id, quantity) VALUES (1, 1, 1, 2), (2, 2, 1, 1);
