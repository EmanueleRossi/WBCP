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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Organization {
    
    private Long id;
    
    private String name;
    private String taxCode;
    private String mailDomain;    
    private User userInCharge;
    private List<User> users;
           
    public Organization() {
        users = new ArrayList<>();
    }     
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    } 
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }       
    
    public String getMailDomain() { return mailDomain; }
    public void setMailDomain(String mailDomain) { this.mailDomain = mailDomain; }       
    
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    
    public User getUserInCharge() { return userInCharge; }
    public void setUserInCharge(User userInCharge) { this.userInCharge = userInCharge; }
}
