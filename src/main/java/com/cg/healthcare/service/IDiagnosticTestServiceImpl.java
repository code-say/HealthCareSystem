package com.cg.healthcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.healthcare.dao.IDiagnosticTestRepository;
import com.cg.healthcare.dao.manualqueries.QueryClassPersisitContext;
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;

/*
 *DiagnosticTestServiceImpl
 *Author: Kewal Darbeshwar 
 *Date created: 09/01/2022
 */
/************************************************************************************
 * Class: IDiagnosticTestServiceImpl
 * Description: It is implementation of the service layer
 * @Service annotation is used in your service layer and annotates classes that perform service tasks
 * @Transactional annotation is the metadata that specifies the semantics of the transactions on a method
 * @Autowired annotation can be used to autowire bean on the setter method
 * Author: Kewal Darbeshwar
 * Created Date: 09/01/2022
 * 
 ************************************************************************************/

@Service
public class IDiagnosticTestServiceImpl implements IDiagnosticTestService {

	@Autowired
	IDiagnosticTestRepository testRepo;
	@Autowired
	QueryClassPersisitContext qcp;
	
	/** 
	 * @return List<DiagnosticTest>
	 */

	/************************************************************************************
	 * Method: getAllTest
	 * Description: It is used to get All Test 
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/
	@Override
	public List<DiagnosticTest> getAllTest() {
		return testRepo.findAll();
	}

	/************************************************************************************
	 * Method: addNewTest
	 * Description: It is used to add new Test 
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/		
	@Override
	public DiagnosticTest addNewTest(DiagnosticTest test) throws DataAlreadyExists {
		if(testRepo.existsById(test.getDiagonasticTestid())) throw new DataAlreadyExists("Test Already Exists Use Update To Change");
		return testRepo.saveAndFlush(test);
	}

	/************************************************************************************
	 * Method: getTestsOfDiagnosticCenter
	 * Description: It get the Test from Diagnostic Center
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/		
	@Override
	public List<DiagnosticTest> getTestsOfDiagnosticCenter(int centerId) throws DataNotFoundInDataBase {
		List<DiagnosticTest> tests = qcp.getTestsOfDiagnosticCenter(centerId);
		if(tests.size()==0)throw new DataNotFoundInDataBase("No Diagnostic Tests Exist");
		return tests;
	}

	/************************************************************************************
	 * Method: getTestById
	 * Description: It get the Test by Id
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/
	@Override
	public DiagnosticTest getTestById(int diagnosticTestid) throws DataNotFoundInDataBase {
		//DiagnosticTest test = qcp.getTestById(diagnosticTestid);
		return testRepo.findById(diagnosticTestid).get();	
	}
	
	/************************************************************************************
	 * Method: updateTestDetail
	 * Description: It is used to update Test Detail
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/
	@Override
	public DiagnosticTest updateTestDetail(DiagnosticTest test) throws DataNotFoundInDataBase{
		
		if(!testRepo.existsById(test.getDiagonasticTestid())) throw new DataNotFoundInDataBase("No test Exist with id : "+test.getDiagonasticTestid()+" To Update");
		
		return testRepo.saveAndFlush(test);
	}

	/************************************************************************************
	 * Method: removeTestFromDiagnosticCenter
	 * Description: It is used to remove Test from Diagnostic Center
	 * @Override: It is used to override the JpaRepository methods for performing CURD operations.
	 * Author: Kewal Darbeshwar
         * Created Date: 09/01/2022
	 * 
	 ************************************************************************************/	
	@Override
	public DiagnosticTest removeTestFromDiagnosticCenter(int centerId, int test) throws Exception {
		return qcp.removeTestFromDiagnosticCenter(centerId, test);
	}


	

}
