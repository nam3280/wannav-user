package com.ssg.wannavapibackend.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OAuthProviderDTO {
    String nickName;
    String email;
    String profile;
}
