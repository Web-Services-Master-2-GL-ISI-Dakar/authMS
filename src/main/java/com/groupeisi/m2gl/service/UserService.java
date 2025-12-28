package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.AdminUserDTO;
import com.groupeisi.m2gl.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 👈 INDISPENSABLE
@Transactional
public interface UserService {
    Page<UserDTO> getAllPublicUsers(Pageable pageable);

    // ✅ MANQUANT — REQUIS PAR JHIPSTER
    AdminUserDTO getUserFromAuthentication(Authentication authentication);
}
