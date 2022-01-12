package com.cg.healthcare.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 *DiagnosticTest Entity
 *Author: Kewal Darbeshwar 
 *Date created: 07/01/2022
 */

@Entity
public class DiagnosticTest {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int diagnosticTestid;
	private String testName;
	private double testPrice;
	private String normalValue;
	private String units;
	
	@JsonIgnore
	@ManyToOne 
	private DiagnosticCenter diagnosticCenter;
	
	public DiagnosticTest() {
	
	}
	//Constructor
	public DiagnosticTest(String testName, double testPrice, String normalValue, String units,
			DiagnosticCenter diagnosticCenter) {
		super();
		this.testName = testName;
		this.testPrice = testPrice;
		this.normalValue = normalValue;
		this.units = units;
		this.diagnosticCenter = diagnosticCenter;
	}

	
	//Getter-Setter
	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public double getTestPrice() {
		return testPrice;
	}

	
	public void setTestPrice(double testPrice) {
		this.testPrice = testPrice;
	}


	public String getNormalValue() {
		return normalValue;
	}


	public void setNormalValue(String normalValue) {
		this.normalValue = normalValue;
	}


	public String getUnits() {
		return units;
	}

	
	public void setUnits(String units) {
		this.units = units;
	}


	public DiagnosticCenter getDiagnosticCenter() {
		return diagnosticCenter;
	}

	
	public void setDiagnosticCenter(DiagnosticCenter diagnosticCenter) {
		this.diagnosticCenter = diagnosticCenter;
	}
	
	
	public int getDiagonasticTestid() {
		return diagnosticTestid;
	}
	
	
	public void setDiagonasticTestid(int diagnosticTestid) {
		this.diagnosticTestid = diagnosticTestid;
	}
	
	
}