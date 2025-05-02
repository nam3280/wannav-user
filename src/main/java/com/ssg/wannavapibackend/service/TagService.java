package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.Tag;

import java.util.List;

public interface TagService {

    List<Tag> findTagsForReview();
}
