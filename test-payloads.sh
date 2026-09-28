#!/bin/bash

URL="http://localhost:8080/api/transactions/post-transaction"
USERNAME="admin"
PASSWORD="password"

echo "======================================"
echo "1. HIGH VALUE TRANSACTION"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
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

echo -e "\n\n======================================"
echo "2. SUSPICIOUS CATEGORY"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-SC-001",
"customerId": "CUST-102",
"amount": 500.00,
"currency": "ZAR",
"category": "Gambling",
"merchantName": "BetWay Online",
"timestamp": "2023-10-27T14:30:00"
}'

echo -e "\n\n======================================"
echo "3. OFF-HOURS TRANSACTION"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-OH-001",
"customerId": "CUST-103",
"amount": 250.00,
"currency": "ZAR",
"category": "Retail",
"merchantName": "LateNight Gas",
"timestamp": "2023-10-27T02:15:00"
}'

echo -e "\n\n======================================"
echo "4. VELOCITY TRANSACTION 1"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-VEL-001",
"customerId": "CUST-VEL-99",
"amount": 10.00,
"currency": "ZAR",
"category": "FastFood",
"merchantName": "Vendor A",
"timestamp": "2023-10-27T15:00:00"
}'

echo -e "\n\n======================================"
echo "5. VELOCITY TRANSACTION 2"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-VEL-002",
"customerId": "CUST-VEL-99",
"amount": 10.00,
"currency": "ZAR",
"category": "FastFood",
"merchantName": "Vendor B",
"timestamp": "2023-10-27T15:01:00"
}'

echo -e "\n\n======================================"
echo "6. VELOCITY TRANSACTION 3"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-VEL-003",
"customerId": "CUST-VEL-99",
"amount": 10.00,
"currency": "ZAR",
"category": "FastFood",
"merchantName": "Vendor C",
"timestamp": "2023-10-27T15:02:00"
}'

echo -e "\n\n======================================"
echo "7. VELOCITY TRANSACTION 4 - SHOULD TRIGGER"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-VEL-004",
"customerId": "CUST-VEL-99",
"amount": 10.00,
"currency": "ZAR",
"category": "FastFood",
"merchantName": "Vendor D",
"timestamp": "2023-10-27T15:03:00"
}'

echo -e "\n\n======================================"
echo "8. NORMAL TRANSACTION"
echo "======================================"

curl -i -u "$USERNAME:$PASSWORD" -X POST "$URL" \
-H "Content-Type: application/json" \
-d '{
"transactionId": "TX-NORM-001",
"customerId": "CUST-200",
"amount": 50.00,
"currency": "ZAR",
"category": "Groceries",
"merchantName": "Checkers",
"timestamp": "2023-10-27T12:00:00"
}'

echo -e "\n\n======================================"
echo "ALL TRANSACTION TESTS COMPLETED"
echo "======================================"

echo ""
echo "Checking saved fraud alerts..."
echo ""

curl -i -u "$USERNAME:$PASSWORD" \
"http://localhost:8080/api/fraud-alerts"

echo -e "\n\n======================================"
echo "TESTING COMPLETED"
echo "======================================"