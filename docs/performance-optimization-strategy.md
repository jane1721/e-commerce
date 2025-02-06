# Cache 와 Redis 를 활용한 이커머스 시스템 성능 개선

---

## 캐시 (Cache)

### 캐시란 무엇인가?
- 자주 사용하는 데이터를 더 빠르게 접근할 수 있도록 임시로 저장하는 메모리 공간

### 왜 사용하는가?
- **속도 향상:** DB나 API 호출보다 훨씬 빠른 메모리에서 데이터를 가져올 수 있다.
- **부하 감소:** 동일한 요청이 반복될 때, 연산을 줄이고 시스템 리소스를 절약할 수 있다.
- **비용 절감:** 외부 API 호출 비용이나 데이터베이스 부하를 줄여 비용을 절약할 수 있다.

### 언제 사용할까?
- 동일한 요청이 자주 반복될 때
- 데이터 변경 빈도가 낮을 때
- 성능 최적화가 필요할 때

### 한계점
- **데이터 일관성 문제:** 캐시가 갱신되지 않으면 오래된 데이터를 반환할 가능성이 있다.
- **메모리 사용량 증가:** 메모리 기반 캐시는 서버의 RAM 을 사용하므로 적절한 설정이 필요하다.
- **캐시 전략 필요:** 모든 데이터를 캐싱하면 오히려 성능이 저하될 수 있다. (ex. TTL, LRU, LFU 등 설정 필요)

→ 캐시 사용은 DB 부하를 줄이고 성능을 향상시키지만, 상황에 따른 적절한 캐시 전략이 필요하다.

---

## 캐시 전략

### 읽기 및 쓰기 (Read & Write) 전략

자주 조회되는 데이터를 어떻게 캐시에서 가져오고, 어떻게 캐시에 저장할 지 결정하는 방식  
→ 잘 활용 시, 성능 ↑ 일관성 유지 OK

### 1. Look Aside (Lazy Loading)
- **설명:** 애플리케이션이 먼저 캐시를 확인하고, 데이터가 없으면 DB 에서 가져와 캐시에 저장한 후 반환하는 방식
- **장점**
  - 캐시가 불필요하게 커지지 않는다. (필요한 데이터만 저장)
  - 최신 데이터가 필요할 때 캐시가 저장되므로 일관성이 높다.
- **단점**
  - 첫번째 요청은 항상 느리다. (Cache Miss 시 DB 조회 발생)
  - 데이터 갱신을 수동으로 관리해야 한다.

### 2. Read Through
- **설명:** 캐시에 데이터가 없으면, 캐시가 DB 로부터 데이터를 가져와 캐시에 저장하고 반환하는 방식
- **장점**
  - 캐시와 DB 가 자동으로 동기화 된다.
- **단점**
  - Cache Miss 시 읽기 지연 발생 가능

### 3. Write Through
- **설명:** 애플리케이션이 데이터를 수정할 때 캐시와 DB 에 동시에 저장하는 방식
- **장점**
  - 캐시와 DB 가 항상 동기화 된다.
  - 캐시 적중률이 높아진다.
- **단점**
  - 모든 변경 사항이 캐시와 유 에 동시에 저장되므로 성능이 낮을 수 있다.
  - 쓰기 연산이 많으면 DB 부하가 증가한다.

### 4. Write Around
- **설명:** 캐시에 데이터를 저장하지 않고, 바로 DB 에만 저장하는 방식
- **장점**
  - 캐시에 불필요한 데이터가 저장되지 않는다.
  - 캐시가 중요한 데이터만 유지하므로 메모리 사용량이 적다.
- **단점**
  - 첫 번째 조회 시 속도가 느릴 수 있다.
  - 데이터 변경 후 캐시가 즉시 반영되지 않아 최신 데이터가 늦게 반영될 수 있다.

### 5. Write Back (Write Behind)
- **설명:** 데이터를 먼저 캐시에만 저장하고, 일정 시간이 지나거나 특정 조건이 충족될 때 비동기적으로 DB 에 반영하는 방식
- **장점**
  - 성능이 가장 좋다. (쓰기 연산이 많아도 부담 ↓)
  - 캐시에서 데이터를 빠르게 제공할 수 있다.
- **단점**
  - 데이터가 캐시에만 존재하는 동안 서버가 다운되면 손실될 위험이 있다.
  - 데이터 일관성이 꺠질 수 있다.

### 캐시 무효화 (Cache Invalidation) 전략

DB 의 데이터가 변경될 경우, 캐시를 어떻게 갱신할 지 결정하는 방식

### 1. TTL (Time-To-Live)
- 일정 시간이 지나면 캐시를 자동으로 제거하여 오래된 데이터가 조회되는 것을 방지한다.
- TTL 이 너무 길면 최신 데이터 반영이 느리다.

### 2. LRU (Least Recently Used)
- 가장 오래 사용되지 않은 항목을 캐시에서 제거(Eviction)한다.

### 3. LFU (Least Frequently Used)
- 접근 빈도가 가장 낮은 항목을 캐시에서 제거(Eviction)한다.

### 캐시 스탬피드 (Cache Stampede) 현상

