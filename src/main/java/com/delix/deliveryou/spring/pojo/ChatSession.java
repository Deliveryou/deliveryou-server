package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "chat_session")
public class ChatSession {

    @EmbeddedId
    ChatSessionId chatSessionId;

    @Column(name = "channel_url")
    String channelUrl;

    @Column(name = "first_created")
    OffsetDateTime firstCreated;
}