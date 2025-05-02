package com.ssg.wannavapibackend.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@ToString(exclude = "restaurant")
public class BusinessDay {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "day_of_week")
  private String dayOfWeek; //특정 요일에 대한 영업 시작 시간 ~ 라스트 오더 시간 배치할 것임

  @DateTimeFormat(pattern = "HH:mm" , iso = ISO.TIME)
  @Column(name = "open_time")
  private LocalTime openTime; //영업 시작 시간

  @DateTimeFormat(pattern = "HH:mm", iso = ISO.TIME)
  @Column(name = "close_time")
  private LocalTime closeTime; //종료 시간

  @DateTimeFormat(pattern = "HH:mm", iso = ISO.TIME)
  @Column(name = "break_start_time")
  private LocalTime breakStartTime; //브레이크 댄스 타임 시작 시간


  @DateTimeFormat(pattern = "HH:mm", iso = ISO.TIME)
  @Column(name = "break_end_time")
  private LocalTime breakEndTime; //브레이크 댄스 타임 종료 시간

  @DateTimeFormat(pattern = "HH:mm" , iso = ISO.TIME)
  @Column(name = "last_order")
  private LocalTime lastOrderTime; //라스트 오더 시간

  @Column(name = "is_day_off")
  private Boolean isDayOff; //문 닫는 날인지 , 이게 요일 정보가 들어가면 그 즉시 문닫는거임 , 근데 설계가 기괴하니 추후 리팩토링 ㄱㄱ

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id") //restaurant_id
  private Restaurant restaurant;


  public static List<BusinessDay> createBusinessDays(List<LocalTime> openTimes , List<LocalTime> closeTimes , List<LocalTime> breakStartTimes ,
      List<LocalTime> breakEndTimes , List<LocalTime> lastOrderTimes , List<String> isDayOffList){
    List<BusinessDay> businessDayList = new ArrayList<>();
    String[] dayOfWeeks = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
    for (int i = 0; i < dayOfWeeks.length; i++) {
      BusinessDay businessDay = new BusinessDay();
      businessDay.setDayOfWeek(dayOfWeeks[i]); //월요일 , 화요일 , ... 일요일
      businessDay.setOpenTime(openTimes.get(i)); // 시작시간
      businessDay.setCloseTime(closeTimes.get(i));
      businessDay.setBreakStartTime(breakStartTimes.get(i));
      businessDay.setBreakEndTime(breakEndTimes.get(i));
      businessDay.setLastOrderTime(lastOrderTimes.get(i));
      for (String isDayOff : isDayOffList) {
        if (isDayOff.equals(dayOfWeeks[i])){
          businessDay.setIsDayOff(true); //목요일과 목요일이 일치하면 그날은 쉬는 날이므로 쉬는날 true !
        }
      }
      businessDayList.add(businessDay);
    }
    return businessDayList;
  }


  /**
   * 연관관계 편의 메서드
   * 걍 N쪽에 편의 메서드 정의하자 !
   */
  public void addRestaurant(Restaurant restaurant){
    this.restaurant = restaurant;
    restaurant.getBusinessDays().add(this);
  }


}
