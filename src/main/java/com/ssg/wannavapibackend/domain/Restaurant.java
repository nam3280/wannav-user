package com.ssg.wannavapibackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssg.wannavapibackend.common.BusinessStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"foods", "businessDays", "reviews", "likes", "seats"})
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "business_num")
  private String businessNum;

  private String contact; //연락처

  @Transient
  @NumberFormat(pattern = "#,###.#")
  private Double averageRating;

  @Transient
  @NumberFormat(pattern = "#,###")
  private Integer reviewCount;

  @Transient
  @NumberFormat(pattern = "#,###")
  private Integer likesCount;

  @Transient
  private String[] restaurantImages;


  @Transient
  @NumberFormat(pattern = "#,###원")
  private Double averageFoodsPrice;


  @Embedded
  private Address address;
  private String image;

  private String description; //설명

  @OneToMany(mappedBy = "restaurant")
  @JsonIgnore
  private List<Seat> seats = new ArrayList<>();

  @Column(name = "created_at")
  @DateTimeFormat(pattern = "yyyy-mm-dd")
  private LocalDate createdAt; //생성일

  @Column(name = "updated_at")
  @DateTimeFormat(pattern = "yyyy-mm-dd")
  private LocalDate updatedAt; //수정일

  @Column(name = "reservation_time_gap")
  private Integer reservationTimeGap;

  @Column(name = "is_penalty")
  private Boolean isPenalty;

  @Enumerated(EnumType.STRING)
  @Column(name = "business_status")
  private BusinessStatus businessStatus; //영업 상태 : 영업 중 , 영업 종료 , 브레이크타임

  @Column(name = "can_park")
  private Boolean canPark; //주차 가능 여부

  @OneToMany(mappedBy = "restaurant")
  @JsonIgnore
  private List<Review> reviews = new ArrayList<>(); //해당 식당에서 작성한 사용자들의 리뷰를 담을  것임


  //오직 Restaurant 부모에게만 Food는 의존되므로 cascade , orphanRemoval 걸었음 , cascade , orphanRemoval 특징 : 리포지토리 없어도 됨 즉 em.perist(BusinessDay)하지 않아도 연쇄적으로 알아서 저장됨 , 생명주기가 전부 rESTAURANT에 의존되었기 때문!
  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<BusinessDay> businessDays = new ArrayList<>();


  //오직 Restaurant 부모에게만 Food는 의존되므로 cascade , orphanRemoval 걸었음 ,
  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Food> foods = new ArrayList<>();


  @OneToMany(mappedBy = "restaurant")
  @JsonIgnore
  private List<Likes> likes = new ArrayList<>();


  /**
   * 체크박스 , 동적 검색조건 데이터 , 변경할 일 없으므로 @ElementCollection 정의
   */
  //여러 포함 음식 종류들(유제품 , 계란 , ...) ContaintFoodType ,조회 : 지연로딩 , 필요한 시점에 조회되게 저장 시 cascade로 연달아 저장됨 ㅇㅇ , 즉 알바없음 !
  // 기본적으로 cascade , orphanRemoval 걸려있음

  @ElementCollection
  @CollectionTable(name = "ContainFoodType", joinColumns = @JoinColumn(name = "restaurant_id"))
  @Column(name = "contain_food_type")
  private Set<String> containFoodTypes = new HashSet<>();


  //여러 제공하는 서비스 종류들(단체석 이용 가능 , 무선 와이파이 존재 , 콜키지 가능 , ...) ProvideServiceType
  @ElementCollection
  @CollectionTable(name = "ProvideServiceType", joinColumns = @JoinColumn(name = "restaurant_id"))
  @Column(name = "provide_service_type")
  private Set<String> provideServiceTypes = new HashSet<>();// enum


  //주로 파는 품목 카테고리(추후 단일 객체 고려)RestaurantType
  @ElementCollection
  @CollectionTable(name = "RestaurantType", joinColumns = @JoinColumn(name = "restaurant_id"))
  @Column(name = "restaurant_type")
  private Set<String> restaurantTypes = new HashSet<>();


  @ElementCollection
  @CollectionTable(name = "MoodType", joinColumns = @JoinColumn(name = "restaurant_id"))
  @Column(name = "mood_type")
  private Set<String> moodTypes = new HashSet<>();

  /**
   * 복잡한 연관관계 => 생성 메서드 , 다른 개발자들이 해당 틀대로 생성하게끔 유도하기 !
   */



  public static Restaurant createRestaurant(String businessNum, String restaurantName, String contact,String description,
      Set<String> moodTypes,
      Set<String> containFoodTypes, Set<String> provideServiceTypes, Set<String> restaurantTypes,
      String image, String roadNameAddress
      , String landLotAddress, String zipcode, String detailAddress, Boolean canPark,
      int reservationTimeGap
      , Boolean isPenalty, List<BusinessDay> businessDays, List<Food> foods) {

    Restaurant restaurant = new Restaurant();
    restaurant.setContact(contact);
    restaurant.setDescription(description);
    restaurant.setBusinessNum(businessNum);
    restaurant.setName(restaurantName);
    restaurant.setMoodTypes(moodTypes);
    restaurant.setContainFoodTypes(containFoodTypes);
    restaurant.setProvideServiceTypes(provideServiceTypes);
    restaurant.setRestaurantTypes(restaurantTypes);
    restaurant.setImage(image);
    restaurant.setAddress(new Address(roadNameAddress, landLotAddress, detailAddress, zipcode));
    restaurant.setCanPark(canPark);
    restaurant.setReservationTimeGap(reservationTimeGap);
    restaurant.setIsPenalty(isPenalty);
    restaurant.setCreatedAt(LocalDate.now());
    restaurant.setUpdatedAt(LocalDate.now());
    businessDays.forEach(restaurant::addBusinessDay);
    foods.forEach(restaurant::addFood);
    return restaurant;

  }

  /**
   * 연관관계 편의 메서드
   */
  //수정 발생 시 여기서 작업해줘도 될듯? ① 리스트 전부 삭제하고 ② 그 다음 add 하기 => 영소성 컨텍스트 초기화하고 하
  public void addBusinessDay(BusinessDay businessDay) {
    businessDays.add(businessDay); //자신에게 연관관계 설정
    businessDay.setRestaurant(this); //B에게 연관관계 설정
  }

  public void addFood(Food food) {
    foods.add(food); //자신에게 연관관계 설정
    food.setRestaurant(this); //B에게 연관관계 설정
  }


  /**
   * 도메인 모델 패턴 : 비즈니스 로직 정의(서비스가 아닌 도메인에 정의하기) DDD로 하면 단위 테스트에서 객체 생성만으로 테스트도 가능한 유라함도 가져갈 수 있음
   */

  public double averageRate() {
    return reviews.stream().mapToInt(Review::getRating).average().orElse(0); //평균 계산 , 리뷰가 없을 경우 그냥 0 반환 ㅇㅇ , 없으니 0이지 !
  }

  public void addStatistics(double averageRating , int likesCount , int reviewCount){
    this.averageRating = averageRating;
    this.likesCount = likesCount;
    this.reviewCount = reviewCount;
  }

  public int totalReviewCount() {
    return reviews.size();
  }

  public int totalLikesCount(){
    return likes.size();
  }

  public void addRestaurantImages(String[] restaurantImages){
    this.restaurantImages = restaurantImages;
  }

  public void addFoodsPriceAverage(Double averageFoodsPrice){
    this.averageFoodsPrice = averageFoodsPrice;
  }



  //상태 설정 메서드로 가자
  public void changeBusinessStatus(BusinessStatus businessStatus) {
    this.businessStatus = businessStatus;
  }

  //수정 메서드
  public void changeRestaurant(String description , String contact, String businessNum, String restaurantName, Set<String> moodTypes,
      Set<String> containFoodTypes, Set<String> provideServiceTypes, Set<String> restaurantTypes,
      String image, String roadNameAddress, String landLotAddress, String zipcode,
      String detailsAddress, Boolean canPark, int reservationTimeGap,
      Boolean isPenalty, List<BusinessDay> businessDays, List<Food> foods) {

    setBusinessNum(businessNum);
    setName(restaurantName);
    setMoodTypes(moodTypes);
    setContainFoodTypes(containFoodTypes);
    setProvideServiceTypes(provideServiceTypes);
    setRestaurantTypes(restaurantTypes);
    setImage(image);
    setAddress(new Address(roadNameAddress, landLotAddress, detailsAddress, zipcode));
    setCanPark(canPark);
    setReservationTimeGap(reservationTimeGap);
    setIsPenalty(isPenalty);
    businessDays.forEach(this::addBusinessDay);
    foods.forEach(this::addFood);
  }


}
