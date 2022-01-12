package com.cg.healthcare;

import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cg.healthcare.entities.User;


@SpringBootTest
public class AdminTest extends AbstractTest {

	
	/*
	@Autowired
	private IAdminRepository repo;
	*/
	
	 @BeforeEach
	 public void setUp() throws Exception
	 {
		 super.setUp();
	 }
	

	@Test
	public void regAdmin() throws Exception
	{
		String uri = "/admin/registeradmin";
		User user = new User("mandal","mandal","");
		 String inputJson = super.mapToJson(user);
		 MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
            .andReturn();
		 assertEquals(200, mvcResult.getResponse().getStatus());
	}
	 
	/*
		@Test
		public void delAdmin() throws Exception
		{
			String uri = "/admin/deleteAdmin/6";

			 MvcResult mvcResult = mvc.perform(
	            MockMvcRequestBuilders.delete(uri))
	            .andReturn();
			 assertEquals(200, mvcResult.getResponse().getStatus());
		}
	*/
	
}
