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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.gpi.wbcp.entity.model.entity.ejb.MessageEjb;
import it.gpi.wbcp.entity.model.entity.ejb.OrganizationEjb;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User extends RootDto {
    
    private String lastName;
    private String firstName;
    private String taxCode;
    private String email;
    private String requestedClearPassword;
    private String passwordHashBase64;      
    @JsonIgnore
    private String publicKeyBase64;         
    private Calendar accountStartInstant;
    private Calendar accountExpirationInstant;    
    @JsonIgnore
    private List<MessageEjb> messages;       
    @JsonIgnore
    private List<OrganizationEjb> organizations;
    
    public User() {
        messages = new ArrayList<>();
        organizations = new ArrayList<>();
    }     
    
    public String getSignature() {
        String signature = new StringBuilder()
                .append(lastName)
                .append(",")
                .append(firstName)
                .append(" <")
                .append(email)
                .append(">").toString();
        return signature;
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }       
	
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRequestedClearPassword() { return requestedClearPassword; }
    public void setRequestedClearPassword(String requestedClearPassword) { this.requestedClearPassword = requestedClearPassword; }
	
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
