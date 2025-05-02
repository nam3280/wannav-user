package com.ssg.wannavapibackend.dto.request;

import com.ssg.wannavapibackend.domain.Review;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.domain.UserGradeLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDTO {

    private Review review;
    private User user;
    private UserGradeLog userGradeLog;
}
