# 인덱스 설계를 통한 쿼리 성능 최적화

## 0. 개요

본 보고서는 데이터베이스 쿼리 성능을 최적화하기 위해 기존 쿼리를 분석하고, 인덱스 설계를 통해 성능 개선 전후를 비교한 결과를 제시한다.
인덱스 적용은 상위 상품 조회 쿼리를 위한 주문(`order_info`) 및 주문 상세(`order_item`) 테이블을 대상으로 하였다.

## 1. 더미 데이터 삽입

성능을 테스트하기 위한 더미 데이터를 삽입하였다.

**(1) `order_info` 테이블 더미 데이터 삽입 (6,000,000 rows)**

- status
    - `COMPLETED` 인 데이터 5,000,000개
    - `CANCELLED` 인 데이터 500,000개
    - `PENDING` 인 데이터 500,000개
- created_at: 2년 이내 랜덤

```sql
-- 5번 실행 (1m)
INSERT INTO order_info (created_at, status, final_amount, total_amount, user_id, user_coupon_id)
SELECT 
    NOW() - INTERVAL FLOOR(RAND() * 730) DAY AS created_at,  -- 0~730일 랜덤 선택 (2년)
    'COMPLETED' AS status,
    FLOOR(RAND() * 200000) AS total_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 200000) AS final_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 1000) AS user_id,         -- 유저 ID 범위: 0~999
    NULL AS user_coupon_id
FROM (
    WITH RECURSIVE seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 1000000 -- 100만 개 생성
    )
    SELECT n FROM seq
) t;

-- 1번 실행 (1m)
INSERT INTO order_info (created_at, status, final_amount, total_amount, user_id, user_coupon_id)
SELECT
    NOW() - INTERVAL FLOOR(RAND() * 730) DAY AS created_at,  -- 0~730일 랜덤 선택 (2년)
    'CANCELLED' AS status,
    FLOOR(RAND() * 200000) AS total_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 200000) AS final_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 1000) AS user_id,         -- 유저 ID 범위: 0~999
    NULL AS user_coupon_id
FROM (
    WITH RECURSIVE seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 500000 -- 50만 개 생성
    )
    SELECT n FROM seq
) t;

-- 1번 실행 (1m)
INSERT INTO order_info (created_at, status, final_amount, total_amount, user_id, user_coupon_id)
SELECT
    NOW() - INTERVAL FLOOR(RAND() * 730) DAY AS created_at,  -- 0~730일 랜덤 선택 (2년)
    'PENDING' AS status,
    FLOOR(RAND() * 200000) AS total_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 200000) AS final_amount,  -- 0~200,000 랜덤 값
    FLOOR(RAND() * 1000) AS user_id,         -- 유저 ID 범위: 0~999
    NULL AS user_coupon_id
FROM (
    WITH RECURSIVE seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 500000 -- 50만 개 생성
    )
    SELECT n FROM seq
) t;
```

**(2) `order_item` 테이블 더미 데이터 삽입 (10,000,000 rows)**

- quantity: 1~5개 랜덤
- item_id: 0~9999 랜덤
- order_id: order_info 의 ID에 맞게 설정

```sql
SET SESSION cte_max_recursion_depth = 10000000;

-- 1번 실행 (45m)
INSERT INTO order_item (created_at, updated_at, quantity, item_id, order_id)
SELECT
    NOW() - INTERVAL FLOOR(RAND() * 730) DAY AS created_at,
    NOW() AS updated_at,
    FLOOR(RAND() * 5) + 1 AS quantity,  -- 1~5개 랜덤 구매
    FLOOR(RAND() * 10000) AS item_id,  -- 상품 ID 범위 (0~9999)
    FLOOR(RAND() * 5000000) AS order_id -- order_info 의 ID에 맞게 설정
FROM (
    WITH RECURSIVE seq(n) AS (
        SELECT 1
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 10000000 -- 1000만 개 생성 (주문보다 많음)
    )
    SELECT n FROM seq
) t;
```

## 2. 인덱스 설계

