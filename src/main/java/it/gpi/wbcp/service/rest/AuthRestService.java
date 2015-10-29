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

import it.gpi.wbcp.entity.model.dao.ApplicationErrorDao;
import it.gpi.wbcp.entity.model.dao.ApplicationParameterDao;
import it.gpi.wbcp.entity.model.dao.UserDao;
import it.gpi.wbcp.entity.model.entity.ejb.ApplicationErrorEjb;
import it.gpi.wbcp.util.JwtAuthUtil;
import it.gpi.wbcp.util.StringUtil;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.jwt.JwtClaims;

@ApplicationPath("/rs")
@Path("/auth")
@Stateless
public class AuthRestService {

    private static final Logger logger = LogManager.getLogger();

    @EJB
    UserDao userDao;
    @EJB
    ApplicationErrorDao aErrorDao;
    @EJB
    ApplicationParameterDao aParameterDao;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest httpRequest, String loginEmail, String loginPassword, String privateKeyBase64) {
        Response response = null;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());
        try {
            if (StringUtil.isNullOrEmpty(loginEmail)) {
                ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("auth.login.email_empty"), loginEmail);
                aErrorDao.persist(ae);
                response = Response.status(Status.BAD_REQUEST).entity(ae).build();
            } else {
                if (StringUtil.isNullOrEmpty(loginPassword)) {
                    ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("auth.login.password_empty"), loginPassword);
                    aErrorDao.persist(ae);
                    response = Response.status(Status.BAD_REQUEST).entity(ae).build();                                                                                
                } else {
                    if (StringUtil.isNullOrEmpty(privateKeyBase64)) {
                        ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("auth.login.privateKeyBase64_empty"), privateKeyBase64);
                        aErrorDao.persist(ae);
                        response = Response.status(Status.BAD_REQUEST).entity(ae).build(); 
                    } else {
                        if (userDao.verifyCredentials(loginEmail, loginPassword)) {
                            
                            Integer jwtTokenExpirationMinutes = aParameterDao.getParameterAsInteger("JWT_TOKEN_EXPIRATION");
                            
                            JwtClaims claims = new JwtClaims();
                            claims.setIssuer("WBCP"); 
                            claims.setAudience("WBCP");
                            claims.setExpirationTimeMinutesInTheFuture(jwtTokenExpirationMinutes);
                            claims.setGeneratedJwtId();
                            claims.setIssuedAtToNow();  
                            claims.setSubject(loginEmail);
                            claims.setClaim("privateKeyBase64", privateKeyBase64);
                            
                            String token = JwtAuthUtil.encodeJWT(claims, "WBCP");
                            
                            response = Response.status(Status.OK).header("AuthorizationToken", token).build();
                        } else {
                            response = Response.status(Status.UNAUTHORIZED).build();
                        } 
                    }                                        
                }
            }
        } catch (Exception eg) {
            ApplicationErrorEjb aeg = new ApplicationErrorEjb(eg);
            aErrorDao.persist(aeg);
            logger.error("Generic exception in login method. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();
        }
        return response;
    }
}
