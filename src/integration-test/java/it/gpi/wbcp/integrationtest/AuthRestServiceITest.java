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
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthRestServiceITest {
    
    private ResteasyClient client;
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }
       
    @Test
    public void testValidLogin() {
        try {                                                                                                               
            ResteasyWebTarget targetLoginValid = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/auth/login", null, null).toASCIIString());
            targetLoginValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            targetLoginParams.add("loginEmail", IntegrationTestSuite.LOGIN_EMAIL);
            targetLoginParams.add("loginPassword", IntegrationTestSuite.LOGIN_PASSWORD);
            targetLoginParams.add("privateKeyBase64", "SECRET");
            Response responseLoginValid = targetLoginValid.request().post(Entity.form(targetLoginParams));  
            System.out.printf("testValidLogin(): |%s|%n", responseLoginValid.readEntity(String.class));
            Assert.assertEquals(responseLoginValid.getStatus(), Status.OK.getStatusCode());   
            IntegrationTestSuite.TOKEN = responseLoginValid.getHeaderString("AuthorizationToken");
            Assert.assertNotNull(IntegrationTestSuite.TOKEN);            
            responseLoginValid.close();
            
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    

    @Test
    public void testWrongLoginParameters() {
        try {                                                                                                              
            ResteasyWebTarget targetLoginValid = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/auth/login", null, null).toASCIIString());
            targetLoginValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            Response responseLoginValid_01 = targetLoginValid.request().header("AuthorizationToken", IntegrationTestSuite.TOKEN).post(Entity.form(targetLoginParams));     
            System.out.printf("testWrongLoginParameters(): |%s|%n", responseLoginValid_01.readEntity(String.class));
            Assert.assertEquals(responseLoginValid_01.getStatus(), Status.BAD_REQUEST.getStatusCode());                   
            targetLoginParams.add("loginEmail", IntegrationTestSuite.LOGIN_EMAIL);
            responseLoginValid_01.close();            
            Response responseLoginValid_02 = targetLoginValid.request().header("AuthorizationToken", IntegrationTestSuite.TOKEN).post(Entity.form(targetLoginParams));
            System.out.printf("testWrongLoginParameters(): |%s|%n", responseLoginValid_02.readEntity(String.class));
            Assert.assertEquals(responseLoginValid_02.getStatus(), Status.BAD_REQUEST.getStatusCode());   
            responseLoginValid_02.close();            
            targetLoginParams.add("loginPassword", IntegrationTestSuite.LOGIN_PASSWORD);
            Response responseLoginValid_03 = targetLoginValid.request().header("AuthorizationToken", IntegrationTestSuite.TOKEN).post(Entity.form(targetLoginParams));   
            System.out.printf("testWrongLoginParameters(): |%s|%n", responseLoginValid_03.readEntity(String.class));            
            Assert.assertEquals(responseLoginValid_03.getStatus(), Status.BAD_REQUEST.getStatusCode());
            responseLoginValid_03.close();            
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }
    
    @Test
    public void testInvalidPassword() {
        try {                                                                                                              
            ResteasyWebTarget targetLoginInValid = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/auth/login", null, null).toASCIIString());
            targetLoginInValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            targetLoginParams.add("loginEmail", IntegrationTestSuite.LOGIN_EMAIL);
            targetLoginParams.add("loginPassword", "WRONG PASSWORD");
            targetLoginParams.add("privateKeyBase64", "SECRET");
            Response responseLoginInValid = targetLoginInValid.request().header("AuthorizationToken", IntegrationTestSuite.TOKEN).post(Entity.form(targetLoginParams));
            System.out.printf("testInvalidPassword(): |%s|%n", responseLoginInValid.readEntity(String.class));             
            Assert.assertEquals(responseLoginInValid.getStatus(), Status.UNAUTHORIZED.getStatusCode());   
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    
    
    @Test
    public void testNoToken() {
        try {                                                                                                              
            String jsonOrganization = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateNoName.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/organization/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                   
            Response response = target.request().post(Entity.json(jsonOrganization));
            String responseString = response.readEntity(String.class);  
            System.out.printf("testNoToken(): |%s|%n", responseString);              
            Assert.assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());    
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException | IOException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }   
    
    @Test
    public void testWrongToken() {
        try {                                                                                                              
            String jsonOrganization = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateNoName.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP/rs/organization/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                   
            Response response = target.request().header("AuthorizationToken", "WRONG TOKEN!!!").post(Entity.json(jsonOrganization));
            String responseString = response.readEntity(String.class);  
            System.out.printf("testWrongToken(): |%s|%n", responseString);              
            Assert.assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());    
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException | IOException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }      
}
