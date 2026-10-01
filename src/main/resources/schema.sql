
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS bases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    location VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS equipment_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    unit_of_measure VARCHAR(30) NOT NULL,
    minimum_reserve INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role_id BIGINT NOT NULL,
    base_id BIGINT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_base FOREIGN KEY (base_id) REFERENCES bases(id)
);

CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    total_quantity INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0,
    assigned_quantity INT NOT NULL DEFAULT 0,
    committed_quantity INT NOT NULL DEFAULT 0,
    in_transfer_quantity INT NOT NULL DEFAULT 0,
    repair_quantity INT NOT NULL DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT uq_inventory_base_equipment UNIQUE (base_id, equipment_type_id),
    CONSTRAINT fk_inventory_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_inventory_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id)
);

CREATE TABLE IF NOT EXISTS inventory_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    transaction_id BIGINT NOT NULL,
    quantity_in INT NOT NULL DEFAULT 0,
    quantity_out INT NOT NULL DEFAULT 0,
    balance_after INT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_ledger_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_ledger_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    CONSTRAINT fk_ledger_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS purchases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    source VARCHAR(255),
    purchase_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by BIGINT NOT NULL,
    approved_by BIGINT NULL,
    approved_at DATETIME NULL,
    created_at DATETIME,
    CONSTRAINT fk_purchase_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_purchase_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_purchase_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS purchase_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    purchase_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uq_purchase_equipment UNIQUE (purchase_id, equipment_type_id),
    CONSTRAINT fk_purchase_item_purchase FOREIGN KEY (purchase_id) REFERENCES purchases(id),
    CONSTRAINT fk_purchase_item_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id)
);

CREATE TABLE IF NOT EXISTS transfers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_base_id BIGINT NOT NULL,
    to_base_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    requested_by BIGINT NOT NULL,
    approved_by BIGINT NULL,
    approved_at DATETIME NULL,
    dispatched_by BIGINT NULL,
    dispatched_at DATETIME NULL,
    received_by BIGINT NULL,
    received_at DATETIME NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_transfer_from_base FOREIGN KEY (from_base_id) REFERENCES bases(id),
    CONSTRAINT fk_transfer_to_base FOREIGN KEY (to_base_id) REFERENCES bases(id),
    CONSTRAINT fk_transfer_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_transfer_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_transfer_dispatched_by FOREIGN KEY (dispatched_by) REFERENCES users(id),
    CONSTRAINT fk_transfer_received_by FOREIGN KEY (received_by) REFERENCES users(id),
    CONSTRAINT chk_transfer_different_bases CHECK (from_base_id <> to_base_id)
);

CREATE TABLE IF NOT EXISTS transfer_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uq_transfer_equipment UNIQUE (transfer_id, equipment_type_id),
    CONSTRAINT fk_transfer_item_transfer FOREIGN KEY (transfer_id) REFERENCES transfers(id),
    CONSTRAINT fk_transfer_item_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id)
);

CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    assigned_to_type VARCHAR(30) NOT NULL,
    assigned_to_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    assigned_at DATETIME,
    returned_at DATETIME,
    created_by BIGINT NOT NULL,
    CONSTRAINT fk_assignment_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_assignment_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    CONSTRAINT fk_assignment_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS expenditures (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    equipment_type_id BIGINT NOT NULL,
    assignment_id BIGINT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    expended_at DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by BIGINT NOT NULL,
    approved_by BIGINT NULL,
    approved_at DATETIME NULL,
    created_at DATETIME,
    CONSTRAINT fk_expenditure_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_expenditure_equipment FOREIGN KEY (equipment_type_id) REFERENCES equipment_types(id),
    CONSTRAINT fk_expenditure_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    CONSTRAINT fk_expenditure_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_expenditure_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    old_value JSON NULL,
    new_value JSON NULL,
    timestamp DATETIME,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);
