USE military_asset_management;

INSERT INTO roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'BASE_COMMANDER'),
(3, 'LOGISTICS_OFFICER')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO bases (id, name, code, location, status) VALUES
(1, 'Alpha Military Base', 'AMB001', 'Chennai', 'ACTIVE'),
(2, 'Bravo Military Base', 'BMB001', 'Coimbatore', 'ACTIVE'),
(3, 'Charlie Military Base', 'CMB001', 'Madurai', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), location=VALUES(location), status=VALUES(status);

INSERT INTO equipment_types (id, name, category, unit_of_measure, minimum_reserve, status) VALUES
(1, '5.56mm Rifle', 'WEAPON', 'UNIT', 100, 'ACTIVE'),
(2, '7.62mm Ammunition', 'AMMUNITION', 'ROUND', 10000, 'ACTIVE'),
(3, '9mm Ammunition', 'AMMUNITION', 'ROUND', 5000, 'ACTIVE'),
(4, 'Military Truck', 'VEHICLE', 'UNIT', 5, 'ACTIVE'),
(5, 'Portable Radio', 'COMMUNICATION', 'UNIT', 20, 'ACTIVE'),
(6, 'Generator', 'POWER', 'UNIT', 5, 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name), category=VALUES(category), unit_of_measure=VALUES(unit_of_measure), minimum_reserve=VALUES(minimum_reserve), status=VALUES(status);

-- Demo password for all seeded users: password
INSERT INTO users (id, username, password_hash, full_name, role_id, base_id, status) VALUES
(1, 'admin', '$2a$10$ouWNqxiIuo9dFScuJyDUReHhANTG5nZUaD6SzX2IOnPsL95FVmxLq', 'System Administrator', 1, NULL, 'ACTIVE'),
(2, 'alpha_commander', '$2a$10$ouWNqxiIuo9dFScuJyDUReHhANTG5nZUaD6SzX2IOnPsL95FVmxLq', 'Alpha Base Commander', 2, 1, 'ACTIVE'),
(3, 'bravo_commander', '$2a$10$ouWNqxiIuo9dFScuJyDUReHhANTG5nZUaD6SzX2IOnPsL95FVmxLq', 'Bravo Base Commander', 2, 2, 'ACTIVE'),
(4, 'logistics_officer', '$2a$10$ouWNqxiIuo9dFScuJyDUReHhANTG5nZUaD6SzX2IOnPsL95FVmxLq', 'Logistics Officer', 3, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE username=VALUES(username), password_hash=VALUES(password_hash), full_name=VALUES(full_name), role_id=VALUES(role_id), base_id=VALUES(base_id), status=VALUES(status);

INSERT INTO inventory (id, base_id, equipment_type_id, total_quantity, available_quantity, assigned_quantity, committed_quantity, in_transfer_quantity, repair_quantity) VALUES
(1,1,1,500,500,0,0,0,0),
(2,1,2,50000,50000,0,0,0,0),
(3,1,3,20000,20000,0,0,0,0),
(4,1,4,20,20,0,0,0,0),
(5,1,5,50,50,0,0,0,0),
(6,1,6,10,10,0,0,0,0),
(7,2,1,300,300,0,0,0,0),
(8,2,2,30000,30000,0,0,0,0),
(9,2,3,10000,10000,0,0,0,0),
(10,2,4,10,10,0,0,0,0),
(11,2,5,30,30,0,0,0,0),
(12,2,6,5,5,0,0,0,0),
(13,3,1,200,200,0,0,0,0),
(14,3,2,20000,20000,0,0,0,0),
(15,3,3,8000,8000,0,0,0,0),
(16,3,4,8,8,0,0,0,0),
(17,3,5,25,25,0,0,0,0),
(18,3,6,5,5,0,0,0,0)
ON DUPLICATE KEY UPDATE total_quantity=VALUES(total_quantity), available_quantity=VALUES(available_quantity), assigned_quantity=VALUES(assigned_quantity), committed_quantity=VALUES(committed_quantity), in_transfer_quantity=VALUES(in_transfer_quantity), repair_quantity=VALUES(repair_quantity);

INSERT INTO inventory_ledger (id, base_id, equipment_type_id, transaction_type, transaction_id, quantity_in, quantity_out, balance_after, created_by, created_at) VALUES
(1,1,1,'OPENING_BALANCE',0,500,0,500,1,NOW()),
(2,1,2,'OPENING_BALANCE',0,50000,0,50000,1,NOW()),
(3,1,3,'OPENING_BALANCE',0,20000,0,20000,1,NOW()),
(4,1,4,'OPENING_BALANCE',0,20,0,20,1,NOW()),
(5,1,5,'OPENING_BALANCE',0,50,0,50,1,NOW()),
(6,1,6,'OPENING_BALANCE',0,10,0,10,1,NOW()),
(7,2,1,'OPENING_BALANCE',0,300,0,300,1,NOW()),
(8,2,2,'OPENING_BALANCE',0,30000,0,30000,1,NOW()),
(9,2,3,'OPENING_BALANCE',0,10000,0,10000,1,NOW()),
(10,2,4,'OPENING_BALANCE',0,10,0,10,1,NOW()),
(11,2,5,'OPENING_BALANCE',0,30,0,30,1,NOW()),
(12,2,6,'OPENING_BALANCE',0,5,0,5,1,NOW()),
(13,3,1,'OPENING_BALANCE',0,200,0,200,1,NOW()),
(14,3,2,'OPENING_BALANCE',0,20000,0,20000,1,NOW()),
(15,3,3,'OPENING_BALANCE',0,8000,0,8000,1,NOW()),
(16,3,4,'OPENING_BALANCE',0,8,0,8,1,NOW()),
(17,3,5,'OPENING_BALANCE',0,25,0,25,1,NOW()),
(18,3,6,'OPENING_BALANCE',0,5,0,5,1,NOW())
ON DUPLICATE KEY UPDATE balance_after=VALUES(balance_after);
