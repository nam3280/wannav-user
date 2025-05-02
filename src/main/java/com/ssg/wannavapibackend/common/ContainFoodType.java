package com.ssg.wannavapibackend.common;

public enum ContainFoodType {

  MILK("유제품 포함") , EGG("계란 포함") , ANIMAL_EGG("동물의 알 포함") , FISH("생선 포함") , CHICKEN("닭고기 포함") , RED_MEAT("붉은 고기 포함");

  //설명 , 키는 영어 값은 한글
  private String description;

  //생성자이자 설정자 역할
  ContainFoodType(String description){
    this.description = description;
  }


  //접근자 역할
  public String getDescription(){
    return description;
  }

}
