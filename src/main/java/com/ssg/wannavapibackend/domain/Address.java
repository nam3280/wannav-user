package com.ssg.wannavapibackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address {

  @Column(name = "zip_code")
  private String zipCode;

  @Column(name = "road_address")
  private String roadAddress;

  @Column(name = "land_lot_address")
  private String landLotAddress;

  @Column(name = "detail_address")
  private String detailAddress;

}
