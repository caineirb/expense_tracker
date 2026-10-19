CREATE TABLE IF NOT EXISTS "transactions" (
    "id" INT PRIMARY KEY,
    "name" TEXT NOT NULL,
    "description" TEXT DEFAULT NULL,
    "date" TEXT DEFAULT CURRENT_TIMESTAMP,
    "location" TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "items" (
    "id" BLOB PRIMARY KEY,
    "transaction_id" INT,
    "name" TEXT NOT NULL,
    "price" REAL DEFAULT NULL,
    "note" TEXT DEFAULT NULL
) WITHOUT ROWID;

INSERT INTO "transactions" ("name", "description", "location")
WITH initial_data(name, description, location) AS (
    VALUES 
        ('trans1', 'This is the first item.', 'Prince Hypermart Bad as'),
        ('trans2', 'This is the second item.', 'Prince Hypermart Branch B'),
        ('trans3', 'This is the third item.', 'Prince Hypermart Branch C')
)
SELECT name, description, location 
FROM initial_data
WHERE (SELECT COUNT(*) FROM "transactions") = 0;
