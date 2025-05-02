package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.common.*;
import com.ssg.wannavapibackend.domain.BusinessDay;
import com.ssg.wannavapibackend.domain.Food;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.domain.Review;
import com.ssg.wannavapibackend.dto.request.*;
import com.ssg.wannavapibackend.service.FileService;
import com.ssg.wannavapibackend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RestaurantController {

  @Value("${restaurant.image.dir}")
  private String restaurantDir;

  @Value("${food.image.dir}")
  private String foodDir;

  private final RestaurantService restaurantService;
  private final FileService fileService;


  @ModelAttribute("containFoodTypes")
  public ContainFoodType[] containFoodTypes() {
    return ContainFoodType.values();
  }

  @ModelAttribute("provideServiceTypes")
  public ProvideServiceType[] provideServiceTypes() {
    return ProvideServiceType.values();
  }

  @ModelAttribute("restaurantTypes")
  public RestaurantType[] restaurantTypes() {
    return RestaurantType.values();
  }

  @ModelAttribute("moodTypes")
  public MoodType[] moodTypes() {
    return MoodType.values();
  }


  @ModelAttribute("sortConditions")
  public Map<String, String> sortConditions() {
    Map<String, String> sortConditions = new HashMap<>();
    sortConditions.put("NEW", "최신 순");
    sortConditions.put("RATE", "평점 순");
    sortConditions.put("LIKE", "좋아요 순");
    sortConditions.put("REVIEW", "리뷰 순");
    return sortConditions;
  }


  @ModelAttribute("reservationTimeGaps")
  public ReservationTimeGap[] reservationGaps() {
    return ReservationTimeGap.values();
  }


  @ModelAttribute("dayOfWeeks")
  public List<String> dayOfWeeks() {
    List<String> dayOfWeeks = new ArrayList<>();
    dayOfWeeks.add("월요일");
    dayOfWeeks.add("화요일");
    dayOfWeeks.add("수요일");
    dayOfWeeks.add("목요일");
    dayOfWeeks.add("금요일");
    dayOfWeeks.add("토요일");
    dayOfWeeks.add("일요일");
    return dayOfWeeks;
  }


  @ModelAttribute("adminSortConditions")
  public Map<String , String> adminSortConditions(){
    Map<String, String> adminSortConditions = new HashMap<>();
    adminSortConditions.put("NEW", "최신 순");
    adminSortConditions.put("REGISTER", "등록 순");
    return adminSortConditions;
  }


  /*@ModelAttribute("regionsSeoul")
  public List<String> regions(){
    List<String> regions = new ArrayList<>();
    regions.add("경기 수원시");

  }*/


  //restaurant
  @GetMapping("restaurants")
  public String getRestaurants(@ModelAttribute("restaurantSearchCond") RestaurantSearchCond restaurantSearchCond,
                               @RequestParam(value = "search" , required = false) String search,
                               Model model) {
    List<Restaurant> restaurants = restaurantService.findRestaurants(restaurantSearchCond, search);
    System.out.println("restaurants = " + restaurants);
    model.addAttribute("restaurants", restaurants);
    return "restaurant/restaurants";
  }

  @GetMapping("restaurants/{id}")
  public String getRestaurant(@PathVariable Long id, Model model) {
    System.out.println("id = " + id);
    Restaurant restaurant = restaurantService.findOne(id);
    String[] restaurantImages = restaurant.getRestaurantImages();
    for (String restaurantImage : restaurantImages) {
      System.out.println("restaurantImage = " + restaurantImage);
    }
    model.addAttribute("restaurant", restaurant);
    model.addAttribute("todayBusinessDay", restaurantService.findToday(restaurant));
    List<Restaurant> similarRestaurants = restaurantService.findSimilarRestaurants(id);
    model.addAttribute("similarRestaurants", similarRestaurants);
    model.addAttribute("reviewsByRating", getReviewsByRating(id));

    return "restaurant/restaurant";
  }

  private Map<Integer, List<Review>> getReviewsByRating(Long id) {
    List<Review> reviewsByOneRating = restaurantService.findReviewsByRating(id, 1);
    List<Review> reviewsByTwoRating = restaurantService.findReviewsByRating(id, 2);
    List<Review> reviewsByThreeRating = restaurantService.findReviewsByRating(id, 3);
    List<Review> reviewsByFourRating = restaurantService.findReviewsByRating(id, 4);
    List<Review> reviewsByFiveRating = restaurantService.findReviewsByRating(id, 5);
    Map<Integer, List<Review>> reviewsByRating = new HashMap<>();
    reviewsByRating.put(1, reviewsByOneRating);
    reviewsByRating.put(2, reviewsByTwoRating);
    reviewsByRating.put(3, reviewsByThreeRating);
    reviewsByRating.put(4, reviewsByFourRating);
    reviewsByRating.put(5, reviewsByFiveRating);
    return reviewsByRating;
  }

  //UrlResource 자체가 필요 없음 , 어차피 Url직접 웹에서 링크로 조회해서 띄우는 것임 ㅇㅇ 내 서버로 들어와서 DB에 접근해서 띄우는 게 아닌 !

}
