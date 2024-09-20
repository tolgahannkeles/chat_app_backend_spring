package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.request.LoginRequest;
import com.tolgahan.chat_app.request.RefreshTokenRequest;
import com.tolgahan.chat_app.request.RegisterRequest;
import com.tolgahan.chat_app.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public interface IAuthController {
    @PostMapping("/login")
    AuthResponse login(@RequestBody @Valid LoginRequest loginRequest);

    @PostMapping("/register")
    AuthResponse register(@RequestBody @Valid RegisterRequest registerRequest);

    @PostMapping("/refresh")
    AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest refreshRequest);

    @GetMapping("/logout")
    String logout(HttpServletRequest request);

    @GetMapping("/isTokenValid")
    Boolean isTokenValid();

}
