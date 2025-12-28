package com.groupeisi.m2gl.service.dto;

import com.groupeisi.m2gl.domain.User;
import java.io.Serializable;
import java.util.Objects;

public class UserDTO implements Serializable {

    private String id;
    private String login;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(login, userDTO.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    @Override
    public String toString() {
        return "UserDTO{" + "id='" + id + '\'' + ", login='" + login + '\'' + '}';
    }
}
