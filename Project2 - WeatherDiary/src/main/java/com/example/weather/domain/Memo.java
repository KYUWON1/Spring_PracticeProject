package com.example.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Entity와 Table의 이름을 명시해줄 필요는 없지만, 만약
// 여러 엔티티를 동일한 테이블에 매핑시키자고 한다면, @Table(name="Memo") 처럼 매칭시킬 테이블을 명시해주어야한다.
@Entity(name="memo")
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto : 알아서 자동
    // identity : 스프링boot는 키생성 x , mysql에서 생성후 스프링이 가져옴
    private int id;
    private String text;
}