- 특정 데이터가 캐시에서 만료될 때 다수의 요청이 동시에 DB 로 몰려 과부하를 일으키는 현상
- **해결 방법**
  - 캐시 만료 전에 Background 에서 미리 데이터를 갱신해 준다. (ex. `@Scheduled` 를 사용하여 주기적으로 캐시 Refresh)
  - 캐시가 만료된 직후, 하나의 요청만 DB 에서 데이터를 가져올 수 있도록 분산 락을 건다.

---

## 이커머스 API 캐시 적용 방안

### 1. 상품 목록 조회 기능 (getItems)
- **캐시 전략**: Look Aside + 분산 캐시(Redis)
- **적용 이유**: 상품 목록은 재고와 같은 정보가 자주 변경되므로 일관성을 유지하면서 빠른 조회가 가능한 분산 캐시를 활용하였다.
- **분산 락 활용**: 첫 번째 요청이 캐시를 갱신할 때 다른 요청은 대기하게 하여 캐시 스탬피드 현상을 방지한다.

```java
package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetItemsUseCase {

    private final ItemService itemService;

    @Cacheable(cacheNames = "items", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ItemResponse> execute(Pageable pageable) {
        return itemService.getItems(pageable).map(ItemResponse::from);
    }
}

```

### 2. 상위 상품 조회 기능 (getTopItems)
- **캐시 전략**: Write-Through 캐시 + 스케줄러 기반 캐시 갱신 + 분산 캐시(Redis)
- **적용 이유**
  - 상위 상품 목록은 1일 주기로 업데이트되는 데이터로, 빈번한 변경이 없어서 정기적인 스케줄러를 통해 캐시를 갱신하는 방식을 채택하였다.
  - 이를 통해 캐시 만료 시 발생할 수 있는 캐시 스탬피드를 사전에 방지하고, 항상 최신 데이터가 유지되도록 보장할 수 있다.
- **스케줄러 활용**: 캐시 만료 전에 스케줄러가 자동으로 캐시를 갱신하여 사용자는 항상 최신 데이터를 조회할 수 있다.

```java
package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.cache.CacheManager;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTopItemsUseCase {

    private final ItemService itemService;
    private final CacheManager cacheManager;

    // 상위 상품 조회 캐시 키
    private static final String TOP_ITEMS_CACHE_KEY = "topItems";

    @Cacheable(cacheNames = "topItems", key = "'topItems'")
    public List<TopItemResponse> execute() {

        return itemService.getTopItems();
    }

    // 상위 상품을 조회하는 메소드 (캐시 갱신)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateTopItemsCache() {

        List<TopItemResponse> topItems = itemService.getTopItems();
        cacheManager.getCache("topItems").put("topItems", topItems);
    }
}
```

---

## 선착순 쿠폰 발급 기능 성능 개선

### Redis 로 선착순 쿠폰 발급 로직을 이관하는 문제 배경
- 현재 시스템에서는 선착순 쿠폰 발급을 DB 를 통해 처리하고 있으며, 다수 유저가 동시에 쿠폰을 발급받을 경우 DB의 부하가 커질 수 있다.
- Redis 는 인메모리 데이터베이스로서 빠른 응답 속도를 제공하여, 대량의 요청이 동시에 발생하는 상황에서도 높은 성능을 유지할 수 있다.

### Redis 없이 MySQL 만 사용했을 떄의 문제점
- **지연 시간:** 매번 MySQL 에 쿼리 요청을 보내고 디스크 I/O 가 발생하므로 성능이 저하될 수 있다.
- **과부하:** 쿠폰 발급에 대량 트래픽이 발생하면 MySQL 에 큰 부하가 걸려서 전체 시스템 성능에 영향을 미칠 수 있다.

### Redis 를 사용했을 때의 기대 효과
- **성능 향상:** Redis 를 사용하면 실시간으로 데이터를 빠르게 읽고 쓸 수 있어 시스템 성능을 최적화 할 수 있다.
- **부하 분산:** Redis 는 메모리 기반이기 때문에 MySQL 에 직접적인 부하를 줄여줄 수 있다.
- **중복 발급 방지:** Redis 의 명령어 (`ZADD`, `SADD`) 를 사용하여 쿠폰 발급 요청을 원자적으로 실행하여 동시성 문제를 해결할 수 있다.
- **비동기 처리:** Redis 를 통해 비동기적으로 상태 관리와 MySQL DB 데이터 업데이트를 분리하여 작업 처리가 가능하다.

### 개선 시나리오

**Step 1) Redis 에 선착순 쿠폰 발급 관리 자료구조 추가**
- Sorted set 을 이용하여 발급 순서를 관리
- Set 을 이용하여 중복 발급을 방지

**Step 2) 쿠폰 발급 요청 시 대기열에 요청 저장**
1. `SCARD` 으로 Set 에서 현재 발급 개수를 조회하여 잔여 수량을 검증한다.
2. `SISMEMBER` 로 Set 에 해당 요청자의 발급 이력을 확인하여 중복 발급을 방지한다.
3. `ZADD` 로 Sorted Set 에 요청자 정보를 저장한다. (발급 순서대로 정렬됨)
4. `SADD` 명령어로 Redis 의 Set 에 해당 요청자 정보를 저장한다.

**Step 3) 스케줄러를 가지고 비동기로 적절한 수량을 대기열에서 꺼내와서 쿠폰 발급을 수행**
1. `ZPOPMIN` 으로 Sorted Set 에서 발급 대상자를 꺼내서 쿠폰 발급 로직(기존 로직)을 수행한다.