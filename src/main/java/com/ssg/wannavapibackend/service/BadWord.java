package com.ssg.wannavapibackend.service;

public interface BadWord {

    //욕설에 주의하세요.
    String[] badWords = {"멍청", "바보", "개맛없", "존나", "새끼", "망해라", "망하면"};
    String[] delimiters = {" ", ",", ".", "!", "@", "?", "1"};
    String replacement = "*";
}
