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

import it.gpi.wbcp.entity.model.entity.dto.Message;
import it.gpi.wbcp.entity.model.entity.ejb.MessageEjb;
import it.gpi.wbcp.entity.model.entity.ejb.UserEjb;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class MessageDao {
    
    private static final Logger logger = LogManager.getLogger();
    
    private final PropertyUtilsBean pub;
	
    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;
    
    public MessageDao() {
        pub = new PropertyUtilsBean();
    }         
	
    public Message persist(Message message) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {    	        
        Message response = new Message();
        MessageEjb messageEjb;
        if (message.getId() != null) {
            messageEjb = this.getEjbById(message.getId());
        } else {
            messageEjb = new MessageEjb(); 
        }
        
        pub.copyProperties(messageEjb, message);
    
    	messageEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	messageEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));    	        
        em.persist(messageEjb);
        
        pub.copyProperties(response, messageEjb);  
        
        return response;        
    }   
    
    private MessageEjb getEjbById(Long id) {
    	MessageEjb response = null;
        try {        
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MessageEjb> q = cb.createQuery(MessageEjb.class);    
            Root<MessageEjb> r = q.from(MessageEjb.class);
            q.select(r).where(cb.equal(r.<Integer>get("id"), id));
            TypedQuery<MessageEjb> tq = em.createQuery(q);
            response = tq.getSingleResult();
        } catch (NoResultException nre) {
            logger.info("Not found Message with id: |{}|", id);            
        }
        return response;
    } 
    
    public Message getById(Long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Message response = null;
        try {        
            MessageEjb responseEjb = this.getEjbById(id);
            pub.copyProperties(response, responseEjb);
        } catch (NoResultException nre) {
            logger.info("Not found User with id: |{}|", id);            
        }
        return response;
    }
    
    public List<Message> searchByInstant(Long userId, Calendar instantFrom, Calendar instantTo) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	List<MessageEjb> responseEjb = new ArrayList<>();
        List<Message> response = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MessageEjb> q = cb.createQuery(MessageEjb.class);    
            Root<MessageEjb> r = q.from(MessageEjb.class);
            Join<MessageEjb, UserEjb> u = r.join("user");   
            q.select(r)
                .where(
                    cb.and(
                        cb.equal(u.<Long>get("id"), userId),
                        cb.greaterThanOrEqualTo(r.<Calendar>get("instant"), instantFrom),
                        cb.lessThanOrEqualTo(r.<Calendar>get("instant"), instantTo)
                    )               
                );            
            TypedQuery<MessageEjb> tq = em.createQuery(q);
            responseEjb = tq.getResultList();
        } catch (NoResultException nre) {
            logger.info("Not found Message between |{}| and |{}|", instantFrom, instantTo);            
        }
        
        for(MessageEjb messageEjb : responseEjb) {
            Message message = new Message();
            pub.copyProperties(message, messageEjb);
            response.add(message);
        }        
        return response;        
    }     
}