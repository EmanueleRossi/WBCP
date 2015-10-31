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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="USERS")
//@Inheritance(strategy = InheritanceType.JOINED)
public class UserEjb {
	
        @Id
    @GeneratedValue
    Long id;

    @JsonIgnore
    @Column(name = "CREATION_INSTANT_UTC", nullable = false)    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar creationInstantUTC;
    
    @JsonIgnore
    @Column(name = "CREATION_INSTANT_LOCALE", nullable = false)        
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar creationInstantLocale;
    
    @JsonIgnore    
    @Column(name = "LASTUPDATE_INSTANT_UTC", nullable = false)       
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)        
    private Calendar lastUpdateInstantUTC;

    @JsonIgnore    
    @Column(name = "LASTUPDATE_INSTANT_LOCALE", nullable = false)       
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)        
    private Calendar lastUpdateInstantLocale;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }                     
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }	

    public Calendar getCreationInstantUTC() { return creationInstantUTC; }
    public void setCreationInstantUTC(Calendar creationInstantUTC) { this.creationInstantUTC = creationInstantUTC; }

    public Calendar getCreationInstantLocale() { return creationInstantLocale; }
    public void setCreationInstantLocale(Calendar creationInstantLocale) { this.creationInstantLocale = creationInstantLocale; }

    public Calendar getUpdateInstantUTC() {	return lastUpdateInstantUTC; }
    public void setUpdateInstantUTC(Calendar updateInstantUTC) { this.lastUpdateInstantUTC = updateInstantUTC; }

    public Calendar getUpdateInstantLocale() { return lastUpdateInstantLocale; }
    public void setUpdateInstantLocale(Calendar updateInstantLocale) { this.lastUpdateInstantLocale = updateInstantLocale; }
    
    private static final long serialVersionUID = 1L;
	
    @Size(max = 255, message="{user.lastName.size}")
    @Basic(optional=false)
    @Column(name = "LAST_NAME", nullable = false, length = 255)	
    private String lastName;
    
    @Size(max = 255, message="{user.firstName.size}")
    @Basic(optional=false)
    @Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;

    @Size(max = 255, message="{user.taxCode.size}")      
    @Basic(optional=true)
    @Column(name = "TAX_CODE", nullable = true, length = 255)
    private String taxCode;
    
    @Size(max = 255, message="{user.email.size}")  
    @Basic(optional=false)
    @Column(name = "EMAIL_ADDRESS", nullable = false, unique = true, length = 255) 
    private String email;
     
    @JsonIgnore
    @Basic(optional=false)
    @Column(name = "PASSWORD_HASH_BASE64", nullable = false, length = 255)
    private String passwordHashBase64;
      
    @JsonIgnore
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
    
    @JsonIgnore
    @Basic(optional=true)
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user")
    private List<MessageEjb> messages;
     
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, mappedBy="users")
    private List<OrganizationEjb> organizations;
         
    public UserEjb() {
        messages = new ArrayList<>();
        organizations = new ArrayList<>();
                this.creationInstantUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
        this.creationInstantLocale = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    } 
    
    public String getAuthorSignature() {
        String response = new String();
        response += this.getLastName() + ", " + this.getFirstName() + System.lineSeparator();
        response += this.getTaxCode() + System.lineSeparator();
        response += this.getEmail();                
        return response;
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
