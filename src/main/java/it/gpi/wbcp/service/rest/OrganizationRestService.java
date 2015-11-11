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
package it.gpi.wbcp.service.rest;

import it.gpi.wbcp.entity.model.dao.OrganizationDao;
import it.gpi.wbcp.entity.model.entity.dto.Organization;
import it.gpi.wbcp.entity.model.entity.dto.ApplicationError;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.util.StringUtil;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationPath("/rs")
@Path("/organization")
@Stateless
public class OrganizationRestService {
		
    private static final Logger logger = LogManager.getLogger();    
    
    @EJB
    OrganizationDao organizationDao;     
	
    @GET
    @Path("/fullTextSearch/{text}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fullTextSearch(@Context HttpServletRequest httpRequest,
                                   @Context String authorization,
                                   @PathParam("text") String text) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());          
        try {
            List<Organization> organizations = organizationDao.fullTextSearch(text);
            if (organizations == null) {
                ApplicationError ae = new ApplicationError(lmb.getString("organization.not_found"));
                logger.warn(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                response = Response.status(Status.OK).entity(organizations).build();                
            }        
        } catch (Exception eg) {
            ApplicationError aeg = new ApplicationError(eg);
            logger.error("Generic exception executing organization full text search. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;        
    }
    
    @POST
    @Path("/create")    
    @Produces(MediaType.APPLICATION_JSON)        
    public Response create(@Context HttpServletRequest httpRequest,
                           @Context User requestUser,
                           Organization organization) {
        
        System.out.printf("user=(%s)", requestUser.toString());
        
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());  
        try {
            if (StringUtil.isNullOrEmpty(organization.getName())) {
                ApplicationError ae = new ApplicationError(lmb.getString("organization.creation.no_name"));
                logger.warn(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                organizationDao.persist(organization); 
                response = Response.status(Status.OK).entity(organization).build();   
            }                        
        } catch (Exception eg) {
            ApplicationError aeg = new ApplicationError(eg);            
            logger.error("Generic exception in creating organization. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }
        return response;
    }    
}
