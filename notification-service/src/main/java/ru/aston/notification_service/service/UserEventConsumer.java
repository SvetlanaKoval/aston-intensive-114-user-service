package ru.aston.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.aston.notification_service.dto.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(String event) throws JsonProcessingException {
        UserEvent userEvent = objectMapper.readValue(event, UserEvent.class);

        emailService.sendMail(userEvent.getEmail(), userEvent.getType().getDescription());
    }

}
