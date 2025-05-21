package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.service.OAuth2UserInfo;
import com.ssg.wannavapibackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuthServiceImpl extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)  throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfoImpl(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            //네이버 구현 예정
        } else {
            System.out.println("지원하지않음.");
        }

        String nickName = oAuth2UserInfo.getProviderInfo().getNickName();
        String profile = oAuth2UserInfo.getProviderInfo().getProfile();
        String email = oAuth2UserInfo.getProviderInfo().getEmail();

        User user = userService.createUser(nickName, profile, email);

        String id = String.valueOf(user.getId());

        return new PrincipalDetails(id, oAuth2User.getAttributes());
    }
}
