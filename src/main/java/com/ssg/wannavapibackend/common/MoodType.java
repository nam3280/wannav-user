package com.ssg.wannavapibackend.common;


public enum MoodType {


  DATE("데이트하기 좋은") , MODERN("모던한") , TRENDY("트랜디한") ,  FAMILY("가족회식하기 좋은")
  , MEETING("비즈니스 미팅하기 좋은") , CALM("차분한") , TRADITIONAL("전통적인") , ACTIVE("활기찬")
  , QUIET("조용한"), BEFORE_MARRY("상견례하기 좋은") ,VIEW("뷰맛집") , GROUP("단체회식하기 좋은")
  , ANNIVERSARY("기념일");

  

  private String description;

  //생성자이자 설정자 역할
  MoodType(String description){
    this.description = description;
  }


  //접근자 역할
  public String getDescription(){
    return description;
  }

}
