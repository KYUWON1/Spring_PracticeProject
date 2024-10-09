package com.example.weather;

import com.example.weather.Repository.JdbcMemoRepository;
import com.example.weather.domain.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional // Transactional이 활성화되어있으면, 테스트가 종료되면 데이터가 사라진다.
public class JdbcMemoRepositoryTest {
    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest(){
        //given
        Memo newMemo = new Memo(2,"insert Memo Test");

        //when
        jdbcMemoRepository.save(newMemo);

        //then
        Optional<Memo> result = jdbcMemoRepository.findById(2);
        assertEquals(result.get().getText(),"insert Memo Test");
    }

    @Test
    void findAllMemoTest() {
        //given
        List<Memo> memoList = jdbcMemoRepository.findAll();
        //when
        //then
        assertNotNull(memoList);
    }
}
