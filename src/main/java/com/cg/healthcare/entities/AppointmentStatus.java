package com.cg.healthcare.entities;


/*
 * =================================
 * Entity Class for Health Care System
	Author : Sayan Mandal
	Date Created : 7/1/2022
	================================
*/

public enum AppointmentStatus {

	statutsnotapproved("statutsnotapproved"),approved("approved"),cancelled("cancelled");
	private String status;
	
	
	AppointmentStatus(String status)
	{
		this.setStatus(status);
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
