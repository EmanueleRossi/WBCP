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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="COUNTERS")
@Inheritance(strategy = InheritanceType.JOINED)
public class CounterEjb extends RootEjb {
	    
    private static final long serialVersionUID = 1L;
	
    @Basic(optional=false)
    @Column(name = "YEAR", nullable = false)	
    private Integer year;
    
    @Basic(optional=false)
    @Column(name = "LENGHT", nullable = false)
    private Integer lenght;
    
    @Basic(optional=false)
    @Column(name = "VALUE", nullable = false)
    private Integer value;  
    
    @Basic(optional=false)
    @ManyToOne    
    @JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "ID")
    private OrganizationEjb organization;
                             
    public CounterEjb() {
    } 
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getLenght() { return lenght; }
    public void setLenght(Integer lenght) { this.lenght = lenght; }        
      
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }    
    
    public OrganizationEjb getOrganization() { return organization; }
    public void setOrganization(OrganizationEjb value) { this.organization = organization; }        
}
