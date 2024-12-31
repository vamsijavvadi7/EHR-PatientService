package com.ehr.patient.service;


import com.ehr.patient.dao.PatientRepository;
import com.ehr.patient.dto.AddressDto;
import com.ehr.patient.dto.PatientDto;
import com.ehr.patient.dto.PatientPersonalDetailsDto;
import com.ehr.patient.dto.UserDto;
import com.ehr.patient.feign.UserServiceInterface;
import com.ehr.patient.mapper.PatientMapper;
import com.ehr.patient.model.Address;
import com.ehr.patient.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private UserServiceInterface userServiceInterface;

@Transactional
    public ResponseEntity<?> createPatient(PatientPersonalDetailsDto patientPersonalDetailsDto) {


        UserDto userDto = new UserDto();
        userDto.setEmail(patientPersonalDetailsDto.getEmail());
        userDto.setFirstName(patientPersonalDetailsDto.getFirstName());
        userDto.setLastName(patientPersonalDetailsDto.getLastName());
        userDto.setIsActive(patientPersonalDetailsDto.getIsActive());
        userDto.setPassword(patientPersonalDetailsDto.getPassword());
        userDto.setRole("patient");

        ResponseEntity<UserDto> user = userServiceInterface.createUser(userDto);

        if (user.getStatusCode() != HttpStatus.CREATED) {
            return user;
        }

        UserDto userDto1 = user.getBody();
        Patient patient = new Patient();



        patient.setUserid(userDto1.getId());
        patient.setPhone(patientPersonalDetailsDto.getPhone());

        Address address = new Address();
        address.setCity(patientPersonalDetailsDto.getAddress().getCity());
        address.setState(patientPersonalDetailsDto.getAddress().getState());
        address.setStreet(patientPersonalDetailsDto.getAddress().getStreet());
        address.setPostalCode(patientPersonalDetailsDto.getAddress().getPostalCode());
        patient.setAddress(address);
        try {

            Patient savedPatient = patientRepository.save(patient);

            return new ResponseEntity<>(patient, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<PatientPersonalDetailsDto> getPatient(Long id) {
        // Find patient by ID
        Optional<Patient> patientOptional = patientRepository.findById(id);

        if (!patientOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Patient patient = patientOptional.get();

        // Retrieve the associated UserDto using the patient’s user ID
        ResponseEntity<UserDto> userResponse = userServiceInterface.getUserById(patient.getUserid());

        if (userResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(userResponse.getStatusCode());
        }

        UserDto userDto = userResponse.getBody();

        if (userDto == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Map UserDto and Patient to PatientPersonalDetailsDto
        PatientPersonalDetailsDto patientDetailsDto = patientMapper.toPersonalDto(patient);
        // Map fields from UserDto
        patientDetailsDto.setUserid(userDto.getId());
        patientDetailsDto.setEmail(userDto.getEmail());
        patientDetailsDto.setFirstName(userDto.getFirstName());
        patientDetailsDto.setLastName(userDto.getLastName());
        patientDetailsDto.setIsActive(userDto.getIsActive());


        return ResponseEntity.ok(patientDetailsDto);
    }



    public ResponseEntity<List<PatientPersonalDetailsDto>> getAllPatients() {
        // Retrieve all patients from the repository
        List<Patient> patients = patientRepository.findAll();

        // Map each patient to a PatientPersonalDetailsDto
        List<PatientPersonalDetailsDto> patientDetailsList = patients.stream().map(patient -> {
                    // Fetch associated user details
                    ResponseEntity<UserDto> userResponse = userServiceInterface.getUserById(patient.getUserid());
                    boolean isUserServiceUp=true;
                    if (userResponse.getStatusCode() != HttpStatus.OK || userResponse.getBody() == null) {
                        isUserServiceUp=false;
                    }

                    UserDto userDto = userResponse.getBody();

                    // Map Patient and UserDto to PatientPersonalDetailsDto
                    PatientPersonalDetailsDto patientDetailsDto = patientMapper.toPersonalDto(patient);
                           if(isUserServiceUp) {
                               patientDetailsDto.setUserid(userDto.getId());
                               patientDetailsDto.setEmail(userDto.getEmail());
                               patientDetailsDto.setFirstName(userDto.getFirstName());
                               patientDetailsDto.setLastName(userDto.getLastName());
                               patientDetailsDto.setIsActive(userDto.getIsActive());
                           }

                    return patientDetailsDto;
                }).filter(Objects::nonNull) // Exclude null entries in case of failures
                .collect(Collectors.toList());

        return ResponseEntity.ok(patientDetailsList);
    }
    @Transactional
    public ResponseEntity<?> updatePatient(PatientPersonalDetailsDto patientPersonalDetailsDto) {
        // Fetch the existing patient record
        Optional<Patient> existingPatientOptional = patientRepository.findById(patientPersonalDetailsDto.getId());

        if (!existingPatientOptional.isPresent()) {
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }

        Patient existingPatient = existingPatientOptional.get();

        // Update User information
        UserDto userDto = new UserDto();
        userDto.setId(patientPersonalDetailsDto.getUserid());
        userDto.setEmail(patientPersonalDetailsDto.getEmail());
        userDto.setFirstName(patientPersonalDetailsDto.getFirstName());
        userDto.setLastName(patientPersonalDetailsDto.getLastName());

        ResponseEntity<Object> userResponse = userServiceInterface.updateUser(userDto);

        if (userResponse.getStatusCode() != HttpStatus.OK) {
            return userResponse; // Return the error response from the user service
        }
        existingPatient.setPhone(patientPersonalDetailsDto.getPhone());


        // Update Address if present
        if (patientPersonalDetailsDto.getAddress() != null) {
            Address updatedAddress = existingPatient.getAddress();
            if (updatedAddress == null) {
                updatedAddress = new Address();
                existingPatient.setAddress(updatedAddress);
            }

            AddressDto addressDto = patientPersonalDetailsDto.getAddress();
            updatedAddress.setStreet(addressDto.getStreet());
            updatedAddress.setCity(addressDto.getCity());
            updatedAddress.setState(addressDto.getState());
            updatedAddress.setPostalCode(addressDto.getPostalCode());
            existingPatient.setAddress(updatedAddress);
        }

        try {

            Patient updatedPatient = patientRepository.save(existingPatient);

            PatientPersonalDetailsDto responseDto = patientMapper.toPersonalDto(updatedPatient);
            responseDto.setEmail(userDto.getEmail());
            responseDto.setFirstName(userDto.getFirstName());
            responseDto.setLastName(userDto.getLastName());
            responseDto.setIsActive(userDto.getIsActive());

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return new ResponseEntity<>("Database Error while updating patient", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> deletePatient(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (patient.isPresent()) {

            ResponseEntity<?> user =userServiceInterface.deleteUser(patient.get().getUserid());

            response.put("message", Objects.requireNonNull(user.getBody()).toString());
            response.put("status", "error");
            return ResponseEntity.status(user.getStatusCode()).body(response);
        }
        response.put("message","Patient Not Found, consider deleted successfully" );
        response.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
