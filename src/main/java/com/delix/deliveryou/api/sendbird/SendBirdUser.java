package com.delix.deliveryou.api.sendbird;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

public class SendBirdUser {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class RequestUser {
        String user_id;
        String nickname;
        String profile_url;

        //    String issue_access_token;
        //    String session_token_expires_at;
        //        "metadata": {
        //        "location": "Seoul",
        //                "marriage": "N",
        //                "hasSomeone": "Y"
        //    }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ResponseUser {
          String user_id;
          String nickname;
          String profile_url;
          String access_token;
          boolean is_online;
          boolean is_active;
          boolean is_created;
          String phone_number;
          boolean require_auth_for_profile_image;
          List<String> session_tokens;
          String last_seen_at;
          List<String> discovery_keys;
          List<String> preferred_languages;
          boolean has_ever_logged_in;
    }
}
