package com.pss.poc.ws.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.springframework.transaction.annotation.Transactional;

import com.pss.poc.orm.bean.UserAccount;
import com.pss.poc.orm.dao.UserAccounts;
import com.pss.poc.ws.model.UserRegistration;

@Path(value = "/registerUser")
public class UserRegistrationService {

	private UserAccounts accounts;

	public UserRegistrationService() {
	}

	public void setAccounts(UserAccounts accounts) {
		this.accounts = accounts;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/")
	@Transactional
	public UserRegistration register(@FormParam("user") String name, @FormParam("alias") String alias, @FormParam("password") String password) {
		if (accounts.findByName(name) != null) {
			throw new WebApplicationException(400);
		}
		UserAccount account = new UserAccount();
		account.setName(name);
		account.setPassword(password);
		accounts.save(account);
		return new UserRegistration(name);
	}
}
