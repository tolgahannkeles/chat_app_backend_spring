package com.tolgahan.chat_app.security;

import com.tolgahan.chat_app.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;
    private JwtUserDetailService jwtUserDetailService;
    private final TokenBlackListService tokenBlackListService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JwtUserDetailService jwtUserDetailService, TokenBlackListService tokenBlackListService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtUserDetailService = jwtUserDetailService;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (token != null && jwtTokenProvider.validateToken(token) && !tokenBlackListService.isTokenInBlacklist(token) ) {
            System.out.println(token);

            UUID id = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails userDetails = jwtUserDetailService.loadUserById(id);
            System.out.println(userDetails);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}