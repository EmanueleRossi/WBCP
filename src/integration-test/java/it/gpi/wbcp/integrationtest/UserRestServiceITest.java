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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.fail;

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
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);                    
            System.out.printf("testUserCreateInvalidPassowrd(): |%s|%n", responseString);               
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
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));
            String responseString = response.readEntity(String.class);       
            System.out.printf("testUserCreateNoPassword(): |%s|%n", responseString);               
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
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            System.out.printf("testUserCreateNoEmail(): |%s|%n", responseString);               
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
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);            
            System.out.printf("testUserCreateInvalidEmail(): |%s|%n", responseString);               
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
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonUser));            
            String responseString = response.readEntity(String.class);             
            System.out.printf("testUserCreate(): |%s|%n", responseString);               
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

    @Test
    public void testUserChangePassword() {
        try {
            String jsonUser = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserChangePwdUserCreate.json").toURI())));
            ResteasyWebTarget targetUser = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/create", null, null).toASCIIString());
            targetUser.request().accept(MediaType.APPLICATION_JSON_TYPE);      
            Response responseUser = targetUser.request().post(Entity.json(jsonUser));                    
            String responseUserString = responseUser.readEntity(String.class);
            System.out.printf("testUserChangePassword - responseUserString(): |%s|%n", responseUserString);    
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseUser.getStatus()); 
            responseUser.close();
            
            ObjectMapper userObjectMapper = new ObjectMapper();
            JsonNode userObjectRootNode = userObjectMapper.readTree(jsonUser);            

            ResteasyWebTarget targetUserLogin = new ResteasyClientBuilder().build().target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/auth/login", null, null).toASCIIString());
            targetUserLogin.request().accept(MediaType.APPLICATION_FORM_URLENCODED);  
            Form loginForm = new Form();              
            loginForm.param("loginEmail", userObjectRootNode.path("email").asText())
                .param("loginPassword", userObjectRootNode.path("requestedClearPassword").asText())
                .param("privateKeyBase64", "I can't know this... sent by e-mail!");
            Response responseLogin = targetUserLogin.request().post(Entity.form(loginForm));             
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseLogin.getStatus());    
           
            String jsonChangePwd = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/UserChangePwd.json").toURI())));
            ResteasyWebTarget targetChangePwd = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/user/changepwd", null, null).toASCIIString());
            Response responseChangePwd = targetChangePwd.request().header("AuthorizationToken", responseLogin.getHeaderString("AuthorizationToken")).post(Entity.json(jsonChangePwd));            
            String responseChangePwdString = responseChangePwd.readEntity(String.class);
            System.out.printf("testUserChangePassword - responseChangePwdString: |%s|%n", responseChangePwdString);               
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseChangePwd.getStatus());     
            responseLogin.close();
            responseChangePwd.close();
            
            ObjectMapper changePwdObjectMapper = new ObjectMapper();
            JsonNode changePwdRootNode = changePwdObjectMapper.readTree(jsonChangePwd); 
            
            ResteasyWebTarget targetUserLoginNewPwd = new ResteasyClientBuilder().build().target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/auth/login", null, null).toASCIIString());
            targetUserLoginNewPwd.request().accept(MediaType.APPLICATION_FORM_URLENCODED);  
            Form loginFormNewPwd = new Form();            
            loginFormNewPwd.param("loginEmail", changePwdRootNode.path("email").asText())
                .param("loginPassword", changePwdRootNode.path("requestedClearPassword").asText())
                .param("privateKeyBase64", "I can't know this... sent by e-mail!");
            Response responseLoginNewPwd = targetUserLoginNewPwd.request().post(Entity.form(loginFormNewPwd));            
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseLoginNewPwd.getStatus());    
            responseLoginNewPwd.close();

        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }      
    
}