**(1) 실행 패턴 분석**
```sql
SELECT oi.item_id
     , SUM(oi.quantity) AS total_sold
  FROM order_info o
       JOIN order_item oi 
         ON o.id = oi.order_id
 WHERE o.created_at > NOW() - INTERVAL 3 DAY
   AND o.status = 'COMPLETED'
 GROUP BY oi.item_id
 ORDER BY total_sold DESC
 LIMIT 3;
```

주요 필터링 조건
1. order_info.created_at > NOW() - INTERVAL 3 DAY 
2. order_info.status = 'COMPLETED' 
3. order_info.id = order_item.order_id (JOIN 키)

**(2) 인덱스 설계**

- `order_info` 테이블
    - `created_at` 필드에 대한 인덱스
    - `status` 필드에 대한 인덱스 (범위 조건 이후의 필드는 인덱스에 포함되지 않을 수 있음)
    - `status` 필드는 가능한 값이 3개 뿐이지만, `created_at` 필드가 카디널리티가 훨씬 높고 필터링이 많이 되기 때문에 인덱스의 순서를 아래와 같이 정함
```sql
CREATE INDEX idx_order_info_status_created_at ON order_info (created_at, status);
```

- `order_item` 테이블
    - `order_id` 필드에 대한 인덱스 (Foreign Key 로 설정되어 있으므로 이미 인덱스가 존재함)
```sql
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
```

- `order_item` 테이블
    - `item_id` 필드에 대한 인덱스
    - `quantity` 필드에 대한 인덱스
```sql
CREATE INDEX idx_order_item_item_id_quantity ON order_item (item_id, quantity);
```

## 3. 실행 계획 분석 및 비교

### 기존 인덱스 없이 실행 계획

**실행 쿼리**
```sql
SELECT oi.item_id, SUM(oi.quantity) AS total_sold
FROM order_info o
JOIN order_item oi ON o.id = oi.order_id
WHERE o.created_at > NOW() - INTERVAL 3 DAY
AND o.status = 'COMPLETED'
GROUP BY oi.item_id
ORDER BY total_sold DESC
LIMIT 3;
```
- 목적: 최근 3일 동안 `COMPLETED` 상태인 주문에서 가장 많이 팔린 상위 3개 상품을 조회
- `order_info` (`o`): 주문 정보 테이블
- `order_item` (`oi`): 주문별 포함된 상품 정보 테이블
- `o.id = oi.order_id` (PK-FK 조인)
- 최근 3일(`o.created_at > NOW() - INTERVAL 3 DAY`) + `COMPLETED` 상태 필터링
- `oi.item_id` 기준으로 `SUM(oi.quantity)`를 집계하여 많이 팔린 순으로 정렬 후 `LIMIT 3`

**EXPLAIN 결과 분석**

(1) `EXPLAIN` 실행 결과

| id  | select_type | table | partitions | type | possible_keys                                           | key                         | key_len | ref            | rows    | filtered | Extra                                        |
|-----|-------------|-------|------------|------|---------------------------------------------------------|-----------------------------|---------|----------------|---------|----------|----------------------------------------------|
| 1   | SIMPLE      | o     | NULL       | ALL  | PRIMARY                                                 | NULL                        | NULL    | NULL           | 5580072 | 11.11    | Using where; Using temporary; Using filesort |
| 1   | SIMPLE      | oi    | NULL       | ref  | FKija6hjjiit8dprnmvtvgdp6ru,FKi9h5ium1uah4jw57pg722osau | FKi9h5ium1uah4jw57pg722osau | 8       | ecommerce.o.id | 3       | 100      | NULL                                         |

- `order_info o`: **FULL SCAN (ALL)**
  - 인덱스를 활용하지 못하고 `o` 테이블 전체를 조회 (`5,580,072` 건)
  - `WHERE o.created_at > NOW() - INTERVAL 3 DAY AND o.status = 'COMPLETED'` 조건 때문에 인덱스 활용이 어려움
- `order_item oi`: **INDEX SCAN (ref)**
  - `FKi9h5ium1uah4jw57pg722osau` 인덱스를 사용 (FK 인덱스)
  - `oi.order_id = o.id` 조인을 수행하여 `o.id`를 검색
  - **조인된 후 필터링이 수행됨**

**문제점**

