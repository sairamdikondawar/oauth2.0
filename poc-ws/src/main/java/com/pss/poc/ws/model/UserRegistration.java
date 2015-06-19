package com.pss.poc.ws.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserRegistration {
	private String login;

	// other properties users are shown
	// after their initial registration succeeded

	public UserRegistration() {

	}

	public UserRegistration(String login) {
		this.login = login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}
}
