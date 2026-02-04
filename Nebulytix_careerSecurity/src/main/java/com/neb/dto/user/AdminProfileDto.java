package com.neb.dto.user;

import lombok.Data;

@Data
public class AdminProfileDto {
    private Long id;
    private String email;
    private boolean enabled;
    private String profilePictureUrl;
}
