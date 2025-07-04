-- Create merchants table
CREATE TABLE merchants (
    id BIGSERIAL PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    settlement_account VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create settlement_batches table
CREATE TABLE settlement_batches (
    id BIGSERIAL PRIMARY KEY,
    batch_ref VARCHAR(255) NOT NULL UNIQUE,
    merchant_id BIGINT NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- Create transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    merchant_ref VARCHAR(255) NOT NULL UNIQUE,
    internal_ref VARCHAR(255) NOT NULL UNIQUE,
    fee DECIMAL(19,2) NOT NULL,
    merchant_id BIGINT NOT NULL,
    settlement_batch_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (settlement_batch_id) REFERENCES settlement_batches(id)
);

-- Add indexes for better performance
CREATE INDEX idx_merchants_email ON merchants(email);
CREATE INDEX idx_merchants_status ON merchants(status);
CREATE INDEX idx_transactions_merchant_id ON transactions(merchant_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_internal_ref ON transactions(internal_ref);
CREATE INDEX idx_transactions_merchant_ref ON transactions(merchant_ref);
CREATE INDEX idx_transactions_settlement_batch_id ON transactions(settlement_batch_id);
CREATE INDEX idx_settlement_batches_merchant_id ON settlement_batches(merchant_id);
CREATE INDEX idx_settlement_batches_batch_ref ON settlement_batches(batch_ref);
CREATE INDEX idx_settlement_batches_created_at ON settlement_batches(created_at); 