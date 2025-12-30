package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.LoginService;
import com.groupeisi.m2gl.service.dto.LoginRequestDTO;
import com.groupeisi.m2gl.service.dto.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = loginService.login(dto);
        return ResponseEntity.ok(response);
    }
}
