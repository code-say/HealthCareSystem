package com.cg.healthcare.service;

import java.util.List;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cg.healthcare.dao.IAppointmentRepository;
import com.cg.healthcare.dao.IDiagnosticCenterRepositoryInt;
import com.cg.healthcare.dao.ITestRepository;

import com.cg.healthcare.entities.Appointment;
import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.exception.DataNotFoundInDataBase;
import com.cg.healthcare.exception.DiagnosticCenterNotFoundException;

/*
 * /*
 * DiagnosticCenterService Implementation Layer
 * Author:Sachin Rathod
 * date created:07/01/2022
 */ 



@Service
public class IDiagnosticCenterServiceImpl implements IDiagnosticCenterService{


	@Autowired
	IDiagnosticCenterRepositoryInt centerDao;

	@Autowired
	IAppointmentRepository appointmentDao;

	@Autowired
	ITestRepository test;



/************************************************************************************
	 * Method: getAllDiagnosticCenters
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/



	@Override
	public List<DiagnosticCenter> getAllDiagnosticCenters() {

		return centerDao.findAll();
	}


/************************************************************************************
	 * Method: addDiagnosticCenter
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/




	@Override
	public DiagnosticCenter addDiagnosticCenter(DiagnosticCenter diagnosticCenter) throws Exception {
		if(centerDao.existsById(diagnosticCenter.getDiagnosticCenterid())){
			throw new Exception("Diagnostic Center with given Id already exists.");
		}
		else
		centerDao.saveAndFlush(diagnosticCenter);
		return diagnosticCenter;
	}



/************************************************************************************
	 * Method: getDiagnosticCenterById
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/

	

	@Override
	public DiagnosticCenter getDiagnosticCenterById(int diagnosticCenterId) throws DataNotFoundInDataBase{
		if(!centerDao.existsById(diagnosticCenterId))throw new DataNotFoundInDataBase("Diagnostic Center Not Found");
		return centerDao.findById(diagnosticCenterId).get();
	}


/************************************************************************************
	 * Method: updateDiagnosticCenter
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/


	@Override
	public DiagnosticCenter updateDiagnosticCenter(DiagnosticCenter diagnosticCenter) throws DataNotFoundInDataBase {
		if(!centerDao.existsById(diagnosticCenter.getDiagnosticCenterid())) throw new DataNotFoundInDataBase("Diagnostic Center Not Found");
		centerDao.saveAndFlush(diagnosticCenter);
		return diagnosticCenter;
	}


/************************************************************************************
	 * Method: viewTestDetails
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/


	@Override
	public List<DiagnosticTest> viewTestDetails(int diagnosticCenterId) {
		return centerDao.viewTestDetails(diagnosticCenterId);
	}


/************************************************************************************
	 * Method: addTest
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/


	@Override
	public DiagnosticTest addTest(int diagnosticcenterId, int testid) throws DataNotFoundInDataBase {
		DiagnosticTest t = test.getOne(testid);
		DiagnosticCenter c = centerDao.getOne(diagnosticcenterId);
		if(t==null || c==null) throw new DataNotFoundInDataBase("Center/test does Not Exist");
		c.getTests().add(t);
		t.setDiagnosticCenter(c);
		test.saveAndFlush(t);
		centerDao.saveAndFlush(c);
		return t;
	}
	


/************************************************************************************
	 * Method: getDiagnosticCenter
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns : It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/


	@Override
	public DiagnosticCenter getDiagnosticCenter(String centername) throws DataNotFoundInDataBase {
		DiagnosticCenter dc = centerDao.getDiagnosticCenter(centername);
		if(dc==null) throw new DataNotFoundInDataBase("Diagnostic Center Not Found");
		return dc;
	}


/************************************************************************************
	 * Method: removeDiagnosticCenters
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns quote: It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/

	@Override
	public DiagnosticCenter removeDiagnosticCenter(int id) throws DiagnosticCenterNotFoundException{
		Optional<DiagnosticCenter> op=centerDao.findById(id);
		if(op.isPresent()) {
			centerDao.deleteById(id);
			return op.get();
		}
		else throw new DiagnosticCenterNotFoundException("Diagnostic Center with given Id doesn't exist.");

	}

/************************************************************************************
	 * Method: getListAppointments
	 * Description: It is used to add DiagnosticCenters into DiagnosticCenters table
	 * @returns quote: It returns ts object
	 *@Override It is used to override DiagnosticCenters Interface
	 ************************************************************************************/

	

	@Override
	public List<Appointment> getListOfAppointments(String centerName) {
		return appointmentDao.findAll();
	}

}
