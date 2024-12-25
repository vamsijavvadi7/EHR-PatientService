package com.ehr.patient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientDto {
    private Long id;
    private Long userid;
    @NotBlank
    private String phone;
    private AddressDto address;
}

