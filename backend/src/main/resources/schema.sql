CREATE TABLE IF NOT EXISTS measurement_ledger (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_name TEXT NOT NULL,
    spec_model TEXT,
    device_no TEXT NOT NULL,
    inspection_unit TEXT,
    inspection_date TEXT,
    test_result TEXT,
    cert_no TEXT,
    cycle TEXT,
    next_inspection_date TEXT,
    remark TEXT,
    department TEXT,
    judgment_standard TEXT,
    create_time TEXT DEFAULT (datetime('now','localtime')),
    update_time TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS device_standard (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    standard TEXT NOT NULL,
    create_time TEXT DEFAULT (datetime('now','localtime')),
    update_time TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS equipment_ledger (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    equipment_name TEXT NOT NULL,
    spec_model TEXT,
    serial_number TEXT,
    device_no TEXT NOT NULL,
    manufacturer TEXT,
    enable_date TEXT,
    expected_scrap_date TEXT,
    installation_location TEXT,
    keeper TEXT,
    maintenance_status TEXT,
    use_status TEXT,
    department TEXT,
    create_time TEXT DEFAULT (datetime('now','localtime')),
    update_time TEXT DEFAULT (datetime('now','localtime'))
);