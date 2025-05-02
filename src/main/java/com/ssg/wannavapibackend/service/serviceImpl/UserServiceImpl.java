package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(String nickName, String profileImage, String email) {
        String chatbotCode = UUID.randomUUID().toString();
        return User.builder().
                    username(nickName).
                    profile(profileImage).
                    email(email).
                    chatbotCode(chatbotCode).
                    code(uniqueCode()).
                    point(0L).
                    consent(true).
                    unregistered(false).
                    createdAt(LocalDateTime.now()).
                    build();
    }

    @Override
    @Transactional(readOnly = true)
    public KakaoResponseDTO getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid ID value: " + userId));
        return new KakaoResponseDTO(user.getId(), user.getEmail(), user.getUsername());
    }

    private String uniqueCode() {
        Random random = new Random();
        String code;
        do {
            int randomNumber = 100000 + random.nextInt(900000);
            code = String.valueOf(randomNumber);
        } while (userRepository.existsByCode(code));
        return code;
    }
}
