package com.example.weather.Repository;

import com.example.weather.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Integer> {
    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate,LocalDate endDate);
    Diary getFirstByDate(LocalDate date); // 맨 처음 1개를 가져옴
    // Transactional 을 붙이지 않으면, 데이터가 삭제되지않는다 !
    // db와 spring 사이에서 발생하는 예외에 대해서 처리를 해준다.
    @Transactional
    void deleteAllByDate(LocalDate date);
}
