package com.delix.deliveryou.api.sendbird;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

public class SendBirdChannel {

    public enum ChannelType {
        OPEN_CHANNELS("open_channels"),
        GROUP_CHANNELS("group_channels");

        final String type;
        ChannelType(String val) {
            type = val;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    @Data
    @ToString
    @NoArgsConstructor
    public static class RequestChannel {
            private String name;
            private String channel_url;
            private String cover_url;
            private String custom_type;
            private boolean is_distinct;
            private List<String> user_ids = Collections.emptyList();
            private List<String> operator_ids = Collections.emptyList();

        public RequestChannel(String name, String channel_url, String cover_url, String custom_type, boolean is_distinct, List<String> user_ids, List<String> operator_ids) {
            this.name = name;
            this.channel_url = channel_url;
            this.cover_url = cover_url;
            setCustom_type(custom_type);
            this.is_distinct = is_distinct;
            this.user_ids = user_ids;
            this.operator_ids = operator_ids;
        }

        public void setCustom_type(String custom_type) {
            if (custom_type != null && custom_type.length() > 128) {
                this.custom_type = custom_type.substring(0, 127);
                return;
            }
            this.custom_type = custom_type;
        }

    }

    @Data
    @ToString
    @NoArgsConstructor
    public static class ResponseChannel {
        String name;
        String channel_url;
        String cover_url;
        String custom_type;
        int unread_message_count;
        String data;
        boolean is_distinct;
        boolean is_public;
        boolean is_super;
        boolean is_ephemeral;
        boolean is_access_code_required;
        int member_count;
        int joined_member_count;
        int unread_mention_count;
        SendBirdUser.ResponseUser created_by;
        List<SendBirdUser.ResponseUser> members;
        List<SendBirdUser.ResponseUser> operators;
        Object last_message;
        int message_survival_seconds;
        int max_length_message;
        long created_at;
        boolean freeze;
    }

}
