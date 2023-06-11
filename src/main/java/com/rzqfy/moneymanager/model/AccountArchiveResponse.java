package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzqfy.moneymanager.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountArchiveResponse {
    private String id;
    private String name;

    private GroupArchiveResponse group;

    private Long amount;

    private String description;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
