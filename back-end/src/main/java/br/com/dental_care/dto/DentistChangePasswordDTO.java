package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DentistChangePasswordDTO {

    private String username;
    private String password;
    private String newPassword;
    private String confirmPassword;

}
