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
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.service.IDiagnosticTestService;

/*
 *DiagnosticTest Controller
 *Author: Kewal Darbeshwar 
 *Date created: 10/01/2022
 */

/************************************************************************************
 * Class: DiagnosticTestController
 * Description: It is the Controller class for Dignostic Test
 * @RestController annotation is used to create RESTful web services using Spring MVC
 * @RequestMapping It is used to map web requests onto specific handler classes and/or handler methods.
 * @CrossOrigin annotation enables cross-origin resource sharing only for this specific method
 * @Autowired annotation can be used to autowire bean on the setter method
 * @GetMapping is a composed annotation that acts as a shortcut for @RequestMapping
 * @RequestBody annotation allows us to retrieve the request's body
 * @PathVariable it indicates that a method parameter should be bound to a URI template variable
 * Author: Kewal Darbeshwar
 * Created Date: 10/01/2022
 * 
 ************************************************************************************/
@RestController
@RequestMapping("/DiagnosticTest")
public class DiagnosticTestController {
	@Autowired
	IDiagnosticTestService dtestService;

	@Autowired
	LoginController logCon;

	/************************************************************************************
	 * Method: getAllTests 
	 * Description: It is used to retrieve all the Tests
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * 
	 ************************************************************************************/
	@GetMapping("/getAllTests")
	public List<DiagnosticTest> getAllTest() throws Exception {
		return dtestService.getAllTest();
	}

	
	/************************************************************************************
	 * Method: AddNewTest 
	 * @returns dt: It returns New test
	 * @PostMapping annotated methods handle the HTTP POST requests matched with given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/	
	
	
	@PostMapping("/addNewTest")
	public DiagnosticTest addNewTest(@RequestBody DiagnosticTest test) throws Exception {
		return dtestService.addNewTest(test);
	}

	/************************************************************************************
	 * Method: getTestsOfDiagnosticCenter 
	 * Description: It is used to retrieve all the Tests from
	 *				 the DiagnosticCenter table
	 * @returns List<DiagnosticTest>: It returns List of DiagnosticTests with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping. 
	 ************************************************************************************/
	
	
	@GetMapping("/getTestofDiagnosticCenter/{centerId}")
	public List<DiagnosticTest> getTestsOfDiagnosticCenter(@PathVariable int centerId) throws Exception {
		try {
			dtestService.getTestsOfDiagnosticCenter(centerId);
		} catch (Exception e) {
			throw new DataNotFoundInDataBase("Diagnostic center with given id not found");
		}
		return dtestService.getTestsOfDiagnosticCenter(centerId);
	}

	
	/************************************************************************************
	 * Method: getTestById 
	 * Description: It is used to retrieve all the Tests from
	 *				 the DiagnosticTest table
	 * @returns It returns DiagnosticTests with details by Id
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @PathVariable: It is used to handle template variables in the request URI mapping. 
	 *               
	 ************************************************************************************/
	
	@GetMapping("/getTestById/{diagnosticTestid}")
	public DiagnosticTest getTestById(@PathVariable int diagnosticTestid) throws Exception{
		try {
			dtestService.getTestById(diagnosticTestid);
		}
		catch(Exception e) {
			throw new DataNotFoundInDataBase("Diagnostic test with given id not found");
			
		}
		return dtestService.getTestById(diagnosticTestid);
	}

	
	/************************************************************************************
	 * Method: UpdateTestDetail 
	 * @returns tr: It returns updateed Test Details
	 * @PutMapping: It is used to handle the HTTP PUT requests matched with given
	 *              URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/	
	
	
	@PutMapping("/updateTestDetail")
	public DiagnosticTest updateTestDetail(@RequestBody DiagnosticTest test)
			throws DataNotFoundInDataBase, ForBiddenException {
		return dtestService.updateTestDetail(test);
	}

	
	/************************************************************************************
	 * Method: RemoveFromDiagnosticCenter
	 * Description: It is used to delete a Test record from DiagnosticCenter table
	 * @DeleteMapping: It is used to handle the HTTP DELETE requests matched with
	 *                 given URI expression.
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping.
	 ************************************************************************************/	
	
	
	@DeleteMapping("/removeTest/{centerId}/{test}")
	public DiagnosticTest removeTestFromDiagnosticCenter(@PathVariable int centerId, @PathVariable int test)
			throws Exception {
		return dtestService.removeTestFromDiagnosticCenter(centerId, test);
	}
}
