package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzqfy.moneymanager.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateResponse {
    private String id;
    private String name;

    private GroupArchiveResponse group;

    private Long amount;

    private String description;
}
