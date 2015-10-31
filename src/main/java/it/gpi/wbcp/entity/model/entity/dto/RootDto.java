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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class RootDto {
    
    private Long id;
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    } 
        
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
