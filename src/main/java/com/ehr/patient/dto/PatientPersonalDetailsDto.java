package com.ehr.patient.dto;



import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PatientPersonalDetailsDto {
    private Long id;
    private Long userid;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private AddressDto address;
    private Boolean isActive;
}
