package com.gi.rhapp.models;

import lombok.Data;

@Data
public class PasswordChangeRequest {

    private String oldPassword;
    private String newPassword;
    private String newPasswordConf;
}
