package com.test.dexplore.testserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.file.Files;
import com.test.dexplore.testserver.repository.ImageRepository; // 가정한 repository의 실제 패키지 경로에 따라 다를 수 있습니다.

import lombok.RequiredArgsConstructor;

import com.test.dexplore.testserver.DTO.CoverUploadDto;
import com.test.dexplore.testserver.entity.Cover; // 가정한 entity의 실제 패키지 경로에 따라 다를 수 있습니다.


@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${file.path}")
    private String uploadFolder;

    @Transactional
    public String 커버사진업로드(CoverUploadDto coverUploadDto) {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + coverUploadDto.getFile().getOriginalFilename();
        System.out.println("커버 이미지 파일이름:"+imageFileName);

        Path imageFilePath = Paths.get(uploadFolder + imageFileName);

        try {
            Files.write(imageFilePath, coverUploadDto.getFile().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cover cover = coverUploadDto.toEntity(imageFileName);
        imageRepository.save(cover);

        String imageUrl = cover.getCoverImageUrl(); // 이미지 URL 생성
        return imageUrl;
    }
}