package com.ssg.wannavapibackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoResponseDTO {

    private Long id;
    private String email;
    private String nickName;

    public Map<String, Object> getDataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("email", email);
        map.put("nickName", nickName);
        return map;
    }
}
