package com.test.dexplore.testserver.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.test.dexplore.testserver.service.S3UploadService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3ImageUploadController {

    private final S3UploadService s3UploadService;

    @PostMapping("/api/bucket_upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
        String imageUrl = s3UploadService.saveFile(file);
        return ResponseEntity.ok().body("Image uploaded successfully. URL: " + imageUrl);
    } catch (AmazonS3Exception s3Exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Failed to upload image. Error: " + s3Exception.getMessage());
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Failed to upload image. Error: " + e.getMessage());
     }
    }

    @GetMapping("/api/get_s3_list")
    public ResponseEntity<List<String>> getS3ImageList() {
        List<String> imageUrls = s3UploadService.listImages();
        return ResponseEntity.ok().body(imageUrls);
    }

    @PostMapping("/api/get_image")
    public ResponseEntity<?> getImage(@RequestParam String imageUrl) {
        
        return s3UploadService.getImage(imageUrl);
    }

}



