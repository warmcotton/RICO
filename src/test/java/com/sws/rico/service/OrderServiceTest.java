package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.constant.Role;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import com.sws.rico.mapper.OrderMapper;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.OrderRepository;
import com.sws.rico.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private EntityManager em;

    @Test
    void orderItem() throws InterruptedException {
        User sell = User.createUser("testSupplier@rico.com","1111", "s1", Role.SUPPLIER, passwordEncoder);
        User user1 = User.createUser("testUser1@rico.com","1111","u1", Role.USER, passwordEncoder);
        User user2 = User.createUser("testUser2@rico.com","1111","u2", Role.USER, passwordEncoder);
        User user3 = User.createUser("testUser3@rico.com","1111","u3", Role.USER, passwordEncoder);
        User user4 = User.createUser("testUser4@rico.com","1111","u4", Role.USER, passwordEncoder);
        User user5 = User.createUser("testUser5@rico.com","1111","u5", Role.USER, passwordEncoder);
        User user6 = User.createUser("testUser6@rico.com","1111","u6", Role.USER, passwordEncoder);
        User user7 = User.createUser("testUser7@rico.com","1111","u7", Role.USER, passwordEncoder);
        User user8 = User.createUser("testUser8@rico.com","1111","u8", Role.USER, passwordEncoder);
        User user9 = User.createUser("testUser9@rico.com","1111","u9", Role.USER, passwordEncoder);
        User user10 = User.createUser("testUser10@rico.com","1111","u10", Role.USER, passwordEncoder);
        userRepository.saveAll(Arrays.asList(sell, user1, user2, user3,user4,user5,user6,user7,user8,user9,user10));

        int quantity = 100;
        int jobs = 10;

        Item item = new Item();
        item.setName("item1");
        item.setPrice(1000);
        item.setQuantity(quantity);
        item.setItemStatus(ItemStatus.FOR_SALE);
        item.setBrief("item brief");
        item.setDescription("item description");
        item.setUser(sell);
        itemRepository.save(item);

        ExecutorService es = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(jobs);

        for (int i=1; i<=jobs; i++) {
            int idx = i;
            es.execute(() -> {
                try {
                    orderService.orderItem(1L, 1, "testUser"+ idx +"@rico.com");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        int remain = itemRepository.findById(1L).get().getQuantity();
        assertThat(jobs).isEqualTo(quantity - remain);
    }
}