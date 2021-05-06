package com.eu.cloud.rabbitmq;

import com.eu.cloud.rabbitmq.config.RabbitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = MqTestApplication.class)
@RunWith(SpringRunner.class)
public class RabbitMqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSend() {
//        for (int i = 0; i < 10; i++) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, "222");
//        }
    }
}
