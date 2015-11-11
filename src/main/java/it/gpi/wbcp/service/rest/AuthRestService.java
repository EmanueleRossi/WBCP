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

import it.gpi.wbcp.entity.model.dao.ApplicationParameterDao;
import it.gpi.wbcp.entity.model.dao.UserDao;
import it.gpi.wbcp.entity.model.entity.dto.ApplicationError;
import it.gpi.wbcp.util.JwtAuthUtil;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import org.jose4j.lang.JoseException;

@ApplicationPath("/rs")
@Path("/auth")
@Stateless
public class AuthRestService {

    private static final Logger logger = LogManager.getLogger();

    @EJB
    UserDao userDao;
    @EJB
    ApplicationParameterDao aParameterDao;

    @POST    
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)    
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(@Context HttpServletRequest httpRequest, 
                          @FormParam("loginEmail") String loginEmail, 
                          @FormParam("loginPassword") String loginPassword, 
                          @FormParam("privateKeyBase64") String privateKeyBase64) {
        Response response = null;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());
        try {
            if (StringUtil.isNullOrEmpty(loginEmail)) {
                ApplicationError ae = new ApplicationError(lmb.getString("auth.login.email_empty"));
                logger.warn(ae);                
                response = Response.status(Status.BAD_REQUEST).entity(ae).build();
            } else {
                if (StringUtil.isNullOrEmpty(loginPassword)) {
                    ApplicationError ae = new ApplicationError(lmb.getString("auth.login.password_empty"));
                    logger.warn(ae);                
                    response = Response.status(Status.BAD_REQUEST).entity(ae).build();                                                                                
                } else {
                    if (StringUtil.isNullOrEmpty(privateKeyBase64)) {
                        ApplicationError ae = new ApplicationError(lmb.getString("auth.login.privateKeyBase64_empty"));
                        logger.warn(ae);                
                        response = Response.status(Status.BAD_REQUEST).entity(ae).build(); 
                    } else {
                        if (userDao.verifyCredentials(loginEmail, loginPassword)) {                            
                            String token = this.generateJwtToken(loginEmail, privateKeyBase64);
                            response = Response.status(Status.OK).header("AuthorizationToken", token).build();
                        } else {
                            response = Response.status(Status.UNAUTHORIZED).build();
                        } 
                    }                                        
                }
            }
        } catch (Exception eg) {
            ApplicationError aeg = new ApplicationError(eg);
            logger.error(aeg);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();
        }
        return response;
    }
    
    private String generateJwtToken(String loginEmail, String privateKeyBase64) throws JoseException, IOException, URISyntaxException {
        String jwtIssuer = aParameterDao.getParameterAsString("JWT_ISSUER");
        String jwtAudience = aParameterDao.getParameterAsString("JWT_AUDIENCE");        
        Integer jwtTokenExpirationMinutes = aParameterDao.getParameterAsInteger("JWT_TOKEN_EXPIRATION");              
        String jwtPassword = aParameterDao.getParameterAsString("JWT_PASSWORD");        
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(jwtIssuer); 
        claims.setAudience(jwtAudience);
        claims.setExpirationTimeMinutesInTheFuture(jwtTokenExpirationMinutes);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();  
        claims.setSubject(loginEmail);
        claims.setClaim("privateKeyBase64", privateKeyBase64);                                                        
        String token = JwtAuthUtil.encodeJWT(claims, jwtPassword);
        return token;        
    }    
}
