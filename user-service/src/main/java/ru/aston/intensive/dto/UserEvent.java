package ru.aston.intensive.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String email;
    private EventType type;
}
