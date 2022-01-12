package com.cg.healthcare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.AppointmentNotFoundException;
import com.cg.healthcare.exception.PatientNotFoundException;
import com.cg.healthcare.exception.TestResultNotFoundException;
import com.cg.healthcare.service.IAppointmentService;


@RestController
@RequestMapping("/Appointment")
public class AppointmentController {
	
	@Autowired
	IAppointmentService appserv;
	
	
	/****************************
	 * Method: addAppointment
	 * Description: It is used to add Appointment into healthcare table
	 * @returns It returns Appointment  indetails
	 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
	 * Created By-Yash A Dhamdhere
     	 * Created Date 10 -01-2022 
	 * 
	 ****************************/
	

	@PostMapping(value = "/addappointment")
	public Appointment addAppointment(@RequestBody Appointment appointment,@RequestParam(required = true) String patientID, @RequestParam(required = true) String diagnosticCenterID,@RequestParam(required = true) List<Integer> testIds) throws Exception {
		return appserv.addAppointment(appointment, patientID, diagnosticCenterID, testIds);
	}


	/****************************
	 * Method: removeAppointment
	 * Description: It is used to remove Appointments from Appointment table.
	 *@DeleteMapping: annotation maps HTTP DELETE requests onto specific handler methods.
	 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
	 * Created By-Yash A Dhadmhere
    	 *Created Date -  10-01-2022 
	 * 
	 ****************************/
	

	@DeleteMapping("/removeappointment")
	public Appointment removeAppointment(@RequestBody Appointment appointment) throws Exception {
		return appserv.removeAppointment(appointment);
	}

	
	/****************************
		 * Method: viewAppointments
		 * Description: It is used to view Appointment of Pateint from the healthcare table
		 * @returns It returns Pateint Appointment with details with help of pateintId
		 *@GetMapping : Annotation for mapping HTTP GET requests onto specific handler methods
		 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
		 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
		 * Created By-Yash A Dhamdhere
	     	 * Created Date 10 -01-2022 
		 * 
		 ****************************/

	
	
	@GetMapping("/viewappointments/{patientId}")
	public List<Appointment> viewAppointments(@PathVariable int patientId) throws Exception {
		return appserv.viewAppointments(patientId);
	}

	
	/****************************
	 * Method: viewAppointment
	 * Description: It is used to view Appointments detalis from the healthcare table
	 * @returns It returns Appointment with details with help of appointmentid
	 *@GetMapping : Annotation for mapping HTTP GET requests onto specific handler methods
	 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
	 * Created By-Yash A Dhamdhere
   	 * Created Date 10 -01-2022 
	 * 
	 ****************************/

	
	@GetMapping("/viewappointment/{appointmentId}")
	public Appointment viewAppointment(@PathVariable int appointmentId) throws Exception {
		return appserv.viewAppointment(appointmentId);
	}

	
	/****************************
	 * Method: updateAppointment
	 * Description: It is used to update Appointments in the healthcare table
	 * @returns It returns Appointment with details with help of appointmentid
	 *@GetMapping : Annotation for mapping HTTP GET requests onto specific handler methods
	 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
	 * @RequestParam is used to read the HTML form data provided by a user and bind it to the request parameter 
	 * Created By-Yash A Dhamdhere
     	 * Created Date 10 -01-2022 
	 * 
	 ****************************/

	
	@PutMapping("/updateappointment")
	public Appointment updateAppointment(@RequestBody Appointment appointment,
			@RequestParam(required = false) List<Integer> testResultId,
			@RequestParam(required = false) String patientID, @RequestParam(required = false) String diagnosticCenterID,
			@RequestParam(required = false) List<Integer> testIds) throws Exception {
		return appserv.updateAppointment(appointment, testResultId, patientID, diagnosticCenterID, testIds);
	}
	
	/****************************
	 * Method: getAppointmentList
	 * Description: It is used to retrive quotes from the getAppointmentList table by checking centerId,test,status
	 * @returns It returns getAppointmentList with details 
	 *@GetMapping : Annotation for mapping HTTP GET requests onto specific handler methods
	 * @PostMapping: It is used to handle the HTTP POST requests matched with given URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain object in method parameter or return type.
	 * @RequestParam is used to read the HTML form data provided by a user and bind it to the request parameter
	 * Created By-Yash A Dhamdhere
     	 * Created Date 10 -01-2022 
	 * 
	 ****************************/


	@GetMapping("/getappointmentlist")
	public List<Appointment> getApppointmentList(@RequestParam String diagnosticCenterid, @RequestParam String testName,
			@RequestParam String appointmentStatus) throws Exception {
		return appserv.getApppointmentList(Integer.parseInt(diagnosticCenterid), testName, appointmentStatus);
	}
	
	@PutMapping("/verify")
	public Appointment verify(@RequestBody Appointment appointment) throws AppointmentNotFoundException {
		
		return appserv.verify(appointment.getAppointmentid(), true);
	}
	
	@PutMapping("/reject")
	public Appointment reject(@RequestBody Appointment appointment) throws AppointmentNotFoundException {
		return appserv.verify(appointment.getAppointmentid() , false);
	}
	
	@GetMapping("/getVerifiable")
	public List<Appointment> verifiable() {
		return appserv.verifiable();
		
	}
	
	@GetMapping("/getWithNoTestResults")
	public List<Appointment> noTestResults(){
		return appserv.noTestResults();
	}
	
	@GetMapping("/getPatient/{AppointmentId}")
	public Patient getPatient(@PathVariable int appID) throws PatientNotFoundException {
		return appserv.getPatient(appID);
	}
	
	@PutMapping("/addTestRes/{AppointmentId}/{testResId}")
	public TestResult addTestRes(@PathVariable int AppointmentId,@PathVariable int testResId) throws AppointmentNotFoundException, TestResultNotFoundException {
		return appserv.setTestResult(AppointmentId,testResId);
	}
	
	@GetMapping("/getAll")
	public List<Appointment> getAll(){
		return appserv.getAll();
	}
	
	
}
