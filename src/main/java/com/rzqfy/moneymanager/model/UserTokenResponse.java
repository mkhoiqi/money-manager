package com.rzqfy.moneymanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenResponse {
    private String token;

    @JsonProperty("token_expired_at")
    private Long tokenExpiredAt;
}
