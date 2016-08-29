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

import it.gpi.wbcp.entity.model.entity.dto.Counter;
import it.gpi.wbcp.entity.util.MapStruct;
import it.gpi.wbcp.entity.model.entity.dto.Organization;
import it.gpi.wbcp.entity.model.entity.ejb.CounterEjb;
import it.gpi.wbcp.entity.model.entity.ejb.OrganizationEjb;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class OrganizationDao {
    
    private static final Logger logger = LogManager.getLogger();
	
    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;  
    
    @EJB
    ApplicationParameterDao aParameterDao; 
	
    public Organization persist(Organization organization) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        
        OrganizationEjb organizationEjb = MapStruct.INSTANCE.organizationToOrganizationEjb(organization);    
    	organizationEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	organizationEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));    	        

        if(organizationEjb.getId() != null) {
            em.merge(organizationEjb);
        } else {          
            em.persist(organizationEjb);
        }
                
        Organization response = MapStruct.INSTANCE.organizationEjbToOrganization(organizationEjb);       
        return response;
    }   
    
    public List<Organization> fullTextSearch(String text) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	List<OrganizationEjb> responseEjb = new ArrayList<>();        
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrganizationEjb> q = cb.createQuery(OrganizationEjb.class);    
            Root<OrganizationEjb> r = q.from(OrganizationEjb.class);
            q.select(r)
                .where(
                    cb.or(
                        cb.like(r.<String>get("name"), StringUtil.createDbLikeString(text)),
                        cb.like(r.<String>get("taxCode"), StringUtil.createDbLikeString(text)),
                        cb.like(r.<String>get("mailDomain"), StringUtil.createDbLikeString(text))
                    )
                );            
            TypedQuery<OrganizationEjb> tq = em.createQuery(q);
            responseEjb = tq.getResultList();
        } catch (NoResultException nre) {
            logger.info("Not found Organization with criteria: |{}|", text);            
        }
        
        List<Organization> response = MapStruct.INSTANCE.organizationListEjbToOrganizationList(responseEjb);      
        return response;
    }    
        
    private CounterEjb persist(CounterEjb counterEjb) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException { 
        counterEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	counterEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));   
        em.persist(counterEjb);
        
        return counterEjb;        
    }     
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)         
    public Counter getNextValue(Organization organization, Integer year) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, URISyntaxException {
        Counter response = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrganizationEjb> q = cb.createQuery(OrganizationEjb.class);    
            Root<OrganizationEjb> r = q.from(OrganizationEjb.class);
            q.select(r).where(cb.equal(r.<Long>get("id"), organization.getId()));
            TypedQuery<OrganizationEjb> tq = em.createQuery(q);
            OrganizationEjb organizationEjb = tq.getSingleResult();
            
            CounterEjb counterEjb;
            Optional<CounterEjb> oCounterEjb = organizationEjb.getCounters().stream().filter(c -> Objects.equals(c.getYear(), year)).findFirst();
            if (!oCounterEjb.isPresent()) {
                CounterEjb newYear = new CounterEjb();
                newYear.setYear(year);
                newYear.setLenght(aParameterDao.getParameterAsInteger("MESSAGE_COUNTER_LENGHT"));
                newYear.setSeparator(aParameterDao.getParameterAsString("COUNTER_SEPARATOR"));
                newYear.setValue(1);
                newYear.setOrganization(organizationEjb);
                counterEjb = this.persist(newYear);                
            } else {
                counterEjb = oCounterEjb.get();
            }            
            response = MapStruct.INSTANCE.counterEjbToCounter(counterEjb);  
            
        } catch (NoResultException nre) {
            logger.info("Not found Organization for id: |{}|", organization.getId());        
        }
        return response;
    }
}
