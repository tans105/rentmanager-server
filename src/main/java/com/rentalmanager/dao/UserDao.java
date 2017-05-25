package com.rentalmanager.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.rentalmanager.entity.database.RoleMst;
import com.rentalmanager.entity.database.Users;
import com.rentalmanager.utils.DbUtil;
import com.rentalmanager.utils.HibernateUtils;

public class UserDao {
	private GenericDao dao;
	private static final String FIRST = "0000000000";

	public UserDao() {
		dao = new GenericDao();
	}

	public Users getUser(String email) {
		HashMap<String, Object> filter = new HashMap<String, Object>();
		filter.put("email", email);
		filter.put("active", Boolean.TRUE);
		return dao.getEntityByProperty(filter, Users.class);
	}

	public RoleMst getRole(Integer roleId) {
		HashMap<String, Object> filter = new HashMap<String, Object>();
		filter.put("roleId", roleId);
		return dao.getEntityByProperty(filter, RoleMst.class);
	}

	@SuppressWarnings("unchecked")
	public String getMaxUserId(Integer roleId) {
		Session session = null;
		Transaction tx = null;
		List<Object> list = new ArrayList<Object>();
		String userId = null;
		try {

			SessionFactory sf = HibernateUtils.getSessionFactory();
			session = sf.openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery("Select max(userId) from Login where roleId=" + roleId);
			list = q.list();
			if (list.size() == 1 && list.get(0)==null) {
				userId=generateFirstLoginEntry(roleId);
			} else
				userId = list.get(0).toString();
			tx.commit();
			tx = null;

		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
		} finally {
			DbUtil.rollBackTransaction(tx);
			DbUtil.closeSession(session);

		}
		return userId;

	}

	private String generateFirstLoginEntry(Integer roleId) {
		RoleMst roleMst = getRole(roleId);
		return roleMst.getRoleAbbr() + FIRST;
	}

}
