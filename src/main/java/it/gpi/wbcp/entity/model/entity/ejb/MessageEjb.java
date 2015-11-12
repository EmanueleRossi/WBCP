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
package it.gpi.wbcp.entity.model.entity.ejb;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
@Table(name="MESSAGES")
@Inheritance(strategy = InheritanceType.JOINED)
public class MessageEjb extends RootEjb {

    private static final long serialVersionUID = 1L;    
	
    @Basic(optional=false)
    @ManyToOne    
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private UserEjb user;
    
    @Transient
    private UserEjb sender;
    
    @Transient
    private UserEjb recipient;
    
    @Basic(optional=false)    
    @Column(name = "PAYLOAD", nullable = false, length = 10485760)
    private String payload;

    @Basic(optional=false)    
    @Column(name = "AUTHOR", nullable = false, length = 10485760)
    private String author;    

    @Basic(optional=false)
    @Column(name = "AES_KEY_RSA_CRYPTED_BASE64", nullable = false, length = 1024)
    private String aesKeyRSACryptedBase64;        
    
    @Column(name = "INSTANT", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar instant;

    public MessageEjb() {
        this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    }
    
    public MessageEjb(UserEjb user, UserEjb sender, UserEjb recipient) {
        this();
        this.user = user;
        this.sender = sender;
        this.recipient = recipient;    	
    }

    public UserEjb getUser() { return user; }
    public void setUser(UserEjb user) { this.user = user; }
    
    public UserEjb getSender() { return sender; }
    
    public UserEjb getRecipient() { return recipient; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }     
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }        
    
    public String getAESKeyRSACryptedBase64() { return aesKeyRSACryptedBase64; }
    public void setAESKeyRSACryptedBase64(String aesKeyRSACryptedBase64) { this.aesKeyRSACryptedBase64 = aesKeyRSACryptedBase64; }

    public Calendar getInstant() { return instant; }    
    public void setInstant(Calendar instant) { this.instant = instant; }      	
}
