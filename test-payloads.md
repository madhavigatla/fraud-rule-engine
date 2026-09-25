### Fraud Rule Engine Test Payloads

Use these payloads to test the different rules in the system via the `TransactionController`.
Endpoint: `POST http://localhost:8080/api/transactions/post-transaction`

#### 1. High Value Transaction Alert
**Rule**: Amount > R10,000.00
```json
{
  "transactionId": "TX-HV-001",
  "customerId": "CUST-101",
  "amount": 15500.50,
  "currency": "ZAR",
  "category": "Electronics",
  "merchantName": "Apple Store",
  "timestamp": "2023-10-27T10:00:00"
}
```

#### 2. Suspicious Category Alert (Gambling)
**Rule**: Category is "Gambling" or "Crypto".
```json
{
  "transactionId": "TX-SC-001",
  "customerId": "CUST-102",
  "amount": 500.00,
  "currency": "ZAR",
  "category": "Gambling",
  "merchantName": "BetWay Online",
  "timestamp": "2023-10-27T14:30:00"
}
```

#### 3. Off-Hours Transaction Alert
**Rule**: Time between 00:00 and 04:00.
```json
{
  "transactionId": "TX-OH-001",
  "customerId": "CUST-103",
  "amount": 250.00,
  "currency": "ZAR",
  "category": "Retail",
  "merchantName": "LateNight Gas",
  "timestamp": "2023-10-27T02:15:00"
}
```

#### 4. High Velocity Alert (Multiple Transactions)
**Rule**: > 3 transactions in 5 minutes for the same customer.
Send these 4 payloads in quick succession (within 5 minutes):

**Payload 1:**
```json
{
  "transactionId": "TX-VEL-001",
  "customerId": "CUST-VEL-99",
  "amount": 10.00,
  "currency": "ZAR",
  "category": "FastFood",
  "merchantName": "Vendor A",
  "timestamp": "2023-10-27T15:00:00"
}
```

**Payload 2:**
```json
{
  "transactionId": "TX-VEL-002",
  "customerId": "CUST-VEL-99",
  "amount": 10.00,
  "currency": "ZAR",
  "category": "FastFood",
  "merchantName": "Vendor B",
  "timestamp": "2023-10-27T15:01:00"
}
```

**Payload 3:**
```json
{
  "transactionId": "TX-VEL-003",
  "customerId": "CUST-VEL-99",
  "amount": 10.00,
  "currency": "ZAR",
  "category": "FastFood",
  "merchantName": "Vendor C",
  "timestamp": "2023-10-27T15:02:00"
}
```

**Payload 4 (Triggers Alert):**
```json
{
  "transactionId": "TX-VEL-004",
  "customerId": "CUST-VEL-99",
  "amount": 10.00,
  "currency": "ZAR",
  "category": "FastFood",
  "merchantName": "Vendor D",
  "timestamp": "2023-10-27T15:03:00"
}
```

#### 5. Normal Transaction (No Alert)
```json
{
  "transactionId": "TX-NORM-001",
  "customerId": "CUST-200",
  "amount": 50.00,
  "currency": "ZAR",
  "category": "Groceries",
  "merchantName": "Checkers",
  "timestamp": "2023-10-27T12:00:00"
}
```

---
### How to test with CURL:
```bash
curl -X POST http://localhost:8080/api/transactions/post-transaction \
     -H "Content-Type: application/json" \
     -d '{
           "transactionId": "TX-HV-001",
           "customerId": "CUST-101",
           "amount": 15500.50,
           "currency": "ZAR",
           "category": "Electronics",
           "merchantName": "Apple Store",
           "timestamp": "2023-10-27T10:00:00"
         }'
```
