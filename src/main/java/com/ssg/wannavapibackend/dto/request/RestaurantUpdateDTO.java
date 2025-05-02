package com.ssg.wannavapibackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantUpdateDTO {
  private Long id;
  private String restaurantName;
  private String businessNum;
  private String contact;
  private String description;
  private Set<String> restaurantTypes = new HashSet<>();
  private Set<String> containFoodTypes = new HashSet<>();
  private Set<String> provideServiceTypes = new HashSet<>();
  private Set<String> moodTypes = new HashSet<>();
  private String roadNameAddress;
  private String landLotAddress;
  private String zipcode;
  private String detailAddress;
  private Boolean canPark;
  private String reservationTimeGap;
  private Boolean isPenalty;
  private List<MultipartFile> restaurantImages = new ArrayList<>(); // 식당 사진 폼에서 꺼내기
  private List<String> restaurantImagesUrl = new ArrayList<>(); //식당 스토리지에 저장 후 URL을 DB에 저장용
  /**
   * BusinessDay DTO
   */
  private List<String> dayOfWeeks = new ArrayList<>();
  private List<LocalTime> openTimes = new ArrayList<>();
  private List<LocalTime> closeTimes = new ArrayList<>();
  private List<LocalTime> breakStartTimes = new ArrayList<>();
  private List<LocalTime> breakEndTimes = new ArrayList<>();
  private List<LocalTime> lastOrders = new ArrayList<>();
  private List<String> isDayOffList = new ArrayList<>();

  /**
   * Food DTO
   */
  private List<FoodUpdateDTO> foodUpdateDtoList = new ArrayList<>();


}
