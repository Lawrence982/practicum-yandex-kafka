package ru.yandex.practicum.model;

import lombok.Data;

@Data
public class BlockedUser {

    private String userId;
    private String blockedUserId;
    private String reason;

}
