package com.example.testkafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public TestProducer(KafkaTemplate<String,Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create() {
        kafkaTemplate.send("topic","kafka Test");
    }

    public void creatediff() {
        kafkaTemplate.send("topic_diff","kafka Test");
    }
}
