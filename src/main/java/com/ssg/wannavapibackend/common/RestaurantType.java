package com.ssg.wannavapibackend.common;

public enum RestaurantType {

  KOREAN("한식") , CHINESE("중식") , JAPANESE("일식") , WESTERN("양식") ,
  MEAT("고기") , SANDWICH("샌드위치") , DIET("다이어트") , THAI("태국음식") , CAFE("카페") , DESSERT("디저트"),
  GIMBAP("김밥") , ITALIAN("이탈리아 음식") , BAKERY("베이커리") , ICECREAM("아이스크림") ,INDIAN("인도 음식") , FRUIT("과일"),
  JUICE("주스전문점") , BRUNCH("브런치") , PIZZA("피자") , FRENCH("프랑스 음식") , TOFU("두부요리") , SASHIMI("생선회") ,
  ASIAN("아시아 음식") , SPAGHETTI("스파게티") , PASTA("파스타전문"), TURKISH("터키 음식");


  //설명 , 키는 영어 값은 한글
  private String description;

  //생성자이자 설정자 역할
  RestaurantType(String description){
    this.description = description;
  }


  //접근자 역할
  public String getDescription(){
    return description;
  }
}
