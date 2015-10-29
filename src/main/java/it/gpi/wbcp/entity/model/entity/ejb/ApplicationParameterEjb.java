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

import java.util.Locale;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Cacheable(false)
@Table(name="APP_PARAMETERS")
@Inheritance(strategy = InheritanceType.JOINED)
public class ApplicationParameterEjb extends RootEjb {
	
    private static final long serialVersionUID = 1L;
	
    @Basic(optional=false)
    @Column(name = "NAME", nullable = false, length = 255, unique = true)
    private String name;
    
    @Basic(optional=false)
    @Column(name = "DESCRIPTION", nullable = false, length = 65535)
    private String description;
    
    @Basic(optional=false)
    @Column(name = "PARAMETER_VALUE", nullable = false, length = 65535)
    private String parameterValue;    
    
    @Basic(optional=false)
    @Column(name = "LOCALE_TAG", nullable = false, length = 255)
    private String localeTag;   
    
    public ApplicationParameterEjb() {
    }
    
    public Locale getLocale() { 
        return Locale.forLanguageTag(localeTag);
    }
    public void setLocale(Locale locale) { 
        this.localeTag = locale.toLanguageTag(); 
    }           

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getParameterValue() { return parameterValue; }
    public void setParameterValue(String parameterValue) { this.parameterValue = parameterValue; }           
}
