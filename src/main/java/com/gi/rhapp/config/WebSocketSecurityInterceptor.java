package com.gi.rhapp.config;

import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.utilities.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private AuthService authService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorization = accessor.getFirstNativeHeader("Authorization");

            log.debug("X-Authorization: {}", authorization);
            String token = authorization.substring(7);
            try {
                UserDetails userDetails = authService.loadUserByUsername(jwt.getUsernameFromToken(token));

                UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

                accessor.setUser(user);

            } catch (ExpiredJwtException e) {
                log.info("JWT is expired: {}", e.getMessage());
            } catch (AccessDeniedException e) {
                log.info("Error connection; {}", e.getMessage());
            }
        }

        return message;
    }
}
