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

import it.gpi.wbcp.util.StringUtil;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="ERRORS")
@Inheritance(strategy = InheritanceType.JOINED)
public class ApplicationErrorEjb extends RootEjb {

    private static final long serialVersionUID = 1L;    
	
    @Basic(optional=false)    
    @Column(name = "CODE", nullable = false, length = 255)
    private String code;
    
    @Basic(optional=false)    
    @Column(name = "MESSAGE", nullable = true, length = 4096)
    private String message;
    
    @Column(name = "INSTANT", nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar instant;
    
    @Basic(optional=true)    
    @Column(name = "DETAILS", nullable = true, length = 65535)
    private String details;
    
    public ApplicationErrorEjb() {
    	this.code = String.valueOf(UUID.randomUUID());
    	this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    }
    
    public ApplicationErrorEjb(String message, String details) {
    	this();
    	this.message = message;
    	this.details = details;
    }
    
    public ApplicationErrorEjb(String message, Exception e) {
        this(message, StringUtil.stringifyStackTrace(e));
    }    
    
    public ApplicationErrorEjb(Exception e) {       
        this(((e.getMessage() != null) ? e.getMessage() : "{null}"), StringUtil.stringifyStackTrace(e));
    } 

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }       

    public Calendar getInstant() { return instant; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }       	
}
