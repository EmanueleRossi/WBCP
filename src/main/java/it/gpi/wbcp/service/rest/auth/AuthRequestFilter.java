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
package it.gpi.wbcp.service.rest.auth;

import it.gpi.wbcp.entity.model.dao.ApplicationParameterDao;
import it.gpi.wbcp.entity.model.entity.dto.ApplicationError;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.service.rest.AuthRestService;
import it.gpi.wbcp.service.rest.UserRestService;
import it.gpi.wbcp.util.JwtAuthUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jose4j.jwt.JwtClaims;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {
    
    private static final Logger logger = LogManager.getLogger();
    
    @EJB
    ApplicationParameterDao aParameterDao;    
    
    @Context
    private ResourceInfo resourceInfo;    

    @Override
    @Produces(MediaType.APPLICATION_JSON)   
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {                        
        try {            
            Method method = resourceInfo.getResourceMethod();       
            if (!(method.equals(UserRestService.class.getMethod("create", HttpServletRequest.class, User.class)) ||  
                 method.equals(AuthRestService.class.getMethod("login", HttpServletRequest.class, String.class, String.class, String.class)))) {
                String jwtAudience = aParameterDao.getParameterAsString("JWT_AUDIENCE");        
                String jwtPassword = aParameterDao.getParameterAsString("JWT_PASSWORD");                             
                String authorization = containerRequestContext.getHeaderString("AuthorizationToken");                  
                JwtClaims claims = JwtAuthUtil.decodeJWT(authorization, jwtPassword, jwtAudience);
                String subject = claims.getSubject();
                String privateKey = claims.getClaimValue("privateKeyBase64", String.class);                
                User user = new User();
                user.setEmail(subject);
                user.setPrivateKeyBase64(privateKey);                               
                ResteasyProviderFactory.pushContext(User.class, user);
            }                
        } catch (Exception ex) {
            ApplicationError ae = new ApplicationError(ex);
            logger.error(ex);
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(ae).build());
        }
    }       
}
