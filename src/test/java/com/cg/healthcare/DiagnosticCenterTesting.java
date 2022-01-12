package com.cg.healthcare;

import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cg.healthcare.dao.IDiagnosticCenterRepository;
import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.User;


@SpringBootTest
public class DiagnosticCenterTesting extends AbstractTest {

	
	/*
	@Autowired
	private IDiagnosticCenterRepository repo;
*/
	
	 @BeforeEach
	 public void setUp() throws Exception
	 {
		 super.setUp();
	 }
	 
	
	 @Test
		@Order(1)
		public void getAllDiagnosticCenter() throws Exception
		{
			String uri = "/DiagnosticCenter/getDiagnosticCenters";
			 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
			 assertEquals(405, mvcResult.getResponse().getStatus());
		}
	 

	 @Test
	 @Order(2)
	 public void addDiagnosticCenter() throws Exception
	 {
		 String uri = "/DiagnosticCenter/addDiagnosticCenter";
		 DiagnosticCenter d = new DiagnosticCenter("kol", "267276", "1-2-4", "sachinmc@123.com", null);
		 String inputJson = super.mapToJson(d);
		 MvcResult mvcResult = mvc.perform(
					MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
					.andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
		 String content = mvcResult.getResponse().getContentAsString();
			DiagnosticCenter d1 = super.mapFromJson(content, DiagnosticCenter.class);
			assertEquals("kol", d1.getName());
	 }
	 
	 

	@Test
	@Order(3)
	public void getDiagnosticCenter() throws Exception
	{
		String uri = "/DiagnosticCenter/getDiagnosticCenter";
	
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		 assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	@Test
	@Order(4)
	public void updateDiagnosticCenter() throws Exception
	{
		String uri = "/DiagnosticCenter/updateDiagnosticCenter/3";
		DiagnosticCenter diagnosticCenter1=new DiagnosticCenter("sachin","87868","12-1-22","Ajay@gmail.com",null);
		
		 String inputJson = super.mapToJson(diagnosticCenter1);
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
            .andReturn();
		 assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	

	
	@Test
	@Order(5)
	public void viewTestDetails() throws Exception
	{
		String uri = "/DiagnosticCenter/viewTestDetail";
		
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		 assertEquals(404, mvcResult.getResponse().getStatus());
	}

	/*
	@Test
	@Order(6)
	public void getDiagnosticCenterName() throws Exception
	{
		String uri = "/DiagnosticCenter/getDiagnosticCenter/sachin";
		
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	*/
	/*
	@Test
	@Order(7)
	public void AddDiagnosticCenter() throws Exception
	{
		String uri = "/DiagnosticCenter/removeDiagnosticCenter/{id}/1";
		
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}
	*/
	
	
	@Test
	@Order(7)
	public void removeDiagnosticCenterName() throws Exception
	{
		String uri = "/DiagnosticCenter/removeDiagnosticCenter/18";
		
		 MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	
	
	
	
	
}
