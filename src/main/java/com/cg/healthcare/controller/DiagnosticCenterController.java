package com.cg.healthcare.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cg.healthcare.dao.IDiagnosticCenterRepository;
import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.DiagnosticCenterNotFoundException;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.loginmodule.LoginService;
import com.cg.healthcare.service.IAppointmentService;
import com.cg.healthcare.service.IDiagnosticCenterService;
import com.cg.healthcare.service.IDiagnosticCenterServiceImpl;
import com.cg.healthcare.service.IDiagnosticTestService;
/*
 * Diagnosticcenter Service Implementation Layer
 * Author:sachin rathod
 * date created:07/01/2022
 *This is Diagnosticcenter Service
 */
@Controller
@ResponseBody
@RequestMapping("/DiagnosticCenter")
public class DiagnosticCenterController {

	    @Autowired
	    IDiagnosticCenterService diagnostic_repo; 
	    
	    @Autowired
		LoginService logServ;
	   
	    
	   
	    /************************************************************************************
		 * Method: getDiagnosticCenter
		 * Description: It is used to retrieve quote from 
		 * 				getDiagnosticCenter table 
		 * @returns  It returns getDiagnosticCenter with details
		 * @GetMapping: annotated methods handle the HTTP GET requests matched with
		 *              given URI expression
		 * @PathVariable: It is used to handle template variables in the request URI
		 *                mapping.
		 * Created By-Sachin Rathod
	     * Created Date 10 -01-2022 
		 ************************************************************************************/
	    
	    
	    
	//1 
	    @GetMapping("/getDiagnosticCenters")
	    public List<DiagnosticCenter> getAllDiagnosticCenters() throws ForBiddenException{
	        return diagnostic_repo.getAllDiagnosticCenters();
	    
	    }
	    
	    
	    
	    
	    /****************************
		 * Method: addDiagnosticCenter
		 * Description: It is used to add DiagnosticCenter into healthcare table
		 * @returns DiagnosticCenter It returns DiagnosticCenter with details
		 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
		 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
		 * Created By-Sachin Rathod
	     * Created Date 10 -01-2022 
		 * 
		 ****************************/
	    @PostMapping("/addDiagnosticCenter")
		public DiagnosticCenter addDiagnosticCenter(@RequestBody DiagnosticCenter diagnosticCenter) throws Exception {
			return diagnostic_repo.addDiagnosticCenter(diagnosticCenter);
	    }
	    
	     /*****************************
	      * Method: getDiagnosticCenter
		 * Description: It is used to retrieve DiagnosticCenter from 
		 * 				getDiagnosticCenter table by checking id
		 * @returns  It returns getDiagnosticCenter with details
		 * @GetMapping: annotated methods handle the HTTP GET requests matched with
		 *              given URI expression
		 * @PathVariable: It is used to handle template variables in the request URI
		 *                mapping.
		 * Created By-Sachin Rathod
	     * Created Date 10 -01-2022 
	     * ****************************/
	    
	
	    @GetMapping("/getDiagnosticCenter/{diagnosticCenterId}")
	    public DiagnosticCenter getDiagnosticCenterById(@PathVariable int diagnosticCenterId)throws DiagnosticCenterNotFoundException,DataNotFoundInDataBase,ForBiddenException{
	        return diagnostic_repo.getDiagnosticCenterById(diagnosticCenterId);
	    }
	    
	    /*****************************
	     * Method: updateDiagnosticCenter
		 * Description: It is used to update DiagnosticCenter into DiagnosticCenter table
		 *
		 * @returns  It returns DiagnosticCenter with details
		 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
		 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
		 * Created By-Sachin Rathod
	     *Created Date -  10-01-2022 
		 * 
		 ****************************/
	    
	    @PutMapping("/updateDiagnosticCenter")
		public DiagnosticCenter updateDiagnosticCenter(@RequestBody DiagnosticCenter diagnosticCenter)throws DataNotFoundInDataBase,ForBiddenException
		{
			return diagnostic_repo.updateDiagnosticCenter(diagnosticCenter);
		
		}
	    
	    /****************************
		 * Method:  viewTestDetail
		 * Description: It is used to view TestDetail by Id from TestDetail table
		 * @returns DiagnosticCenter It returns viewTestDetail with details
		 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
		 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
		 * Created By-Sachin Rathod
	     *Created Date 10- 01-2022 
		 * 
		 ****************************/
	    
	    

	    @GetMapping("/viewTestDetail/{diagnosticCenterId}")
	    public List<DiagnosticTest> viewTestDetails(@PathVariable int diagnosticCenterId) throws ForBiddenException
		{
			return diagnostic_repo.viewTestDetails(diagnosticCenterId);
		};
		
		
		 /****************************
		 * Method: addDiagnosticCenter
		 * Description: It is used to add DiagnosticCenter into healthcare table
		 * @returns DiagnosticCenter It returns DiagnosticCenter with details
		 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
		 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
		 * Created By-Sachin Rathod
	     * Created Date 10 -01-2022 
		 * 
		 ****************************/
		
		
		
		
		@PostMapping("/addTest/{diagnosticcenterId}/{testid}")
		public DiagnosticTest  addTest(@PathVariable int diagnosticCenterId,@PathVariable int testid) throws DataNotFoundInDataBase,ForBiddenException {
			return diagnostic_repo.addTest(diagnosticCenterId, testid);
	    }
		
		/************************************************************************************
				 * Method: getDiagnosticCenter
				 * Description: It is used to retrieve from 
				 * 				getDiagnosticCenter table 
				 * @returns  It returns getDiagnosticCenter with details
				 * @GetMapping: annotated methods handle the HTTP GET requests matched with
				 *              given URI expression
				 * @PathVariable: It is used to handle template variables in the request URI
				 *                mapping.
				 * Created By-Sachin Rathod
			     * Created Date 10 -01-2022 
				 ************************************************************************************/
		
		
		
		@GetMapping("/getDiagnosticCenter/{centername}")
	    public DiagnosticCenter getDiagnosticCenter(@PathVariable String centername) throws DataNotFoundInDataBase,ForBiddenException{
	        return diagnostic_repo.getDiagnosticCenter(centername);
	    }
	   
			/****************************
			 * Method: removeDiagnosticCenter
			 * Description: It is used to remove DiagnosticCenter from DiagnosticCenter table.
			 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
			 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
			 * Created By-Sachin Rathod
		     *Created Date -  10-01-2022 
			 * 
			 ****************************/
		
		
		@DeleteMapping("/removeDiagnosticCenter/{id}")
		public DiagnosticCenter removeDiagnosticCenter(@PathVariable int id)throws DiagnosticCenterNotFoundException,ForBiddenException {
			return diagnostic_repo.removeDiagnosticCenter(id);
			}
			
			
			  /*****************************
			  * Method: getListOfAppointments
			 * Description: It is used to retrieve quote from 
			 * 				getListOfAppointments table by checking id
			 * @returns  It returns getListOfAppointments with details
			 * @GetMapping: annotated methods handle the HTTP GET requests matched with
			 *              given URI expression
			 * @PathVariable: It is used to handle template variables in the request URI
			 *                mapping.
			 * Created By-Sachin Rathod
		     * Created Date 10 -01-2022 
		     *****************************/
			
	    

		@GetMapping("/appointments/{centerName}")
		public List<Appointment> getListOfAppointments(@PathVariable String centerName) throws ForBiddenException{
			return diagnostic_repo.getListOfAppointments(centerName);
	    
	    
	    
			 }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
		
	    
	

}

