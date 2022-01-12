package com.cg.healthcare.validators;


/*===========================================
 * Author : Sayan Mandal
 * Date : 07-01-2022
 * Description : This is InputValidator Interface
 * ===========================================
*/


public interface InputValidator {

	public boolean nameValidator(String name);
	
	public boolean contactValidator(String contact);
	
	public boolean emailValidator(String email);
	
	public boolean passwordValidator(String password);
	
	public boolean usernameValidator(String username);
}
