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
package it.gpi.wbcp.entity.util;

import it.gpi.wbcp.entity.model.entity.dto.Counter;
import it.gpi.wbcp.entity.model.entity.dto.Message;
import it.gpi.wbcp.entity.model.entity.dto.Organization;
import it.gpi.wbcp.entity.model.entity.dto.User;
import it.gpi.wbcp.entity.model.entity.ejb.CounterEjb;
import it.gpi.wbcp.entity.model.entity.ejb.MessageEjb;
import it.gpi.wbcp.entity.model.entity.ejb.OrganizationEjb;
import it.gpi.wbcp.entity.model.entity.ejb.UserEjb;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapStruct {    
    MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);
 
    @Mappings({        
        @Mapping(target = "requestedClearPassword", ignore = true),
        @Mapping(target = "privateKeyBase64", ignore = true)        
    })
    User userEjbToUser(UserEjb userEjb);    
    @Mappings({        
        @Mapping(target = "creationInstantUTC", ignore = true),
        @Mapping(target = "creationInstantLocale", ignore = true),
        @Mapping(target = "updateInstantUTC", ignore = true),
        @Mapping(target = "updateInstantLocale", ignore = true)    
    })
    UserEjb userToUserEjb(User user);    
    
    Organization organizationEjbToOrganization(OrganizationEjb organizationEjb);
    @Mappings({        
        @Mapping(target = "creationInstantUTC", ignore = true),
        @Mapping(target = "creationInstantLocale", ignore = true),
        @Mapping(target = "updateInstantUTC", ignore = true),
        @Mapping(target = "updateInstantLocale", ignore = true)    
    })    
    OrganizationEjb organizationToOrganizationEjb(Organization organization);
    List<Organization> organizationListEjbToOrganizationList(List<OrganizationEjb> organizationEjbList);
    
    @Mappings({
        @Mapping(target = "instant", ignore = true),
        @Mapping(target = "creationInstantUTC", ignore = true),
        @Mapping(target = "creationInstantLocale", ignore = true),
        @Mapping(target = "updateInstantUTC", ignore = true),
        @Mapping(target = "updateInstantLocale", ignore = true)    
    })     
    MessageEjb messageToMessageEjb(Message message);
    Message messageEjbToMessage(MessageEjb messageEjb);
    List<Message> messageListEjbToMessageList(List<MessageEjb> messageEjbList); 
    
    @Mappings({
        @Mapping(target = "creationInstantUTC", ignore = true),
        @Mapping(target = "creationInstantLocale", ignore = true),
        @Mapping(target = "updateInstantUTC", ignore = true),
        @Mapping(target = "updateInstantLocale", ignore = true)    
    })     
    CounterEjb counterToCounterEjb(Counter counter);    
    Counter counterEjbToCounter(CounterEjb counter);    
}
