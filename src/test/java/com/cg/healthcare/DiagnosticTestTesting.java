package com.cg.healthcare;

import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cg.healthcare.dao.IDiagnosticTestRepository;
import com.cg.healthcare.entities.DiagnosticTest;

/*
 *DiagnosticTest Testing
 *Author: Kewal Darbeshwar 
 *Date created: 11/01/2022
 */
/************************************************************************************
 * Class: DiagnosticTestTesting
 * Description: It is the Testing class for Dignostic Test
 * @Autowired annotation can be used to autowire bean on the setter method
 * @BeforeEach is used to signal that the annotated method should be executed before 
               each @Test method in the current test class.
 * @Test which tells the JUnit that the public void method in which it is used can run as a test case 
 * @Order annotation defines the sorting order of an annotated component or bean         
 * Author: Kewal Darbeshwar
 * Created Date: 11/01/2022
 * 
 ************************************************************************************/
@SpringBootTest
public class DiagnosticTestTesting extends AbstractTest  {
	
	DiagnosticTest dtest = new DiagnosticTest("COVID-19", 1200 ,"100","Low",null);
	
	
	@Autowired
	private IDiagnosticTestRepository diatestRepo;
	
	
	 @BeforeEach
	 public void setUp() throws Exception
	 {
		 super.setUp();
	 }
	 
	/*****************************************************************************************************
	* Method: AddTest
	* Description: It is used to test whether the service layer is successfuly adding DiagnosticTest
    *****************************************************************************************************/	
	@Test
	public void AddTest() throws Exception
	{
		String uri = "/DiagnosticTest/addNewTest";
			
		 String inputJson = super.mapToJson(dtest);
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}	 
	
	 /*****************************************************************************************************
	 * Method: getAllTest
	 * Description: It is used to test whether the service layer is successfuly Getting all DiagnosticTest
     *****************************************************************************************************/	 

	@Test
	@Order(2)
	public void getAllTest() throws Exception
	{
		String uri = "/DiagnosticTest/getAllTests";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
			
		assertEquals(200, mvcResult.getResponse().getStatus());
			
	}
	
	/*****************************************************************************************************
	* Method: TestofDiagnosticCenter
	* Description: It is used to test whether the service layer is successfuly Viewing/Displaying TestofDiagnosticCenter
    *****************************************************************************************************/		

	@Test
	@Order(3)
	public void TestofDiagnosticCenter() throws Exception
	{
		String uri = "/DiagnosticTest/getTestofDiagnosticCenter";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
			
		assertEquals(404, mvcResult.getResponse().getStatus());		
	}
	
	/*****************************************************************************************************
	* Method: TestById
	* Description: It is used to test whether the service layer is successfuly Viewing/Displaying Test by Id
    *****************************************************************************************************/	
/*
	@Test
	@Order(4)
	public void TestById() throws Exception
	{
		String uri = "/DiagnosticTest/getTestById/1000";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
			
		assertEquals(200, mvcResult.getResponse().getStatus());		
	}
	*/
	/*****************************************************************************************************
	* Method: UpdateTest
	* Description: It is used to test whether the service layer is successfuly updating all DiagnosticTest
    *****************************************************************************************************/	

	@Test
	@Order(5)
	public void UpdateTest() throws Exception
	{
		String uri = "/DiagnosticTest/updateTestDetail/2";
		
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		 assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	/*****************************************************************************************************
	* Method: RemoveTest
	* Description: It is used to test whether the service layer is successfuly removing DiagnosticTest
    *****************************************************************************************************/	

	@Test
	@Order(6)
	public void RemoveTest() throws Exception
	{
		String uri = "/DiagnosticTest/removeTest/14";

		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
		 assertEquals(404, mvcResult.getResponse().getStatus());
	}
	

		
}