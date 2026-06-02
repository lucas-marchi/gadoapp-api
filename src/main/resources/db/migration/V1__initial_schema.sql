-- V1: Initial schema for GadoApp
-- Generated from existing Hibernate-managed tables

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS herds (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id INTEGER NOT NULL REFERENCES users(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bovines (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'VIVO',
    gender VARCHAR(50) NOT NULL,
    breed VARCHAR(255),
    weight DOUBLE PRECISION,
    birth TIMESTAMP,
    description TEXT,
    herd_id INTEGER NOT NULL REFERENCES herds(id),
    mom_id INTEGER REFERENCES bovines(id),
    dad_id INTEGER REFERENCES bovines(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS weight_records (
    id SERIAL PRIMARY KEY,
    bovine_id INTEGER NOT NULL REFERENCES bovines(id),
    weight DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS birth_records (
    id SERIAL PRIMARY KEY,
    mother_id INTEGER NOT NULL REFERENCES bovines(id),
    calf_id INTEGER REFERENCES bovines(id),
    birth_date TIMESTAMP NOT NULL,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_records (
    id SERIAL PRIMARY KEY,
    bovine_id INTEGER NOT NULL REFERENCES bovines(id),
    type VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP NOT NULL,
    dosage VARCHAR(255),
    veterinarian VARCHAR(255),
    next_due_date TIMESTAMP,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_herds_user_id ON herds(user_id);
CREATE INDEX IF NOT EXISTS idx_bovines_herd_id ON bovines(herd_id);
CREATE INDEX IF NOT EXISTS idx_bovines_mom_id ON bovines(mom_id);
CREATE INDEX IF NOT EXISTS idx_bovines_dad_id ON bovines(dad_id);
CREATE INDEX IF NOT EXISTS idx_weight_records_bovine_id ON weight_records(bovine_id);
CREATE INDEX IF NOT EXISTS idx_birth_records_mother_id ON birth_records(mother_id);
CREATE INDEX IF NOT EXISTS idx_birth_records_calf_id ON birth_records(calf_id);
CREATE INDEX IF NOT EXISTS idx_health_records_bovine_id ON health_records(bovine_id);
