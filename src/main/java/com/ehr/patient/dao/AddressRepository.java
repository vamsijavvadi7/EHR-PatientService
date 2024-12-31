package com.ehr.patient.dao;


import com.ehr.patient.model.Address;
import com.ehr.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
