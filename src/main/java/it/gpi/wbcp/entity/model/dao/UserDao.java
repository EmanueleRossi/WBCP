/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.gpi.wbcp.entity.model.dao;

import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.ejb.UserEjb;
import it.gpi.wbcp.util.StringUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class UserDao {
    
    private static final Logger logger = LogManager.getLogger();
    
    private final PropertyUtilsBean pub;
	
    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;
    
    public UserDao() {
        pub = new PropertyUtilsBean();
    } 
	
    public User persist(User user) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {    	        
        User response = new User();
        UserEjb userEjb;
        if (user.getId() != null) {
            userEjb = this.getEjbById(user.getId());
        } else {
            userEjb = new UserEjb(); 
        }
        
        pub.copyProperties(userEjb, user);          
    
    	userEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	userEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));    	        
        em.persist(userEjb);
        
        pub.copyProperties(response, userEjb);  
        
        return response;
    }   
    
    public List<User> getAll() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);        
        Root<UserEjb> r = q.from(UserEjb.class);
        q.select(r);
        TypedQuery<UserEjb> tq = em.createQuery(q);
        List<UserEjb> rs = tq.getResultList();   
        
        List<User> users = new ArrayList<>();
        for(UserEjb userEjb : rs) {
            User user = new User();
            pub.copyProperties(user, userEjb);
            users.add(user);
        }        
        return users;    		        		
    }  
    
    private UserEjb getEjbById(Long id) throws NoResultException {
        UserEjb response = null;   
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);    
        Root<UserEjb> r = q.from(UserEjb.class);
        q.select(r).where(cb.equal(r.<Integer>get("id"), id));
        TypedQuery<UserEjb> tq = em.createQuery(q);
        response = tq.getSingleResult();
        return response;
    }
    
    public User getById(Long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	User response = new User();
        try {        
            UserEjb responseEjb = this.getEjbById(id);
            pub.copyProperties(response, responseEjb);
        } catch (NoResultException nre) {
            logger.info("Not found User with id: |{}|", id);            
        }
        return response;
    }
    
    public boolean verifyCredentials(String email, String password) {        
        boolean response = false;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);    
            Root<UserEjb> r = q.from(UserEjb.class);
            q.select(r).where(cb.equal(r.<String>get("email"), email));
            TypedQuery<UserEjb> tq = em.createQuery(q);
            UserEjb u = tq.getSingleResult();            
            String dbPasswordHashBase64 = u.getPasswordHashBase64();
            String requestPasswordHashBase64 = StringUtil.getSHA512Base64(password);            
            if (requestPasswordHashBase64.equals(dbPasswordHashBase64)) {
                response = true;
            }                                    
        } catch (NoResultException nre) {
            logger.info("Login refused: not found User with email: |{}|", email);            
        }      
        return response;        
    }
    
    public User getByEmail(String email) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	User response = new User();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<UserEjb> q = cb.createQuery(UserEjb.class);    
            Root<UserEjb> r = q.from(UserEjb.class);
            q.select(r).where(cb.equal(r.<String>get("email"), email));
            TypedQuery<UserEjb> tq = em.createQuery(q);
            UserEjb responseEjb = tq.getSingleResult();
            pub.copyProperties(response, responseEjb);
        } catch (NoResultException nre) {
            logger.info("Not found User with email: |{}|", email);            
        }
        return response;
    } 
}
