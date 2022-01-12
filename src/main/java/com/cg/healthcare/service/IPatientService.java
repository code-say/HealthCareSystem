package com.cg.healthcare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;

/*===========================================
 * Author : Sayan Mandal
 * Date : 07-01-2022
 * Description : This is Patient Service
 * ===========================================
*/

@Service
public interface IPatientService {

	Patient registerPatient(Patient patient, int userID) throws DataAlreadyExists, DataNotFoundInDataBase;
	Patient updatePatientDetails(Patient patient) throws DataNotFoundInDataBase;
	Patient viewPatient(String patientUserName) throws DataNotFoundInDataBase;
	List<TestResult> getAllTestResult(String patientUserName) throws DataNotFoundInDataBase;
	TestResult viewTestResult(int testResultId) throws DataNotFoundInDataBase;
	Patient deletePatient(Patient patient) throws DataNotFoundInDataBase;
	
	List<Patient> getAll();
}
