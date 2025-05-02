package com.ssg.wannavapibackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private Integer rating;
    private String content;
    private String image;

    @Temporal(TemporalType.DATE)
    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;
    private String note;

    @OneToMany(mappedBy = "review")
    @JsonIgnore
    private List<ReviewTag> reviewTags;

    /**
     * 연관관계 편의 메서드
     */
    public void addRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        restaurant.getReviews().add(this);
    }

    public void addUser(User user){
        this.user = user;
    }

}
