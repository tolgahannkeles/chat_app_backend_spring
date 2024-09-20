package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.controller.interfaces.IAuthController;
import com.tolgahan.chat_app.exceptions.AlreadyDefinedException;
import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.InvalidArgumentException;
import com.tolgahan.chat_app.model.RefreshToken;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.LoginRequest;
import com.tolgahan.chat_app.request.RefreshTokenRequest;
import com.tolgahan.chat_app.request.RegisterRequest;
import com.tolgahan.chat_app.response.AuthResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.interfaces.IEmailService;
import com.tolgahan.chat_app.service.interfaces.IRefreshTokenService;
import com.tolgahan.chat_app.service.interfaces.ITokenBlackListService;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AuthController implements IAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;
    private final ITokenBlackListService tokenBlackListService;
    private final IEmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthenticationManager authenticationManager,
                          IUserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          IRefreshTokenService refreshTokenService,
                          ITokenBlackListService tokenBlackListService,
                          IEmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlackListService = tokenBlackListService;
        this.emailService = emailService;
    }


    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            logger.error("Invalid login request");
            throw new InsufficientAuthenticationException("Please provide username and password");
        }

        try {
            logger.info("Logging in user: {}", loginRequest.getUsername());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwtToken = jwtTokenProvider.generateToken(auth);

            User user = userService.getUserByUsername(loginRequest.getUsername());

            AuthResponse response = new AuthResponse();
            response.setUserId(user.getId());
            response.setAccessToken("Bearer " + jwtToken);
            response.setRefreshToken(refreshTokenService.generateRefreshToken(user));
            return response;
        } catch (Exception e) {
            logger.error("Error logging in: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userService.getUserByUsername(registerRequest.getUsername()) != null) {
            logger.error("Username already in use");
            throw new AlreadyDefinedException("Username already in use.");
        }
        if (emailService.getUserByEmail(registerRequest.getEmail()) != null) {
            logger.error("Email already in use");
            throw new AlreadyDefinedException("Email already in use.");
        }

        try {
            logger.info("Registering user: {}", registerRequest.getUsername());
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            userService.saveUser(user, registerRequest.getEmail());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwtToken = jwtTokenProvider.generateToken(auth);


            AuthResponse response = new AuthResponse();
            response.setUserId(user.getId());
            response.setAccessToken("Bearer " + jwtToken);
            response.setRefreshToken(refreshTokenService.generateRefreshToken(user));
            return response;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    @Override
    public AuthResponse refresh(RefreshTokenRequest refreshRequest) {
        if (refreshRequest == null || refreshRequest.getUserId() == null || refreshRequest.getRefreshToken() == null) {
            logger.error("Invalid refresh request");
            throw new InvalidArgumentException("Please provide user userId and refresh token");
        }
        logger.info("Refreshing token for user: {}", refreshRequest.getUserId());
        try {
            AuthResponse response = new AuthResponse();
            RefreshToken token = refreshTokenService.getByUserId(refreshRequest.getUserId());
            if (token.getToken().equals(refreshRequest.getRefreshToken()) &&
                    !refreshTokenService.isRefreshTokenExpired(token)) {

                User user = token.getUser();
                String jwtToken = jwtTokenProvider.generateJwtTokenByUserId(user.getId());
                response.setAccessToken("Bearer " + jwtToken);
                response.setRefreshToken(token.getToken());
                response.setUserId(user.getId());
                return response;
            } else {
                logger.error("Invalid refresh token");
                throw new InvalidArgumentException("Invalid refresh token");
            }
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

    }


    @Override
    public String logout(HttpServletRequest request) {
        try {
            User user = getCurrentUser();
            logger.info("Logging out user: {}", user.getUsername());
            RefreshToken refreshToken = refreshTokenService.getByUserId(user.getId());
            String token = request.getHeader("Authorization").substring(7);
            if (refreshToken != null) {
                tokenBlackListService.add(token);
                refreshTokenService.deleteRefreshToken(refreshToken);
                return "Logout successful";
            } else {
                logger.error("Refresh token not found");
                throw new InvalidArgumentException("Refresh token not found");
            }
        } catch (Exception e) {
            logger.error("Error logging out: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    @Override
    public Boolean isTokenValid() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            return context.getAuthentication() != null;
        } catch (Exception e) {
            logger.error("Error checking token validity: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null;
    }


}
