package com.tolgahan.chat_app.config;

import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.security.JwtUserDetailService;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUserDetailService jwtUserDetailService;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider, JwtUserDetailService jwtUserDetailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtUserDetailService = jwtUserDetailService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/api/messages");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String token = accessor.getFirstNativeHeader("Authorization");

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    Authentication authentication = getAuthentication(token);
                    if (authentication != null) {
                        accessor.setUser(authentication);
                    }
                }

                return message;
            }
        });
    }


    private Authentication getAuthentication(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails userDetails = jwtUserDetailService.loadUserById(userId);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
        System.out.println("Token geçersiz");
        return null; // Token geçersizse null döndür
    }
}
