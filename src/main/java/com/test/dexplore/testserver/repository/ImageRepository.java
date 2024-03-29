package com.test.dexplore.testserver.repository;

import com.test.dexplore.testserver.entity.Cover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Cover, Integer> {
    // 여기에 필요한 추가적인 메소드를 정의할 수 있습니다.
}