1. **`order_info` 테이블이 풀 스캔**→ `created_at`과 `status`에 적절한 인덱스가 없어서 필터링이 비효율적
2. **임시 테이블 + 파일 정렬 발생**→ `GROUP BY` + `ORDER BY` 때문에 MySQL 이 임시 테이블을 사용하고 파일 정렬이 수행됨

(2) `EXPLAIN ANALYZE` 실행 결과

```sql
-> Limit: 3 row(s)  (actual time=45230..45230 rows=3 loops=1)
    -> Sort: total_sold DESC, limit input to 3 row(s) per chunk  (actual time=45230..45230 rows=3 loops=1)
        -> Table scan on <temporary>  (actual time=45227..45228 rows=10000 loops=1)
            -> Aggregate using temporary table  (actual time=45227..45227 rows=10000 loops=1)
                -> Nested loop inner join  (cost=2.8e+6 rows=2.19e+6) (actual time=13.6..43956 rows=630597 loops=1)
                    -> Filter: ((o.`status` = 'COMPLETED') and (o.created_at > <cache>((now() - interval 3 day))))  (cost=463951 rows=619946) (actual time=10.5..7218 rows=316428 loops=1)
                        -> Table scan on o  (cost=463951 rows=5.58e+6) (actual time=10.5..5694 rows=6e+6 loops=1)
                    -> Index lookup on oi using FKi9h5ium1uah4jw57pg722osau (order_id=o.id)  (cost=3.41 rows=3.53) (actual time=0.0992..0.115 rows=1.99 loops=316428)

```

- **전체 실행 시간:** `~45초` (느림)
- **조인 과정**
  - `o.status = 'COMPLETED' AND o.created_at > NOW() - INTERVAL 3 DAY` 필터링 후 `316,428`건 남음
  - `order_item` (`oi`) 조회 수행 (조인 시 `~2건`씩 매칭)
- **정렬 및 집계**
  - `GROUP BY oi.item_id` → **임시 테이블 사용**
  - `ORDER BY total_sold DESC` → **정렬 비용 큼**
  - `LIMIT 3` 수행

**병목 구간**

1. `order_info` 풀 스캔 (약 `600만` 건 중 `31만` 건 필터링)
2. `order_item` 조인 (약 `63만` 건 처리)
3. `GROUP BY` 이후 정렬 (임시 테이블 사용)

### 인덱스 적용 후 실행 계획 1

**EXPLAIN 결과 분석**

(1) `EXPLAIN` 실행 결과

| id  | select_type | table | partitions | type  | possible_keys                                           | key                              | key_len | ref            | rows   | filtered | Extra                                                     |
|-----|-------------|-------|------------|-------|---------------------------------------------------------|----------------------------------|---------|----------------|--------|----------|-----------------------------------------------------------|
| 1   | SIMPLE      | o     | NULL       | range | PRIMARY,idx_order_info_created_at_status                | idx_order_info_created_at_status | 9       | NULL           | 629956 | 33.33    | Using where; Using index; Using temporary; Using filesort |
| 1   | SIMPLE      | oi    | NULL       | ref   | idx_order_item_item_id_quantity,idx_order_item_order_id | idx_order_item_order_id          | 8       | ecommerce.o.id | 3      | 100      | NULL                                                      |

(2) `EXPLAIN ANALYZE` 실행 결과

```sql
-> Limit: 3 row(s)  (actual time=8118..8118 rows=3 loops=1)
    -> Sort: total_sold DESC, limit input to 3 row(s) per chunk  (actual time=8118..8118 rows=3 loops=1)
        -> Table scan on <temporary>  (actual time=8116..8117 rows=10000 loops=1)
            -> Aggregate using temporary table  (actual time=8116..8116 rows=10000 loops=1)
                -> Nested loop inner join  (cost=855765 rows=735967) (actual time=0.52..7533 rows=630597 loops=1)
                    -> Filter: ((o.status = 'COMPLETED') and (o.created_at > <cache>((now() - interval 3 day))))  (cost=127374 rows=209985) (actual time=0.373..211 rows=316428 loops=1)
                        -> Covering index range scan on o using idx_order_info_created_at_status over ('2025-02-10 14:19:25.000000' < created_at)  (cost=127374 rows=629956) (actual time=0.368..93.2 rows=320518 loops=1)
                    -> Index lookup on oi using idx_order_item_order_id (order_id=o.id)  (cost=3.12 rows=3.5) (actual time=0.0188..0.0229 rows=1.99 loops=316428)
```

