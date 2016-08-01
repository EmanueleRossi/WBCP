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

import it.gpi.wbcp.entity.model.entity.ejb.ApplicationParameterEjb;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
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
public class ApplicationParameterDao {
	
    private static final Logger logger = LogManager.getLogger();
    
    @PersistenceContext(unitName="WBCP_PU")
    private EntityManager em;
	
    private void persist(ApplicationParameterEjb anAppParameter) {    	
    	anAppParameter.setUpdateInstantUTC(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK));
    	anAppParameter.setUpdateInstantLocale(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));    	    
    	
        em.persist(anAppParameter);
    }     
    
    private ApplicationParameterEjb getByName(String parameterName, Locale locale) {
    	ApplicationParameterEjb response = null;
        try {         
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ApplicationParameterEjb> q = cb.createQuery(ApplicationParameterEjb.class);    
            Root<ApplicationParameterEjb> r = q.from(ApplicationParameterEjb.class);            
            q.select(r)
                .where(
                    cb.and(
                        cb.equal(r.<String>get("name"), parameterName),
                        cb.equal(r.<String>get("localeTag"), locale.toLanguageTag())
                    )
                );                                            
            TypedQuery<ApplicationParameterEjb> tq = em.createQuery(q);
            response = tq.getSingleResult();            
        } catch (NoResultException nre) {
            logger.info("Not found ApplicationParameter with parameterName: |{}|", parameterName);            
        }            
        return response;  
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)     
    private String getParameterValueString(String parameterName, Locale locale) throws IOException, URISyntaxException {
        ApplicationParameterEjb parameter = this.getByName(parameterName, locale);
        if (parameter == null) {                        
            URL jsonDefaultParametersFileURL = Thread.currentThread().getContextClassLoader().getResource("ApplicationDefaultParameters.json");
            InputStream jsonDefaultParametersFileInputStream = jsonDefaultParametersFileURL.openStream();
            String jsonDefaultParmetersString = StringUtil.readString(jsonDefaultParametersFileInputStream);
            JsonReader jsonDefaultParameterStringReader = Json.createReader(new StringReader(jsonDefaultParmetersString));            
            JsonArray jsonDefaultParmetersArray = jsonDefaultParameterStringReader.readArray();            
            ApplicationParameterEjb defaultParameter = new ApplicationParameterEjb();
            
            for (int i = 0; i < jsonDefaultParmetersArray.size(); i++) {
                JsonObject jsonDefaultParameter = jsonDefaultParmetersArray.getJsonObject(i);                                
                if (parameterName.equalsIgnoreCase(jsonDefaultParameter.getString("name")) && 
                        locale.toLanguageTag().equalsIgnoreCase(jsonDefaultParameter.getString("localeTag"))) {     
                    logger.trace("Read default parameter from JSON: |{}|", jsonDefaultParameter.toString());  
                    defaultParameter.setName(jsonDefaultParameter.getString("name"));
                    defaultParameter.setDescription(jsonDefaultParameter.getString("description"));
                    defaultParameter.setParameterValue(jsonDefaultParameter.getString("parameterValue"));
                    defaultParameter.setLocale(Locale.forLanguageTag(jsonDefaultParameter.getString("localeTag")));
                }                                                               
            }
            this.persist(defaultParameter);
            logger.trace("Inserted new Parameter: |{}|=|{}|", defaultParameter.getName(), defaultParameter.getParameterValue());   
            
            parameter = this.getByName(parameterName, locale);
        }                       
        return parameter.getParameterValue();                                                   
    }
    
    public Boolean getParameterAsBoolean(String parameterName, Locale locale) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, locale);
        return Boolean.parseBoolean(parameterValueString);
    }
    public Boolean getParameterAsBoolean(String parameterName) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, Locale.US);
        return Boolean.parseBoolean(parameterValueString);
    }    
    
    public Integer getParameterAsInteger(String parameterName, Locale locale) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, locale);
        return Integer.parseInt(parameterValueString);
    }
    public Integer getParameterAsInteger(String parameterName) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, Locale.US);
        return Integer.parseInt(parameterValueString);
    }
    
    public Long getParameterAsLong(String parameterName, Locale locale) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, locale);
        return Long.parseLong(parameterValueString);
    }
    public Long getParameterAsLong(String parameterName) throws IOException, URISyntaxException {
        String parameterValueString = this.getParameterValueString(parameterName, Locale.US);
        return Long.parseLong(parameterValueString);
    }    
    
    public String getParameterAsString(String parameterName, Locale locale) throws IOException, URISyntaxException {
        return this.getParameterValueString(parameterName, locale);
    }    
    public String getParameterAsString(String parameterName) throws IOException, URISyntaxException {
        return this.getParameterValueString(parameterName, Locale.US);
    }    
}
