package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.repository.UserRepository;
import com.groupeisi.m2gl.service.UserService;
import com.groupeisi.m2gl.service.dto.AdminUserDTO;
import com.groupeisi.m2gl.service.dto.UserDTO;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    @Override
    public AdminUserDTO getUserFromAuthentication(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return null;
        }

        OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();

        Map<String, Object> attributes = oauth2User.getAttributes();

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(attributes.getOrDefault("preferred_username", attributes.get("sub")).toString().toLowerCase());
        userDTO.setFirstName((String) attributes.get("given_name"));
        userDTO.setLastName((String) attributes.get("family_name"));
        userDTO.setEmail((String) attributes.get("email"));
        userDTO.setActivated(true);
        userDTO.setLangKey("fr");

        return userDTO;
    }
}
