package com.ssg.wannavapibackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSaveDTO {

    @NotNull(message = "평점을 선택해주세요.")
    private Integer rating;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 10, max = 500, message = "10자 이상 500자 이하만 가능합니다.")
    private String content;

    @Size(min = 1, max = 3, message = "사진은 1장 이상 3장 이하만 가능합니다.")
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    @Size(min = 1, max = 5, message = "태그를 1개 이상 5개 이하로 선택해주세요.")
    @Builder.Default
    private List<String> tagNames = new ArrayList<>();

    private String restaurant;

    private String visitDate;
}
