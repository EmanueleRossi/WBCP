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
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ApplicationError extends Exception {
	
    private String code;
    @JsonIgnore
    private final Calendar instant;
    
    public ApplicationError() {
    	this.code = String.valueOf(UUID.randomUUID());
    	this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());               
    }
    
    public ApplicationError (String message) {        
        super(message);
    	this.code = String.valueOf(UUID.randomUUID());
    	this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());         
    }

    public ApplicationError (Throwable cause) {
        super (cause);
    	this.code = String.valueOf(UUID.randomUUID());
    	this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());          
    }

    public ApplicationError (String message, Throwable cause) {
        super (message, cause);
    	this.code = String.valueOf(UUID.randomUUID());
    	this.instant = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());          
    }    

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }     
    
    public Calendar getInstant() { return instant; }       	
}
