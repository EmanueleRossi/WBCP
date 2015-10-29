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
package it.gpi.wbcp.service.rest;

import it.gpi.wbcp.service.rest.validation.ValidationExceptionMapper;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
 
public class RestApplication extends Application {
	
    private final Set<Object> singletons;
    private final Set<Class<?>> classes;
 
    public RestApplication() {
        this.singletons = new HashSet<>();
        this.classes = new HashSet<>();
        
    	classes.add(OrganizationRestService.class);
    	classes.add(UserRestService.class);
        classes.add(MessageRestService.class);
        classes.add(AuthRestService.class);
        
        classes.add(ValidationExceptionMapper.class);
    }
 
    @Override
    public Set<Class<?>> getClasses() {
        return this.classes;
    }
 
    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
}