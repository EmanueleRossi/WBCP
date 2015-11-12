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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="BASE_ENTITIES")
@Inheritance(strategy = InheritanceType.JOINED)
public class RootEjb implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    Long id;

    @Column(name = "CREATION_INSTANT_UTC", nullable = false)    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar creationInstantUTC;
    
    @Column(name = "CREATION_INSTANT_LOCALE", nullable = false)        
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar creationInstantLocale;
    
    @Column(name = "LASTUPDATE_INSTANT_UTC", nullable = false)       
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)        
    private Calendar lastUpdateInstantUTC;

    @Column(name = "LASTUPDATE_INSTANT_LOCALE", nullable = false)       
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)        
    private Calendar lastUpdateInstantLocale;

    public RootEjb() {   
        this.creationInstantUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
        this.creationInstantLocale = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    }

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
}
