package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.User;

public interface UserService {
    User createUser(String nickName, String profile, String email);
}
