package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.ChatSession;
import com.delix.deliveryou.spring.pojo.ChatSessionId;
import com.delix.deliveryou.spring.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatSession, ChatSessionId>{
    @Query("select cs from ChatSession cs where cs.chatSessionId.user.id = :userId and cs.chatSessionId.shipper.id = :shipperId")
    ChatSession getChatSession(@Param("userId") long userId, @Param("shipperId") long shipperId);

    @Query("select case when exists (select s from ChatSession s where s.chatSessionId.user.id = :userId and s.chatSessionId.shipper.id = :shipperId) then true else false end")
    boolean sessionExists(@Param("userId") long userId, @Param("shipperId") long shipperId);
}
