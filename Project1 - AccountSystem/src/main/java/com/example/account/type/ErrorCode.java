package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_COUNT_FOR_USER_10("사용자 최대 계좌는 10개 입니다."),
    ACCOUNT_NOT_FOUND("해당 계좌번호가 존재하지 않습니다."),
    USER_ACCOUNT_NOT_MATCH("계좌번호의 유저와 일치하지 않습니다"),
    ACCOUNT_UNREGISTERED("해당 계좌는 이미 해지되었습니다."),
    BALANCE_LEFT("계좌에 잔액이 남아있습니다."),
    AMOUNT_EXCEED_BALANCE("잔액이 부족합니다.");

    private final String description;
}
