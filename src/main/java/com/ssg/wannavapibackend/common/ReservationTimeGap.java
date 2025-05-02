package com.ssg.wannavapibackend.common;

public enum ReservationTimeGap {
  HALF("30분") , ONE("1시간"), TWO("2시간");

  private String description;

  ReservationTimeGap(String description){
    this.description = description;
  }

  public String getDescription(){
    return description;
  }
}
