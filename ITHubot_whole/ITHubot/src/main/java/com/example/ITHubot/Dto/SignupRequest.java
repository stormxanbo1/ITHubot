package com.example.ITHubot.Dto;

import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    private String Name;
    private Set<String> roles;
}
