package com.example.myapp.repository;

import com.example.myapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 중복 체크용
    Optional<User> findByEmail(String email);
}
