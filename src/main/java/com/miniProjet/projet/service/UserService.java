package com.miniProjet.projet.service;

import com.miniProjet.projet.models.Role;
import com.miniProjet.projet.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    Role saveRole(Role role);
    void assignRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
    public void save(List<User> users);


}
