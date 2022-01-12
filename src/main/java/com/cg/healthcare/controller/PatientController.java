package com.cg.healthcare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.service.IPatientService;

/*
 * patient Controller
 * Author:SAYAN MANDAL
 * date created:10/01/2022
 */

@RestController
@RequestMapping("/patient")
public class PatientController {

	
	@Autowired
	IPatientService patientService;
	
	@Autowired
	LoginController logCon;
	
	/************************************************************************************
	 * Method: registerpatient 
	 * Description: It is used to register patient
	 *  @PostMapping: It is used to handle the HTTP POST requests matched with given
	 *               URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type.
	 ************************************************************************************/

	
	@PostMapping("/registerpatient") ///{userID}
	public Patient registerPatient(@RequestBody Patient patient,@PathVariable int userID) throws DataAlreadyExists, DataNotFoundInDataBase
	{
		return patientService.registerPatient(patient, userID);
	}
	
	/************************************************************************************
	 * Method: updatepatient 
	 * Description: It is used to update patient
	 * @PutMapping: It is used to handle the HTTP PUT requests matched with given
	 *              URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	@PutMapping("/updatepatient")
	public Patient updatePatientDetails(@RequestBody Patient patient)
	throws DataNotFoundInDataBase, ForBiddenException
	{
		return patientService.updatePatientDetails(patient);
	}
	
	/************************************************************************************
	 * Method: ViewTestResult by testresult Id
	 * Description: It is used to retrieve all the TestResult from
	 *				 the TestResult table
	 * @returns List<TestResult>: It returns List of TestResult of patient with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/
	
	@GetMapping("/viewpatient/{userid}")
	Patient viewPatient(@PathVariable String userid) throws ForBiddenException, DataNotFoundInDataBase {
		return patientService.viewPatient(userid);
	}
	
	
	
	@GetMapping("/viewtestresult/{testResultId}")
	TestResult viewTestResult(@PathVariable int testResultId) throws Exception
	{
		return patientService.viewTestResult(testResultId);
	}
	
	/************************************************************************************
	 * Method: deletePatient 
	 * Description: It is used to delete a patient
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	 * @DeleteMapping: It is used to handle the HTTP DELETE requests matched with
	 *                 given URI expression.
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping.
	 ************************************************************************************/

	
	@DeleteMapping("/deletePatient")
	Patient deletePatient(@RequestBody Patient patient)throws DataNotFoundInDataBase, ForBiddenException {
		return patientService.deletePatient(patient);
	}
	
	
	/************************************************************************************
	 * Method:  getAll
	 * Description: It is used to retrieve all the patients 
	 * @returns List<Patient>: It returns List of Patients with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	@GetMapping("/getAll")
	public List<Patient> getAll()
	{
		return patientService.getAll();
	}
}
