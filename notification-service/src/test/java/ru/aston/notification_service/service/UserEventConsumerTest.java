package ru.aston.notification_service.service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.*;

import java.util.concurrent.TimeUnit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class UserEventConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog").withExposedPorts(1025, 8025);

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "notification-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");

        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
    }

    @Test
    public void testSendEmailMessageWhenSaveNewUser() {
        String eventJson = "{\"email\":\"test@example.com\",\"type\":\"CREATED\"}";
        kafkaTemplate.send("user-events", eventJson);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String mailhogUrl = "http://" + mailhog.getHost() + ":" + mailhog.getMappedPort(8025);

            List<Map<String, Object>> messages = RestAssured
                .get(mailhogUrl + "/api/v2/messages")
                .then()
                .extract()
                .jsonPath()
                .getList("items");

            Map<String, Object> firstEmail = messages.get(0);

            Map<String, Object> content = (Map<String, Object>) firstEmail.get("Content");
            Map<String, List<String>> headers = (Map<String, List<String>>) content.get("Headers");

            String subject = decodeSubject(headers.get("Subject").get(0));

            String body = decodeBody(content.get("Body").toString());

            assertThat(subject).isEqualTo("Уведомление с сайта");
            assertThat(body).contains("Здравствуйте! Ваш аккаунт на сайте успешно создан.");
        });
    }

    private String decodeSubject(String subject) {
        String base64 = subject
            .replace("=?UTF-8?B?", "")
            .replace("?=", "");
        return new String(Base64.getDecoder().decode(base64), UTF_8);
    }

    private String decodeBody(String body) {
        body = body.replaceAll("\\r\\n", "");
        return new String(Base64.getDecoder().decode(body), UTF_8);
    }

}