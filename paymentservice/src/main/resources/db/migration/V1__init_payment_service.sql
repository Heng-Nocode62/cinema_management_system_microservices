CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE TABLE payments(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL ,
    user_id UUID NOT NULL ,
    amount DECIMAL(10,2) NOT NULL ,
    currency CHAR(3)  NOT NULL DEFAULT 'USD',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    khqr_string TEXT,
    khqr_md5 VARCHAR(32),
    khqr_deeplink TEXT,
    khqr_expires_at TIMESTAMPTZ,

    bakong_hash TEXT,
    bakong_from_id VARCHAR(100),
    bakong_to_id VARCHAR(100),
    bakong_amount DECIMAL(10,2),
    bakong_currency CHAR(3),
    bakong_description TEXT,

    bill_number VARCHAR(50) UNIQUE ,
    error_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    paid_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ
);

CREATE INDEX idx_payment_booking ON payments(booking_id);
CREATE INDEX idx_payment_khqr_md5 ON payments(khqr_md5);
CREATE INDEX idx_payment_bill_number ON payments(bill_number);

CREATE TABLE refunds(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL REFERENCES payments(id),
    amount DECIMAL(10,2) NOT NULL ,
    reason TEXT,
    status VARCHAR(20) NOT NULL  DEFAULT 'PENDING',
    error_message TEXT,
    initiated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);
CREATE TABLE promo_codes(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE ,
    discount_type VARCHAR(10) NOT NULL ,
    discount_value DECIMAL(8,2) NOT NULL ,
    max_uses INT,
    uses_count INT NOT NULL DEFAULT 0,
    valid_until DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);