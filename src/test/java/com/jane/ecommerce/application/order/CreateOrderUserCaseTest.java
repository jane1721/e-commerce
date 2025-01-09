package com.jane.ecommerce.application.order;
import com.jane.ecommerce.application.order.CreateOrderUseCase;
import com.jane.ecommerce.domain.coupon.CouponService;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.order.OrderCreateResponse;
import com.jane.ecommerce.interfaces.dto.order.OrderItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@Testcontainers
@SpringBootTest
public class CreateOrderUserCaseTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.32")
            .withDatabaseName("ecommerce")
            .withUsername("ecommerce")
            .withPassword("ecommerce");

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    private OrderService orderService;
    private UserService userService;
    private ItemService itemService;
    private CouponService couponService;

    private User mockUser;
    private Item mockItem;
    private UserCoupon mockUserCoupon;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        // Mock Dependencies
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        orderService = Mockito.mock(OrderService.class);
        couponService = Mockito.mock(CouponService.class);

        // Inject Mocks into UseCase
        createOrderUseCase = new CreateOrderUseCase(orderService, userService, itemService, couponService);

        // Mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Jane Lee");

        // Mock Item
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Test Item");
        mockItem.setPrice(100L);
        mockItem.setStock(50);

        // Mock UserCoupon
        mockUserCoupon = new UserCoupon();
        mockUserCoupon.setId(1L);;

        // Mock Order
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus("CREATED");
        mockOrder.setTotalAmount(190L);
        mockOrder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void execute_shouldCreateOrderSuccessfully() {
        // Arrange
        String userId = "1";
        List<OrderItemDTO> orderItemDTOs = List.of(
                new OrderItemDTO("1", 2)
        );
        String userCouponId = "1";

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(itemService.getItemById(1L)).thenReturn(mockItem);
        Mockito.when(couponService.getUserCouponById(1L)).thenReturn(mockUserCoupon);
        Mockito.when(orderService.createOrder(eq(mockUser), any(), eq(mockUserCoupon))).thenReturn(mockOrder);

        // Act
        OrderCreateResponse response = createOrderUseCase.execute(userId, orderItemDTOs, userCouponId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getStatus()).isEqualTo("CREATED");
        assertThat(response.getTotalAmount()).isEqualTo(190);
        assertThat(response.getCreatedAt()).isNotNull();

        // Verify stock update
        assertThat(mockItem.getStock()).isEqualTo(48); // Stock reduced by 2
        Mockito.verify(itemService).save(mockItem);
        Mockito.verify(orderService).createOrder(eq(mockUser), any(), eq(mockUserCoupon));
    }
}
