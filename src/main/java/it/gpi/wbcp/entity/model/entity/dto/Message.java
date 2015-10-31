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
package it.gpi.wbcp.entity.model.entity.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Calendar;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property="type")
public class Message extends RootDto {

    private static final long serialVersionUID = 1L;    
	
    private User user;
    private User sender;    
    private User recipient;
    private String payload;
    private String author;    
    private String aesKeyRSACryptedBase64;        
    private Calendar instant;

    public Message() {
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }     
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }        
    
    public String getAESKeyRSACryptedBase64() { return aesKeyRSACryptedBase64; }
    public void setAESKeyRSACryptedBase64(String aesKeyRSACryptedBase64) { this.aesKeyRSACryptedBase64 = aesKeyRSACryptedBase64; }

    public Calendar getInstant() { return instant; }    
    public void setInstant(Calendar instant) { this.instant = instant; }      	
}
