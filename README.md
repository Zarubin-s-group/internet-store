# Internet-store

The project contains the following subprojects:

1. Gateway-service
2. Discovery-service (combined with Config-service)
3. Auth-service
4. Order-service
5. Payment-service
6. Inventory-service
7. Delivery-service

## Environment

To run PostgreSQL with Kafka you have to execute the command in project root Report-service:
```
$sudo docker-compose up -d
```

Also, you have convenient [UI for Apache Kafka](https://github.com/provectus/kafka-ui) at URL

http://localhost:9999/

Now you can run application services from your IDE in this order
- Discovery
- Auth-service
- Order-service
- Payment-service
- Inventory-service
- Delivery-service
- Gateway-service

At Gateway, you can find joined Swagger UI
http://localhost:9090/swagger-ui.html

## Basic interactions

You can use those curl commands, or you can do all that with Swagger UI

### Authentication

To create user use this request to auth service
```bash
curl -X 'POST' \
  'http://localhost:9090/auth-service/users/signup' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "some_user",
  "password": "some_pass"
}'
```

After that you can get a tokens
```bash
curl -X 'POST' \
  'http://localhost:9090/auth-service/auth/login' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "some_user",
  "password": "some_pass"
}'
```

Now you can use access token to authenticate requests to other services

### Payment service

To create an e-wallet use POST request:

```bash
curl --location --request POST 'http://localhost:9090/payment-service/wallet' \
--header 'Authorization: Bearer <put token here>'
```

To replenish the user's balance you can send PATCH request:

```bash
curl --location --request PATCH 'http://localhost:9090/payment-service/wallet/balance/replenish' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "amount": 100
}'
```

### Inventory service

Only administrator can add, update and delete products and categories. Use name: admin, password: admin to get a token.

To create a category and a product you can send POST requests:

```bash
curl --location --request POST 'http://localhost:9090/inventory-service/category/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "title": "new category"
}'
```

```bash
curl --location --request POST 'http://localhost:9090/inventory-service/product/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <put token here>' \
--data '{
    "title": "new product",
    "description": "some description",
    "unitPrice": 6.3,
    "count": 217,
    "categoryTitle": "new category"
}'
```

### Order service

To create an order you can send POST request:

```bash
curl -X 'POST' \
  'http://localhost:9090/order-service/order' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'Bearer <put token here>'
  -d '{
  "description": "string",
  "destinationAddress": "string",
  "products": [
    {
      "productId": 1,
      "count": 1
    }
  ],
  "cost": 10
}'
```

### Delivery service

The delivery service has a schematic implementation. In 15% of cases, delivery fails. This allows us to evaluate
the possibility of rollback at any stage of order fulfillment according to the SAGA pattern.

## Running all services with docker-compose

To run all services with docker-compose use this command
```bash
 docker-compose -f docker-compose.yml -f docker-compose.services.yml up -d
```
