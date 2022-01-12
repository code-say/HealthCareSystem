package com.cg.healthcare.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.healthcare.entities.DiagnosticCenter;

@Repository
public interface IDiagnosticCenterRepositoryInt extends IDiagnosticCenterRepository,JpaRepository<DiagnosticCenter, Integer>{

	
}
