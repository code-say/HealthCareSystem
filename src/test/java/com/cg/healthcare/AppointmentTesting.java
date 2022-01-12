package com.cg.healthcare;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cg.healthcare.dao.IAppointmentRepository;

import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.AppointmentStatus;
import com.cg.healthcare.service.IAppointmentServiceImpl;

/****************************
 * Class: AppointmentTesting
 * Description: It is the Testing class for Appointment Test
 * @Autowired annotation can be used to autowire bean on the setter method
 * @BeforeEach is used to signal that the annotated method should be executed before 
               each @Test method in the current test class.
 * @Test which tells the JUnit that the public void method in which it is used can run as a test case 
 * @Order annotation defines the sorting order of an annotated component or bean         
 * Author: Yash	A Dhamdhere
 * Created Date: 11-01-2022
 * 
 ****************************/

@SpringBootTest
public class AppointmentTesting extends AbstractTest {
	Appointment appoint = new Appointment();
	
	/*
	@Autowired
	IAppointmentRepository appointRepo;
	*/
	
	
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
	}
	//appoint.setAppointmentDate(LocalDate.now());
	@Test
	@Order(1)
	public void addApointment() throws Exception
	{
		String uri="/Appointment/addappointment";
		appoint.setAppointmentid(100);
		appoint.setApprovalStatus(AppointmentStatus.cancelled);
		String inputJson =super.mapToJson(appoint);
		
		MvcResult mvcResult =mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
	
	@Test
	@Order(2)
	public void viewAppointments() throws Exception
	{
		String uri="/Apoointment/viewappointments";
		MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	@Test
	@Order(3)
	public void removeAppointment() throws Exception
	{
		String uri ="/Appointment/removeappointment";
		MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
	@Test
	@Order(4)
	public void viewAppointment() throws Exception
	{
		String uri="/Appointment/viewappointment";
		MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		assertEquals(404, mvcResult.getResponse().getStatus());
	}
	
	@Test
	@Order(5)
	public void updateAppointment() throws Exception
	{
		String uri = "/Appointment/updateappointment";
		MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.put(uri)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
		
	}
	
	@Test
	@Order(6)
	public void getAppointmentList() throws Exception
	{
		String uri = "/Appointment/getappointmentlist";
		MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
}
