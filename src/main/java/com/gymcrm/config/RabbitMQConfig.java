package com.gymcrm.config;

import com.gymcrm.util.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Bean
    public Queue workloadQueue() {
        return QueueBuilder.durable(Constants.QUEUE_UPDATE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "trainer-workload-dlq")
                .build();
    }

    @Bean
    public Queue workloadResponseQueue() {
        return new Queue(Constants.QUEUE_RESPONSE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("trainer-workload-dlq", true);
    }
}
