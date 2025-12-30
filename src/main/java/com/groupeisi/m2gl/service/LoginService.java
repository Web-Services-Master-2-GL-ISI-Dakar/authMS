package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.LoginRequestDTO;
import com.groupeisi.m2gl.service.dto.LoginResponseDTO;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;

    public LoginService(UtilisateurAuthRepository utilisateurAuthRepository) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        LoginResponseDTO response = new LoginResponseDTO();

        Optional<UtilisateurAuth> optUser = utilisateurAuthRepository.findByNumeroTelephone(dto.getNumeroTelephone());
        if (optUser.isEmpty()) {
            response.setSucces(false);
            response.setMessage("Utilisateur inconnu");
            return response;
        }

        UtilisateurAuth utilisateur = optUser.get();
        if (!utilisateur.getPin().equals(dto.getPin())) {
            response.setSucces(false);
            response.setMessage("PIN incorrect");
            return response;
        }

        response.setSucces(true);
        response.setMessage("Connexion réussie !");
        response.setKeycloakId(utilisateur.getKeycloakId());

        return response;
    }
}
