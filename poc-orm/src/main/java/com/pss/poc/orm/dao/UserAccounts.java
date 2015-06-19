/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package com.pss.poc.orm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.pss.poc.orm.bean.UserAccount;

@Transactional
public class UserAccounts {

	private static final Logger log = Logger.getLogger(UserAccounts.class);
	// property constants
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void initDao() {
		// do nothing
	}

	public void save(UserAccount transientInstance) {
		log.debug("saving UserAccount instance");
		try {
			getCurrentSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void merger(UserAccount transientInstance) {
		log.debug("saving UserAccount instance");
		try {
			getCurrentSession().merge(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(UserAccount persistentInstance) {
		log.debug("deleting UserAccount instance");
		try {
			getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserAccount findById(Long id) {
		log.debug("getting UserAccount instance with id: " + id);
		try {
			UserAccount instance = (UserAccount) getCurrentSession().get("com.pss.poc.orm.bean.UserAccount", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public UserAccount findByName(String name) {
		log.debug("getting UserAccount instance with id: " + name);
		try {

			Criteria criteria = getCurrentSession().createCriteria(UserAccount.class);
			criteria.add(Restrictions.like("name", name).ignoreCase());
			UserAccount instance = (UserAccount) criteria.uniqueResult();
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
