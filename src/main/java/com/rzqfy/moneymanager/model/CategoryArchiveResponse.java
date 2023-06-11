package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryArchiveResponse {
    private String id;

    private String name;

    private String type;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
