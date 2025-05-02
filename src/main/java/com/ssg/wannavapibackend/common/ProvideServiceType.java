package com.ssg.wannavapibackend.common;

public enum ProvideServiceType {

  CORKAGE("콜키지 가능 유료") , GFD("글루텐프리 메뉴") , GROUP("단체 이용 가능")
  , RESERVATION("예약 가능") , INTERNET("무선 인터넷") , PACK("포장 가능")
  , BATHROOM("남녀 화장실 구분") , DELIVERY("배달 가능") , WHEEL_CHAIR("휠체어 이용 가능")
  , CHILD_CHAIR("유아의자 존재") , PET("반려동물 동반 가능") , ONE("1인 이용 가능");



  //설명 , 키는 영어 값은 한글
  private String description;

  //생성자이자 설정자 역할
  ProvideServiceType(String description){
    this.description = description;
  }


  //접근자 역할
  public String getDescription(){
    return description;
  }
}
