package com.ssg.wannavapibackend.repository;


import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.RestaurantAdminSearchCond;
import com.ssg.wannavapibackend.dto.request.RestaurantSearchCond;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Long save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    List<Restaurant> findAll(RestaurantSearchCond restaurantSearchCond , String search);


    List<Restaurant> findSimilarRestaurantsAll(RestaurantSearchCond restaurantSearchCond);

    List<Restaurant> findAllAdmin(RestaurantAdminSearchCond restaurantAdminSearchCond);



}