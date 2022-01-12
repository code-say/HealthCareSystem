package com.cg.healthcare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.exception.TestResultNotFoundException;
import com.cg.healthcare.service.ITestResultService;

/*
 * TestResult Service Layer
 * Author:Achuth Bhaskar
 * date created:09/01/2022
 */



@RestController
@RequestMapping("/testresult")
public class TestResultController {
	@Autowired
	ITestResultService testresultService;

	@Autowired
	LoginController logCon;

	/************************************************************************************
	 * Method: addTestResult 
	 * Description: It is used to add TestResults into TestResult table
	 * @returns tr: It returns TestResult with details
	 *  @PostMapping: It is used to handle the HTTP POST requests matched with given
	 *               URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type.
	 ************************************************************************************/


	@PostMapping("/addresult")
	public TestResult addTestResult(@RequestBody TestResult tr) throws ForBiddenException, DataAlreadyExists {
		return testresultService.addTestResult(tr);
	}
	
	/************************************************************************************
	 * Method: updateTestResult 
	 * Description: It is used to update a TestResult record from TestResult table
	 * @returns tr: It returns TestResult with details
	 * @PutMapping: It is used to handle the HTTP PUT requests matched with given
	 *              URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/
	
	
	@PutMapping("/updateresult")
	public TestResult updateResult(@RequestBody TestResult tr) throws DataNotFoundInDataBase, ForBiddenException {
		return testresultService.updateResult(tr);
	}

	/************************************************************************************
	 * Method: RemoveTestResult 
	 * Description: It is used to delete a TestResult record from TestResult table
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	 * @DeleteMapping: It is used to handle the HTTP DELETE requests matched with
	 *                 given URI expression.
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping.
	 ************************************************************************************/
	
	
	@DeleteMapping("/removeresult/{id}")
	public List<TestResult> removeTestResult(@PathVariable int id) throws ForBiddenException, TestResultNotFoundException {
		return testresultService.removeTestResult(id);
	}
	
	/************************************************************************************
	 * Method: viewresultsbypatient 
	 * Description: It is used to retrieve the TestResult from
	 *				 the TestResult table by patientid
	 * @returns List<TestResult>: It returns List of TestResult with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	@GetMapping("/viewresultsbypatient/{patientID}")
	public List<TestResult> viewResultsByPatient(@PathVariable int patientID) throws Exception {
		Patient pat = new Patient();
		try {
			pat.setPatientId(patientID);
		} catch (Exception e) {
			throw new Exception("This is Not An ID");
		}
		return testresultService.viewResultsByPatient(pat);
	}
	
	/************************************************************************************
	 * Method: getAllTestResults 
	 * Description: It is used to retrieve all the TestResult from
	 *				 the TestResult table
	 * @returns List<TestResult>: It returns List of TestResult with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/
	

	@GetMapping("/getAllTestResults")
	public List<TestResult> getAllTestResults(){
		return testresultService.getAll();
	}

	/************************************************************************************
	 * Method: resultbyid 
	 * Description: It is used to retrieve all the TestResult from
	 *				 the TestResult table by id
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	@GetMapping("/resultbyid/{id}")
	public TestResult getById(@PathVariable int id) throws DataNotFoundInDataBase {
		return testresultService.getById(id);
	}
	

}

