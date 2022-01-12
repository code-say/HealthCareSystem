package com.cg.healthcare;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cg.healthcare.dao.ITestResultRepository;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.entities.User;


/*
 * Test for TestResult Service Layer
 * Author:Achuth Bhaskar
 * date created:09/05/2021
 */

@SpringBootTest
public class TestResultTesting extends AbstractTest {

	TestResult tr = new TestResult(2,10929405,"DP-Test",null);
	
	/*
	@Autowired
	private ITestResultRepository testresultrepo;
	*/
	
	 @BeforeEach
	 public void setUp() throws Exception
	 {
		 super.setUp();
	 }
	 
	 /************************************************************************************
		 * Method: addTest
		 * Description: It is used to test whether the service layer is successfuly adding testresult
	************************************************************************************/
		
	
	@Test
	@Order(1)
	public void addTest() throws Exception
	{
		String uri = "/testresult/addresult";
		
		 String inputJson = super.mapToJson(tr);
		 MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
            .andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	/************************************************************************************
	 * Method: removeTest
	 * Description: It is used to test whether the service layer is successfuly removing testresult
	 ************************************************************************************/

	@Test
	@Order(2)
	public void removeTest() throws Exception
	{
		String uri = "/testresult/removeresult/11";
		 MvcResult mvcResult = mvc.perform(
         MockMvcRequestBuilders.delete(uri)).andReturn();
       
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}

	/************************************************************************************
	 * Method: viewTest
	 * Description: It is used to test whether the service layer is successfuly Viewing/Displaying testresult
	 ************************************************************************************/
	
	@Test
	@Order(3)
	public void viewTest() throws Exception
	{
		String uri = "/testresult/viewresultsbypatient";
		 MvcResult mvcResult = mvc.perform(
		 MockMvcRequestBuilders.get(uri)).andReturn();
		 
		 assertEquals(404, mvcResult.getResponse().getStatus());
		       
	}
	
	/************************************************************************************
	 * Method: getAllTest
	 * Description: It is used to test whether the service layer is successfuly Getting all testresults
	 ************************************************************************************/
	@Test
	@Order(4)
	public void getAllTest() throws Exception
	{
		String uri = "/testresult/getAllTestResults";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri)).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
	}
	
	/************************************************************************************
	 * Method:updateTest
	 * Description: It is used to test whether the service layer is successfuly updating all testresults
	 ************************************************************************************/

	@Test
	@Order(5)
	public void updateTest() throws Exception
	{
		String uri = "/testresult/updateresult/13";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri)).andReturn();
		
		assertEquals(404, mvcResult.getResponse().getStatus());
		
	}

}
