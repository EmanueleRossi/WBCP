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
package it.gpi.wbcp.entity.mapper;

import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.ejb.UserEjb;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
 
    @Mappings({        
        @Mapping(target = "requestedClearPassword", ignore = true)
    })
    User userEjbToUser(UserEjb userEjb);    
    @Mappings({        
        @Mapping(target = "creationInstantUTC", ignore = true),
        @Mapping(target = "creationInstantLocale", ignore = true),
        @Mapping(target = "updateInstantUTC", ignore = true),
        @Mapping(target = "updateInstantLocale", ignore = true)    
    })
    UserEjb userToUserEjb(User user);    
}
