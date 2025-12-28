package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.UserService;
import com.groupeisi.m2gl.service.dto.UserDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private final UserService userService;

    public PublicUserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(Pageable pageable) {
        Page<UserDTO> page = userService.getAllPublicUsers(pageable);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
