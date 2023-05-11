package com.delix.deliveryou.spring.repository.mockdata;

import com.delix.deliveryou.spring.pojo.ChatSession;
import com.delix.deliveryou.spring.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository
public class ChatRepository {
    private List<ChatSession> chatSessionMockData = new ArrayList<>();

    /**
     * @param chatSession
     * @return 1 if added, 0 if already exists, -1 if failed
     */
//    public int addSessionIfNotExist(ChatSession chatSession) {
//        try {
//            for (var chat : chatSessionMockData) {
//                if (chat.getUser().getId() == chatSession.getUser().getId() && chat.getShipper().getId() == chatSession.getShipper().getId())
//                    return 0;
//            }
//            chatSessionMockData.add(chatSession);
//            return 1;
//        } catch (Exception e) {
//            return -1;
//        }
//    }


    // enum for logic only
    public enum ChatSessionStateSelector {
        /**
         * Select only active chat sessions
         */
        ACTIVE(1),
        /**
         * Select only inactive chat sessions
         */
        READONLY(2),
        /**
         * Select all chat sessions
         */
        BOTH(3);

        public final int state;
        ChatSessionStateSelector(int state) {
            this.state = state;
        }
    }

//    public ChatSession getChatSession(User user, User shipper) {
//        try {
//            for (var chat : chatSessionMockData) {
//                if (chat.getUser().getId() == user.getId() && chat.getShipper().getId() == shipper.getId())
//                    return chat;
//            }
//            return null;
//        } catch (Exception e) {
//            return null;
//        }
//    }

}
