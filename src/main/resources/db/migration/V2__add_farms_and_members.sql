-- V2: Add Farms, FarmMembers, and link Herds to Farms

-- Farm entity
CREATE TABLE IF NOT EXISTS farms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    inscricao_estadual VARCHAR(50),
    city VARCHAR(255),
    state VARCHAR(2),
    address TEXT,
    total_area_ha DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- FarmMember join table
CREATE TABLE IF NOT EXISTS farm_members (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES app_users(id),
    farm_id INTEGER NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'OWNER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    invited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, farm_id)
);

-- Add farm_id to herds
ALTER TABLE herds ADD COLUMN IF NOT EXISTS farm_id INTEGER REFERENCES farms(id);

-- Add phone and stripe_customer_id to users
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS phone VARCHAR(50);
ALTER TABLE app_users ADD COLUMN IF NOT EXISTS stripe_customer_id VARCHAR(255);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_farm_members_user_id ON farm_members(user_id);
CREATE INDEX IF NOT EXISTS idx_farm_members_farm_id ON farm_members(farm_id);
CREATE INDEX IF NOT EXISTS idx_herds_farm_id ON herds(farm_id);

-- Migrate existing data: create a default farm for each existing user
-- and link their herds to it
DO $$
DECLARE
    u RECORD;
    new_farm_id INTEGER;
BEGIN
    FOR u IN SELECT id, name FROM app_users LOOP
        INSERT INTO farms (name, active, created_at, updated_at)
        VALUES (u.name || ' - Propriedade', true, NOW(), NOW())
        RETURNING id INTO new_farm_id;

        INSERT INTO farm_members (user_id, farm_id, role, status, invited_at)
        VALUES (u.id, new_farm_id, 'OWNER', 'ACTIVE', NOW());

        UPDATE herds SET farm_id = new_farm_id WHERE user_id = u.id;
    END LOOP;
END $$;
