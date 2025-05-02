package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.response.OCRResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface OCRService {

    OCRResponseDTO findReceiptData(MultipartFile file) throws IOException;

    Restaurant findCorrectRestaurant(OCRResponseDTO.StoreInfo storeInfo);

    LocalDate findCorrectVisitDate(String visitDate);
}
