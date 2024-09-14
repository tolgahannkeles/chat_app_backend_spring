package com.tolgahan.chat_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tolgahan.chat_app.model.RefreshToken;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.RoleRepository;
import com.tolgahan.chat_app.request.LoginRequest;
import com.tolgahan.chat_app.request.RefreshTokenRequest;
import com.tolgahan.chat_app.request.RegisterRequest;
import com.tolgahan.chat_app.response.AuthResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.EmailService;
import com.tolgahan.chat_app.service.RefreshTokenService;
import com.tolgahan.chat_app.service.TokenBlackListService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import com.tolgahan.chat_app.validation.RegistrationValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, TokenBlackListService tokenBlackListService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlackListService = tokenBlackListService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            logger.error("Invalid login request");
            return ResponseCreator.badRequest("Please provide username and password");
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
            return ResponseCreator.ok(response);
        } catch (Exception e) {
            logger.error("Error logging in: {}", e.getMessage());
            return ResponseCreator.badRequest(e.toString());
        }


    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {

        if (!RegistrationValidator.isUserValid(registerRequest)) {
            logger.error("Invalid user data");
            return ResponseCreator.badRequest("Invalid user data.");
        }
        if (userService.getUserByUsername(registerRequest.getUsername()) != null) {
            logger.error("Username already in use");
            return ResponseCreator.badRequest("Username already in use.");
        }
        if (emailService.getUserByEmail(registerRequest.getEmail()) != null) {
            logger.error("Email already in use");
            return ResponseCreator.badRequest("Email already in use.");
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
            return ResponseCreator.ok(response);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseCreator.internalServerError(e.toString());
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RefreshTokenRequest refreshRequest) {
        if (refreshRequest == null || refreshRequest.getUserId() == null || refreshRequest.getRefreshToken() == null) {
            logger.error("Invalid refresh request");
            return ResponseCreator.badRequest("Please provide user id and refresh token");
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
                return ResponseCreator.ok(response);
            } else {
                logger.error("Invalid refresh token");
                return ResponseCreator.unauthorized("Invalid refresh token");
            }
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage());
            return ResponseCreator.internalServerError(e.toString());
        }


    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        try {
            User user = getCurrentUser();
            logger.info("Logging out user: {}", user.getId());
            RefreshToken refreshToken = refreshTokenService.getByUserId(user.getId());
            String token = request.getHeader("Authorization").substring(7);
            if (refreshToken != null) {
                tokenBlackListService.add(token);
                refreshTokenService.deleteRefreshToken(refreshToken);
                return ResponseCreator.ok("Logout successful");
            } else {
                logger.error("Invalid refresh token");
                return ResponseCreator.unauthorized("Invalid refresh token");
            }
        } catch (Exception e) {
            logger.error("Error logging out: {}", e.getMessage());
            return ResponseCreator.internalServerError(e.toString());
        }

    }

    @GetMapping("/isTokenValid")
    public ResponseEntity<String> isTokenValid() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context.getAuthentication() == null) {
                return ResponseCreator.ok(false);
            }
            return ResponseCreator.ok(true);
        } catch (Exception e) {
            logger.error("Error checking token validity: {}", e.getMessage());
            return ResponseCreator.internalServerError(e.toString());
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Invalid request body: {}", ex.getMessage());
        return ResponseCreator.badRequest("Invalid request body: " + ex.getMessage());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }


}
