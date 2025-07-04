# MPR (Merchant Payment Reconciliation) System

A Kotlin Spring Boot application for managing merchant transactions and settlements with Redis-based idempotency and automated batch processing.

## Features

### Core Features
- **Merchant Management**: Create and manage merchants
- **Transaction Processing**: Initiate transactions with idempotency support
- **Settlement Batching**: Automated settlement processing with batch creation
- **Redis Integration**: Idempotency and caching support
- **RESTful APIs**: Fully documented with Swagger/OpenAPI
- **Security**: Basic authentication for all endpoints
- **Docker Support**: Complete containerized setup

### Technical Features
- **Kotlin**: Modern JVM language with Spring Boot
- **PostgreSQL**: Relational database with Flyway migrations
- **Redis**: Caching and idempotency
- **JPA/Hibernate**: Lightweight ORM with custom queries
- **Scheduling**: Automated settlement processing (9 AM and 6 PM daily)
- **Validation**: Input validation with proper error handling
- **Unit Tests**: Comprehensive test coverage

## Architecture

### Entities
- **Merchant**: Business information and status
- **Transaction**: Payment transactions with status tracking
- **SettlementBatch**: Batched settlements for merchants

### Services
- **MerchantService**: Merchant CRUD operations
- **TransactionService**: Transaction processing with Redis idempotency
- **SettlementService**: Automated settlement processing

### Key Features
- **Idempotency**: Redis-based transaction deduplication
- **Fee Calculation**: 1.5% fee with $200 maximum cap
- **Reference Generation**: Unique, readable references (max 36 chars)
- **Batch Processing**: 5 transactions per settlement batch
- **Error Handling**: Generic API response format

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)

### Running with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd mpr
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - PostgreSQL: localhost:5432
   - Redis: localhost:6379

### Local Development

1. **Start dependencies**
   ```bash
   docker-compose up -d postgres redis
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

## API Documentation

### Authentication
All endpoints require basic authentication:
- Username: `admin`
- Password: `admin123`

### Merchant APIs

#### Create Merchant
```http
POST /api/v1/merchants
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
  "businessName": "Test Business",
  "email": "test@example.com",
  "settlementAccount": "1234567890"
}
```

#### Get Merchant
```http
GET /api/v1/merchants/{id}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Transaction APIs

#### Initiate Transaction
```http
POST /api/v1/transactions/initiate
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
  "amount": 100.00,
  "currency": "NGN",
  "merchantId": 1,
  "merchantRef": "MERCH123456789"
}
```

#### List Transactions
```http
GET /api/v1/transactions?merchantId=1&status=SUCCESS&page=0&size=20
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Get Transaction
```http
GET /api/v1/transactions/{internalRef}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Settlement APIs

#### Process Settlements
```http
POST /api/v1/settlements/process/{merchantId}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Get Settlement Summary
```http
GET /api/v1/settlements/summary/{merchantId}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Get Settlement Batches
```http
GET /api/v1/settlements/batches/{merchantId}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/mpr_db
spring.datasource.username=postgres
spring.datasource.password=password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Security
spring.security.user.name=admin
spring.security.user.password=admin123

# Scheduling (9 AM and 6 PM daily)
# Configured in SettlementService
```

### Environment Variables
The application supports extensive environment variable configuration. Copy `env.example` to `.env` and customize as needed:

```bash
cp env.example .env
```

#### Key Environment Variables

**Database Configuration:**
- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: mpr_db)
- `DB_USERNAME` - Database username (default: postgres)
- `DB_PASSWORD` - Database password (default: password)

**Redis Configuration:**
- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)
- `REDIS_TIMEOUT` - Redis connection timeout (default: 2000ms)

**Server Configuration:**
- `SERVER_PORT` - Application port (default: 8080)
- `SERVER_CONTEXT_PATH` - Context path (default: /)

**Security Configuration:**
- `SECURITY_USER_NAME` - Basic auth username (default: admin)
- `SECURITY_USER_PASSWORD` - Basic auth password (default: admin123)
- `CSRF_ENABLED` - Enable/disable CSRF protection (default: false)

**Logging Configuration:**
- `LOG_LEVEL_MPR` - Application log level (default: DEBUG)
- `LOG_LEVEL_SECURITY` - Security log level (default: DEBUG)
- `LOG_LEVEL_HIBERNATE_SQL` - SQL log level (default: DEBUG)

**Scheduling Configuration:**
- `SCHEDULE_ENABLED` - Enable/disable scheduling (default: true)
- `SCHEDULE_INTERVAL` - Cron expression for settlement processing (default: 0 15 19 ? * WED)
- `SCHEDULE_TIMEZONE` - Timezone for scheduling (default: UTC)

#### Using Environment Variables

**With Docker Compose:**
```bash
# Set environment variables
export DB_PASSWORD=my_secure_password
export SECURITY_USER_PASSWORD=my_admin_password

# Start with custom configuration
docker-compose up -d
```

**With .env file:**
```bash
# Create .env file from example
cp env.example .env

# Edit .env file with your values
DB_PASSWORD=my_secure_password
SECURITY_USER_PASSWORD=my_admin_password

# Start application
docker-compose up -d
```

**Direct environment variable override:**
```bash
DB_PASSWORD=my_secure_password docker-compose up -d
```

## Business Logic

### Transaction Processing
1. **Idempotency Check**: Redis key prevents duplicate processing
2. **Fee Calculation**: 1.5% of amount, max $200
3. **Customer Debit**: Simulated payment processing
4. **Merchant Credit**: Simulated settlement
5. **Status Update**: Transaction marked as SUCCESS/FAILED

### Settlement Processing
1. **Scheduled Job**: Runs at 9 AM and 6 PM daily
2. **Batch Creation**: Groups 5 successful transactions per batch
3. **Reference Generation**: Unique batch references
4. **Status Tracking**: Transactions linked to settlement batches

### Reference Generation
- **InternalRef**: `TXN{timestamp}{uuid}` (e.g., TXN2024120112000012345678)
- **BatchRef**: `BATCH{timestamp}{uuid}` (e.g., BATCH202412011200001234)
- **MerchantRef**: `MERCH{timestamp}{uuid}` (e.g., MERCH202412011200001234)

## Testing

### Run Tests
```bash
./gradlew test
```

### Test Coverage
- Unit tests for all services
- Mock-based testing with Mockito
- JUnit 5 test framework

## Database Schema

### Tables
- `merchants`: Merchant information and status
- `transactions`: Transaction records with relationships
- `settlement_batches`: Settlement batch information

### Indexes
- Performance indexes on frequently queried columns
- Unique constraints on business keys
- Foreign key relationships

## Monitoring and Logging

### Logging Levels
- `DEBUG`: Application logic
- `INFO`: Business operations
- `WARN`: Potential issues
- `ERROR`: Error conditions

### Health Checks
- Database connectivity
- Redis connectivity
- Application status

## Security

### Authentication
- Basic authentication for all endpoints
- Configurable credentials
- Swagger UI access without authentication

### Data Protection
- Input validation
- SQL injection prevention
- XSS protection

## Troubleshooting

### Common Issues

1. **Database Connection**
   ```bash
   docker-compose logs postgres
   ```

2. **Redis Connection**
   ```bash
   docker-compose logs redis
   ```

3. **Application Startup**
   ```bash
   docker-compose logs app
   ```

### Health Check
```bash
curl -u admin:admin123 http://localhost:8080/actuator/health
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 