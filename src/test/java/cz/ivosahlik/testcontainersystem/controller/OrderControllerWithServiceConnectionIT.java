package cz.ivosahlik.testcontainersystem.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ivosahlik.testcontainersystem.constants.DockerImageConstants;
import cz.ivosahlik.testcontainersystem.model.Order;
import cz.ivosahlik.testcontainersystem.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OrderControllerWithServiceConnectionIT {

    private static final Integer TIMEOUT = 120;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>(DockerImageName.parse(DockerImageConstants.MYSQL_IMAGE));

    @BeforeAll
    static void startContainers() {
        // Ensure PostgreSQL is running
        Awaitility.await().atMost(Duration.ofSeconds(TIMEOUT)).until(mysql::isRunning);
        log.info("PostgreSQL is up and running!");
    }


    @Test
    public void shouldCreateOrderAndPublishEvent() throws Exception {

        log.info("JDBC URL: {}", mysql.getJdbcUrl());
        log.info("JDBC URL: {}", mysql.getLogs());

//        WaitingConsumer consumer = new WaitingConsumer();
//        mysql.followOutput(consumer, OutputFrame.OutputType.STDOUT);
//
//        consumer.waitUntil(outputFrame -> outputFrame.getUtf8String().contains("ready"), 5, TimeUnit.SECONDS);
//        log.info("Mysql is up and running!");

        // Create a new order
        Order order = new Order("DUMMY_STATUS", "Order from Integration Test");

        // Perform POST request to create the order (using objectMapper)
        MvcResult mvcResult = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("DUMMY_STATUS")))
                .andReturn();
        // Extract the created order's ID from the response
        String responseContent = mvcResult.getResponse().getContentAsString();
        Long orderId = Long.parseLong(responseContent.substring(responseContent.indexOf("\"id\":") + 5, responseContent.indexOf(",")));

        // Verify order saved in the database
        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo("DUMMY_STATUS");
    }

}
