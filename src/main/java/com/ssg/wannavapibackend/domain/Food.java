package com.ssg.wannavapibackend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "restaurant")
public class Food {

  @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String image;

  @NumberFormat(pattern = "#,###원")
  private int price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id")  //현재 테이블에서 정의할 FK 아이디 이름 기준임
  private Restaurant restaurant;

  public Food(String name , String image , int price){
    this.name =name;
    this.image = image;
    this.price = price;
  }


  /**
   * 연관관계 편의 메서드
   */

  public void addRestaurant(Restaurant restaurant){
    this.restaurant = restaurant;
    restaurant.getFoods().add(this);
  }



}
