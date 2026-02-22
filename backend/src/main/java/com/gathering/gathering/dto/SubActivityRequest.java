package com.gathering.gathering.dto;

import jakarta.validation.constraints.NotBlank;

public record SubActivityRequest(
        @NotBlank String name
) {
}
