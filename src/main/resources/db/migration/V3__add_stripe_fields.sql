ALTER TABLE app_users ADD COLUMN IF NOT EXISTS stripe_subscription_id VARCHAR(255);
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS subscription_status VARCHAR(50);
