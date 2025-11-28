package com.spring.unictive.module.auth.dto.response;

import com.spring.unictive.module.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Long id;
    private String email;
    private String username;
    private Role role;
}
