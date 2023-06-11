package com.rzqfy.moneymanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionGetDetailResponse {

    private String id;

    private LocalDateTime date;

    private AccountArchiveResponse account;

    private CategoryArchiveResponse category;

    private String type;

    private Long amount;

    private String note;

    private String description;
}
