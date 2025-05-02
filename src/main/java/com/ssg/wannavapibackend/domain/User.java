package com.ssg.wannavapibackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String username;

    @Column(nullable = false)
    private String profile;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 10)
    private String name;

    @Column(name = "chatbot_code", length = 10)
    private String chatbotCode;

    @Column(length = 15)
    private String phone;

    @Embedded
    private Address address;

    @Column(name="referral_code", length = 6, unique = true)
    private String code;

    @ColumnDefault("0")
    private Long point;

    @Column(nullable = false)
    private Boolean consent;

    @Column(name="is_unregistered", nullable = false)
    @ColumnDefault("0")
    private Boolean unregistered;

    @Column(name="created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(name="unregistered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime unregisteredAt;

    public void updatePoint(long point) {
        this.point = point;
        this.updatedAt = LocalDateTime.now();
    }
}