package com.ssg.wannavapibackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OCRRequestDTO {

    @JsonProperty("version")
    private String version;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("timestamp")
    private Integer timestamp;

    @JsonProperty("lang")
    private String lang;

    @JsonProperty("images")
    private List<Image> images;

    @Builder
    public static class Image {

        @JsonProperty("format")
        private String format;

        @JsonProperty("name")
        private String name;

        @JsonProperty("url")
        private String url;
    }

    public OCRRequestDTO(String url) {
        this.version = "V2";
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = 0;
        this.lang = "ko";
        this.images = Collections.singletonList(
                Image.builder()
                        .format("png")
                        .name("test")
                        .url(url).build());
    }
}
