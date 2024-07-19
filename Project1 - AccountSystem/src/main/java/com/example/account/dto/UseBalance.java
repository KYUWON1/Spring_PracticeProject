    package com.example.account.dto;

    import com.example.account.aop.AccountLockIdInterface;
    import com.example.account.type.TransactionResultType;
    import jakarta.validation.constraints.*;
    import lombok.*;

    import java.time.LocalDateTime;

    public class UseBalance {
        @Getter
        @Setter
        @AllArgsConstructor
        public static class Request implements AccountLockIdInterface {
            //getAccountNumber는 롬복에 있어서 자동으로 인터페이스가 구현이됨
            @NotNull
            @Min(1)
            private Long userId;

            @NotBlank
            @Size(min = 10, max = 10)
            private String accountNumber;

            @NotNull
            @Max(1000_000_000)
            @Min(10)
            private Long amount;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Response {
            private String accountNumber;
            private TransactionResultType transactionResultType;
            private String transactionId;
            private Long amount;
            private LocalDateTime transactedAt;

            public static Response from(TransactionDto transactionDto){
                return Response.builder()
                        .accountNumber(transactionDto.getAccountNumber())
                        .transactionResultType(transactionDto.getTransactionResultType())
                        .transactionId(transactionDto.getTransactionId())
                        .amount(transactionDto.getAmount())
                        .transactedAt(transactionDto.getTransactionAt())
                        .build();
            }
        }
    }
