package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserCreateRequest;
import com.example.myapp.dto.UserResponse;
import com.example.myapp.dto.UserUpdateRequest;
import com.example.myapp.global.exception.DuplicateResourceException;
import com.example.myapp.global.exception.ResourceNotFoundException;
import com.example.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(UserCreateRequest request) {
        userRepository.findByEmail(request.email())
                .ifPresent(u -> { throw new DuplicateResourceException("email already exists"); });

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return UserResponse.from(user);
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        // 같은 이메일로 바꾸는 건 허용, 다른 유저의 이메일이면 금지
        userRepository.findByEmail(request.email())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(u -> { throw new DuplicateResourceException("email already exists"); });

        user.update(request.name(), request.email());
        return UserResponse.from(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("user not found");
        }
        userRepository.deleteById(id);
    }
}