package com.cg.healthcare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cg.healthcare.entities.User;

import com.cg.healthcare.entities.Patient;

public interface IPatientRepository extends JpaRepository<Patient, Integer> {

	List<Patient> findAllByname(String name);
	
	Patient findByUser(User user);
}
