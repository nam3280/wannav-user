package com.ssg.wannavapibackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OCRResponseDTO {

    @JsonProperty("images")
    List<Image> images;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Image {

        @JsonProperty("inferResult")
        private String inferResult;

        @JsonProperty("message")
        private String message;

        @JsonProperty("receipt")
        private Receipt receipt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Receipt {

        @JsonProperty("result")
        private Result result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Result {

        @JsonProperty("storeInfo")
        private StoreInfo storeInfo;

        @JsonProperty("paymentInfo")
        private PaymentInfo paymentInfo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class StoreInfo {
        @JsonProperty("name")
        private Name name;

        @JsonProperty("bizNum")
        private BizNum bizNum;

        @JsonProperty("subName")
        private SubName subName;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Name {

        @JsonProperty("text")
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class BizNum {

        @JsonProperty("text")
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class SubName {
        @JsonProperty("text")
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class PaymentInfo {
        @JsonProperty("date")
        private Date date;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Date {

        @JsonProperty("text")
        private String text;
    }
}
