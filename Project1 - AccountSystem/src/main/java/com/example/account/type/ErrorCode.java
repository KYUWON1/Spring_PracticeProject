package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다"),
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중입니다."),
    MAX_COUNT_FOR_USER_10("사용자 최대 계좌는 10개 입니다."),
    ACCOUNT_NOT_FOUND("해당 계좌번호가 존재하지 않습니다."),
    USER_ACCOUNT_NOT_MATCH("계좌번호의 유저와 일치하지 않습니다"),
    TRANSACTION_ACCOUNT_NOT_MATCH("해당 계좌에서 발생한 거래가 아닙니다."),
    CANCEL_MUST_FULLY("부분 취소는 허용되지않습니다"),
    TOO_OLD_ORDER_TO_CANCEL("1년 이상 지난 거래는 취소불가합니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    ACCOUNT_UNREGISTERED("해당 계좌는 이미 해지되었습니다."),
    BALANCE_LEFT("계좌에 잔액이 남아있습니다."),
    AMOUNT_EXCEED_BALANCE("잔액이 부족합니다."),
    TRANSACTION_NOT_FOUND("거래내역이 존재하지 않습니다.");

    private final String description;
}
