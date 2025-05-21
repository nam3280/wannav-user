package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.config.KakaoConfig;
import com.ssg.wannavapibackend.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LoginServiceImpl implements LoginService {

    private final KakaoConfig kakaoConfig;

    // 카카오 로그인 URL 반환
    public String getKakaoLogin() {
        return kakaoConfig.getAuthUri() + "/oauth/authorize"
                + "?client_id=" + kakaoConfig.getClientId()
                + "&redirect_uri=" + kakaoConfig.getRedirectUri()
                + "&response_type=code";
    }
}
