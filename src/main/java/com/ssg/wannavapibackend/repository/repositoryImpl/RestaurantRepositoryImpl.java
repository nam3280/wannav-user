package com.ssg.wannavapibackend.repository.repositoryImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.common.BusinessStatus;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.RestaurantAdminSearchCond;
import com.ssg.wannavapibackend.dto.request.RestaurantSearchCond;
import com.ssg.wannavapibackend.repository.RestaurantRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

import static com.ssg.wannavapibackend.domain.QFood.food;
import static com.ssg.wannavapibackend.domain.QLikes.likes;
import static com.ssg.wannavapibackend.domain.QRestaurant.restaurant;
import static com.ssg.wannavapibackend.domain.QReview.review;


@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

  private final EntityManager em;
  private final JPAQueryFactory query;



  @Autowired
  public RestaurantRepositoryImpl(EntityManager em){
    this.em=em;
    this.query = new JPAQueryFactory(em);
  }

  public Long save(Restaurant restaurant){
    em.persist(restaurant);
    return restaurant.getId();
  }

  public Optional<Restaurant> findById(Long id){
    return Optional.ofNullable(em.find(Restaurant.class , id));
  }



  public List<Restaurant> findAll(RestaurantSearchCond restaurantSearchCond , String search){

    Boolean canPark = restaurantSearchCond.getCanPark();
    Boolean isOpen = restaurantSearchCond.getIsOpen();
    Integer startPrice = restaurantSearchCond.getStartPrice();
    Integer endPrice = restaurantSearchCond.getEndPrice();
    String roadAddress = restaurantSearchCond.getRoadAddress();
    List<Integer> rates = restaurantSearchCond.getRates();
    Set<String> restaurantTypes = restaurantSearchCond.getRestaurantTypes();
    Set<String> containFoodTypes = restaurantSearchCond.getContainFoodTypes();
    Set<String> provideServiceTypes = restaurantSearchCond.getProvideServiceTypes();
    Set<String> moodTypes = restaurantSearchCond.getMoodTypes();
    List<String> sortConditions = restaurantSearchCond.getSortConditions();

    BooleanBuilder whereBuilder = new BooleanBuilder();
    for (String restaurantType : restaurantTypes) {
      whereBuilder.and(restaurant.restaurantTypes.any().eq(restaurantType));
    }

    for (String moodType : moodTypes) {
      whereBuilder.and(restaurant.moodTypes.any().eq(moodType));
    }
    for (String containFoodType : containFoodTypes) {
      whereBuilder.and(restaurant.containFoodTypes.any().eq(containFoodType));
    }
    for (String provideServiceType : provideServiceTypes) {
      whereBuilder.and(restaurant.provideServiceTypes.any().eq(provideServiceType));
    }

    BooleanBuilder havingBuilder = new BooleanBuilder();
    for (Integer rate : rates) {
      havingBuilder.and(review.rating.avg().goe(rate).and(review.rating.avg().lt(rate + 1)));
    }


    JPAQuery<Restaurant> dynamicQuery = query.selectFrom(restaurant)
        .leftJoin(restaurant.reviews, review)
        .join(restaurant.foods, food)
        .leftJoin(restaurant.likes , likes)
        .where(whereBuilder, eqCanPark(canPark), eqIsOpen(isOpen),
                likeSimilarRoadAddress(roadAddress), mergeRestaurantRegionNameSearch(search))
        .groupBy(restaurant)
        .having(havingBuilder, loeGoePrice(startPrice, endPrice));
    addOrderBy(sortConditions, dynamicQuery);


    return dynamicQuery.fetch();
  }

  private BooleanExpression restaurantNameSearch(String search){
    return StringUtils.hasText(search) ? restaurant.name.like("%"+search+"%") : null;
  }

  private BooleanExpression regionNameSearch(String search){
    return StringUtils.hasText(search) ? restaurant.address.roadAddress.like("%"+search+"%") : null;
  }

  private BooleanExpression mergeRestaurantRegionNameSearch(String search) {
    BooleanExpression restaurantNameCondition = restaurantNameSearch(search);
    BooleanExpression regionNameCondition = regionNameSearch(search);
    return restaurantNameCondition!= null && regionNameCondition!=null ?  restaurantNameCondition.or(regionNameCondition) : null;
  }

  private BooleanExpression likeRoadAddress(String roadAddress){
    return StringUtils.hasText(roadAddress) ? restaurant.address.roadAddress.like("%"+roadAddress+"%") : null;
  }

  public List<Restaurant> findAllAdmin(RestaurantAdminSearchCond restaurantAdminSearchCond) {
    //where
    Long id = restaurantAdminSearchCond.getId();
    String name = restaurantAdminSearchCond.getName();
    String restaurantTypes = restaurantAdminSearchCond.getRestaurantTypes();
    String businessNum = restaurantAdminSearchCond.getBusinessNum();
    LocalDate createdAtStart = restaurantAdminSearchCond.getCreatedAtStart();
    LocalDate createdAtEnd = restaurantAdminSearchCond.getCreatedAtEnd();

    //having
    List<String> adminSortConditions = restaurantAdminSearchCond.getAdminSortConditions();

    JPAQuery<Restaurant> dynamicQuery = query.selectFrom(restaurant)
            .where(adminLikeName(name), adminLikeBusinessNum(businessNum),
                    adminRangeCreateAt(createdAtStart, createdAtEnd)
                    ,adminLikeRestaurantTypes(restaurantTypes) , adminLikeId(id));

    for (String adminSortCondition : adminSortConditions) {
      if (adminSortCondition.equals("NEW")){
        dynamicQuery.orderBy(restaurant.createdAt.desc());
      }
      if (adminSortCondition.equals("REGISTER")){
        dynamicQuery.orderBy(restaurant.createdAt.asc());
      }
    }
    return dynamicQuery.fetch();


  }


  private BooleanExpression adminLikeId(Long id){
    return id != null ? restaurant.id.eq(id) : null;
  }
  private BooleanExpression adminLikeRestaurantTypes(String restaurantTypes){
    return restaurantTypes != null ? restaurant.restaurantTypes.any().like("%" + restaurantTypes + "%") : null;
  }



  private BooleanExpression adminRangeCreateAt(LocalDate createdAtStart, LocalDate createdAtEnd) {
      return createdAtStart != null || createdAtEnd != null ? restaurant.createdAt.goe(createdAtStart).and(restaurant.createdAt.loe(createdAtEnd)) : null;
  }


  private BooleanExpression adminLikeName(String name) {
   return  StringUtils.hasText(name) ? restaurant.name.like("%"+name+"%") : null;
  }


  private BooleanExpression adminLikeBusinessNum(String businessNum) {
   return  StringUtils.hasText(businessNum) ? restaurant.businessNum.like("%"+businessNum+"%") : null;
  }



  public List<Restaurant> findSimilarRestaurantsAll(RestaurantSearchCond restaurantSearchCond) {
    Set<String> containFoodTypes = restaurantSearchCond.getContainFoodTypes();
    Set<String> restaurantTypes = restaurantSearchCond.getRestaurantTypes();
    String roadAddress = restaurantSearchCond.getRoadAddress();
    BooleanBuilder builder = new BooleanBuilder();
    for (String restaurantType : restaurantTypes) {
      builder.or(restaurant.restaurantTypes.any().eq(restaurantType));
    }
    for (String containFoodType : containFoodTypes) {
      builder.or(restaurant.containFoodTypes.any().eq(containFoodType));
    }
    return query.selectFrom(restaurant).where(builder, likeSimilarRoadAddress(roadAddress)).fetch();


  }

  private BooleanExpression likeSimilarRoadAddress(String roadAddress) {
    if (roadAddress == null ){
      return null;
    }
    roadAddress = roadAddress.replace("경기도", "경기")
            .replace("서울특별시", "서울")
            .replace("부산광역시", "부산")
            .replace("대구광역시", "대구")
            .replace("인천광역시", "인천")
            .replace("광주광역시", "광주")
            .replace("대전광역시", "대전")
            .replace("울산광역시", "울산")
            .replace("세종특별자치시", "세종")
            .replace("제주특별자치도", "제주");

    String result =  roadAddress.trim();
    String[] s = result.split(" ");
    String similarAddress = s[0]+" " +s[1];
    System.out.println("similarAddress = " + similarAddress);
    return restaurant.address.roadAddress.like(similarAddress+"%");
  }


  private void addOrderBy(List<String> sortConditions, JPAQuery<Restaurant> dynamicQuery) {

    //생각해보면 굳이 Boolean으로 판정할 필요가 아예 없었네 ㅇㅇ 그냥 라디오 버튼으로 String 값 넘어오면 이 String 값 있냐고 확인해서 판정하면 끝나는 일을 ;; ㅋㅋ
    for (String sortCondition : sortConditions) {
      switch (sortCondition) {
        case "NEW":
          dynamicQuery.orderBy(restaurant.createdAt.desc().nullsLast()); //최신 순
          break;
        case "RATE":
          dynamicQuery.orderBy(review.rating.avg().desc().nullsLast()); //별점 높은 순
          break;
        case "LIKE":
          dynamicQuery.orderBy(likes.count().desc().nullsLast()); //좋아요 많은 순
          break;
        case "REVIEW":
          dynamicQuery.orderBy(review.count().desc().nullsLast()); //리뷰 많은 순
          break;
      }
    }
//      sortConditions.clear();
  }

  //모달 보안성 우수 => 제3자가 접근하기 어려움, 단순 팝업창 느낌이므로 , 이런 2가지 방법을 고려했을 때 ~~가 더 괜찮아서 이거를 선정하였다. 이렇게 면접이든 포폴이든 정의하자!


  private BooleanExpression eqContainFoodTypes(Set<String> containFoodTypes) {
    if (containFoodTypes == null || containFoodTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }
    BooleanExpression booleanExpression = null;
    for (String restaurantType : containFoodTypes) {
      if (restaurantType != null) {
        BooleanExpression condition = restaurant.restaurantTypes.any().eq(restaurantType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }




  private BooleanExpression eqRestaurantTypes(Set<String> restaurantTypes) {
    if (restaurantTypes == null || restaurantTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }
    BooleanExpression booleanExpression = null;
    for (String restaurantType : restaurantTypes) {
      if (restaurantType != null) {
        BooleanExpression condition = restaurant.restaurantTypes.any().eq(restaurantType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }



  private BooleanExpression eqProvideServiceTypes(Set<String> provideServiceTypes) {
    if (provideServiceTypes == null || provideServiceTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (String provideServiceType : provideServiceTypes) {
      if (provideServiceType != null) {
        BooleanExpression condition = restaurant.provideServiceTypes.any().eq(provideServiceType);
        booleanExpression =
            (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }

  private BooleanExpression eqMoodTypes(Set<String> moodTypes){
    if (moodTypes == null || moodTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (String moodType : moodTypes) {
      if (moodType != null) {
        BooleanExpression condition = restaurant.moodTypes.any().eq(moodType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition);
      }
    }

    return booleanExpression;
  }


  //goeRate : 별표 1,2,3,4,5 체크박스 중 여러 개를 누를 수 있어서 List를 받아올 수 있는 것이고,  이때 별점은 평균별점임 그래서 avg로 계산했음
  private BooleanExpression goeRate(List<Integer> rates) {
    if (rates == null || rates.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (Integer rate : rates) {
      if (rate != null) {
        BooleanExpression condition = review.rating.avg().goe(rate).and(review.rating.avg().lt(rate + 1));
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition);
      }
    }

    return booleanExpression;
  }

  private BooleanExpression loeGoePrice(Integer startPrice, Integer endPrice) {
    return startPrice != null && endPrice != null ? food.price.avg().goe(startPrice).and(food.price.avg().loe(endPrice)) : null;
  }





  private BooleanExpression eqIsOpen(Boolean isOpen) {
    return Boolean.TRUE.equals(isOpen) ? restaurant.businessStatus.eq(BusinessStatus.OPEN) : null; //영업 중인지 판별 , 누군가 체크박스에 영업 중 여부를 체크했을 경우 영업 중만 뜨게끔 조건 추가하는 것!
  }

  private BooleanExpression eqCanPark(Boolean canPark) {
    return Boolean.TRUE.equals(canPark) ? restaurant.canPark.eq(true) : null;
  }



}
