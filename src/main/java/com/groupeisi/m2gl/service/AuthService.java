package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.LoginDTO;
import com.groupeisi.m2gl.service.dto.LoginResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;

    public AuthService(UtilisateurAuthRepository utilisateurAuthRepository) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
    }

    public LoginResponseDTO login(LoginDTO dto) {
        LoginResponseDTO response = new LoginResponseDTO();

        UtilisateurAuth user = utilisateurAuthRepository.findByNumeroTelephone(dto.getNumeroTelephone()).orElse(null);

        if (user == null) {
            response.setSucces(false);
            response.setMessage("Utilisateur inexistant. Veuillez vous inscrire.");
            return response;
        }

        if (!user.getPin().equals(dto.getPin())) {
            response.setSucces(false);
            response.setMessage("PIN incorrect.");
            return response;
        }

        response.setSucces(true);
        response.setMessage("Connexion réussie !");
        response.setKeycloakId(user.getKeycloakId());
        return response;
    }
}
