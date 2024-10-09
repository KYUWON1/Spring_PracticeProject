package com.example.weather.Repository;

import com.example.weather.domain.Memo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    // datasource 는 properties에 담긴 datasource 정보
    @Autowired
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // 메모객체 저장하는 함수
    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql,memo.getId(),memo.getText());
        return memo;
    }

    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql,memoRowMapper());
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        // spring은 id가 pk인것을 모르기때문에, 옵셔널로 return;
        return jdbcTemplate.query(sql,memoRowMapper(),id).stream().findFirst();
    }

    // JDBC를 통해서 데이터를 가져오면, ResultSet이라는 형태로 가져오는데,
    // 가져온 ResultSet을 Memo 객체에 매핑해줘야함
    private RowMapper<Memo> memoRowMapper(){
        //ResultSet
        //{id = 1, text= 'this is memo'}
        return(rs,rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
