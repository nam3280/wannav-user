package com.ssg.wannavapibackend.service.serviceImpl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssg.wannavapibackend.dto.request.FileDTO;
import com.ssg.wannavapibackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1); //파일 확장자 (ex. png, jpg ...)
        return UUID.randomUUID() + "." + ext;
    }

    public List<FileDTO> uploadFiles(List<MultipartFile> multipartFiles, String filePath) {
        List<FileDTO> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = filePath + "/" + uploadFileName;   //directory/UUID.png

                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));    //img url 생성

                uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString(); //S3에서 파일 URL을 가져옴

            } catch (IOException e) {
                e.printStackTrace();
            }
            s3files.add(
                    FileDTO.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build());
        }
        return s3files;
    }
}