**기존 실행 계획과 개선 후 실행 계획 비교**

(1) 기존 실행 계획

- **주요 문제점**
  - `order_info` 테이블: `created_at`과 `status` 조건을 필터링하는 데 인덱스를 사용하지 못함 → **풀 테이블 스캔 발생**
  - `order_item` 테이블: `order_id`를 통한 조인은 기본 FK 인덱스(`FKi9h5ium1uah4jw57pg722osau`)를 사용했지만, `GROUP BY` 및 `SUM(quantity)` 연산 시 별도 최적화가 부족함
  - 정렬(`ORDER BY total_sold DESC`)과 제한(`LIMIT 3`)을 위해 **임시 테이블 생성** 및 **파일 정렬(filesort) 사용**
  - 전체적으로 성능 저하 요소:
    1. `order_info`에서 `created_at > NOW() - INTERVAL 3 DAY`와 `status = 'COMPLETED'` 조건으로 **약 316,428개 행 스캔**
    2. `order_item`을 **약 630,597번 인덱스 조회**
    3. `GROUP BY` 및 `ORDER BY` 수행을 위해 **임시 테이블 사용**
    4. 최종 정렬 후 `LIMIT 3` 수행


(2) 개선 후 실행 계획

- **변경 사항 및 최적화된 부분**
  - **(1) `order_info` 테이블**
    - `idx_order_info_status_created_at (status, created_at)` 인덱스를 추가함으로써, `status = 'COMPLETED'`와 `created_at` 조건을 **효율적으로 필터링**할 수 있게 됨
    - 기존 **풀 테이블 스캔 → 인덱스 범위 스캔으로 개선됨**→ **(기존) 6M 행 스캔 → (개선) 320,518 행만 스캔** (대략 5.7M 행 절약)
  - **(2) `order_item` 테이블**
    - `idx_order_item_item_id_quantity (item_id, quantity)` 추가했지만, `item_id`를 통한 `GROUP BY` 성능 향상이 크지 않음
    - `idx_order_item_order_id (order_id)`를 이용하여 **order_id 기반 조인 성능 최적화**
    - 기존 조인 횟수는 동일하지만, order_info 에서 불필요한 행을 줄였기 때문에 조인 효율이 향상됨
  - **(3) 임시 테이블 및 정렬**
    - 여전히 `GROUP BY`와 `ORDER BY` 수행을 위해 **임시 테이블 사용 및 filesort 발생**
    - 하지만, 불필요한 스캔이 줄어들었기 때문에 전체 실행 시간이 감소

(3) 실행 시간 비교

| 실행 단계            | 기존 실행 시간     | 개선 후 실행 시간  | 개선 효과           |
|------------------|--------------|-------------|-----------------|
| 전체 실행            | **45,230ms** | **8,118ms** | **~5.5배 성능 향상** |
| `order_info` 필터링 | 7,218ms      | 211ms       | **34배 빨라짐**     |
| `order_item` 조인  | 43,956ms     | 7,533ms     | **5.8배 빨라짐**    |

### 인덱스 적용 후 실행 계획 2
- FORCE INDEX 적용 (idx_order_item_item_id_quantity 강제 사용)
- `ORDER BY total_sold DESC`를 생략

**실행 쿼리**
```sql
SELECT oi.item_id, SUM(oi.quantity) AS total_sold
FROM order_info o
JOIN order_item oi FORCE INDEX (idx_order_item_item_id_quantity)
ON o.id = oi.order_id
WHERE o.created_at > NOW() - INTERVAL 3 DAY
AND o.status = 'COMPLETED'
GROUP BY oi.item_id
-- ORDER BY total_sold DESC
LIMIT 3;
```

