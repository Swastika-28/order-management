# Order Management System

A robust **Spring Boot** application designed to process customer orders efficiently and safely using stored procedures and database-level locking for atomic transaction management.

---

## 🚀 Features

- **Atomic Order Processing**: Utilizes MySQL Stored Procedures (`sp_process_order`) with explicit row locking (`FOR UPDATE`) to prevent race conditions during concurrent orders.
- **Stock & Balance Verification**: Automatically checks product inventory and customer wallet balance before executing transactions.
- **RESTful API**: Standardized endpoints for order placement, account recharges, and user management.
- **Global Error Handling**: Centralized exception handling via Spring `@ControllerAdvice`.
- **Email Notifications**: Integrated email services for order confirmations and system alerts.
- **Token Management**: Service implementation for token-based authentication/authorization handling.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build Tool**: Apache Maven
- **Database**: MySQL 8.x
- **Persistence Framework**: Spring Data JPA / Hibernate

---

## 📁 Project Structure

```text
order_management/
├── src/
│   ├── main/
│   │   ├── java/com/project/order_management/
│   │   │   ├── controller/      # REST Endpoints (OrderController)
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Custom Exception Handling
│   │   │   ├── model/           # JPA Entities (Customer, Token, etc.)
│   │   │   ├── repo/            # Spring Data Repositories & Custom Procedure Callers
│   │   │   ├── service/         # Business Logic & External Services (Email, Token)
│   │   │   └── util/            # Constants & Helpers
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit & Integration Tests
├── pom.xml                      # Maven Dependencies
└── README.md
```

---

## 🛢️ Database Setup

Run the following SQL scripts in your MySQL instance to create the necessary tables and stored procedure:

### 1. Stored Procedure: `sp_process_order`

```sql
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_process_order`(
    IN p_customer_id BIGINT,
    IN p_product_id BIGINT,
    IN p_quantity INT,
    OUT p_order_id BIGINT,
    OUT p_total_amount DECIMAL(10,2),
    OUT p_status_code VARCHAR(30),
    OUT p_status_message VARCHAR(255)
)
BEGIN
    DECLARE v_unit_price DECIMAL(10,2);
    DECLARE v_stock INT;
    DECLARE v_balance DECIMAL(10,2);

    SET p_order_id = NULL;
    SET p_total_amount = 0.00;

    -- 1. Check Product availability and lock row
    SELECT price, stock_quantity INTO v_unit_price, v_stock 
    FROM products WHERE id = p_product_id FOR UPDATE;

    IF v_stock IS NULL THEN
        SET p_status_code = 'PRODUCT_NOT_FOUND';
        SET p_status_message = 'Requested product does not exist.';
    ELSEIF v_stock < p_quantity THEN
        SET p_status_code = 'INSUFFICIENT_STOCK';
        SET p_status_message = 'Insufficient product stock available.';
    ELSE
        -- 2. Check Customer Wallet Balance and lock row
        SELECT wallet_balance INTO v_balance 
        FROM customers WHERE id = p_customer_id FOR UPDATE;

        IF v_balance IS NULL THEN
            SET p_status_code = 'CUSTOMER_NOT_FOUND';
            SET p_status_message = 'Customer account not found.';
        ELSEIF v_balance < (v_unit_price * p_quantity) THEN
            SET p_status_code = 'INSUFFICIENT_FUNDS';
            SET p_status_message = 'Insufficient customer wallet balance.';
        ELSE
            -- 3. Execute Atomic Transaction
            SET p_total_amount = v_unit_price * p_quantity;

            UPDATE products SET stock_quantity = stock_quantity - p_quantity WHERE id = p_product_id;
            UPDATE customers SET wallet_balance = wallet_balance - p_total_amount WHERE id = p_customer_id;

            INSERT INTO orders(customer_id, product_id, quantity, total_amount, created_at) 
            VALUES(p_customer_id, p_product_id, p_quantity, p_total_amount, NOW());

            SET p_order_id = LAST_INSERT_ID();
            SET p_status_code = 'SUCCESS';
            SET p_status_message = 'Order processed successfully.';
        END IF;
    END IF;
END$$
DELIMITER ;
```

---

## ⚙️ Configuration

Update `src/main/resources/application.properties` with your database credentials and SMTP email settings:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/order_management?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🏃 Getting Started

### Prerequisites
- **JDK 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+**

### Building and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/order-management.git
   cd order-management
   ```

2. **Build the application:**
   ```bash
   ./mvnw clean package
   ```

3. **Run the Spring Boot app:**
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on `http://localhost:8080`.
