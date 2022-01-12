package com.cg.healthcare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.healthcare.entities.User;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.exception.UserCreationError;
import com.cg.healthcare.exception.UserNotFoundException;
import com.cg.healthcare.service.IUserService;

/*
 * Author : SAYAN MANDAL
 * Version : 1.0
 * Date : 10-01-2022
 * Description : This is UserController
*/

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	IUserService userService;
	
	@Autowired
	LoginController logCon;
	
	/************************************************************************************
	 * Method: validateUser 
	 * Description: It is used to validate the user
	 *  @PostMapping: It is used to handle the HTTP POST requests matched with given
	 *               URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type.
	 ************************************************************************************/

	
	@PostMapping("/validateUser")
	public User validateUser(@RequestBody User user) throws Exception 
	{
		return userService.validateUser(user.getUsername(), user.getPassword());
	}
	
	/************************************************************************************
	 * Method: addUser 
	 * Description: It is used to add user into user table
	 *  @PostMapping: It is used to handle the HTTP POST requests matched with given
	 *               URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type.
	 ************************************************************************************/

	
	@PostMapping("/addUser")
	public User addUser(@RequestBody User user) throws UserCreationError, ForBiddenException
	{
		return userService.addUser(user);
	}
	
	/************************************************************************************
	 * Method: removeUser  
	 * Description: It is used to delete a user record from user table
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	 * @DeleteMapping: It is used to handle the HTTP DELETE requests matched with
	 *                 given URI expression.
	 * @PathVariable: It is used to handle template variables in the request URI
	 *                mapping.
	 ************************************************************************************/

	
	@DeleteMapping("/removeUser")
	public User removeUser(@RequestBody User user) throws ForBiddenException, UserNotFoundException
	{
		return userService.updateUser(user);
	}
	
	
	/************************************************************************************
	 * Method: updateUser 
	 * @returns user: It returns user with details
	 * @PutMapping: It is used to handle the HTTP PUT requests matched with given
	 *              URI expression.
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/
	
	
	@PutMapping("/updateUser")
	public User updateUser(@RequestBody User user) throws ForBiddenException, UserNotFoundException
	{
		return userService.updateUser(user);
	}
	
	
	/************************************************************************************
	 * Method: getAll 
	 * Description: It is used to retrieve all the users from
	 *				 the user table
	 * @returns List<user>: It returns List of users with details
	 * @GetMapping: annotated methods handle the HTTP GET requests matched with
	 *              given URI expression
	 * @RequestBody: It used to bind the HTTP request/response body with a domain
	 *               object in method parameter or return type. 
	 ************************************************************************************/

	
	
	@GetMapping("/getAll")
	public List<User> getAll()
	{
		return userService.getAll();
	}
}
