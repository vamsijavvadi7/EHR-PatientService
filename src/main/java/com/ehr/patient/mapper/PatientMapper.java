package com.ehr.patient.mapper;
// PatientMapper.java


import com.ehr.patient.dto.AddressDto;
import com.ehr.patient.dto.PatientDto;
import com.ehr.patient.dto.PatientPersonalDetailsDto;
import com.ehr.patient.model.Address;
import com.ehr.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

  @Mapping(source = "address",target = "address")
    Patient toEntity(PatientDto patientDto);

    @Mapping(source = "address",target = "address")
    PatientDto toDto(Patient patient);

  @Mapping(source = "address",target = "address")
  PatientPersonalDetailsDto toPersonalDto(Patient patient);

  @Mapping(source = "address",target = "address")
  Patient personaltoEntity(PatientPersonalDetailsDto patientPersonalDetailsDto);

}
