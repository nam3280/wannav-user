package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.Tag;
import com.ssg.wannavapibackend.repository.TagRepository;
import com.ssg.wannavapibackend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    /**
     * 태그 전체 조회 - 리뷰 작성 시 사용
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Tag> findTagsForReview() {
        return tagRepository.findAll();
    }
}
