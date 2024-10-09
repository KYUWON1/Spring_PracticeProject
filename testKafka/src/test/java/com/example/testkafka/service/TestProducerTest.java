package com.example.testkafka.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestProducerTest {
    @Autowired
    private TestProducer testProducer;

    @Test
    void test() {
        testProducer.create();

    }

    @Test
    void test2(){
        testProducer.creatediff();
    }
}