package com.cg.healthcare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.exception.TestNotFoundException;

/*
 * TestResult Service Layer
 * Author:Achuth Bhaskar
 * date created:07/01/2022
 */
@Service
public interface ITestService {

	public DiagnosticTest addTest(DiagnosticTest test);
	public DiagnosticTest updateTest(DiagnosticTest test) throws TestNotFoundException;
	public String removeTest(DiagnosticTest test) throws TestNotFoundException;
	List<DiagnosticTest> viewAllTest(String criteria);
	
}
