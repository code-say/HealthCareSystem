package com.cg.healthcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.healthcare.dao.IDiagnosticTestRepository;
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.exception.TestNotFoundException;

/*
 * Test Service Implementation Layer
 * Author:Achuth Bhaskar
 * date created:07/01/2022
 */

@Service
@Transactional
public class ITestServiceImpl implements ITestService{

	
	@Autowired
	IDiagnosticTestRepository diagnostictestdao;
	
	/************************************************************************************
	 * Method: addTest
	 * Description: It is used to add Tests into DiagnosticTest table
	 * @returns quote: It returns ts object
	 *@Override It is used to override Test Service Interface
	 ************************************************************************************/
	
	
	@Override
	public DiagnosticTest addTest(DiagnosticTest test) 
	{
		diagnostictestdao.save(test);
		return test;
	}
	
	/************************************************************************************
	 * Method: updateTest 
	 * Description: It is used to update Tests in the DiagnosticTest table
	 * @returns quote: It returns ts object
	 *@Override It is used to override Test Service Interface
	 ************************************************************************************/


	@Override
	public DiagnosticTest updateTest(DiagnosticTest test) throws TestNotFoundException {
		if(!diagnostictestdao.existsById(test.getDiagonasticTestid()))
			throw new TestNotFoundException("Test not found");
			diagnostictestdao.save(test);
			return test;
	}
	
	/************************************************************************************
	 * Method: removeTest 
	 * Description: It is used to remove Test from DiagnosticTest table
	 * @returns quote:  It returns success message if Test is deleted otherwise throws exception
	 *@Override It is used to override Test Service Interface
	 ************************************************************************************/


	@Override
	public String removeTest(DiagnosticTest test) throws TestNotFoundException {
		if(!diagnostictestdao.existsById(test.getDiagonasticTestid()))
			throw new TestNotFoundException("Test not found");
			diagnostictestdao.delete(test);
		return "TestResult deleted successfully";
	}
	
	/************************************************************************************
	 * Method: viewAllTest 
	 * Description: It is used to view all the tests in a particular criteria
	 * @returns quote:   It returns the List of all tests  
	 *@Override It is used to override Test Service Interface
	 ************************************************************************************/

	@Override
	public List<DiagnosticTest> viewAllTest(String criteria) {
		
		List<DiagnosticTest> set = diagnostictestdao.findAll();
		return set;
	}

}
