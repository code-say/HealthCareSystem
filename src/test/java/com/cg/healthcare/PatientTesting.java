package com.cg.healthcare;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.User;

@SpringBootTest
public class PatientTesting extends AbstractTest{

	Patient pat = new Patient("RAM", "1234567890", 23, "MALE", null);
	
	@Override
	 @BeforeEach
	 public void setUp() throws Exception
	 {
		 super.setUp();
	 }
	
	@Test
	public void addPatient() throws Exception
	{
		String uri = "/patient/registerpatient";
		 String inputJson = super.mapToJson(pat);
		 MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
            .andReturn();
		 assertEquals(500, mvcResult.getResponse().getStatus());
	}
	
// /RAM
	@Test
	public void viewPatient() throws Exception{
		String uri = "/patient/viewpatient";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri)).andReturn();
		assertEquals(404, mvcResult.getResponse().getStatus());
	}
	@Test
	public void updatePatient() throws Exception{
		String uri = "/patient/updatepatient";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void deletePatient() throws Exception{
		String uri = "/patient/deletePatient";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.delete(uri)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
}
