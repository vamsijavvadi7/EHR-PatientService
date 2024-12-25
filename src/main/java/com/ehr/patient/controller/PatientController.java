package com.ehr.patient.controller;


import com.ehr.patient.dto.PatientDto;
import com.ehr.patient.dto.PatientPersonalDetailsDto;
import com.ehr.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<?> createPatient(@RequestBody PatientPersonalDetailsDto patientPersonalDetailsDto) {
        return patientService.createPatient(patientPersonalDetailsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientPersonalDetailsDto> getPatient(@PathVariable Long id) {
        return patientService.getPatient(id);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PatientPersonalDetailsDto>> getAllPatients() {
        return patientService.getAllPatients();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePatient(@RequestBody PatientPersonalDetailsDto patientPersonalDetailsDto) {
        return patientService.updatePatient(patientPersonalDetailsDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        return patientService.deletePatient(id);
    }
}
