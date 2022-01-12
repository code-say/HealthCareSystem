package com.cg.healthcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.healthcare.dao.IUserRepository;
import com.cg.healthcare.dao.manualqueries.QueryClassPersisitContext;
import com.cg.healthcare.entities.User;
import com.cg.healthcare.exception.UserCreationError;
import com.cg.healthcare.exception.UserNotFoundException;
import com.cg.healthcare.validators.InputValidator;

/*===========================================
 * Author : Sayan Mandal
 * Date : 07-01-2022
 * Description : This is User ServiceImplementation
 * ===========================================
*/


@Service
public class IUserServiceImpl implements IUserService{

	@Autowired
	IUserRepository userrepo;
	
	@Autowired
	InputValidator validate;
	
	@Autowired
	QueryClassPersisitContext qcp;
	
	/************************************************************************************
	 * Method: validateUser 
	 * Description: It is used to validate the user
	 *@Override It is used to override Test Service Interface
	************************************************************************************/

	
	@Override
	public User validateUser(String username, String password) throws Exception {
		// TODO Auto-generated method stub
		
		User pUser = qcp.findByUserName(username);
		if(pUser == null)throw new UserNotFoundException("Invalid Username");
		if(pUser.getPassword().equals(password)) return pUser;
		else {
			throw new UserNotFoundException("Invalid Password");
		}
	}
	
	/************************************************************************************
	 * Method: addUser 
	 * Description: It is used to add user into user table
	 * *@Override It is used to override Test Service Interface
	 ************************************************************************************/


	@Override
	public User addUser(User user) throws UserCreationError {
		// TODO Auto-generated method stub
		
		if(!validate.usernameValidator(user.getUsername()))throw new UserCreationError("Check Username !");
		if(userrepo.existsByusername(user.getUsername()))throw new UserCreationError("username Already exists!");
		if(!validate.passwordValidator(user.getPassword()))throw new UserCreationError("Cannot register this user with this password");
		user.setRole("user");
		return userrepo.saveAndFlush(user);
	}

	/************************************************************************************
	 * Method: removeUser 
	 * Description: It is used to delete a user record from user table
	 * @returns String: It returns string value specifying successful deletion or failure of deletion
	 * *@Override It is used to override Test Service Interface
	  ************************************************************************************/

	
	@Override
	public User removeUser(User user) throws UserNotFoundException {
		// TODO Auto-generated method stub
		if(!userrepo.existsById(user.getUserid()))throw new UserNotFoundException("User with id :"+ user.getUserid()+" Doesn't Exist");
		userrepo.deleteById(user.getUserid());
		return user;
	}

	
	@Override
	public User updateUser(User user) throws UserNotFoundException {
		// TODO Auto-generated method stub
		if(!userrepo.existsById(user.getUserid()))throw new UserNotFoundException("User with id :"+ user.getUserid()+" Doesn't Exist");
		User use = userrepo.findById(user.getUserid()).get();
		userrepo.delete(use);
		return use;
	}

	/************************************************************************************
	 * Method: getAll 
	 * Description: It is used to retrieve all the users from
	 *				 the user table
	 * @returns List<user>: It returns List of users with details
	 * *@Override It is used to override Test Service Interface
	  ************************************************************************************/

	
	@Override
	public List<User> getAll() {
		
		return userrepo.findAll();
	}

}
