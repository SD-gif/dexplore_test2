package com.test.dexplore.testserver.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.dexplore.testserver.DTO.CoverUploadDto;
import com.test.dexplore.testserver.service.ImageService;
@RestController
public class FileUploadController {

    private final ImageService imageService;

    public FileUploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<String> coverImageUpload(@RequestParam("file") MultipartFile file) throws IOException {
        CoverUploadDto coverUploadDto = new CoverUploadDto();
        coverUploadDto.setFile(file);

        String amazonBucket = imageService.커버사진업로드(coverUploadDto);
        System.out.println("-------------------------------------------");
        System.out.println(amazonBucket);

        return ResponseEntity.ok().body("Image uploaded successfully");
    }
}

