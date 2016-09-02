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
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.dto.ApplicationError;
import it.gpi.wbcp.util.CryptoUtil;
import it.gpi.wbcp.util.MailUtil;
import it.gpi.wbcp.util.PasswordUtil;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
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

@ApplicationPath("/rs")
@Path("/user")
@Stateless
public class UserRestService {
    
    private static final Logger logger = LogManager.getLogger();
    private PasswordUtil pwUtil;
		
    @EJB
    UserDao userDao;
    @EJB
    ApplicationParameterDao aParameterDao;   
    

    @POST
    @Path("/changepwd")
    @Produces(MediaType.APPLICATION_JSON)        
    public Response changePassword(@Context HttpServletRequest httpRequest,
                                    @Context User requestUser,
                                    User user) {    
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());  
        try {
            if (!StringUtil.isNullOrEmpty(user.getEmail()) && !StringUtil.validateEmailPattern(user.getEmail())) {
                ApplicationError ae = new ApplicationError(String.format(lmb.getString("user.email_address_not_valid"), user.getEmail()));
                logger.warn(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                User backEndUser = userDao.getByEmail(user.getEmail());
                if (backEndUser == null) {
                    ApplicationError ae = new ApplicationError(String.format(lmb.getString("user.email_not_exists"), user.getEmail()));
                    logger.warn(ae);
                    response = Response.status(Status.NOT_FOUND).entity(ae).build();                    
                } else {              
                    if (!requestUser.getEmail().equalsIgnoreCase(backEndUser.getEmail())) {
                        ApplicationError ae = new ApplicationError(lmb.getString("user.changepwd_not_allowed_for_different_user"));
                        logger.warn(ae);
                        response = Response.status(Status.BAD_REQUEST).entity(ae).build();                        
                    } else {
                        if (StringUtil.isNullOrEmpty(user.getRequestedClearPassword())) {
                            ApplicationError ae = new ApplicationError(lmb.getString("user.requested_password_empty"));
                            logger.warn(ae);
                            response = Response.status(Status.NOT_FOUND).entity(ae).build();
                        } else {
                            String requestedClearPassword = user.getRequestedClearPassword();
                            pwUtil = new PasswordUtil(requestedClearPassword, httpRequest.getLocale());
                            if (!pwUtil.isAValidPassword()) {
                                ApplicationError ae = new ApplicationError(lmb.getString("user.requested_password_not_valid"));
                                logger.warn(ae);
                                response = Response.status(Status.NOT_FOUND).entity(ae).build();                            
                            } else {
                                backEndUser.setPasswordHashBase64(StringUtil.getSHA512Base64(requestedClearPassword));
                                backEndUser.setRequestedClearPassword(null);                                             

                                backEndUser.setAccountExpirationInstant(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()));

                                User responseUser = userDao.persist(backEndUser); 

                                String smtpHost = aParameterDao.getParameterAsString("SMTP_HOST");
                                Integer smtpPort = aParameterDao.getParameterAsInteger("SMTP_PORT");
                                Boolean sslEnabled = aParameterDao.getParameterAsBoolean("SSL_ENABLED");                                
                                String smtpAuthUsername = aParameterDao.getParameterAsString("SMTP_AUTH_USERNAME");
                                String smtpAuthPassword = aParameterDao.getParameterAsString("SMTP_AUTH_PASSWORD");
                                String mailFromAddress = aParameterDao.getParameterAsString("MAIL_FROM_ADDRESS");
                                String mailSubject = aParameterDao.getParameterAsString("MAIL_SUBJECT_CHANGE_PWD", httpRequest.getLocale());
                                String mailBody = aParameterDao.getParameterAsString("MAIL_BODY_CHANGE_PWD", httpRequest.getLocale());

                                MailUtil mu = new MailUtil(smtpHost, smtpPort, sslEnabled, smtpAuthUsername, smtpAuthPassword);
                                mu.sendMail(mailFromAddress, backEndUser.getEmail(), mailSubject, mailBody);

                                response = Response.status(Status.OK).entity(responseUser).build();   
                            }
                        }       
                    }
                }                
            }  
        } catch (IOException | URISyntaxException ex) {
            ApplicationError ae = new ApplicationError(ex);
            logger.error("Exception in reading JSON default parameters file. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(ex));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();                       
        } catch (MessagingException ex) {
            ApplicationError ae = new ApplicationError(ex);
            logger.error("Exception in sending mail message with private Key. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(ex));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();               
        } catch (Exception eg) {
            ApplicationError aeg = new ApplicationError(eg);
            logger.error("Generic exception in changing user password. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(eg));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }
        return response;
    }            
            
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)        
    public Response create(@Context HttpServletRequest httpRequest, 
                           User user) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());  
        try {
            if (!StringUtil.isNullOrEmpty(user.getEmail()) && !StringUtil.validateEmailPattern(user.getEmail())) {
                ApplicationError ae = new ApplicationError(String.format(lmb.getString("user.email_address_not_valid"), user.getEmail()));
                logger.warn(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                if (userDao.getByEmail(user.getEmail()) != null) {      
                    ApplicationError ae = new ApplicationError(String.format(lmb.getString("user.email_already_exists"), user.getEmail()));
                    logger.warn(ae);
                    response = Response.status(Status.NOT_FOUND).entity(ae).build();
                } else {
                    if (StringUtil.isNullOrEmpty(user.getRequestedClearPassword())) {
                        ApplicationError ae = new ApplicationError(lmb.getString("user.requested_password_empty"));
                        logger.warn(ae);
                        response = Response.status(Status.NOT_FOUND).entity(ae).build();
                    } else {                
                        if (StringUtil.isNullOrEmpty(user.getEmail())) {
                            ApplicationError ae = new ApplicationError(lmb.getString("user.email_empty"));
                            logger.warn(ae);
                            response = Response.status(Status.NOT_FOUND).entity(ae).build();
                        } else {                                        
                            String requestedClearPassword = user.getRequestedClearPassword(); 
                            pwUtil = new PasswordUtil(requestedClearPassword, httpRequest.getLocale());
                            if (!pwUtil.isAValidPassword()) {
                                ApplicationError ae = new ApplicationError(lmb.getString("user.requested_password_not_valid"));
                                logger.warn(ae);
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
                                Boolean sslEnabled = aParameterDao.getParameterAsBoolean("SSL_ENABLED");                                
                                String smtpAuthUsername = aParameterDao.getParameterAsString("SMTP_AUTH_USERNAME");
                                String smtpAuthPassword = aParameterDao.getParameterAsString("SMTP_AUTH_PASSWORD");
                                String mailFromAddress = aParameterDao.getParameterAsString("MAIL_FROM_ADDRESS");
                                String mailSubject = aParameterDao.getParameterAsString("MAIL_SUBJECT_SEND_KEY", httpRequest.getLocale());
                                String mailBody = aParameterDao.getParameterAsString("MAIL_BODY_SEND_KEY", httpRequest.getLocale());
                                
                                MailUtil mu = new MailUtil(smtpHost, smtpPort, sslEnabled, smtpAuthUsername, smtpAuthPassword);
                                String privateKeyBase64 = StringUtil.getBase64EncodedUTF8String(cu.getPrivateKey().getEncoded());
                                mu.sendMailWithAttachment(mailFromAddress, user.getEmail(), mailSubject, mailBody, privateKeyBase64);

                                response = Response.status(Status.OK).entity(responseUser).build();   
                            }
                        }
                    }
                }
            }                        
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            ApplicationError ae = new ApplicationError(e);
            logger.error("Exception creating crypto keys. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(e));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();   
        } catch (IOException | URISyntaxException ex) {
            ApplicationError ae = new ApplicationError(ex);
            logger.error("Exception in reading JSON default parameters file. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(ex));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();                       
        } catch (MessagingException ex) {
            ApplicationError ae = new ApplicationError(ex);
            logger.error("Exception in sending mail message with private Key. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(ex));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();             
        } catch (Exception eg) {
            ApplicationError aeg = new ApplicationError(eg);
            logger.error("Generic exception in creating user. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(eg));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }
        return response;
    }
}
