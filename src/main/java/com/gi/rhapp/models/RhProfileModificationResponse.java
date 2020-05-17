package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RhProfileModificationResponse {

    @JsonUnwrapped
    User user;

    String token;
}
