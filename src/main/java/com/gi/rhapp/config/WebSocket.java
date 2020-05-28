package com.gi.rhapp.config;

import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.utilities.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

//@Configuration
//@EnableWebSocketMessageBroker
//@Order(99)
@Log4j2
public class WebSocket implements WebSocketMessageBrokerConfigurer {

    @Value("${web.app.uri}")
    private String WEB_URI;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/notifications");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.info("WS interceptor");
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    log.info("X-Authorization: {}", authorization);

                    String accessToken = authorization.get(0).split(" ")[1];
                    String username = jwtTokenUtil.getUsernameFromToken(accessToken);

                    if (username != null) {
                    log.info("WS user: {}", username);
                        UserDetails userDetails = authService.loadUserByUsername(username);
                        // if token is valid configure Spring Security to manually set authentication
                        if (jwtTokenUtil.validateToken(accessToken, userDetails)) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                            log.info("Token is valid");
//                            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

// After setting the Authentication in the context, we specify that the current user is authenticated.
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                            accessor.setUser(SecurityContextHolder.getContext().getAuthentication());
                        }
                    }
                }

                return message;

            }

        });

    }
}
