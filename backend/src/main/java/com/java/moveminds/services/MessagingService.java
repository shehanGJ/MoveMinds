package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.dto.ConversationDTO;
import com.java.moveminds.dto.MessageDTO;

import java.security.Principal;
import java.util.List;

@Service
public interface MessagingService {
    MessageDTO createMessage(Principal principal, MessageDTO messageDTO);
    List<ConversationDTO> getConversations(Principal principal);
    List<MessageDTO> getMessagesForConversation(Principal principal, Integer conversationUserId);
}
