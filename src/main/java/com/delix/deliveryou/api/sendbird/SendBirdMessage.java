package com.delix.deliveryou.api.sendbird;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class SendBirdMessage {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class RequestMessage {
            String user_id;
            String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ResponseMessage {
        String type;
        long message_id;
        String message;
        String data;
        String custom_type;
        long created_at;
    }
}
