package ru.aston.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {
    CREATED("Здравствуйте! Ваш аккаунт на сайте успешно создан."),
    DELETED("Здравствуйте! Ваш аккаунт был удалён.");

    private final String description;
}
