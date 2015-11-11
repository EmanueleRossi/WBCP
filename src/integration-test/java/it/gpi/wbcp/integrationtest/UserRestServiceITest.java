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
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);                    
            assertTrue(response.getStatus() == 404);                                   
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
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));
            String responseString = response.readEntity(String.class);            
            assertTrue(response.getStatus() == 404);                       
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
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            assertTrue(response.getStatus() == 404);           
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
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            assertTrue(response.getStatus() == 404);           
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);            
            Assert.assertNotNull(rootNode.path("message"));                      
            response.close();                        
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    

    @Test
    public void testUserCreateBeanValidation() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserCreateBeanValidation.json").toURI())));
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));
            String responseString = response.readEntity(String.class);            
            assertTrue(response.getStatus() == 404);                                   
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
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);             
            assertTrue(response.getStatus() == 200);                       
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

    @Test
    public void testUserGetByEMailAndId() {
        try {
            ResteasyWebTarget targetEmail = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/email/emanuele.rossi@gpi.it", null, null).toASCIIString());
            targetEmail.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseEmail = targetEmail.request().get();
            String responseEmailString = responseEmail.readEntity(String.class); 
            assertTrue(responseEmail.getStatus() == 200);                    
            ObjectMapper responseEmailObjectMapper = new ObjectMapper();
            JsonNode rootNodeEmail = responseEmailObjectMapper.readTree(responseEmailString);                        
            Assert.assertNotNull(rootNodeEmail.path("email"));                                              
            Assert.assertNotNull(rootNodeEmail.path("id"));
            Assert.assertEquals("emanuele.rossi@gpi.it", rootNodeEmail.path("email").getTextValue());
            responseEmail.close();
            
            ResteasyWebTarget targetId =  client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/id/" + rootNodeEmail.path("id"), null, null).toASCIIString());            
            targetId.request().accept(MediaType.APPLICATION_JSON_TYPE);  
            Response responseId = targetId.request().get(); 
            String responseIdString = responseId.readEntity(String.class);   
            assertTrue(responseId.getStatus() == 200);                                    
            ObjectMapper responseIdObjectMapper = new ObjectMapper();
            JsonNode rootNodeId = responseIdObjectMapper.readTree(responseIdString);                              
            Assert.assertNotNull(rootNodeId.path("id"));
            Assert.assertNotNull(rootNodeId.path("email"));                           
            Assert.assertEquals(rootNodeEmail.path("id").asInt(), rootNodeId.path("id").asInt());            
            Assert.assertEquals("emanuele.rossi@gpi.it", rootNodeId.path("email").getTextValue());
            responseId.close();
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }     
    
    @Test
    public void testUserGetByEMailNotFound() {
        try {            
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/email/emanuele.rossi@gpi.com", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().get();
            String responseString = response.readEntity(String.class);            
            assertTrue(response.getStatus() == 404);                                   
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);            
            Assert.assertNotNull(rootNode.path("message"));                      
            response.close();
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }            
    } 
    
    @Test
    public void testUserListWithResult() {
        try {
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/list", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().get();         
            assertTrue(response.getStatus() == 200);                                   
            
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }       
}
