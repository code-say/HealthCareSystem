package com.cg.healthcare.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/*
 * =================================
 * Entity Class for Health Care System
	Author : Sayan Mandal
	Date Created : 7/1/2022
	================================
*/

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userid;
	private String username;
	private String password;
	private String role;
	
	@Column(columnDefinition = "boolean default false")
	private boolean isLoggedIn;
	
	public boolean isLoggedIn()
	{
		return isLoggedIn;
	}
	
	public void setLoggedIn(boolean isLoggedIn)
	{
		this.isLoggedIn = isLoggedIn;
	}
	User()
	{
		
	}

	public User(String username, String password, String role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public int getUserid() {
		return userid;
	}
	
	
	public void setUserid(int userid) {
		this.userid = userid;
	}
	
	
	public String getUsername() {
		return username;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getPassword() {
		return password;
	}
	

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}
	

	public void setRole(String role) {
		this.role = role;
	}
}
