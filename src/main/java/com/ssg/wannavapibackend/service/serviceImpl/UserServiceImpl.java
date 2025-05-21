package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(String nickName, String profile, String email) {
        Optional<User> existingUser = userRepository.findUserByEmail(email);

        if (existingUser.isPresent())
            return existingUser.get();

        String chatbotCode = UUID.randomUUID().toString();

        User user = User.builder()
                .id(null)
                .username(nickName)
                .profile(profile)
                .email(email)
                .chatbotCode(chatbotCode)
                .code(uniqueCode())
                .point(0L)
                .consent(true)
                .unregistered(false)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
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
