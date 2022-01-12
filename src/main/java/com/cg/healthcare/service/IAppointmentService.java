package com.cg.healthcare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.exception.AppointmentNotFoundException;
import com.cg.healthcare.exception.DataAlreadyExists;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.DiagnosticCenterNotFoundException;
import com.cg.healthcare.exception.InvalidAppointmentStatusException;
import com.cg.healthcare.exception.PatientNotFoundException;
import com.cg.healthcare.exception.TestResultNotFoundException;


@Service
public interface IAppointmentService {
	
	Appointment addAppointment(Appointment appointment, String patientID, String diagnosticCenterID,List<Integer> testsId)	throws DataAlreadyExists, DataNotFoundInDataBase;


	
	Appointment removeAppointment(Appointment appointment) throws AppointmentNotFoundException;

	
	List<Appointment> viewAppointments(int patientId) throws AppointmentNotFoundException, PatientNotFoundException;

	
	Appointment viewAppointment(int appointmentId) throws AppointmentNotFoundException;

	

	Appointment updateAppointment(Appointment appointment, List<Integer> testResultId,String patientID ,
			String diagnosticCenterID, List<Integer> testIds) throws AppointmentNotFoundException, PatientNotFoundException, DiagnosticCenterNotFoundException, TestResultNotFoundException, DataNotFoundInDataBase ;

	

	List<Appointment> getApppointmentList(int centreId, String test, String status) throws InvalidAppointmentStatusException, AppointmentNotFoundException;

	
	 Appointment verify( int appointmentID , boolean approved) throws AppointmentNotFoundException;


	List<Appointment> verifiable();


	List<Appointment> noTestResults();


	Patient getPatient(int appID) throws PatientNotFoundException;


	TestResult setTestResult(int appointmentId, int testResId) throws AppointmentNotFoundException, TestResultNotFoundException;


	List<Appointment> getAll();




}
