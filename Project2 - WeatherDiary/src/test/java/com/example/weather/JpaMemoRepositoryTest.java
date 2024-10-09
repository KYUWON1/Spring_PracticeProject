package com.example.weather;

import com.example.weather.Repository.JpaMemoRepository;
import com.example.weather.domain.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest(){
        // given
        Memo newMemo = new Memo(10,"this is new Memo");

        // when
        jpaMemoRepository.save(newMemo);

        // then
        List<Memo> memoList =
                jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);
    }

    @Test
    void findById() {
        //given
        Memo newMemo = new Memo(11,"jpa");
        //when
        Memo save = jpaMemoRepository.save(newMemo);
        System.out.println(save.getId()); // mysql에서 return된 id값을 가진다.
        //then
        Optional<Memo> result =
                jpaMemoRepository.findById(save.getId()); // 11을 넣으면 에러
        assertEquals(result.get().getText(),"jpa");
    }
}
