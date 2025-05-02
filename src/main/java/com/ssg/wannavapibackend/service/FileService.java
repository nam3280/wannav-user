package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.dto.request.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String getUuidFileName(String fileName);

    List<FileDTO> uploadFiles(List<MultipartFile> multipartFiles, String filePath);
}
