package com.cg.healthcare.loginmodule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.healthcare.dao.IUserRepository;
import com.cg.healthcare.dao.manualqueries.QueryClassPersisitContext;
import com.cg.healthcare.entities.User;
import com.cg.healthcare.exception.UserNotFoundException;

@Service
public class LoginService{
	
	@Autowired
	private QueryClassPersisitContext qcp;
	
	@Autowired
	private IUserRepository uRepo;
	
	public User loginWithData(String username, String password) throws UserNotFoundException
	{
		User user;
		user = qcp.findByUserName(username);
		if(user.isLoggedIn())throw new UserNotFoundException("User already Logged In");
		if(!user.getPassword().equals(password))throw new UserNotFoundException("Password Invalid");
		user.setLoggedIn(true);
		uRepo.saveAndFlush(user);
		return user;
	}
	
	public User logoutPresentUser(String username) throws UserNotFoundException
	{
		User user = qcp.findByUserName(username);
		user.setLoggedIn(false);
		return uRepo.saveAndFlush(user);
	}

}
