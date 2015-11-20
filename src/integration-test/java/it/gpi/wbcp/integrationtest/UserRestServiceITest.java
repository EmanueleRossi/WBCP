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
public class UserRestServiceITest {
    
    private ResteasyClient client;
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }           

    @Test
    public void testUserCreateInvalidPassowrd() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreateInvalidPassword.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);                    
            System.out.printf("testUserCreateInvalidPassowrd(): |{}|", responseString);               
            Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());                                   
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);                        
            Assert.assertNotNull(rootNode.path("message"));              
            response.close(); 
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    
    
    @Test
    public void testUserCreateNoPassword() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreateNoPassword.json").toURI())));
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));
            String responseString = response.readEntity(String.class);       
            System.out.printf("testUserCreateNoPassword(): |{}|", responseString);               
            Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());                  
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);                                           
            Assert.assertNotNull(rootNode.path("message"));  
            response.close();
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    
    
    @Test
    public void testUserCreateNoEmail() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreateNoEmail.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            System.out.printf("testUserCreateNoEmail(): |{}|", responseString);               
            Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());     
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);            
            Assert.assertNotNull(rootNode.path("message"));                      
            response.close();                        
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }
    
    @Test
    public void testUserCreateInvalidEmail() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreateInvalidEmail.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            System.out.printf("testUserCreateInvalidEmail(): |{}|", responseString);               
            Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());              
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);            
            Assert.assertNotNull(rootNode.path("message"));                      
            response.close();                        
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    

    @Test
    public void testUserCreate() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreate.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);             
            System.out.printf("testUserCreate(): |{}|", responseString);               
            Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());                       
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);            
            Assert.assertNotNull(rootNode.path("id"));            
            Assert.assertNotNull(rootNode.path("email"));
            Assert.assertNotNull(rootNode.path("passwordHashBase64"));            
            Assert.assertNotNull(rootNode.path("accountStartDate"));                  
            Assert.assertNotNull(rootNode.path("accountExpirationDate"));              
            Assert.assertTrue(rootNode.path("requestedClearPassword").isNull());                                    
            response.close(); 

        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    
}
