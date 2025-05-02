package com.ssg.wannavapibackend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FoodSaveDTO {

  private String name;
  private Integer price;
  private MultipartFile foodImage; //음식 사진 폼에서 꺼내기
  private String foodImageUrl; //음식 스토리지에 저장 후 URL을 DB에 저장용

}
