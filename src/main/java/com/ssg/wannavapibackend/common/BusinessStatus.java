package com.ssg.wannavapibackend.common;

public enum BusinessStatus {

  OPEN("영업 중") , CLOSE("영업 종료") , BREAK_TIME("브레이크 타임") , DAY_OFF("오늘 휴무");

  private String description;

  BusinessStatus(String description){
    this.description = description;
  }

  public String getDescription(){
    return description;
  }

}
