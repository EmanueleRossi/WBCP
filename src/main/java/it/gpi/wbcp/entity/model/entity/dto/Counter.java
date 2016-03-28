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
import it.gpi.wbcp.util.StringUtil;

public class Counter extends RootDto {
       
    private Integer year;
    private Integer lenght;   
    private String separator;
    private Long value;   
    private Organization organization;
    
    public Counter() {
    }
    
    @JsonIgnore    
    public String getFormattedValue() {
        String response = new StringBuilder()
            .append(this.getYear())
            .append(this.getSeparator())
            .append(StringUtil.padLeft(String.valueOf(this.getValue()), this.getLenght()))
            .toString();
        return response;
    }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getLenght() { return lenght; }
    public void setLenght(Integer lenght) { this.lenght = lenght; }     
    
    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }
      
    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }               

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }     
}
