package com.cg.healthcare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.healthcare.entities.TestResult;

public interface ITestResultRepository extends JpaRepository<TestResult, Integer> 
{

}
