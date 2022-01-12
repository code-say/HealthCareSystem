package com.cg.healthcare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.DiagnosticTest;






@Repository
public interface IDiagnosticCenterRepository{
	List<DiagnosticTest> viewTestDetails(int diagnosticCenterId);
	DiagnosticCenter getDiagnosticCenter(String centername);
}
