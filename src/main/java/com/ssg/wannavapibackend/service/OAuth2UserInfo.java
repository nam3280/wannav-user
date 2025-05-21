package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.dto.OAuthProviderDTO;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    OAuthProviderDTO getProviderInfo();
}
