package com.example.chat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.chat.chat.ChatMessage;
import com.example.chat.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);


    public WebSocketEventListener() {
        this.messageTemplate = null;
    }


    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ){

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null){
            logger.info("User disconnected: {}" ,username);


            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(username);

            messageTemplate.convertAndSend("/topic/public",chatMessage);
        }
    }
}
