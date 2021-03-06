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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ORGANIZATIONS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OrganizationEjb extends RootEjb {
	
    private static final long serialVersionUID = 1L;
	
    @Basic(optional=false)
    @Column(name = "NAME", nullable = false, length = 255)
    private String name;
          
    @Basic(optional=true)
    @Column(name = "TAX_CODE", nullable = true, length = 255)
    private String taxCode;
          
    @Basic(optional=true)
    @Column(name = "MAIL_DOMAIN", nullable = true, length = 255)
    private String mailDomain;    
    
    @Basic(optional=false)
    @Column(name = "NEW_MESSAGE_TEMPLATE", nullable = false, length = 65535)
    private String newMessageTemplate;
           
    @Basic(optional=false)
    @Column(name = "UI_STYLE", nullable = false, length = 65535)
    private String uiStyle;    
    
    @Basic(optional=true)
    @ManyToOne    
    @JoinColumn(name = "USER_IN_CHARGE_ID", referencedColumnName = "ID")
    private UserEjb userInCharge;
            
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="ORGANIZATIONS_USERS",
        joinColumns={@JoinColumn(name="ORGANIZATION_ID", referencedColumnName="ID")},
        inverseJoinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")}
    )
    private List<UserEjb> users;
    
    @Basic(optional=true)
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "organization")
    private List<CounterEjb> counters;    
           
    public OrganizationEjb() {
        users = new ArrayList<>();
    }           

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }       
    
    public String getMailDomain() { return mailDomain; }
    public void setMailDomain(String mailDomain) { this.mailDomain = mailDomain; }       
    
    public String getNewMessageTemplate() { return newMessageTemplate; }
    public void setNewMessageTemplate(String newMessageTemplate) { this.newMessageTemplate = newMessageTemplate; }    
    
    public String getUiStyle() { return uiStyle; }
    public void setUiStyle(String uiStyle) { this.uiStyle = uiStyle; }    
    
    public List<UserEjb> getUsers() { return users; }
    public void setUsers(List<UserEjb> users) { this.users = users; }
    
    public UserEjb getUserInCharge() { return userInCharge; }
    public void setUserInCharge(UserEjb userInCharge) { this.userInCharge = userInCharge; }
    
    public List<CounterEjb> getCounters() { return counters; }
    public void setCounters(List<CounterEjb> counters) { this.counters = counters; }    
}
