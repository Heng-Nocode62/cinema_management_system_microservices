package com.heng.cms.cinemaservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaUpdateRequest {
    private String name;
    private String location;
    private String city;
    @Size(min = 8, max = 15, message = "phone must be at least 8 digits and maximum 15 digits")
    private String phone;
    @Email(message = "email must be the written in correct formate")
    private String email;

}
