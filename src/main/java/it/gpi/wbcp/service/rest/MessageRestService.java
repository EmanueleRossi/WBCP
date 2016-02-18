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

import it.gpi.wbcp.entity.model.dao.MessageDao;
import it.gpi.wbcp.entity.model.dao.UserDao;
import it.gpi.wbcp.entity.model.entity.dto.Message;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.dto.ApplicationError;
import it.gpi.wbcp.util.CryptoUtil;
import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
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
@Path("/message")
@Stateless
public class MessageRestService {
    
    private static final Logger logger = LogManager.getLogger();
		
    @EJB
    UserDao userDao;
    @EJB
    MessageDao messageDao;
	
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)    
    public Response create(@Context HttpServletRequest httpRequest, 
                           @Context User requestUser,
                           Message message) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());
        try {
            if (message.getUser() == null | StringUtil.isNullOrEmpty(message.getUser().getEmail())) {              
                ApplicationError ae = new ApplicationError(lmb.getString("message.creation.no_user"));
                logger.warn(ae);
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {
                if (message.getSender() == null | StringUtil.isNullOrEmpty(message.getSender().getEmail())) {              
                    ApplicationError ae = new ApplicationError(lmb.getString("message.creation.no_sender"));
                    logger.warn(ae);
                    response = Response.status(Status.NOT_FOUND).entity(ae).build();
                } else {
                    if (message.getRecipient() == null | StringUtil.isNullOrEmpty(message.getRecipient().getEmail())) {              
                        ApplicationError ae = new ApplicationError(lmb.getString("message.creation.no_recipient"));
                        logger.warn(ae);
                        response = Response.status(Status.NOT_FOUND).entity(ae).build();
                    } else {                       
                        User user = userDao.getByEmail(requestUser.getEmail());
                        if (user == null) {
                            ApplicationError ae = new ApplicationError(lmb.getString("message.creation.user_not_registered"));
                            logger.warn(ae);
                            response = Response.status(Status.NOT_FOUND).entity(ae).build();
                        } else {
                            User sender = userDao.getByEmail(message.getSender().getEmail());
                            if (sender == null) {
                                ApplicationError ae = new ApplicationError(lmb.getString("message.creation.sender_not_registered"));
                                logger.warn(ae);
                                response = Response.status(Status.NOT_FOUND).entity(ae).build();                                
                            } else {
                                User recipient = userDao.getByEmail(message.getRecipient().getEmail());
                                if (recipient == null) {
                                    ApplicationError ae = new ApplicationError(lmb.getString("message.creation.recipient_not_registered"));
                                    logger.warn(ae);
                                    response = Response.status(Status.NOT_FOUND).entity(ae).build();   
                                } else {                         
                                    String clearTextPayload = message.getPayload();
                                    
                                    message.setUser(user);                                        
                                    CryptoUtil cuSender = new CryptoUtil(sender.getPublicKeyBase64());
                                    SecretKey senderAESMessageKey = cuSender.newAESKey();
                                    byte[] senderEncryptedPayload = cuSender.encrypt_AES(clearTextPayload.getBytes(StandardCharsets.UTF_8.name()), senderAESMessageKey);
                                    byte[] senderEncryptedAuthor = cuSender.encrypt_AES(sender.getSignature().getBytes(StandardCharsets.UTF_8.name()), senderAESMessageKey);
                                    message.setPayload(StringUtil.getBase64EncodedUTF8String(senderEncryptedPayload));  
                                    message.setAuthor(StringUtil.getBase64EncodedUTF8String(senderEncryptedAuthor));
                                    byte[] cryptedSenderAESMessageKey = cuSender.encrypt_RSA(senderAESMessageKey.getEncoded());
                                    message.setAESKeyRSACryptedBase64(StringUtil.getBase64EncodedUTF8String(cryptedSenderAESMessageKey));
                                    messageDao.persist(message);                

                                    Message recipientMessage = new Message();     
                                    recipientMessage.setUser(recipient);
                                    recipientMessage.setSender(sender);
                                    recipientMessage.setRecipient(recipient);                                                                        
                                    CryptoUtil cuRecipient = new CryptoUtil(recipient.getPublicKeyBase64());
                                    SecretKey recipientAESMessageKey = cuRecipient.newAESKey();
                                    byte[] recipientEncryptedPayload = cuRecipient.encrypt_AES(clearTextPayload.getBytes(StandardCharsets.UTF_8.name()), recipientAESMessageKey);
                                    byte[] recipientEncryptedAuthor = cuRecipient.encrypt_AES(recipient.getSignature().getBytes(StandardCharsets.UTF_8.name()), recipientAESMessageKey);                                    
                                    recipientMessage.setPayload(StringUtil.getBase64EncodedUTF8String(recipientEncryptedPayload));
                                    recipientMessage.setAuthor(StringUtil.getBase64EncodedUTF8String(recipientEncryptedAuthor));
                                    byte[] cryptedRecipientAESMessageKey = cuRecipient.encrypt_RSA(recipientAESMessageKey.getEncoded());
                                    recipientMessage.setAESKeyRSACryptedBase64(StringUtil.getBase64EncodedUTF8String(cryptedRecipientAESMessageKey));
                                    messageDao.persist(recipientMessage);
                                    
                                    response = Response.status(Status.OK).entity(message).build();
                                }
                            }                            
                        }
                    }    
                }   
            }                                
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | IOException e) {
            ApplicationError ae = new ApplicationError(e);
            logger.error("Exception using crypto utility. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(e));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();   
        } catch (Exception eg) {
            ApplicationError ae = new ApplicationError(eg);
            logger.error("Generic exception. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(eg));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(ae).build();   
        }        
        return response;
    }        
    
    @POST
    @Path("/searchByInstant/{instantFrom}/{instantTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByInstant(@Context HttpServletRequest httpRequest,
                                    @PathParam("instantFrom") String instantFrom, 
                                    @PathParam("instantTo") String instantTo,
                                    @Context User requestUser) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());
        Calendar instantFromCalendar = Calendar.getInstance(httpRequest.getLocale());
        Calendar instantToCalendar = Calendar.getInstance(httpRequest.getLocale());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");        
        try {                     
            instantFromCalendar.setTime(sdf.parse(instantFrom));   
            instantToCalendar.setTime(sdf.parse(instantTo));
            User user = userDao.getByEmail(requestUser.getEmail());                                    
            List<Message> messages = messageDao.searchByInstant(user, instantFromCalendar, instantToCalendar);                       
            if (messages == null || messages.isEmpty()) {
                ApplicationError ae = new ApplicationError(lmb.getString("message.not_found") + String.format("instantFrom=|{}|, instantTo=|{}|", instantFrom, instantTo));
                logger.trace(ae);                
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {                                                                                           
                CryptoUtil cu = new CryptoUtil(user.getPublicKeyBase64(), requestUser.getPrivateKeyBase64());    
                logger.trace(user);
                logger.trace(requestUser);
                for (Message message : messages) {
                    logger.trace(message);
                    byte[] aesKeyByteArray = cu.decrypt_RSA(StringUtil.decodeBase64(message.getAESKeyRSACryptedBase64()));                                       
                    byte[] decodedMessagePayload = cu.decrypt_AES(StringUtil.decodeBase64(message.getPayload()), new SecretKeySpec(aesKeyByteArray, 0, aesKeyByteArray.length, "AES"));
                    message.setPayload(new String(decodedMessagePayload, StandardCharsets.UTF_8.name())); 
                }                                              
                response = Response.status(Status.OK).entity(messages).build();                
            }                   
        } catch (ParseException pe) {
            ApplicationError ae = new ApplicationError(String.format(lmb.getString("message.search_by_instant.invalid_date"), instantFrom, instantTo), pe);
            logger.warn(ae);                   
            response = Response.status(Status.NOT_FOUND).entity(ae).build();
        } catch (Exception eg) { 
            ApplicationError aeg = new ApplicationError(eg);                        
            logger.error("Generic exception executing message search. STACKTRACE=|{}|", StringUtil.stringifyStackTrace(eg));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;        
    }  
    
    @POST
    @Path("/getAuthor/{messageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthor(@Context HttpServletRequest httpRequest,
                              @PathParam("messageId") Long messageId,
                              @Context User requestUser) {
        Response response;
        ResourceBundle lmb = ResourceBundle.getBundle("WBCP-web", httpRequest.getLocale());
        try {                     
            User user = userDao.getByEmail(requestUser.getEmail());                                    
            Message message = messageDao.getById(messageId);
            if (message == null) {
                ApplicationError ae = new ApplicationError(lmb.getString("message.not_found") + String.format("messageId=|{}|", messageId));
                logger.trace(ae);                
                response = Response.status(Status.NOT_FOUND).entity(ae).build();
            } else {                                                                                           
                CryptoUtil cu = new CryptoUtil(user.getPublicKeyBase64(), requestUser.getPrivateKeyBase64());                
               
                byte[] aesKeyByteArray = cu.decrypt_RSA(StringUtil.decodeBase64(message.getAESKeyRSACryptedBase64()));                                       
                byte[] decodedMessageAuthor = cu.decrypt_AES(StringUtil.decodeBase64(message.getAuthor()), new SecretKeySpec(aesKeyByteArray, 0, aesKeyByteArray.length, "AES"));
                message.setAuthor(new String(decodedMessageAuthor, StandardCharsets.UTF_8.name())); 
                                                              
                response = Response.status(Status.OK).entity(message).build();                
            }                   
        } catch (Exception eg) { 
            ApplicationError aeg = new ApplicationError(eg);                        
            logger.error("Generic exception executing getAuthor(). STACKTRACE=|{}|", StringUtil.stringifyStackTrace(eg));
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(aeg).build();             
        }     
        return response;        
    }    
}
