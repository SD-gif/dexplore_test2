package com.test.dexplore.testserver.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.test.dexplore.testserver.entity.Cover;

import lombok.Data;

@Data
public class CoverUploadDto {
    private MultipartFile file;
    private String title;

    public Cover toEntity(String coverImageUrl) {
        return Cover.builder()
                .coverImageUrl(coverImageUrl)
                .title(title)
                .build();
    }
}
