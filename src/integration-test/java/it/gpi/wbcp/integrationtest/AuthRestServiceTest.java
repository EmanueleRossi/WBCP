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
package it.gpi.wbcp.integrationtest;

import it.gpi.wbcp.util.StringUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthRestServiceTest {
    
    private static final Logger logger = LogManager.getLogger();
    
    private ResteasyClient client;
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }
       
    @Test
    public void testValidLogin() {
        try {                                              
            String jsonAuthLoginUserCreate = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/AuthLoginUserCreate.json").toURI())));                        
            ResteasyWebTarget targetUserCreate = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetUserCreate.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseUserCreate = targetUserCreate.request().post(Entity.json(jsonAuthLoginUserCreate));   
            String responseUserCreateString = responseUserCreate.readEntity(String.class);
            System.out.println("CICICI" + responseUserCreateString);
            assertTrue(responseUserCreate.getStatus() == 200);            
            responseUserCreate.close();
                                                                                          
            String jsonAuthLoginValid = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/AuthLoginValid.json").toURI())));                       
            ResteasyWebTarget targetLoginValid = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/auth/login", null, null).toASCIIString());
            targetLoginValid.request().accept(MediaType.APPLICATION_JSON_TYPE);
            Response responseLoginValid = targetLoginValid.request().post(Entity.json(jsonAuthLoginValid));                  
            assertTrue(responseLoginValid.getStatus() == 200);                        
            byte[] responseLoginValidByteArray = responseLoginValid.readEntity(byte[].class);      
            ObjectMapper responseLoginValidObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseLoginValidObjectMapper.readTree(responseLoginValidByteArray);            
            Assert.assertNotNull(rootNode.path("code"));            
            Assert.assertTrue(rootNode.path("loginEmail").isNull());     
            Assert.assertTrue(rootNode.path("loginPassword").isNull());
            responseLoginValid.close();
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    

    /*
    @Test
    public void testInValidLogin() {
        try {                                                                                                              
            String jsonAuthLoginInValid = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/AuthLoginInValid.json").toURI())));                       
            ResteasyWebTarget targetLoginInValid = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/auth/login", null, null).toASCIIString());
            targetLoginInValid.request().accept(MediaType.APPLICATION_JSON_TYPE);
            Response responseLoginInValid = targetLoginInValid.request().post(Entity.json(jsonAuthLoginInValid));       
            assertTrue(responseLoginInValid.getStatus() == 401);
            responseLoginInValid.close();
                       
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }
    */
}
