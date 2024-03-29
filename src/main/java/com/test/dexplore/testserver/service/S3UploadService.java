package com.test.dexplore.testserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public List<String> listImages() {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket);
        ListObjectsV2Result result;
        List<String> imageUrls = new ArrayList<>();

        do {
            result = amazonS3.listObjectsV2(req);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                // 이미지 URL을 생성하여 리스트에 추가합니다.
                String imageUrl = amazonS3.getUrl(bucket, objectSummary.getKey()).toString();
                imageUrls.add(imageUrl);
            }

            req.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return imageUrls;
    }

    public ResponseEntity<?> getImage(String imageUrl) {
        try {
            byte[] imageData = fetchImageData(imageUrl);
            HttpHeaders headers = new HttpHeaders();

            String fileExtension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();

            if ("jpg".equals(fileExtension) || "jpeg".equals(fileExtension)) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else if ("png".equals(fileExtension)) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch image from URL: " + imageUrl + ". Error: " + e.getMessage());
        }
    }

    private byte[] fetchImageData(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection conn = url.openConnection();
        InputStream inputStream = conn.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

}