package com.cg.healthcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cg.healthcare.entities.User;
import com.cg.healthcare.exception.ForBiddenException;
import com.cg.healthcare.exception.UserNotFoundException;
import com.cg.healthcare.loginmodule.LoginService;

@RestController
public class LoginController {

	@Autowired
	LoginService logServ;


	@PostMapping("/Login")
	public User loginUser(@RequestBody User user) throws UserNotFoundException {
		return logServ.loginWithData(user.getUsername(), user.getPassword());
	}


	@PostMapping("/Logout")
	public User logOut(@RequestBody User user) throws ForBiddenException, UserNotFoundException {
			return logServ.logoutPresentUser(user.getUsername());
	}

}
