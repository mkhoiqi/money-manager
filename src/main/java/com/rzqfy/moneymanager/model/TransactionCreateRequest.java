package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreateRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @NotBlank
    @Size(max = 100)
    @JsonProperty("account_id")
    private String accountId;

    @NotBlank
    @Size(max = 100)
    @JsonProperty("category_id")
    private String categoryId;

    @NotBlank
    @Size(max = 100)
    private String type;

    @Min(value = 0)
    private Long amount;

    @Size(max = 100)
    private String note;

    @Size(max = 200)
    private String description;
}
