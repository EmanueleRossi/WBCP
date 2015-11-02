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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthRestServiceTest {
    
    private static final Logger logger = LogManager.getLogger();
    
    private ResteasyClient client;
    private static String loginEmail;
    private static String loginPassword;
    private String authorizationToken;
    
    @BeforeClass
    public static void createTestUser() {
        try {          
            String jsonAuthLoginUserCreate = new String(Files.readAllBytes(Paths.get(AuthRestServiceTest.class.getResource("/AuthLoginUserCreate.json").toURI())));                        
            ObjectMapper responseLoginValidObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseLoginValidObjectMapper.readTree(jsonAuthLoginUserCreate);   
            loginEmail = rootNode.path("email").getTextValue();
            loginPassword = rootNode.path("requestedClearPassword").getTextValue();            
            ResteasyWebTarget targetUserCreate = new ResteasyClientBuilder().build().target(new URI("http", null, "localhost", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetUserCreate.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseUserCreate = targetUserCreate.request().post(Entity.json(jsonAuthLoginUserCreate));
            Assert.assertTrue(responseUserCreate.getStatus() == 200);            
            responseUserCreate.close();
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException | IOException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }            
    }
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }
       
    @Test
    public void testValidLogin() {
        try {                                                                                                               
            ResteasyWebTarget targetLoginValid = client.target(new URI("http", null, "localhost", 8080, "/WBCP-1.0/rs/auth/login", null, null).toASCIIString());
            targetLoginValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            targetLoginParams.add("loginEmail", loginEmail);
            targetLoginParams.add("loginPassword", loginPassword);
            targetLoginParams.add("privateKeyBase64", "SECRET");
            Response responseLoginValid = targetLoginValid.request().post(Entity.form(targetLoginParams));  
            Assert.assertEquals(responseLoginValid.getStatus(), Status.OK.getStatusCode());   
            authorizationToken = responseLoginValid.getHeaderString("AuthorizationToken");
            Assert.assertNotNull(authorizationToken);            
            responseLoginValid.close();
            
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    

    @Test
    public void testWrongLoginParameters() {
        try {                                                                                                              
            ResteasyWebTarget targetLoginValid = client.target(new URI("http", null, "localhost", 8080, "/WBCP-1.0/rs/auth/login", null, null).toASCIIString());
            targetLoginValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            Response responseLoginValid_01 = targetLoginValid.request().post(Entity.form(targetLoginParams));                                          
            Assert.assertEquals(responseLoginValid_01.getStatus(), Status.BAD_REQUEST.getStatusCode());                   
            targetLoginParams.add("loginEmail", loginEmail);
            responseLoginValid_01.close();            
            Response responseLoginValid_02 = targetLoginValid.request().post(Entity.form(targetLoginParams));                                          
            Assert.assertEquals(responseLoginValid_02.getStatus(), Status.BAD_REQUEST.getStatusCode());   
            responseLoginValid_02.close();            
            targetLoginParams.add("loginPassword", loginPassword);
            Response responseLoginValid_03 = targetLoginValid.request().post(Entity.form(targetLoginParams));                                          
            Assert.assertEquals(responseLoginValid_03.getStatus(), Status.BAD_REQUEST.getStatusCode());
            responseLoginValid_03.close();            
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }
    
    @Test
    public void testInvalidPassword() {
        try {                                                                                                              
            ResteasyWebTarget targetLoginInValid = client.target(new URI("http", null, "localhost", 8080, "/WBCP-1.0/rs/auth/login", null, null).toASCIIString());
            targetLoginInValid.request().accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);                        
            MultivaluedHashMap<String, String> targetLoginParams = new MultivaluedHashMap<>();
            targetLoginParams.add("loginEmail", loginEmail);
            targetLoginParams.add("loginPassword", "WRONG PASSWORD");
            targetLoginParams.add("privateKeyBase64", "SECRET");
            Response responseLoginInValid = targetLoginInValid.request().post(Entity.form(targetLoginParams));
            Assert.assertEquals(responseLoginInValid.getStatus(), Status.UNAUTHORIZED.getStatusCode());   
                       
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }    
}
