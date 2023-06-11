package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionGetAllResponse {
    String id;

    LocalDateTime date;

    @JsonProperty("account_name")
    String accountName;

    @JsonProperty("category_name")
    String categoryName;

    Long amount;

    String type;
}
