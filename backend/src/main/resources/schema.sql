CREATE TABLE IF NOT EXISTS measurement_ledger (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_name TEXT NOT NULL,
    spec_model TEXT,
    device_no TEXT NOT NULL,
    inspection_unit TEXT,
    inspection_date TEXT,
    actual_implementation TEXT,
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