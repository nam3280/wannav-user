package com.ssg.wannavapibackend.service;

public interface BadWordService extends BadWord {

    void createBadWordMap();
    String changeBadWord(String input);
}
