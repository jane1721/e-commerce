# API Specification

## 1️⃣ 잔액 충전 / 조회 API

| Method | Endpoint                      | Description | Request Body                                        | Response Body                                                                                   |
|--------|-------------------------------|-------------|-----------------------------------------------------|-------------------------------------------------------------------------------------------------|
| POST   | `/api/users/charge`           | 잔액 충전       | {<br/> "userId": "123",<br/> "amount": 10000<br/> } | {<br/> "status": "success",<br/> "message": "충전 성공하였습니다.",<br/> "currentBalance": 20000<br/> }` |
| GET    | `/api/users/{userId}/balance` | 잔액 조회       | -                                                   | {<br/> "userId": "123",<br/> "currentBalance": 20000<br/> }                                     |

---

## 2️⃣ 상품 조회 API

| Method | Endpoint     | Description | Request Body | Response Body                                                                                                                                                                                         |
|--------|--------------|-------------|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | `/api/items` | 상품 정보 조회    | -            | [<br/> {<br/> "itemId": "1",<br/> "name": "Item A",<br/> "price": 5000,<br/> "stock": 10<br/> },<br/> {<br/> "itemId": "2",<br/> "name": "Item B",<br/> "price": 10000,<br/> "stock": 5<br/> }<br/> ] |

---

## 3️⃣ 선착순 쿠폰 발급 및 조회 API

| Method | Endpoint                      | Description | Request Body                | Response Body                                                                                                                                                                                                                                                                         |
|--------|-------------------------------|-------------|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/coupons/claim`          | 쿠폰 발급       | {<br/> "userId": "1"<br/> } | {<br/> "status": "success",<br/> "message": "쿠폰 발급 성공하였습니다.",<br/> "code": "DISCOUNT-10"<br/> }                                                                                                                                                                                       |
| GET    | `/api/coupons/users/{userId}` | 보유 쿠폰 조회    | -                           | [<br/> {<br/> "code": "DISCOUNT-10",<br/> "discount_percent": 10,<br/> "expiryDate": "2025-03-31T23:59:59",<br/> "isUsed": false<br/> },<br/> {<br/> "code": "DISCOUNT-20",<br/> "discount_percent": 20,<br/> "expiryDate": "2025-01-31T23:59:59",<br/> "isUsed": false<br/> }<br/> ] |

---

## 4️⃣ 주문 / 결제 API

### 4.1 주문 API
| Method | Endpoint           | Description    | Request Body                                                                                                                                                                                     | Response Body                                                                                                                                                                                                                                                     |
|--------|--------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/orders`      | 주문 생성          | {<br/> "userId": "1",<br/> "orderItems": [<br/> {<br/> "itemId": "1",<br/> "quantity": 2<br/> },<br/> {<br/> "itemId": "2",<br/> "quantity": 5<br/> }<br/> ],<br/> "code": "DISCOUNT-10"<br/> }` | {<br/> "status": "success",<br/> "message": "주문 성공하였습니다.",<br/> "order": {<br/> "id": "123",<br/> "status": "PENDING",<br/> "totalAmount": 15000,<br/> "createdAt": "2025-01-01T12:00:00"<br/> }<br/> }                                                           |
| GET    | `/api/orders/{id}` | 특정 주문 조회       | -                                                                                                                                                                                                | {<br/> "orderId": "123",<br/> "status": "CONFIRMED",<br/> "orderItems": [<br/>{<br/>"itemId": "1",<br/> "quantity": 2<br/>},<br/> {<br/> "itemId": "2",<br/> "quantity": 5<br/>}<br/>],<br/> "totalAmount": 15000,<br/> "createdAt": "2025-01-01T12:00:00"<br/> } |
| PATCH  | `/api/orders/{id}` | 주문 상태 업데이트(확정) | {<br/> "status": "CONFIRMED"<br/> }                                                                                                                                                              | {<br/> "status": "success",<br/> "message": "주문이 확정되었습니다.",<br/> "order": {<br/> "id": 123,<br/> "status": "CONFIRMED",<br/> "updatedAt": "2025-01-02T12:00:00"<br/> }<br/> }                                                                                     |
| PATCH  | `/api/orders/{id}` | 주문 상태 업데이트(취소) | {<br/> "status": "CANCELLED"<br/> }                                                                                                                                                              | {<br/> "status": "success",<br/> "message": "주문이 취소되었습니다.",<br/> "order": {<br/> "id": 234,<br/> "status": "CANCELLED",<br/> "updatedAt": "2025-01-02T12:00:00"<br/> }<br/> }                                                                                     |

### 4.2 결제 API
| Method | Endpoint             | Description | Request Body                                                 | Response Body                                                                                                                                                                     |
|--------|----------------------|-------------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/api/payments`      | 결제 요청       | {<br/> "orderId": "123",<br/> "paymentMethod": "CARD"<br/> } | {<br/> "status": "success",<br/> "message": "결제 성공하였습니다.",<br/> "payment": {<br/> "id" : "456",<br/> "status": "INITIATED",<br/> "createdAt": "2025-01-01T12:05:00"<br/> }<br/> } |
| GET    | `/api/payments/{id}` | 결제 상태 조회    | -                                                            | {<br/> "paymentId": 456,<br/> "status": "SUCCESS",<br/> "amount": 15000,<br/> "paymentMethod": "CARD",<br/> "updatedAt": "2025-01-01T12:10:00"<br/> }                             |

---

## 5️⃣ 상위 상품 조회 API

| Method | Endpoint         | Description | Request Body | Response Body                                                                                                                                                                                                                                                                                                                                                                                                  |
|--------|------------------|-------------|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GET    | `/api/items/top` | 상위 상품 조회    | -            | [<br/> {<br/> "id": "3",<br/> "name": "Product A",<br/> "soldCount": 100<br/> },<br/> {<br/> "id": "5",<br/> "name": "Product B",<br/> "soldCount": 80<br/> },<br/> {<br/> "id": "6",<br/> "name": "Product C",<br/> "soldCount": 70<br/> },<br/> {<br/> "id": "4",<br/> "name": "Product D",<br/> "soldCount": 50<br/> },<br/> {<br/> "id": "8",<br/> "name": "Product E",<br/> "soldCount": 20<br/> }<br/> ] |

---

## 6️⃣ 장바구니 기능

| Method | Endpoint                    | Description | Request Body                                                        | Response Body                                                                                            |
|--------|-----------------------------|-------------|---------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| POST   | `/api/carts`                | 장바구니 추가     | {<br/> "userId": "1",<br/> "itemId": "3",<br/> "quantity": 1<br/> } | {<br/> "status": "success",<br/> "message": "장바구니 추가 성공하였습니다."<br/> }                                    |
| DELETE | `/api/carts`                | 장바구니 삭제     | {<br/> "userId": "1",<br/> "itemId": "3" }                          | {<br/> "status": "success",<br/> "message": "장바구니 상품 삭제 성공하였습니다."<br/> }                                 |
| GET    | `/api/carts/users/{userId}` | 장바구니 조회     | -                                                                   | [<br/> {<br/> "itemId": "1",<br/> "name": "Item A",<br/> "quantity": 2,<br/> "price": 5000<br/> }<br/> ] |
