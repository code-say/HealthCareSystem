package com.cg.healthcare.dao.manualqueries;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.AppointmentStatus;
import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.entities.Patient;
import com.cg.healthcare.entities.TestResult;
import com.cg.healthcare.entities.User;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.TestResultNotFoundException;
import com.cg.healthcare.exception.UserNotFoundException;

@Repository
public class QueryClassPersisitContext {

	@PersistenceContext
	EntityManager entityManager;
	
	
	public List<Appointment> getAppointmentList(int centreId,String test,AppointmentStatus status){
		TypedQuery<Appointment> exe = entityManager.createQuery("select a from Appointment a join a.diagnosticTests d where"
				+ " a.diagnosticCenter.diagonasticCenterid = :id and d.testName like :test and a.approvalStatus like :status", Appointment.class);
		exe.setParameter("id", centreId);
		exe.setParameter("test",test);
		exe.setParameter("status",status);
		List<Appointment> result = exe.getResultList();
		return result;
	}
	
	public List<Appointment> viewAppointments( String patientName){
		TypedQuery<Appointment> qry = entityManager.createQuery("select a from Appointment a where a.patient.name like :pname",Appointment.class);
		qry.setParameter("pname",patientName);
		return qry.getResultList();
	}
	
	public List<DiagnosticTest> getTestsOfDiagnosticCenter(int centerId){
		
		TypedQuery<DiagnosticTest> exe = entityManager.createQuery("select d from DiagnosticTest d join d.diagnosticCenter dc where dc.diagonasticCenterid like :id", DiagnosticTest.class);
		exe.setParameter("id", centerId);
		List<DiagnosticTest> resultList = exe.getResultList();
		return resultList;
	}
	public  DiagnosticTest getTestById(int diagnosticTestid) {
		TypedQuery<DiagnosticTest> exe=entityManager.createQuery("select d from DiagnosticTest where d.diagnosticTestid like :id", DiagnosticTest.class);
		exe.setParameter("id", diagnosticTestid);
		DiagnosticTest result = exe.getSingleResult();
		return result;
	}
	
	
	//public List<Appointment> getAppointmentList(int center)
	@Transactional
	public DiagnosticTest removeTestFromDiagnosticCenter(int centerId, int test)
	{
		Query qry = entityManager.createQuery("select c from DiagnosticCenter c where c.diagonasticCenterid = :id");
		qry.setParameter("id", centerId);
		DiagnosticCenter diagnosticCenter = (DiagnosticCenter) qry.getSingleResult();
		qry= entityManager.createQuery("Select t from DiagnosticTest t where t.diagnoasticTestid = :tid");
		qry.setParameter("tid", test);
		DiagnosticTest diagnosticTest = (DiagnosticTest) qry.getSingleResult();
		diagnosticTest.setDiagnosticCenter(null);
		diagnosticCenter.getTests().remove(diagnosticCenter);
		entityManager.persist(diagnosticTest);
		entityManager.persist(diagnosticCenter);
		return diagnosticTest;
	}
	
	
	public List<TestResult> getAllTestResult(String patientUserName)
	{
		TypedQuery<TestResult> qry = entityManager.createQuery("",TestResult.class);
		qry.setParameter("n", patientUserName);
		List<TestResult> tr = qry.getResultList();
		return tr;
	}
	
	public User findByUserName(String username) throws UserNotFoundException
	{
		TypedQuery<User> qry = entityManager.createQuery("Select u from User u where u.username like :name",User.class);
		qry.setParameter("name", username);
		List<User> user = qry.getResultList();
		if(user.size()==0) throw new UserNotFoundException("User Not Avaliable!"+username);
		return user.get(0);
	}
	public List<TestResult> viewResultByPatient(Patient patient) throws TestResultNotFoundException, DataNotFoundInDataBase{
		// TODO Auto-generated method stub
		TypedQuery<TestResult> qry = entityManager.createQuery("",TestResult.class);
		qry.setParameter("id", patient.getPatientId());
		List<TestResult> tr = qry.getResultList();
		if(tr.size() == 0)throw new DataNotFoundInDataBase("Tests Does not Exists");
		return tr;
	}
}
