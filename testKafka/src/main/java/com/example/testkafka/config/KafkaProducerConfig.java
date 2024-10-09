package com.example.testkafka.config;

import jakarta.websocket.OnClose;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    /*
      BOOTSTRAP_SERVERS_CONFIG: Producer 가 처음으로 연결할 kafka 브로커의 위치 : 현재 9092 포트
      KEY_SERIALIZER_CLASS_CONFIG: 브로커로 데이터를 전송하기 전에, byte array 로 변경하는 과정
      VALUE_SERIALIZER_CLASS_CONFIG: 위에 과정과 동일
     */
    @Bean
    public ProducerFactory<String,Object> producerFactory() {
        Map<String ,Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /*
        KafkaTemplate을 활용해서, kafka 와 통신
     */
    @Bean
    public KafkaTemplate<String , Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
