import http from 'k6/http';
import { sleep, check } from 'k6';

// 테스트 옵션 (예: 1000명의 가상 유저가 30초 동안 실행)
export let options = {
    vus: 1000,
    duration: '30s',
};

// API 기본 URL (실제 API 서버 주소로 변경)
const BASE_URL = 'http://springboot-app:8080';

export default function () {
    // 1. 상품 리스트 조회 (GET /api/items)
    let resItems = http.get(`${BASE_URL}/api/items`);
    check(resItems, {
        'GET /api/items status is 200': (r) => r.status === 200,
    });

    // 2. 1초 대기
    sleep(1);

    // 3. 주문 생성 (POST /api/orders)
    let orderPayload = JSON.stringify({
        "userId": 1,
        "orderItems": [
            { "itemId": 1, "quantity": 2 },
            { "itemId": 2, "quantity": 5 }
        ]
    });

    let params = { headers: { 'Content-Type': 'application/json' } };
    let resOrder = http.post(`${BASE_URL}/api/orders`, orderPayload, params);
    check(resOrder, {
        'POST /api/orders status is 201': (r) => r.status === 201,
    });

    // 4. 주문 생성 응답에서 주문 ID 추출 후 특정 주문 조회 (GET /api/orders/{id})
    let orderResponse = resOrder.json();
    let orderId = orderResponse.content.id;

    let resOrderDetail = http.get(`${BASE_URL}/api/orders/${orderId}`);
    check(resOrderDetail, {
        'GET /api/orders/{id} status is 200': (r) => r.status === 200,
    });

    // console.log('orderId :::::::::::: ', orderId);

    // 5. 결제 처리 (POST /api/payments)
    // let paymentPayload = JSON.stringify({
    //     "userId": 1,
    //     "orderId": orderId,
    //     "method": "CARD"
    // });
    //
    // let resPayment = http.post(`${BASE_URL}/api/payments`, paymentPayload, params);
    //
    // console.log('resPayment :::::::::::: ', resPayment);
    //
    // check(resPayment, {
    //     'POST /api/payments status is 200': (r) => r.status === 200,
    // });
}