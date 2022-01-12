package com.cg.healthcare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.healthcare.entities.DiagnosticTest;
import com.cg.healthcare.entities.TestResult;

/*
 * TestResult Service Layer
 * Author:Achuth Bhaskar
 * date created:07/01/2022
 */

public interface ITestRepository extends JpaRepository<DiagnosticTest, Integer>
{

	

}
