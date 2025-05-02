package com.ssg.wannavapibackend.config;

import com.ssg.wannavapibackend.dto.request.PointDTO;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.repository.mypage.query.PointDTORepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PointScheduler {

    private final PointDTORepository pointDTORepository;
    private final UserRepository userRepository;
    private final Long BASIC_POINT = 100L;
    private final Long IMAGE_POINT = 300L;
    private final Double SPROUT = 0.1;
    private final Double SEEDLING = 0.15;
    private final Double FRUIT = 0.2;
    private final Double TREE = 0.3;


    /**
     * 매일 새벽 2시, 리뷰 작성 5일 후 회원 등급별 포인트 지급 스케쥴러
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void givePoint() {
        List<PointDTO> pointDTOs = pointDTORepository.findAllWithUserGrade();

        for (PointDTO pointDTO : pointDTOs) {
            if (pointDTO.getReview().getCreatedAt().toLocalDate().equals(LocalDate.now().minusDays(5))) {
                if (pointDTO.getReview().getIsActive()) {

                    switch (pointDTO.getUserGradeLog().getGrade()) {

                        case SEED -> {
                            if (pointDTO.getReview().getImage() == null) {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), BASIC_POINT);
                            } else {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), IMAGE_POINT);
                            }
                        }

                        case SPROUT -> {
                            if (pointDTO.getReview().getImage() == null) {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (BASIC_POINT + BASIC_POINT * SPROUT));
                            } else {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (IMAGE_POINT + IMAGE_POINT * SPROUT));
                            }
                        }

                        case SEEDLING -> {
                            if (pointDTO.getReview().getImage() == null) {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (BASIC_POINT + BASIC_POINT * SEEDLING));
                            } else {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (IMAGE_POINT + IMAGE_POINT * SEEDLING));
                            }
                        }

                        case FRUIT -> {
                            if (pointDTO.getReview().getImage() == null) {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (BASIC_POINT + BASIC_POINT * FRUIT));
                            } else {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (IMAGE_POINT + IMAGE_POINT * FRUIT));
                            }
                        }

                        case TREE -> {
                            if (pointDTO.getReview().getImage() == null) {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (BASIC_POINT + BASIC_POINT * TREE));
                            } else {
                                userRepository.updateUserPoint(pointDTO.getUser().getId(), (long) (IMAGE_POINT + IMAGE_POINT * TREE));
                            }
                        }
                    }
                }
            }
        }
    }
}
