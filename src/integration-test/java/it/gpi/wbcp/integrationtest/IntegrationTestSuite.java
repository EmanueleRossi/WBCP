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
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    AuthRestServiceITest.class,
    OrganizationRestServiceITest.class,
    MessageRestServiceITest.class
} )
public class IntegrationTestSuite {

    public static String ITEST_SERVER_URL = "127.0.0.1";
    public static Integer ITEST_SERVER_PORT = 8080;
    
    public static String LOGIN_EMAIL = null;
    public static String LOGIN_PASSWORD = null;
    public static String TOKEN = null;
    
    @ClassRule
    public static ExternalResource testRule = new ExternalResource(){
        @Override
        protected void before() throws Throwable {
            try {          
                String jsonAuthLoginUserCreate = new String(Files.readAllBytes(Paths.get(AuthRestServiceITest.class.getResource("/AuthLoginUserCreate.json").toURI())));                        
                ObjectMapper responseLoginValidObjectMapper = new ObjectMapper();
                JsonNode rootNode = responseLoginValidObjectMapper.readTree(jsonAuthLoginUserCreate);   
                IntegrationTestSuite.LOGIN_EMAIL = rootNode.path("email").getTextValue();
                IntegrationTestSuite.LOGIN_PASSWORD = rootNode.path("requestedClearPassword").getTextValue();            
                ResteasyWebTarget targetUserCreate = new ResteasyClientBuilder().build().target(new URI("http", null, IntegrationTestSuite.ITEST_SERVER_URL, IntegrationTestSuite.ITEST_SERVER_PORT, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
                targetUserCreate.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
                Response responseUserCreate = targetUserCreate.request().post(Entity.json(jsonAuthLoginUserCreate));
                System.out.printf("createTestUser(): |%s|", responseUserCreate.readEntity(String.class));
                Assert.assertEquals(Response.Status.OK.getStatusCode(), responseUserCreate.getStatus());            
                responseUserCreate.close();
                
            } catch (IllegalArgumentException | NullPointerException | URISyntaxException | IOException e) {
                fail("Exception! " + StringUtil.stringifyStackTrace(e));
            }                                     
        };

        @Override
        protected void after(){
        };
    };
    
    @Test
    public void testSuiteStart() {
        System.out.printf("### INTEGRATION TEST SUITE STARTED! ###");
    } 
}
