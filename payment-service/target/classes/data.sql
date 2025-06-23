-- Insert sample payments
INSERT INTO payments (payment_reference, order_id, user_id, amount, currency, payment_method, status, gateway_transaction_id, gateway_response, created_at, updated_at) VALUES
('PAY-ABC123DEF456', 1001, 1, 99.99, 'USD', 'STRIPE', 'COMPLETED', 'stripe_ch_1234567890', 'Payment completed successfully', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAY-XYZ789GHI012', 1002, 2, 149.50, 'USD', 'PAYPAL', 'COMPLETED', 'paypal_tx_9876543210', 'Payment completed via PayPal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAY-MNO345PQR678', 1003, 1, 75.25, 'USD', 'CREDIT_CARD', 'FAILED', NULL, 'Card declined', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAY-STU901VWX234', 1004, 3, 200.00, 'USD', 'STRIPE', 'PENDING', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample refunds
INSERT INTO refunds (refund_reference, payment_id, amount, reason, status, gateway_refund_id, created_at, updated_at) VALUES
('REF-ABC123DEF456', 1, 25.00, 'Partial refund requested by customer', 'COMPLETED', 'stripe_re_1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REF-XYZ789GHI012', 2, 149.50, 'Full refund - item out of stock', 'PROCESSING', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
