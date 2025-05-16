CREATE TABLE Customers (
                           customer_id SERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           id_type VARCHAR(100) NOT NULL,
                           id_number VARCHAR(50) NOT NULL,
                           registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
                           do_not_serve BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Roles (
                       role_id SERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL,
                       description TEXT,
                       permissions_level INTEGER NOT NULL,
                       max_buy INTEGER
);

CREATE TABLE Employees (
                           employee_id SERIAL PRIMARY KEY,
                           login VARCHAR(50) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           role_id INTEGER NOT NULL REFERENCES Roles(role_id),
                           hire_date DATE NOT NULL,
                           address VARCHAR(255),
                           phone_number VARCHAR(20),
                           email VARCHAR(100),
                           status VARCHAR(50) NOT NULL DEFAULT 'active'
);

CREATE TABLE Categories (
                            category_id SERIAL PRIMARY KEY,
                            category_name VARCHAR(100) NOT NULL,
                            description TEXT
);

CREATE TABLE Items (
                       item_id SERIAL PRIMARY KEY,
                       category_id INTEGER NOT NULL REFERENCES Categories(category_id),
                       description TEXT NOT NULL,
                       serial_number VARCHAR(100),
                       name VARCHAR(100) NOT NULL,
                       brand VARCHAR(100),
                       model VARCHAR(100),
                       condition VARCHAR(50),
                       bought_for DECIMAL(10, 2),
                       asking_price DECIMAL(10, 2),
                       status VARCHAR(50) NOT NULL DEFAULT 'in_inventory'
                           CHECK (status IN ('in_inventory', 'pawned', 'sold', 'redeemed', 'forfeited')),
                       reported_stolen BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by INTEGER REFERENCES Employees(employee_id),
                       updated_by INTEGER REFERENCES Employees(employee_id)
);

CREATE TABLE Transactions (
                              transaction_id SERIAL PRIMARY KEY,
                              customer_id INTEGER REFERENCES Customers(customer_id),
                              employee_id INTEGER NOT NULL REFERENCES Employees(employee_id),
                              transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
                              transaction_type VARCHAR(50) NOT NULL
                                  CHECK (transaction_type IN ('purchase', 'sale', 'pawn', 'redemption', 'forfeiture')),
                              total_amount DECIMAL(10, 2) NOT NULL,
                              pawn_duration_days INTEGER,
                              interest_rate DECIMAL(5, 2),
                              redemption_price DECIMAL(10, 2),
                              expiry_date DATE,
                              related_transaction_id INTEGER REFERENCES Transactions(transaction_id), --For linking redemptions to pawns
                              notes TEXT,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Transaction_Items (
                                   transaction_item_id SERIAL PRIMARY KEY,
                                   transaction_id INTEGER NOT NULL REFERENCES Transactions(transaction_id),
                                   item_id INTEGER NOT NULL REFERENCES Items(item_id),
                                   price DECIMAL(10, 2) NOT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Item_Images (
                             image_id SERIAL PRIMARY KEY,
                             item_id INTEGER NOT NULL REFERENCES Items(item_id),
                             image_path VARCHAR(255) NOT NULL,
                             is_primary BOOLEAN DEFAULT FALSE,
                             upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

