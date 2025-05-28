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

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = switch (registrationId) {
            case "kakao" -> new KakaoUserInfoImpl(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfoImpl(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 공급자입니다.");
        };

        String nickName = oAuth2UserInfo.getProviderInfo().getNickName();
        String profile = oAuth2UserInfo.getProviderInfo().getProfile();
        String email = oAuth2UserInfo.getProviderInfo().getEmail();

        User user = userService.createUser(nickName, profile, email);

        String id = String.valueOf(user.getId());

        return new PrincipalDetails(id, registrationId, oAuth2User.getAttributes());
    }
}
