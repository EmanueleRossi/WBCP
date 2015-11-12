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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="USERS")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEjb extends RootEjb {
	    
    private static final long serialVersionUID = 1L;
	
    @Basic(optional=false)
    @Column(name = "LAST_NAME", nullable = false, length = 255)	
    private String lastName;
    
    @Basic(optional=false)
    @Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;

    @Basic(optional=true)
    @Column(name = "TAX_CODE", nullable = true, length = 255)
    private String taxCode;
      
    @Basic(optional=false)
    @Column(name = "EMAIL_ADDRESS", nullable = false, unique = true, length = 255) 
    private String email;
     
    @Basic(optional=false)
    @Column(name = "PASSWORD_HASH_BASE64", nullable = false, length = 255)
    private String passwordHashBase64;
      
    @Basic(optional=false)
    @Column(name = "PUBLIC_KEY_BASE64", nullable = false, length = 1024)
    private String publicKeyBase64;         
      
    @Basic(optional=false)
    @Column(name = "ACCOUNT_START_INSTANT", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar accountStartInstant;
        
    @Basic(optional=false)
    @Column(name = "ACCOUNT_EXPIRATION_INSTANT", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar accountExpirationInstant;
    
    @Basic(optional=true)
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user")
    private List<MessageEjb> messages;
     
    @ManyToMany(cascade = {CascadeType.ALL}, mappedBy="users")
    private List<OrganizationEjb> organizations;
         
    public UserEjb() {
        messages = new ArrayList<>();
        organizations = new ArrayList<>();
    } 
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }       
	
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
	
    public String getPasswordHashBase64() { return passwordHashBase64; }
    public void setPasswordHashBase64(String passwordHashBase64) { this.passwordHashBase64 = passwordHashBase64; }
    
    public String getPublicKeyBase64() { return publicKeyBase64; }
    public void setPublicKeyBase64(String publicKeyBase64) { this.publicKeyBase64 = publicKeyBase64; }
	
    public Calendar getAccountStartInstant() { return accountStartInstant; }
    public void setAccountStartInstant(Calendar accountStartInstant) { this.accountStartInstant = accountStartInstant; }
	
    public Calendar getAccountExpirationInstant() { return accountExpirationInstant; }
    public void setAccountExpirationInstant(Calendar accountExpirationInstant) { this.accountExpirationInstant = accountExpirationInstant; }
    
    public List<MessageEjb> getMessages() { return messages; }
    public void setMessages(List<MessageEjb> messages) { this.messages = messages; } 
    
    public List<OrganizationEjb> getOrganizations() { return organizations; }
    public void setOrganizations(List<OrganizationEjb> organizations) { this.organizations = organizations; }        
}
