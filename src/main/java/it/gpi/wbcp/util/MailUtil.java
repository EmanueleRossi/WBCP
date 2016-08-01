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
package it.gpi.wbcp.util;

import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
 
public class MailUtil {
    
    private final String smtpHost;
    private final int smtpPort;
    
    private final String username;
    private final String password;
    private final String sslEnabled;
    
    public MailUtil(String smtpHost, int smtpPort, Boolean sslEnabled, String username, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.sslEnabled = String.valueOf(sslEnabled);
        this.username = username;
        this.password = password;        
    }
        
    public void sendMail(String fromAddress, String toAddress, String subject, String text, String attachmentFileString) throws MessagingException, IOException {
        
        Properties props = new Properties();
	props.put("mail.smtp.host", this.smtpHost);
        props.put("mail.smtp.port", this.smtpPort);                                     
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", this.sslEnabled);        
	
        Session session;
        if (StringUtil.isNullOrEmpty(this.username) && StringUtil.isNullOrEmpty(this.password)) {
            session = Session.getInstance(props);
        } else {
            session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        }
 
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        message.setSubject(subject);
                        
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(text, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();                
        DataSource source = new ByteArrayDataSource(attachmentFileString, "text/plain");
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName("private.key");        
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);
        
        Transport.send(message);        
    }    
}