- `order_item` 테이블(`oi`)
  - `idx_order_item_item_id_quantity` 인덱스를 강제 사용하여 **item_id를 기반으로 먼저 검색**
  - `GROUP BY oi.item_id`가 있기 때문에, **먼저 item_id를 정렬하여 접근하는 방식으로 변경됨**
  - 결과적으로 **Index Scan 수행 (Index scan on oi using idx_order_item_item_id_quantity)**
  - 기존에는 `order_info`를 먼저 필터링 후 `order_item`을 조회했지만,**이번 실행에서는 `order_item`을 먼저 조회한 후 `order_info`와 매칭**
- `order_info` 테이블(`o`)
  - 기존과 다르게, **`PRIMARY KEY(id)` 인덱스를 사용하여 단일 조회(Single-row index lookup on o using PRIMARY)**
  - `oi.order_id`를 `o.id`로 단일 조회하면서 **각 order_item 행마다 `order_info`를 조인**
  - 기존에는 `order_info`를 먼저 필터링하고 `order_item`을 가져왔지만,**이번 실행에서는 `order_item`에서 `order_info`를 참조하는 방식으로 변경됨**
- **정렬과 그룹화**
  - 기존처럼 임시 테이블을 사용하지 않고, **`item_id` 기준으로 그룹화가 빠르게 수행됨**
  - `ORDER BY total_sold DESC`를 생략한 상태에서도 성능이 크게 개선됨
- **성능 개선 포인트**
  - `order_item`에서 먼저 `item_id`를 기준으로 검색하고, 이후 `order_info`를 조회하는 방식으로 변경
  - **조인 수행 속도가 6733ms → 86.2ms로 대폭 감소**
  - 기존에는 `order_info`의 `created_at` 필터링 후 조인했지만,**이번에는 `order_item`의 인덱스를 먼저 활용하여 빠르게 필터링 후 조인**

**인덱스 적용 효과 비교**

| 실행 계획          | 인덱스 사용 방식                                                      | 주요 차이점                               | 실행 시간           |
|----------------|----------------------------------------------------------------|--------------------------------------|-----------------|
| 기본 실행 계획       | `idx_order_info_created_at_status` + `idx_order_item_order_id` | `order_info` 필터링 후 `order_item` 조인   | **7015~8116ms** |
| FORCE INDEX 적용 | `idx_order_item_item_id_quantity`                              | `order_item` 먼저 조회 후 `order_info` 조인 | **86.2ms**      |


## 4. 마무리

**결론**

1. **`idx_order_item_item_id_quantity` 강제 적용이 가장 큰 큰 성능 향상을 가져옴**
- 기존에는 `order_info`를 먼저 필터링 후 조인했지만,
- `order_item`에서 먼저 필터링하고 `order_info`를 조회하는 방식이 더 효과적이었음

2. **임시 테이블과 파일 정렬(Using temporary; Using filesort) 제거 효과**
- 기존에는 `GROUP BY`와 `ORDER BY`에서 임시 테이블을 사용했지만,
- `FORCE INDEX (idx_order_item_item_id_quantity)`로 인해 정렬과 그룹화가 훨씬 빠르게 수행됨

**한계 분석**

- `GROUP BY` 후 `ORDER BY`를 수행해야 하기 때문에 인덱스만으로 정렬을 최적화하기 어려움
- `idx_order_item_item_id_quantity_desc (item_id, quantity DESC)` 인덱스를 활용해도, `GROUP BY` 자체가 별도의 정렬을 요구하므로 효과가 크지 않음
- 커버링 인덱스 `idx_order_item_item_id_quantity_order (item_id, quantity, order_id)`를 추가했지만 성능 개선이 크지 않음
- 결국, MySQL 이 `GROUP BY` 후 정렬을 다시 수행하면서 성능 저하가 발생

**개선 방안**
1. **애플리케이션 레벨에서 정렬 처리**
- DB 에서 `GROUP BY item_id` 까지만 수행하고, 정렬(`ORDER BY total_sold DESC`)은 애플리케이션에서 처리
- 특히 상위 상품의 개수 데이터 크기가 크지 않거나, 캐싱이 가능할 경우 효과적일 것이라고 예상

2. **데이터 미리 집계하여 저장**
- `order_item`에서 자주 조회되는 데이터는 `daily_item_sales` 같은 별도 테이블로 관리
- 매일 또는 특정 간격마다 `item_id`별 판매량을 미리 집계하여 저장한 후, 정렬된 상태로 가져오기




