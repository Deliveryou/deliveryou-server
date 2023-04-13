package com.delix.deliveryou.api.sendbird;

import com.delix.deliveryou.spring.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

@Component
public class SendBird {
    @Value("${sendbird.applicationid}")
    private String APPLICATION_ID;
    @Value("${sendbird.apikey}")
    private String API_KEY;
    @Autowired
    private ObjectMapper jsonObjectMapper;

    private HttpHeaders defaultHeader;

    private String buildEndpoint(String subdirectory) {
        String baseUrl = String.format("https://api-%s.sendbird.com/v3", APPLICATION_ID);
        if (subdirectory == null)
            return baseUrl;
        else {
            subdirectory = subdirectory.trim().replaceAll("\\s", "");
            if (subdirectory.length() > 1 && subdirectory.charAt(0) != '/')
                subdirectory = "/" + subdirectory;
            return baseUrl + subdirectory;
        }
    }

    private HttpHeaders getHeader() {
        if (defaultHeader == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api-Token", API_KEY);
            defaultHeader = headers;
        }
        return defaultHeader;
    }

    public void createUser(SendBirdUser.RequestUser user, Consumer<SendBirdUser.ResponseUser> onCreated, Consumer<Exception> onError) {
        try {
            String endpoint = buildEndpoint("/users");
            RestTemplate restTemplate = new RestTemplate();
            // serialize the user into json
            String body = jsonObjectMapper.writeValueAsString(user);
            // http headers and body
            HttpEntity<String> entity = new HttpEntity<>(body, getHeader());

            SendBirdUser.ResponseUser resUser = restTemplate.postForObject(endpoint, entity, SendBirdUser.ResponseUser.class);

            if (resUser != null)
                onCreated.accept(resUser);
            else
                throw new Exception("User is not created");

        } catch (Exception e) {
            onError.accept(e);
        }
    }

    public void createGroupChannel(User user1, User user2, Consumer<SendBirdChannel.ResponseChannel> onCreated, Consumer<Exception> onError) {
        try {
            String endpoint = buildEndpoint("/group_channels");
            RestTemplate restTemplate = new RestTemplate();
            // create channel info
            SendBirdChannel.RequestChannel channel = new SendBirdChannel.RequestChannel() {{
                setName(String.format("%s, %s", user1.getFirstName(), user2.getFirstName()));
                set_distinct(true);
                setUser_ids(Arrays.asList(String.valueOf(user1.getId()), String.valueOf(user2.getId()), "bot"));
            }};

            String body = jsonObjectMapper.writeValueAsString(channel);
            HttpEntity<String> entity = new HttpEntity<>(body, getHeader());

            SendBirdChannel.ResponseChannel resChannel = restTemplate.postForObject(endpoint, entity, SendBirdChannel.ResponseChannel.class);
            if (resChannel == null)
                throw new Exception("Response Channel is null");
            onCreated.accept(resChannel);

        } catch (Exception e) {
            onError.accept(e);
        }
    }

    public boolean SendFirstMessage(SendBirdChannel.ResponseChannel channel) {
        final String MESSAGE = "Feel free to send your first message! \uD83D\uDE42";
        RestTemplate restTemplate = new RestTemplate();
        try {
            String ENDPOINT = buildEndpoint(String.format("/group_channels/%s/messages", channel.channel_url));
            SendBirdMessage.RequestMessage message = new SendBirdMessage.RequestMessage(){{
                setMessage(MESSAGE);
                setUser_id("bot");
            }};

            String body = jsonObjectMapper.writeValueAsString(message);
            HttpEntity<String> entity = new HttpEntity<>(body, getHeader());

            SendBirdMessage.ResponseMessage resMessage = restTemplate.postForObject(ENDPOINT, entity, SendBirdMessage.ResponseMessage.class);

            return (resMessage.getMessage_id() != 0);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
