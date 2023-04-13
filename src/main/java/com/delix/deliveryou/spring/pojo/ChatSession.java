package com.delix.deliveryou.spring.pojo;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ChatSession {
    User user;
    User shipper;
    String channelUrl;
    OffsetDateTime firstCreated;
}