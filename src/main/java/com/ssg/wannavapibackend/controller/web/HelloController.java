package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.RestaurantSearchCond;
import com.ssg.wannavapibackend.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HelloController {

  private final RestaurantService restaurantService;


  @RequestMapping
  public String hello(@RequestParam(value = "address", required = false) String address, Model model , HttpServletRequest request) {


    List<Restaurant> currentLocationPopularRestaurants = getPopularRestaurantsByCurrentLocation(address);
    List<Restaurant> popularRestaurants = getPopularRestaurants();
    List<Restaurant> manyReviewsRestaurants = getManyReviewsRestaurants();
    List<Restaurant> highRatingRestaurants = getHighRatingRestaurants();
    List<Restaurant> manyLikesRestaurants = getManyLikesRestaurants();
    List<Restaurant> restaurantsByPriceRange1 = getRestaurantsByPriceRange(10000 , 20000);
    List<Restaurant> restaurantsByPriceRange2 = getRestaurantsByPriceRange(20000 , 30000);

    model.addAttribute("currentLocationPopularRestaurants", currentLocationPopularRestaurants);
    model.addAttribute("popularRestaurants", popularRestaurants);
    model.addAttribute("manyReviewsRestaurants", manyReviewsRestaurants);
    model.addAttribute("highRatingRestaurants", highRatingRestaurants);
    model.addAttribute("manyLikesRestaurants", manyLikesRestaurants);
    model.addAttribute("restaurantsByPriceRange1", restaurantsByPriceRange1);
    model.addAttribute("restaurantsByPriceRange2", restaurantsByPriceRange2);

    return "index";
  }

  /**
   * 현재 위치 기준 인기 식당 , 일단 보류
   */
  private List<Restaurant> getPopularRestaurantsByCurrentLocation(String currentLocationRoadAddress) {
    RestaurantSearchCond currentLocationRestaurantCond = new RestaurantSearchCond();
    currentLocationRestaurantCond.setRoadAddress(currentLocationRoadAddress);
    currentLocationRestaurantCond.setSortConditions(Arrays.asList("LIKE" , "RATE" , "REVIEW"));
    List<Restaurant> popularRestaurants = restaurantService.findRestaurants(currentLocationRestaurantCond, null)
            .stream().limit(8).toList();
    System.out.println("popularRestaurants = " + popularRestaurants);
    return popularRestaurants;
  }

  /**
   * 전국 인기 식당
   */
  private List<Restaurant> getPopularRestaurants() {

    RestaurantSearchCond popularRestaurantCond = new RestaurantSearchCond();
    List<String> popularConditions = Arrays.asList("RATE", "LIKE", "REVIEW");
    popularRestaurantCond.setSortConditions(popularConditions);
    List<Restaurant> popularRestaurants = restaurantService.findRestaurants(popularRestaurantCond, null)
        .stream().limit(8).toList();
    return popularRestaurants;
  }

  /**
   * 좋아요 수 많은 추천 식당
   */
  private List<Restaurant> getManyLikesRestaurants() {
    RestaurantSearchCond manyLikesRestaurantCond = new RestaurantSearchCond();
    List<String> manyLikesConditions = List.of("LIKE");
    manyLikesRestaurantCond.setSortConditions(manyLikesConditions);
    return restaurantService.findRestaurants(manyLikesRestaurantCond , null).stream().limit(8).toList();
  }

  /**
   * 리뷰 수 많은 추천 식당
   */
  private List<Restaurant> getManyReviewsRestaurants() {
    RestaurantSearchCond manyReviewsRestaurantCond = new RestaurantSearchCond();
    List<String> manyReviewsConditions = List.of("REVIEW");
    manyReviewsRestaurantCond.setSortConditions(manyReviewsConditions);
    return restaurantService.findRestaurants(manyReviewsRestaurantCond , null).stream().limit(8).toList();
  }

  /**
   * 평점 높은 추천 식당
   */
  private List<Restaurant> getHighRatingRestaurants() {
    RestaurantSearchCond highRatingRestaurantCond = new RestaurantSearchCond();
    List<String> highRatingConditions = List.of("RATE");
    highRatingRestaurantCond.setSortConditions(highRatingConditions);
    return restaurantService.findRestaurants(highRatingRestaurantCond , null).stream().limit(8).toList();
  }

  /**
   * 가격대 별 추천 식당
   */
  private List<Restaurant> getRestaurantsByPriceRange(Integer startPrice , Integer endPrice) {
    RestaurantSearchCond priceRangeRestaurantCond = new RestaurantSearchCond();
    priceRangeRestaurantCond.setStartPrice(startPrice);
    priceRangeRestaurantCond.setEndPrice(endPrice);
    priceRangeRestaurantCond.setSortConditions(Arrays.asList("RATE" , "LIKE" , "REVIEW"));
    return restaurantService.findRestaurants(priceRangeRestaurantCond , null).stream().limit(8).toList();
  }

}
