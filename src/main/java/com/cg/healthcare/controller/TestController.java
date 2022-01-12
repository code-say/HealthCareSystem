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
import com.cg.healthcare.exception.TestNotFoundException;
import com.cg.healthcare.service.ITestService;

/*
 * Test Controller
 * Author:Achuth Bhaskar
 * date created:09/01/2022
 */

@RestController
@RequestMapping("/TestResult")
public class TestController {
	
	/*Autowired Test Service Class object */
	@Autowired
	ITestService testimpl;
	
	@Autowired
	LoginController logCon;
	
	/************************************************************************************
	 * Method: addTest 
	 * Description: It is used to add Test into DiagnosticTest table
	 * @returns tr: It returns Test with details
	 *  @PostMapping: It is used to handle the HTTP POST requests matched with given
	 *               URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type.
	 ************************************************************************************/

	
	@PostMapping("/addTest")
	public DiagnosticTest addTest(@RequestBody DiagnosticTest ts) //throws DataAlreadyExists 
	{
		return testimpl.addTest(ts);
	}
	
	/************************************************************************************
	 * Method: updateTest 
	 * @returns tr: It returns TestResult with details
	 * @PutMapping: It is used to handle the HTTP PUT requests matched with given
	 *              URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	@PutMapping("/UpdateTest")
	public DiagnosticTest updateTest(@RequestBody DiagnosticTest ts) throws  TestNotFoundException 
	{
		return testimpl.updateTest(ts);
	}
	
	
	/************************************************************************************
	 * Method: RemoveTest
	 * Description: It is used to delete a Test record from DiagnosticTest table
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	 * @DeleteMapping: It is used to handle the HTTP DELETE requests matched with
	 *                 given URI expression.
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping.
	 ************************************************************************************/

	
	@DeleteMapping("/RemoveTest/{id}")
	public String removeTest(@PathVariable DiagnosticTest ts) throws TestNotFoundException {
		// TODO Auto-generated method stub
		return testimpl.removeTest(ts);
	}

	/************************************************************************************
	 * Method: ViewAllTest 
	 * Description: It is used to retrieve all the Tests from
	 *				 the DiagnosticTest table
	 * @returns List<DiagnosticTest>: It returns List of DiagnosticTests with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	@GetMapping("/ViewAllTest")
	public List<DiagnosticTest> viewAllTest(@PathVariable String criteria) throws TestNotFoundException {
			
		return testimpl.viewAllTest(criteria);
	}
}
