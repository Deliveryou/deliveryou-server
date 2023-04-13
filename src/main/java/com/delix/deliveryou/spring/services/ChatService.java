package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.ChatSession;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserService userService;

    /**
     * add new chat session, only if it does not exist
     * @param chatSession
     * @return 1 if added, 0 if already exists, -1 if failed
     * @throws HttpBadRequestException if the session is null
     */
    public int addSession(ChatSession chatSession) {
        if (chatSession == null)
            throw new HttpBadRequestException();

        int result = chatRepository.addSessionIfNotExist(chatSession);
        switch (result) {
            case 1:
                System.out.println("[ChatService] added new session: " + chatSession);
                break;
            case 0:
                System.out.println("[ChatService] session already exists url=" + chatSession.getChannelUrl());
                break;
            case -1:
                System.out.println("[ChatService] exception: cannot add session");
        }
        return result;
    }

    /**
     *
     * @return a chat session object, null if id not exist
     * @throws HttpBadRequestException if [packageId] is invalid
     */
    public ChatSession getChatSession(long userId, long shipperId) {
        User user = ((JWTUserDetails) userService.loadUserById(userId)).getUser();
        User shipper = ((JWTUserDetails) userService.loadUserById(shipperId)).getUser();

        if ((user == null || user.getRole().getId() != UserRole.USER.getId()) &&
                (shipper == null || shipper.getRole().getId() != UserRole.SHIPPER.getId()))
            throw new HttpBadRequestException();

        return chatRepository.getChatSession(user, shipper);
    }

}
