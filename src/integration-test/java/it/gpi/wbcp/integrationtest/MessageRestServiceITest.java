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
import java.net.URI;
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
public class MessageRestServiceITest {
    
    private ResteasyClient client;
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }    
    
    @Test
    public void testMessageCreate() {
        try {
            String jsonSender = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/MessageCreateSender.json").toURI())));            
            ResteasyWebTarget targetSender = client.target(new URI("http", null, "127.0.0.1", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetSender.request().accept(MediaType.APPLICATION_JSON_TYPE);                                                    
            Response responseSender = targetSender.request().header("Authorization", IntegrationTestSuite.TOKEN).post(Entity.json(jsonSender));
            String responseSenderString = responseSender.readEntity(String.class);
            System.out.printf("testMessageCreate(): |%s|", responseSenderString);               
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseSender.getStatus());  
            responseSender.close();
            
            String jsonRecipient = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/MessageCreateRecipient.json").toURI())));
            ResteasyWebTarget targetRecipient = client.target(new URI("http", null, "127.0.0.1", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetRecipient.request().accept(MediaType.APPLICATION_JSON_TYPE);      
            Response responseRecipient = targetRecipient.request().header("Authorization", IntegrationTestSuite.TOKEN).post(Entity.json(jsonRecipient));                    
            String responseRecipientString = responseSender.readEntity(String.class);
            System.out.printf("testMessageCreate(): |%s|", responseRecipientString);              
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseRecipient.getStatus());    
            responseRecipient.close();
            
            String jsonMessage = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/MessageCreate.json").toURI())));
            ResteasyWebTarget targetMessage = client.target(new URI("http", null, "127.0.0.1", 8080, "/WBCP-1.0/rs/message/create", null, null).toASCIIString());
            Response responseMessage = targetMessage.request().header("Authorization", IntegrationTestSuite.TOKEN).post(Entity.json(jsonMessage));            
            String responseMessageString = responseMessage.readEntity(String.class);
            System.out.printf("testMessageCreate(): |%s|", responseMessageString);               
            Assert.assertEquals(Response.Status.OK.getStatusCode(), responseMessage.getStatus());                   
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseMessageString);
            Assert.assertNotNull(rootNode.path("id"));              
            responseMessage.close();
                        
        } catch (Exception e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }
}
