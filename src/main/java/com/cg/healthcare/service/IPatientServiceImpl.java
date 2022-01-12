package com.cg.healthcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.healthcare.dao.IPatientRepository;
import com.cg.healthcare.dao.ITestResultRepository;
import com.cg.healthcare.dao.IUserRepository;
import com.cg.healthcare.dao.manualqueries.QueryClassPersisitContext;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;

/*===========================================
 * Author : Sayan Mandal
 * Date : 07-01-2022
 * Description : This is Patient Service Implementation
 * ===========================================
*/

@Service
public class IPatientServiceImpl implements IPatientService {
	
	@Autowired
	IPatientRepository patRepo;
	
	@Autowired
	QueryClassPersisitContext qcp;
	
	@Autowired
	ITestResultRepository testRepo;
											
	@Autowired
	IUserRepository userRepo;                                                                                                       

	/************************************************************************************
	 * Method: registerPatient 
	 * Description: It is used to register patient
	 * returns: a string if patient already exists orelse will register patient
	  ************************************************************************************/

	
	@Override
	public Patient registerPatient(Patient patient, int userID) throws DataAlreadyExists, DataNotFoundInDataBase {
		// TODO Auto-generated method stub
		if(patRepo.existsById(patient.getPatientId()))throw new DataAlreadyExists(
				"Patient Already exists with id "+patient.getPatientId()+" use update to change");
		patient.setUser(userRepo.findById(userID).orElseThrow(()->new DataNotFoundInDataBase(
				"No Such User Exists with Id :"+userID)));
		
		return patRepo.saveAndFlush(patient);
	}
	
	/************************************************************************************
	 * Method: updatePatientDetails 
	 * Description: It is used to update patient
	 * returns: a string if patient is not registered previously else will update
	 ************************************************************************************/


	@Override
	public Patient updatePatientDetails(Patient patient) throws DataNotFoundInDataBase {
		// TODO Auto-generated method stub
		Patient p = patRepo.findById(patient.getPatientId()).orElseThrow(()->new DataNotFoundInDataBase("Patient Details Not Found in DataBase"));
		patient.setUser(p.getUser());
		return patRepo.saveAndFlush(patient);
	}

	/************************************************************************************
	 * Method: ViewTestResult by Patient Id
	 * Description: It is used to retrieve all the TestResult from
	 *				 the TestResult table
	 * @returns List<TestResult>: It returns List of TestResult of patient with details
	 ************************************************************************************/
	
	
	@Override
	public Patient viewPatient(String userid) throws DataNotFoundInDataBase {

		return patRepo.findByUser(
				userRepo.findById(Integer.parseInt(userid))
				.orElseThrow(()->new DataNotFoundInDataBase("No Such User Exists with Id :"+userid)));
	}
	
	/************************************************************************************
	 * Method:  getAllTestResult
	 * Description: It is used to retrieve all the patients 
	 * @returns List<Patient>: It returns List of Patients with details
	  ************************************************************************************/


	@Override
	public List<TestResult> getAllTestResult(String patientUserName) throws DataNotFoundInDataBase {
		
		List<TestResult> res = qcp.getAllTestResult(patientUserName);
		if(res == null) throw new DataNotFoundInDataBase("Patient UserName Might Not Exist");
		return res;
	}

	/************************************************************************************
	 * Method: ViewTestResult by testresult Id
	 * Description: It is used to retrieve all the TestResult from
	 *				 the TestResult table
	 * @returns List<TestResult>: It returns List of TestResult of patient with details
	 ************************************************************************************/
	
	
	@Override
	public TestResult viewTestResult(int testResultId) throws DataNotFoundInDataBase {
		// TODO Auto-generated method stub
		
		if(!testRepo.existsById(testResultId))throw new DataNotFoundInDataBase("TestResult Does not Exist!");
		return testRepo.findById(testResultId).get();
	}
	
	/************************************************************************************
	 * Method: deletePatient 
	 * Description: It is used to delete a patient
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	  ************************************************************************************/


	@Override
	public Patient deletePatient(Patient patient) throws DataNotFoundInDataBase {
		// TODO Auto-generated method stub
		
		if(!patRepo.existsById(patient.getPatientId()))throw new DataNotFoundInDataBase("Patient Details Not Found in Database");
		Patient pat = patRepo.findById(patient.getPatientId()).get();
		patRepo.deleteById(patient.getPatientId());
		return pat;
	}
	
	/************************************************************************************
	 * Method:  getAll
	 * Description: It is used to retrieve all the patients 
	 * @returns List<Patient>: It returns List of Patients with details
	 ************************************************************************************/

	@Override
	public List<Patient> getAll() {
		// TODO Auto-generated method stub
		return patRepo.findAll();
	}

}
