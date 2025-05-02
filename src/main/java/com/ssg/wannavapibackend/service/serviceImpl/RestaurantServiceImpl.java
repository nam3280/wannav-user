package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.common.BusinessStatus;
import com.ssg.wannavapibackend.domain.BusinessDay;
import com.ssg.wannavapibackend.domain.Food;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.domain.Review;
import com.ssg.wannavapibackend.dto.request.RestaurantAdminSearchCond;
import com.ssg.wannavapibackend.dto.request.RestaurantSaveDTO;
import com.ssg.wannavapibackend.dto.request.RestaurantSearchCond;
import com.ssg.wannavapibackend.dto.request.RestaurantUpdateDTO;
import com.ssg.wannavapibackend.repository.RestaurantRepository;
import com.ssg.wannavapibackend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;


  @Transactional
  public Long save(RestaurantSaveDTO restaurantSaveDto) {
    List<BusinessDay> businessDays = BusinessDay.createBusinessDays(
            restaurantSaveDto.getOpenTimes(),
            restaurantSaveDto.getCloseTimes(), restaurantSaveDto.getBreakStartTimes(),
            restaurantSaveDto.getBreakEndTimes(), restaurantSaveDto.getLastOrderTimes(),
            restaurantSaveDto.getIsDayOffList());


    /**
     * 식당 이미지 저장(음식은 그냥 dto에 있던 거 저장하면 됨)
     */
    String storeRestaurantImagesUrl = getStoreRestaurantImagesUrl(restaurantSaveDto.getRestaurantImagesUrl());

    /**
     * 예약 시간 간격 String => Integer 파싱
     */
    int realReservationTimeGap = getRealReservationTimeGap(restaurantSaveDto.getReservationTimeGap());


    List<Food> foods = restaurantSaveDto.getFoodSaveDtoList()
            .stream().map(foodSaveDto -> new Food(foodSaveDto.getName(), foodSaveDto.getFoodImageUrl(), foodSaveDto.getPrice())).toList();

    Restaurant restaurant = Restaurant.createRestaurant(restaurantSaveDto.getBusinessNum(),
            restaurantSaveDto.getRestaurantName(), restaurantSaveDto.getContact(), restaurantSaveDto.getDescription()
            , restaurantSaveDto.getMoodTypes(), restaurantSaveDto.getContainFoodTypes(), restaurantSaveDto.getProvideServiceTypes(),
            restaurantSaveDto.getRestaurantTypes(), storeRestaurantImagesUrl, restaurantSaveDto.getRoadNameAddress(),
            restaurantSaveDto.getLandLotAddress(), restaurantSaveDto.getZipcode(), restaurantSaveDto.getDetailAddress(),
            restaurantSaveDto.getCanPark(), realReservationTimeGap, restaurantSaveDto.getIsPenalty(), businessDays, foods);
    restaurant.changeBusinessStatus(BusinessStatus.OPEN);

    return restaurantRepository.save(restaurant);
  }


  private static int getRealReservationTimeGap(String restaurantSaveDto) {
    String reservationTimeGap = restaurantSaveDto;
    int realReservationTimeGap = 0;
    switch (reservationTimeGap) {
      case "HALF":
        realReservationTimeGap = 30;
        break;
      case "ONE":
        realReservationTimeGap = 60;
        break;
      case "TWO":
        realReservationTimeGap = 120;
        break;
    }
    return realReservationTimeGap;
  }

  private static String getStoreRestaurantImagesUrl(List<String> changeRestaurantsImagesUrl) {
    String storeRestaurantImagesUrl = "";
    List<String> restaurantImagesUrl = changeRestaurantsImagesUrl;
    for (String restaurantImageUrl : restaurantImagesUrl) {
      storeRestaurantImagesUrl = String.join(",", restaurantImageUrl);
    }
    return storeRestaurantImagesUrl;
  }


  public List<Review> findReviewsByRating(Long id , Integer rating){
    return findOne(id).getReviews().stream().filter(review -> review.getRating().equals(rating)).toList();
  }



  public Restaurant findOne(Long id){
    Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
    double avgRating = restaurant.averageRate();
    int likesCount = restaurant.totalLikesCount();
    int reviewCount = restaurant.totalReviewCount();
    String[] splitImages = restaurant.getImage().split(",");
    restaurant.addStatistics(avgRating, likesCount, reviewCount);
    restaurant.addRestaurantImages(splitImages);
    double foodsPriceAverage = Math.round(restaurant.getFoods().stream().mapToInt(Food::getPrice).average().getAsDouble()/1000.0)*1000; //백의 자리에서 반올림
    restaurant.addFoodsPriceAverage(foodsPriceAverage);
    return restaurant;

  }

  public List<Restaurant> findRestaurantsAdmin(RestaurantAdminSearchCond restaurantAdminSearchCond){
    return restaurantRepository.findAllAdmin(restaurantAdminSearchCond);
  }

  public List<Restaurant> findSimilarRestaurants(Long id){
    Restaurant ownerRestaurant = findOne(id);
    RestaurantSearchCond restaurantSearchCond = getRestaurantSearchCond(
        ownerRestaurant);
    List<Restaurant> similarRestaurants = restaurantRepository.findSimilarRestaurantsAll(
        restaurantSearchCond);
    return setRestaurantDefaultInfo(similarRestaurants);
  }



  private static RestaurantSearchCond getRestaurantSearchCond(Restaurant ownerRestaurant) {
    Set<String> restaurantTypes = ownerRestaurant.getRestaurantTypes(); // 유제품 포함 , 계란 포함
    Set<String> containFoodTypes = ownerRestaurant.getContainFoodTypes(); //양식 , 중식 , 한식
    String roadAddress = ownerRestaurant.getAddress().getRoadAddress(); //도로명 주소 , 근처 주소면 좋으니 ! , 서울 강남구 까지 비슷할 경우 ㅇㅇ
    RestaurantSearchCond restaurantSearchCond = new RestaurantSearchCond();
    restaurantSearchCond.setContainFoodTypes(containFoodTypes);
    restaurantSearchCond.setRestaurantTypes(restaurantTypes);
    restaurantSearchCond.setRoadAddress(roadAddress);
    return restaurantSearchCond;
  }

  public List<Restaurant> findRestaurants(RestaurantSearchCond restaurantSearchCond , String search){
    List<Restaurant> restaurants = restaurantRepository.findAll(restaurantSearchCond , search);
    return setRestaurantDefaultInfo(restaurants);
  }


  private List<Restaurant> setRestaurantDefaultInfo(List<Restaurant> similarRestaurants) {
    similarRestaurants.forEach(restaurant -> {
      double avgRating = restaurant.averageRate();
      int likesCount = restaurant.totalLikesCount();
      int reviewCount = restaurant.totalReviewCount();
      restaurant.addStatistics(avgRating, likesCount, reviewCount);
      String[] splitImages = restaurant.getImage().split(",");
      restaurant.addRestaurantImages(splitImages);
    });
    return similarRestaurants;
  }


  public BusinessDay findToday(Restaurant restaurant){
    String todayDayOfWeek = restaurant.getBusinessDays().stream()
        .map(BusinessDay::getDayOfWeek)
        .filter(dayOfWeek -> dayOfWeek.equals(
            LocalDateTime.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN)))
        .findAny().get();
    BusinessDay todayBusinessDay = restaurant.getBusinessDays().stream().filter(businessDay -> businessDay.getDayOfWeek().equals(todayDayOfWeek)).findAny().get();
    return todayBusinessDay;
  }


  //변경 감지 !
  @Transactional
  public void updateRestaurant(RestaurantUpdateDTO restaurantUpdateDto) {
    Long id = restaurantUpdateDto.getId();
    Restaurant updateRestaurant = findOne(id);

    /**
     * 식당 이미지
     */
    List<String> restaurantImagesUrl = restaurantUpdateDto.getRestaurantImagesUrl();
    String storeRestaurantImagesUrl = getStoreRestaurantImagesUrl(restaurantImagesUrl);


      /**
       * String => 숫자
     */
    int realReservationTimeGap = getRealReservationTimeGap(restaurantUpdateDto.getReservationTimeGap());


    List<BusinessDay> businessDays = BusinessDay.createBusinessDays(
            restaurantUpdateDto.getOpenTimes(),
            restaurantUpdateDto.getCloseTimes(), restaurantUpdateDto.getBreakStartTimes(),
            restaurantUpdateDto.getBreakEndTimes(), restaurantUpdateDto.getLastOrders(),
            restaurantUpdateDto.getIsDayOffList());

    List<Food> foods = restaurantUpdateDto.getFoodUpdateDtoList()
            .stream().map(foodUpdateDto -> new Food(foodUpdateDto.getName(), foodUpdateDto.getFoodImageUrl(), foodUpdateDto.getPrice())).toList();


    updateRestaurant.changeRestaurant(restaurantUpdateDto.getDescription() , restaurantUpdateDto.getContact(),
            restaurantUpdateDto.getBusinessNum() , restaurantUpdateDto.getRestaurantName() ,
            restaurantUpdateDto.getMoodTypes() , restaurantUpdateDto.getContainFoodTypes() ,
            restaurantUpdateDto.getProvideServiceTypes() , restaurantUpdateDto.getRestaurantTypes() ,
            storeRestaurantImagesUrl , restaurantUpdateDto.getRoadNameAddress() , restaurantUpdateDto.getLandLotAddress() ,
            restaurantUpdateDto.getZipcode() , restaurantUpdateDto.getDetailAddress() , restaurantUpdateDto.getCanPark() , realReservationTimeGap , restaurantUpdateDto.getIsPenalty(),
            businessDays , foods);

  }

  // 프로세스 : 현재 요일과는 아무 상관없음 , 그냥 전체에 대한 로직임
  @Transactional
  @Scheduled(cron = "0 */30 * * * *")
  public void updateBusinessStatus(){
    LocalDateTime now = LocalDateTime.now();
    List<Restaurant> restaurants = restaurantRepository.findAll(new RestaurantSearchCond() , null);
    //다 계산해주는 거니까 여기서 그냥 모든 상태를 이 메서드 안에서 동시에 업데이트 해주는 것!

    //아니네 휴무일 계산은 마지막에 해줘야하네
    for (Restaurant restaurant : restaurants) {
      List<BusinessDay> businessDays = restaurant.getBusinessDays(); //한 식당에 대한 영업일들(7일)
      for (BusinessDay businessDay : businessDays) {

        LocalTime openTime = businessDay.getOpenTime();
        LocalTime closeTime = businessDay.getCloseTime();
        LocalTime breakStartTime = businessDay.getBreakStartTime();
        LocalTime breakEndTime = businessDay.getBreakEndTime();
        Boolean isDayOff = businessDay.getIsDayOff();

        if (isDayOff.equals(true)){
          restaurant.changeBusinessStatus(BusinessStatus.DAY_OFF);
          return; //휴무일이면 아래로 내려가지 않고 바로 종료되게 , 30분마다 스케줄링해서 휴무일 시 계속해서 리턴됨
        }

        if ((now.toLocalTime().isAfter(openTime) && now.toLocalTime().isBefore(breakStartTime))
            || (now.toLocalTime().isAfter(breakEndTime) && now.toLocalTime().isBefore(closeTime))) {
          restaurant.changeBusinessStatus(BusinessStatus.OPEN);
        }

        else if (now.toLocalTime().isAfter(breakStartTime) && now.toLocalTime().isBefore(breakEndTime)) {
          restaurant.changeBusinessStatus(BusinessStatus.BREAK_TIME);
        }
        else {
          restaurant.changeBusinessStatus(BusinessStatus.CLOSE);
        }
      }
    }
  }




}
