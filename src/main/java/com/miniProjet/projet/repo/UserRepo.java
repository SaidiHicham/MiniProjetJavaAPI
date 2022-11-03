package com.miniProjet.projet.repo;

import com.miniProjet.projet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
    User findUserByEmail(String email);
}
