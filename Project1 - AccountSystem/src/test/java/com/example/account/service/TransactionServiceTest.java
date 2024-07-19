package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountUserRepository accountUserRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("잔액 사용 성공")
    void successUseBalance() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactionAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(9000L)
                        .build());
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);
        //when
        TransactionDto transactionDto
                = transactionService.useBalance(1L, "1000000012", 200L);
        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(200L,captor.getValue().getAmount());
        assertEquals(9800L,captor.getValue().getBalanceSnapShot());

        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(9000L,transactionDto.getBalanceSnapShot());
        assertEquals(1000L,transactionDto.getAmount());

    }

    @Test
    @DisplayName("해당 유저 없음 - 잔액 사용 실패 ")
    void useBalance_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890",1000L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패 ")
    void deleteAccount_AccountNotFound() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890",1000L));
        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
    void useBalance_userUnMatch() {
        //given
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        AccountUser Babo = AccountUser.builder()
                .id(13L)
                .name("Babo").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Babo)
                        .balance(0L)
                        .accountNumber("1000000012").build()));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890",1000L));
        //then
        assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH,accountException.getErrorCode());
    }

    @Test
    @DisplayName("이미 해지된 계좌 - 잔액 사용 실패")
    void useBalance_AlreadyDelete() {
        //given
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi)
                        .balance(0L)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000012").build()));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890",1000L));
        //then
        assertEquals(ErrorCode.ACCOUNT_UNREGISTERED,accountException.getErrorCode());

    }

    @Test
    @DisplayName("거래 금액이 잔액보다 큼 - 잔액 사용 실패")
    void exceedBalance_useBalance() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .balance(100L)
                .accountNumber("1000000012").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        //then
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890",1000L));
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE,accountException.getErrorCode());
        verify(transactionRepository,times(0)).save(any());
    }

    @Test
    @DisplayName("실패 트랙잭션 저장 성공")
    void saveFailedUseTransaction() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactionAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(9000L)
                        .build());
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);
        //when
        transactionService.saveFailedUseTransaction("1000000012",
                200L);
        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(200L,captor.getValue().getAmount());
        assertEquals(10000L,captor.getValue().getBalanceSnapShot());
        assertEquals(TransactionResultType.F,captor.getValue().getTransactionResultType());
    }

    @Test
    @DisplayName("잔액 취소 성공")
    void successCancelBalance() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactionAt(LocalDateTime.now())
                .amount(200L)
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(transaction);
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);
        //when
        TransactionDto transactionDto
                = transactionService.cancelBalance("transactionId",
                "1000000012", 200L);
        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(200L,captor.getValue().getAmount());
        assertEquals(10200L,captor.getValue().getBalanceSnapShot());

        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(9000L,transactionDto.getBalanceSnapShot());
        assertEquals(200L,transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 거래 없음- 잔액 취소 실패 ")
    void cancelTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionid",
                        "1234567890",1000L));
        //then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 취소 실패 ")
    void cancelTransaction_AccountNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("T_id", "1234567890",
                        1000L));
        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 정보 다름 - 잔액 취소 실패")
    void cancelTransaction_AccountUnMatch() {
        //given
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(Pobi)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        Account accountNotUse = Account.builder()
                .id(2L)
                .accountUser(Pobi)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000013").build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactionAt(LocalDateTime.now())
                .amount(200L)
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotUse));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("T_id", "1234567890",
                        200L));
        //then
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_NOT_MATCH,accountException.getErrorCode());
    }

    @Test
    @DisplayName("취소 금액 다름 - 잔액 취소 실패")
    void cancelTransaction_AmountUnMatch() {
        //given
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(Pobi)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactionAt(LocalDateTime.now())
                .amount(200L)
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("T_id", "1234567890",
                        201L));
        //then
        assertEquals(ErrorCode.CANCEL_MUST_FULLY,accountException.getErrorCode());
    }

    @Test
    @DisplayName("1년이상된 거래는 취소 불가능 - 잔액 취소 실패")
    void cancelTransaction_tooOldTransaction() {
        //given
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(Pobi)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactionAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(200L)
                .balanceSnapShot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("T_id", "1234567890",
                        200L));
        //then
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL,accountException.getErrorCode());
    }

    @Test
    @DisplayName("거래 정보 가져오기 성공")
    void successQueryTransaction() {
        AccountUser Pobi = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        Account account = Account.builder()
                .id(1L)
                .accountUser(Pobi)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactionAt(LocalDateTime.now().minusYears(1).minusDays(1))
                        .amount(200L)
                        .balanceSnapShot(9000L)
                        .build()));
        //when
        TransactionDto transactionDto = transactionService.queryTransaction("12345");
        //then
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(200L,transactionDto.getAmount());
        assertEquals("transactionId",transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("해당 거래 없음 - 거래 조회 실패 ")
    void getTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.queryTransaction("trxId"));
        //then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND,accountException.getErrorCode());
    }
}