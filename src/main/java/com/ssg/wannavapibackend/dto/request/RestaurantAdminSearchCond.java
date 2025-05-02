package com.ssg.wannavapibackend.dto.request;


import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RestaurantAdminSearchCond {

    /**
     * where 동적 조건
     */
    private Long id;
    private String name;
    private String businessNum;
    private String restaurantTypes;
    private LocalDate createdAtStart;
    private LocalDate createdAtEnd;

    /**
     * having 동적 조건
     */
    List<String> adminSortConditions = new ArrayList<>(); //최신 순 , 등록 순

}
