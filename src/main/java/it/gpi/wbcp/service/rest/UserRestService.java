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
import it.gpi.wbcp.entity.model.dao.OrganizationDao;
import it.gpi.wbcp.entity.model.dao.UserDao;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.ejb.ApplicationErrorEjb;
import it.gpi.wbcp.util.CryptoUtil;
import it.gpi.wbcp.util.MailUtil;
import it.gpi.wbcp.util.PasswordUtil;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
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
@Path("/user")
@Stateless
public class UserRestService {
    
    private static final Logger logger = LogManager.getLogger();
    private PasswordUtil pwUtil;
		
    @EJB
    UserDao userDao;
    @EJB
    ApplicationErrorDao aErrorDao;  
    @EJB
    ApplicationParameterDao aParameterDao;   
    @EJB
    OrganizationDao organizationDao;
/*    
    @GET
    @Path("/list")    
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@Context HttpServletRequest httpRequest) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());         
        try {
            List<User> users = userDao.getAll();
            if (users.isEmpty()) {
                ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.not_found"), new String());
                aErrorDao.persist(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                response = Response.status(Status.OK).entity(users).build();
            }            
        } catch (Exception eg) {
            ApplicationErrorEjb aeg = new ApplicationErrorEjb(eg);
            aErrorDao.persist(aeg);
            logger.error("Generic exception in retrieving users. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;   
    }
	
    @GET
    @Path("/id/{id : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest httpRequest, @PathParam("id") Long id) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());         
        try {
            User u = userDao.getById(id);
            if (u == null) {
                ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.not_found"), String.format("id=|{{}|", id));
                aErrorDao.persist(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                response = Response.status(Status.OK).entity(u).build();
            }            
        } catch (Exception eg) {
            ApplicationErrorEjb aeg = new ApplicationErrorEjb(eg);
            aErrorDao.persist(aeg);
            logger.error("Generic exception in retrieving user by id. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;              
    }    
    
    @GET
    @Path("/email/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest httpRequest, @PathParam("email") String email) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());          
        try {
            if (!StringUtil.validateEmailPattern(email)) {      
                ApplicationErrorEjb ae = new ApplicationErrorEjb(String.format(lmb.getString("user.email_address_not_valid"), email), new String());
                aErrorDao.persist(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                User u = userDao.getByEmail(email);
                if (u == null) {
                ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.not_found"), String.format("email=|{}|", email));
                    aErrorDao.persist(ae);
                    response = Response.status(Status.NOT_FOUND).entity(ae).build();
                } else {
                    response = Response.status(Status.OK).entity(u).build();
                }
            }        
        } catch (Exception eg) {
            ApplicationErrorEjb aeg = new ApplicationErrorEjb(eg);
            aErrorDao.persist(aeg);
            logger.error("Generic exception in retrieving user by email. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;        
    }
*/
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)        
    public Response create(@Context HttpServletRequest httpRequest, @Valid User user) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());  
        try {
            if (!StringUtil.isNullOrEmpty(user.getEmail()) && !StringUtil.validateEmailPattern(user.getEmail())) {
                ApplicationErrorEjb ae = new ApplicationErrorEjb(String.format(lmb.getString("user.email_address_not_valid"), user.getEmail()), new String());
                aErrorDao.persist(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                if (userDao.getByEmail(user.getEmail()) != null) {      
                    ApplicationErrorEjb ae = new ApplicationErrorEjb(String.format(lmb.getString("user.email_already_exists"), user.getEmail()), new String());
                    aErrorDao.persist(ae);
                    response = Response.status(Status.NOT_FOUND).entity(ae).build();
                } else {
                    if (StringUtil.isNullOrEmpty(user.getRequestedClearPassword())) {
                        ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.requested_password_empty"), "user.getRequestedClearPassword()=|" + user.getRequestedClearPassword()+ "|");
                        aErrorDao.persist(ae);
                        response = Response.status(Status.NOT_FOUND).entity(ae).build();
                    } else {                
                        if (StringUtil.isNullOrEmpty(user.getEmail())) {
                            ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.email_empty"), "user.getEmail()=|" + user.getEmail()+ "|");
                            aErrorDao.persist(ae);
                            response = Response.status(Status.NOT_FOUND).entity(ae).build();
                        } else {                                        
                            String requestedClearPassword = user.getRequestedClearPassword();
                            pwUtil = new PasswordUtil(requestedClearPassword, httpRequest.getLocale());
                            if (!pwUtil.isAValidPassword()) {
                                ApplicationErrorEjb ae = new ApplicationErrorEjb(lmb.getString("user.requested_password_not_valid"), pwUtil.getValidationResultMessagesConcat());
                                aErrorDao.persist(ae);
                                response = Response.status(Status.NOT_FOUND).entity(ae).build();                            
                            } else {
                                user.setPasswordHashBase64(StringUtil.getSHA512Base64(requestedClearPassword));
                                user.setRequestedClearPassword(null);                                             

                                CryptoUtil cu = new CryptoUtil();                       
                                user.setPublicKeyBase64(StringUtil.getBase64EncodedUTF8String(cu.getPublicKey().getEncoded()));

                                user.setAccountStartInstant(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));
                                user.setAccountExpirationInstant(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));

                                User responseUser = userDao.persist(user); 

                                String smtpHost = aParameterDao.getParameterAsString("SMTP_HOST");
                                Integer smtpPort = aParameterDao.getParameterAsInteger("SMTP_PORT");
                                String smtpAuthUsername = aParameterDao.getParameterAsString("SMTP_AUTH_USERNAME");
                                String smtpAuthPassword = aParameterDao.getParameterAsString("SMTP_AUTH_PASSWORD");
                                String mailFromAddress = aParameterDao.getParameterAsString("MAIL_FROM_ADDRESS");
                                String mailSubject = aParameterDao.getParameterAsString("MAIL_SUBJECT", httpRequest.getLocale());
                                String mailBody = aParameterDao.getParameterAsString("MAIL_BODY", httpRequest.getLocale());
                                
                                MailUtil mu = new MailUtil(smtpHost, smtpPort, smtpAuthUsername, smtpAuthPassword);
                                String privateKeyBase64 = StringUtil.getBase64EncodedUTF8String(cu.getPrivateKey().getEncoded());
                                mu.sendMailSSL(mailFromAddress, user.getEmail(), mailSubject, mailBody, privateKeyBase64);

                                response = Response.status(Status.OK).entity(responseUser).build();   
                            }
                        }
                    }
                }
            }                        
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            ApplicationErrorEjb ae = new ApplicationErrorEjb(e);
            aErrorDao.persist(ae);
            logger.error("Exception creating crypto keys. CODE=|{}|", ae.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();   
        } catch (IOException | URISyntaxException ex) {
            ApplicationErrorEjb ae = new ApplicationErrorEjb(ex);
            aErrorDao.persist(ae);
            logger.error("Exception in reading JSON default parameters file. CODE=|{}|", ae.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();                       
        } catch (MessagingException ex) {
            ApplicationErrorEjb ae = new ApplicationErrorEjb(ex);
            aErrorDao.persist(ae);
            logger.error("Exception in sending mail message with private Key. CODE=|{}|", ae.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();             
        } catch (Exception eg) {
            ApplicationErrorEjb aeg = new ApplicationErrorEjb(eg);
            aErrorDao.persist(aeg);
            logger.error("Generic exception in creating user. CODE=|{}|", aeg.getCode());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }
        return response;
    }
}
