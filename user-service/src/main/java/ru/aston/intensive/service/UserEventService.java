package ru.aston.intensive.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.intensive.dto.EventType;
import ru.aston.intensive.dto.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendMessage(String email, EventType type) {
        UserEvent event = new UserEvent(email, type);
        kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(event));
    }

}
