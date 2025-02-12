package com.elogix.api.shared.infraestructure.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    @Builder.Default
    @JsonProperty("isActive")
    private boolean isActive = true;

    @Builder.Default
    @JsonProperty("isLocked")
    private boolean isLocked = false;

    @Builder.Default
    @JsonProperty(value = "isDeleted", defaultValue = "false")
    private boolean isDeleted = false;

    private Set<String> roles;
}
