-- Add merchant_ref column to merchants table (nullable first)
ALTER TABLE merchants ADD COLUMN merchant_ref VARCHAR(255);

-- Update existing records with generated merchant_ref values
UPDATE merchants SET merchant_ref = 'MERCH' || id || '_' || EXTRACT(EPOCH FROM created_at)::bigint WHERE merchant_ref IS NULL;

-- Make the column NOT NULL and UNIQUE
ALTER TABLE merchants ALTER COLUMN merchant_ref SET NOT NULL;
ALTER TABLE merchants ADD CONSTRAINT uk_merchants_merchant_ref UNIQUE (merchant_ref);

-- Create index for better performance
CREATE INDEX idx_merchants_merchant_ref ON merchants(merchant_ref); 