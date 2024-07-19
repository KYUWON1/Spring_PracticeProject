package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Bean으로 등록하기위해서
public interface AccountRepository extends JpaRepository<Account, Long> { //활용할 엔티티, PK 타입
    // ID를 가져오는데, Id를 내림차순으로 정렬해서 가장 높은 Id 번호를 가져옴
    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);

    Integer countByAccountUser(AccountUser accountUser);
}
