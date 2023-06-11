package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreateResponse {

    private String id;

    private LocalDateTime date;

    private AccountCreateResponse account;

    private CategoryCreateResponse category;

    private String type;

    private Long amount;

    private String note;

    private String description;
}
