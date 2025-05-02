package com.ssg.wannavapibackend.dto.request;

import com.ssg.wannavapibackend.domain.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageUpdateDTO {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "2자 이상 10자 이하만 가능합니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣]+$", message = "한글만 가능합니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 5, message = "2자 이상 5자 이하만 가능합니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]+$", message = "한글, 영어, 숫자만 가능합니다.")
    private String username;

    private Address address;
}
