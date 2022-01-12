package com.cg.healthcare.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cg.healthcare.entities.DiagnosticTest;

/*
 *DiagnosticTest Entity
 *Author: Kewal Darbeshwar 
 *Date created: 07/01/2022
 */

public interface IDiagnosticTestRepository extends JpaRepository< DiagnosticTest , Integer > {

}
