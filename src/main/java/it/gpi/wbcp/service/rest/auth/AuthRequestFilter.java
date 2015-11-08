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
package it.gpi.wbcp.service.rest.auth;

import java.io.IOException;
import java.util.Iterator;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthRequestFilter implements ContainerRequestFilter {
    
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        
        SecurityContext originalContext = containerRequestContext.getSecurityContext();
            
        String headersString = new String();
        MultivaluedMap headers = containerRequestContext.getHeaders();
        Iterator<String> it = headers.keySet().iterator();
        
        while(it.hasNext()) {
            String key = it.next();
            headersString += key + "=|" + headers.get(key) + "| ";
        }
               
        logger.debug("Authorization DEBUG: |{}|", headersString);
                        
        //containerRequestContext.abortWith(response);
        //ResteasyProviderFactory.pushContext(String.class, authorization);
    }       
}
