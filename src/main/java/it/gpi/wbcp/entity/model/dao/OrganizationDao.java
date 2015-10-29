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

import it.gpi.wbcp.entity.model.entity.dto.Organization;
import it.gpi.wbcp.entity.model.entity.ejb.OrganizationEjb;
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
public class OrganizationDao {
    
    private static final Logger logger = LogManager.getLogger();
    
    private final PropertyUtilsBean pub;
	
    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;
    
    public OrganizationDao() {
        pub = new PropertyUtilsBean();
    }     
	
    public Organization persist(Organization organization) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {    	
        Organization response = new Organization();
        OrganizationEjb organizationEjb;
        if (organization.getId() != null) {
            organizationEjb = this.getEjbById(organization.getId());
        } else {
            organizationEjb = new OrganizationEjb(); 
        }
        
        pub.copyProperties(organizationEjb, organization);
    
    	organizationEjb.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	organizationEjb.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));    	        
        em.persist(organizationEjb);
        
        pub.copyProperties(response, organizationEjb);  
        
        return response;
    }
 
    public List<Organization> getAll() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrganizationEjb> q = cb.createQuery(OrganizationEjb.class);        
        Root<OrganizationEjb> r = q.from(OrganizationEjb.class);
        q.select(r);
        TypedQuery<OrganizationEjb> tq = em.createQuery(q);
        List<OrganizationEjb> rs = tq.getResultList();   
        
        List<Organization> organizations = new ArrayList<>();
        for(OrganizationEjb organizationEjb : rs) {
            Organization organization = new Organization();
            pub.copyProperties(organization, organizationEjb);
            organizations.add(organization);
        }        
        return organizations;          
    }    
    
    private OrganizationEjb getEjbById(Long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	OrganizationEjb response = null;
        try {           
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrganizationEjb> q = cb.createQuery(OrganizationEjb.class);    
            Root<OrganizationEjb> r = q.from(OrganizationEjb.class);
            q.select(r).where(cb.equal(r.<Integer>get("id"), id));
            TypedQuery<OrganizationEjb> tq = em.createQuery(q);
            response = tq.getSingleResult();
        } catch (NoResultException nre) {
            logger.info("Not found Organization with id: |{}|", id);            
        }
        return response; 
    }

    public Organization getById(Long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	Organization response = null;
        try {        
            OrganizationEjb responseEjb = this.getEjbById(id);
            pub.copyProperties(response, responseEjb);
        } catch (NoResultException nre) {
            logger.info("Not found User with id: |{}|", id);            
        }
        return response;
    }
    
    public List<Organization> fullTextSearch(String text) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	List<OrganizationEjb> responseEjb = new ArrayList<>();        
    	List<Organization> response = new ArrayList<>();
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
        
        for(OrganizationEjb organizationEjb : responseEjb) {
            Organization organization = new Organization();
            pub.copyProperties(organization, organizationEjb);
            response.add(organization);
        }        
        return response;
    }     
}