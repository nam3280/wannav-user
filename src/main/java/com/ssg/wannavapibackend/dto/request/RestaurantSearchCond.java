package com.ssg.wannavapibackend.dto.request;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RestaurantSearchCond {

  /**
   * where 동적 조건
   */
  //별점 순 , 좋아요 순 이건 정렬 정보이니 뭐 상관 없지 ㅇㅇ
  private Integer startPrice; //조건 : 이거보다 크고 roe
  private Integer endPrice; //조건 : 이거보다 작음 loe
  private Boolean isOpen = false; // 조건 : eq
  private Boolean canPark = false; // 조건 : eq
  private String roadAddress;

  private List<Integer> rates = new ArrayList<>(); //별점
  private Set<String> containFoodTypes = new HashSet<>(); //여러 포함음식 체크박스 중 하나 설계
  private Set<String> restaurantTypes = new HashSet<>(); // 조건 : forEach로 돌려가면서 ₩일치하는지
  private Set<String> provideServiceTypes = new HashSet<>();
  private Set<String> moodTypes = new HashSet<>();

  /**
   * orderBy 동적 조건 , 체크박스 동적 조건으로 갈라면 컬렉션으로 묶어야됨 ㅇㅇ
   */
  private List<String> sortConditions = new ArrayList<>();



}
