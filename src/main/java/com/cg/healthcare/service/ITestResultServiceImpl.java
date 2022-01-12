package com.cg.healthcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.healthcare.dao.ITestResultRepository;
import com.cg.healthcare.dao.manualqueries.QueryClassPersisitContext;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.TestResultNotFoundException;

/*
 * TestResult Service Implementation Layer
 * Author:Achuth Bhaskar
 * date created:07/01/2022
 */



@Service
public class ITestResultServiceImpl implements ITestResultService {
	
	@Autowired
	ITestResultRepository resultrepo;
	
	@Autowired
	QueryClassPersisitContext qcp;
	
	/************************************************************************************
	 * Method: addTestResult 
	 * Description: It is used to add Test Results into TestResult table
	 * @returns quote: It returns tr object
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/	

	@Override
	public TestResult addTestResult(TestResult tr)  throws DataAlreadyExists{
		
		if(resultrepo.existsById(tr.getTestResultid())) throw new DataAlreadyExists("Test Result Already exists with id :"+tr.getTestResultid());
		return resultrepo.saveAndFlush(tr);
	}
	
	/************************************************************************************
	 * Method: updateTestResult 
	 * Description: It is used to update Test Results into TestResult table
	 * @returns quote: It returns tr object
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/
	
	@Override
	public TestResult updateResult(TestResult tr) throws DataNotFoundInDataBase {
		if(!resultrepo.existsById(tr.getTestResultid()))throw new DataNotFoundInDataBase("TestResult Not Found in DataBase to update");
		return resultrepo.saveAndFlush(tr);
	}

	/************************************************************************************
	 * Method: removeTestResult 
	 * Description: It is used to remove Test Results from TestResult table
	 * @returns quote:  It returns success message if TesTResult is deleted otherwise throws exception
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/

	@Override
	public List<TestResult> removeTestResult(int id) throws TestResultNotFoundException {
		// TODO Auto-generated method stub
		if(!resultrepo.existsById(id)) throw new TestResultNotFoundException("Test Result Does Not Exist  "+ id);
		TestResult tr = resultrepo.findById(id).get();
		resultrepo.deleteById(id);
		return resultrepo.findAll();
		
	}

	/************************************************************************************
	 * Method: viewResultByPatient 
	 * Description: It is used to find particular TestResult by Patient  in quote table
	 * @returns quote:   It returns the specific TestResult object if found otherwise throws exception
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/
	

	@Override
	public List<TestResult> viewResultsByPatient(Patient patient) throws DataNotFoundInDataBase, TestResultNotFoundException {
		List<TestResult> testRes =  qcp.viewResultByPatient(patient);
		if(testRes.size() ==0 )throw new DataNotFoundInDataBase("User/Tests Doesn't Exits");
		return testRes;
	}

	/************************************************************************************
	 * Method: getAll 
	 * Description: It is used to find all TestResults by Patient id in quote table
	 * @returns quote:   It returns the specific TestResult object if found otherwise throws exception
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/
		

	@Override
	public List<TestResult> getAll() {
		return resultrepo.findAll();
	}
	
	/************************************************************************************
	 * Method: getById
	 * Description: It is used to find particular TestResult by Id in quote table
	 * @returns quote:   It returns the specific TestResult object if found otherwise throws exception
	 *@Override It is used to override TestResult Service Interface
	 ************************************************************************************/	

	@Override
	public TestResult getById(int id) throws DataNotFoundInDataBase {
		// TODO Auto-generated method stub
		return resultrepo.findById(id).orElseThrow(()-> new DataNotFoundInDataBase("No Patient With ID "+id));
	}


	


	

}
