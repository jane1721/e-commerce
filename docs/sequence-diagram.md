# Sequence Diagram

아래는 주문 생성과 결제 처리의 상세 로직을 포함한 시퀀스 다이어그램이다.  
서비스 내부 로직(재고 확인, 잔액 비교 등)을 나타내도록 작성되었으며, 결제 실패 시 주문 취소 및 재고 반환 로직을 포함하였다.

```mermaid
sequenceDiagram
    autonumber

    participant User as 사용자
    participant API as API Gateway
    participant OrderService as 주문 서비스
    participant PaymentService as 결제 서비스
    participant ItemService as 상품 서비스
    participant UserService as 유저 서비스
    participant CouponService as 쿠폰 서비스
    participant DataPlatform as 외부 데이터 플랫폼

    %% 주문 생성 상세 로직
    User ->> API: POST /api/orders
    API ->> OrderService: 주문 생성 요청

    %% 상품 재고 확인
    OrderService ->> ItemService: 상품 재고 확인
    ItemService -->> OrderService: 재고 상태 (충분함)

    %% 재고 차감
    OrderService ->> ItemService: 상품 재고 차감
    ItemService -->> OrderService: 재고 차감 완료

    %% 쿠폰 적용
    OrderService ->> CouponService: 유효한 쿠폰 확인
    CouponService -->> OrderService: 쿠폰 정보 (할인 금액)
    
    %% 쿠폰 사용 처리
    OrderService ->> CouponService: 쿠폰 사용 처리 (isUsed = true)
    CouponService -->> OrderService: 쿠폰 사용 완료

    %% 주문 금액 계산 (쿠폰 적용 후)
    OrderService ->> OrderService: 주문 금액 계산 (할인 적용)

    %% 주문 데이터 저장
    OrderService ->> OrderService: 주문 데이터 생성 및 저장
    OrderService -->> API: 주문 생성 성공 메시지
    API -->> User: 주문 생성 성공 메시지

    %% 결제 처리 상세 로직
    User ->> API: POST /api/payments
    API ->> PaymentService: 결제 요청

    %% 사용자 잔액 확인
    PaymentService ->> UserService: 사용자 잔액 확인
    UserService -->> PaymentService: 사용자 잔액 정보

    %% 잔액 비교
    PaymentService ->> PaymentService: 잔액 >= 최종 금액 확인
    alt 잔액 부족
        %% 결제 실패 시 처리
        PaymentService -->> API: 결제 실패 (잔액 부족)
        API -->> User: 결제 실패 메시지
        
		    %% 주문 취소 상세 로직
		    User ->> API: PATCH /api/payments
		    API ->> OrderService: 주문 상태 업데이트 요청 (주문 취소)
		    
		    %% 재고 원복
		    OrderService ->> ItemService: 상품 재고 원상복구
		    ItemService -->> OrderService: 재고 복구 완료
		    
		    %% 쿠폰 미사용 처리
		    OrderService ->> CouponService: 쿠폰 미사용 처리 (isUsed = false)
		    CouponService -->> OrderService: 쿠폰 복구 완료
		    
		    %% 주문 상태 업데이트
		    OrderService -->> OrderService: 주문 취소 완료 (status = cancel)
		    
		    %% 최종 주문 취소 업데이트 메시지
		    OrderService -->> API: 주문 취소 메시지
		    API -->> User: 주문 취소 메시지
    
    else 잔액 충분
        %% 잔액 차감
        PaymentService ->> UserService: 사용자 잔액 차감
        UserService -->> PaymentService: 잔액 차감 완료

        %% 결제 데이터 저장
        PaymentService ->> PaymentService: 결제 데이터 생성 및 저장

        %% 최종 결제 완료 메시지
        PaymentService -->> API: 결제 성공 메시지
        API -->> User: 결제 성공 메시지

        %% 외부 데이터 플랫폼으로 주문 정보 전송
        PaymentService ->> DataPlatform: 주문 정보 전송
        DataPlatform -->> PaymentService: 주문 정보 전송 완료
    end
```

### **Description**

1. **주문 생성 로직**
    - API 에서 주문 생성 요청을 받으면:
        - **상품 서비스**에서 재고를 확인한다.
        - 재고가 충분하면, **상품 서비스**에서 재고를 차감한다.
    - 이후 **쿠폰 서비스**에서 쿠폰을 확인하고, 유효한 쿠폰이 있으면 할인 금액을 적용하여 최종 금액을 계산한다.
    - 계산된 금액을 바탕으로 주문 데이터를 저장한 뒤, 주문 생성 성공 메시지를 반환한다.
2. **결제 처리 로직**
    - **유저 서비스**에서 사용자 잔액을 확인한 후, 잔액과 최종 금액을 비교하여 결제 가능 여부를 판단한다.
    - 잔액이 충분할 경우:
        - 사용자 잔액을 차감하고 결제 데이터를 저장한 뒤 결제 성공 메시지를 반환한다.
        - 결제 성공 후, 주문 정보를 **외부 데이터 플랫폼**으로 전송한다.
        - **외부 데이터 플랫폼**이 주문 정보를 정상적으로 수신하면, 전송 완료 상태를 기록한다.
    - 잔액이 부족하거나 결제 실패 상황이 발생하면:
        - **주문 서비스**에 주문 취소 요청을 보낸다.
        - **주문 서비스**는 **상품 서비스**에 상품 재고 원상복구 요청을 보낸다.
        - **상품 서비스**에서 재고를 복구한다.
        - 사용한 쿠폰이 있다면 **주문 서비스**는 **쿠폰 서비스**에 유저 쿠폰 원상복구 요청을 보낸다.
        - **쿠폰 서비스**에서 유저 쿠폰을 미사용 처리한다.
        - 주문 상태를 취소로 업데이트한 뒤, 주문 취소 성공 메시지를 반환한다.

### **동작 흐름 예**

1. **성공 흐름**
    - 주문 생성 → 재고 차감 → 쿠폰 적용 → 주문 금액 계산 → 결제 성공 → 사용자 잔액 차감 → 결제 데이터 저장 → 결제 완료 → 주문 정보 외부 전송
2. **실패 흐름**
    - 주문 생성 → 재고 차감 → 결제 실패 → 주문 취소 → 재고 복구 → 쿠폰 미사용 처리
