package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.java.moveminds.models.entities.MessageEntity;

import java.util.List;

@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity, Integer> {
    List<MessageEntity> findAllBySenderIdOrRecipientId(Integer senderId, Integer recipientId);


    /**
     * Retrieves a list of messages exchanged between two users, ordered by the time they were sent.
     *
     * @param userId             the ID of the first user
     * @param conversationUserId the ID of the second user
     * @return a list of MessageEntity objects representing the messages exchanged between the two users
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(m.sender.id = :userId AND m.recipient.id = :conversationUserId) OR " +
            "(m.sender.id = :conversationUserId AND m.recipient.id = :userId) " +
            "ORDER BY m.sentAt ASC")
    List<MessageEntity> findMessagesBetweenUsers(@Param("userId") Integer userId, @Param("conversationUserId") Integer conversationUserId);

}
