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
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
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
public class OrganizationRestServiceTest {
    
    private static final Logger logger = LogManager.getLogger();
    
    private ResteasyClient client;
    
    @Before 
    public void initialize() {
       ResteasyClientBuilder rcb = new ResteasyClientBuilder();
       client = rcb.build();
    }           

    @Test
    public void testOrganizationCreateNoName() {
        try {
            String jsonOrganization = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateNoName.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonOrganization));            
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
    public void testOrganizationCreate() {
        try {
            String jsonOrganization = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreate.json").toURI())));            
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().post(Entity.json(jsonOrganization));            
            String responseString = response.readEntity(String.class);                    
            assertTrue(response.getStatus() == 200);                                   
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);                        
            Assert.assertNotNull(rootNode.path("id"));            
            Assert.assertNotNull(rootNode.path("name"));
            Assert.assertNotNull(rootNode.path("taxCode"));             
            Assert.assertNotNull(rootNode.path("mailDomain"));                 
            response.close(); 
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    } 
    
    @Test
    public void testOrganizationCreateWithUsers() {
        try {
            String jsonAuthLoginUserCreateFirst = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateUserCreateFirst.json").toURI())));                        
            ResteasyWebTarget targetUserCreateFirst = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetUserCreateFirst.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseUserCreateFirst = targetUserCreateFirst.request().post(Entity.json(jsonAuthLoginUserCreateFirst));                                                    
            String responseUserCreateFirstString = responseUserCreateFirst.readEntity(String.class);                              
            assertTrue(responseUserCreateFirst.getStatus() == 200);                       
            ObjectMapper responseObjectMapperUserCreateFirst = new ObjectMapper();
            JsonNode rootNodeUserCreateFirst = responseObjectMapperUserCreateFirst.readTree(responseUserCreateFirstString);                                              
            responseUserCreateFirst.close();
            
            String jsonAuthLoginUserCreateSecond = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateUserCreateSecond.json").toURI())));                        
            ResteasyWebTarget targetUserCreateSecond = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/user/create", null, null).toASCIIString());
            targetUserCreateSecond.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseUserCreateSecond = targetUserCreateSecond.request().post(Entity.json(jsonAuthLoginUserCreateSecond));                                                    
            String responseUserCreateSecondString = responseUserCreateSecond.readEntity(String.class);                              
            assertTrue(responseUserCreateSecond.getStatus() == 200);                       
            ObjectMapper responseObjectMapperUserCreateSecond = new ObjectMapper();
            JsonNode rootNodeUserCreateSecond = responseObjectMapperUserCreateSecond.readTree(responseUserCreateSecondString);
            responseUserCreateSecond.close(); 
                        
            String jsonOrganization = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/OrganizationCreateWithUsers.json").toURI())));            
            ObjectMapper jsonOrganizationObjectMapper = new ObjectMapper();
            JsonNode jsonOrganizationJsonNode = jsonOrganizationObjectMapper.readTree(jsonOrganization);            
            ObjectNode jsonOrganizationObjectNode = (ObjectNode)jsonOrganizationJsonNode;                                    
            ArrayNode jsonUsers = jsonOrganizationObjectNode.putArray("users");
            jsonUsers.add(rootNodeUserCreateFirst);
            jsonUsers.add(rootNodeUserCreateSecond);
            
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/create", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);        
            Response response = target.request().post(Entity.json(jsonOrganizationObjectNode));            
            String responseString = response.readEntity(String.class);   
            
            assertTrue(response.getStatus() == 200);                                   
            ObjectMapper responseObjectMapper = new ObjectMapper();
            JsonNode rootNode = responseObjectMapper.readTree(responseString);              
            
            Assert.assertNotNull(rootNode.path("id"));            
            Assert.assertNotNull(rootNode.path("name"));
            Assert.assertNotNull(rootNode.path("taxCode"));             
            Assert.assertNotNull(rootNode.path("mailDomain"));
            response.close(); 
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }     
    
    @Test
    public void testOrganizationFullTextSearch() {
        try {
            ResteasyWebTarget targetName = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/fullTextSearch/GPI S.", null, null).toASCIIString());
            targetName.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseName = targetName.request().get();
            String responseNameString = responseName.readEntity(String.class); 
            assertTrue(responseName.getStatus() == 200);                    
            ObjectMapper responseEmailObjectMapper = new ObjectMapper();
            JsonNode rootNodeName = responseEmailObjectMapper.readTree(responseNameString); 
            Assert.assertNotNull(rootNodeName.get(0).path("id"));                                              
            Assert.assertNotNull(rootNodeName.get(0).path("name"));
            Assert.assertEquals("GPI S.p.A.", rootNodeName.get(0).path("name").getTextValue());
            responseName.close();
            
            ResteasyWebTarget targetTaxCode = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/fullTextSearch/4260", null, null).toASCIIString());
            targetTaxCode.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseTaxCode = targetTaxCode.request().get();
            String responseTaxCodeString = responseTaxCode.readEntity(String.class); 
            assertTrue(responseTaxCode.getStatus() == 200);                    
            ObjectMapper responseTaxCodeObjectMapper = new ObjectMapper();
            JsonNode rootNodeTaxCode = responseTaxCodeObjectMapper.readTree(responseTaxCodeString);                        
            Assert.assertNotNull(rootNodeTaxCode.get(0).path("id"));                                              
            Assert.assertNotNull(rootNodeTaxCode.get(0).path("vatCode"));
            Assert.assertEquals("01944260221", rootNodeTaxCode.get(0).path("taxCode").getTextValue());
            responseName.close();
            
            ResteasyWebTarget targetMailDomain = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/fullTextSearch/gpi.i", null, null).toASCIIString());
            targetMailDomain.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response responseMailDomain = targetMailDomain.request().get();
            String responseMailDomainString = responseMailDomain.readEntity(String.class); 
            assertTrue(responseMailDomain.getStatus() == 200);                    
            ObjectMapper responseMailDomainObjectMapper = new ObjectMapper();
            JsonNode rootNodeMailDomain = responseMailDomainObjectMapper.readTree(responseMailDomainString);                        
            Assert.assertNotNull(rootNodeMailDomain.get(0).path("id"));                                              
            Assert.assertNotNull(rootNodeMailDomain.get(0).path("vatCode"));
            Assert.assertEquals("gpi.it", rootNodeMailDomain.get(0).path("mailDomain").getTextValue());
            responseName.close();            
            
        } catch (IllegalArgumentException | NullPointerException | IOException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }         
    
    @Test
    public void testListEmptyResult() {
        try {
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/list", null, null).toASCIIString());
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
    public void testOrganizationListWithResult() {
        try {
            ResteasyWebTarget target = client.target(new URI("http", null, "devsrv03.erossi.org", 8080, "/WBCP-1.0/rs/organization/list", null, null).toASCIIString());
            target.request().accept(MediaType.APPLICATION_JSON_TYPE);                                        
            Response response = target.request().get();         
            assertTrue(response.getStatus() == 200);                                   
            
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException e) {
            fail("Exception! " + StringUtil.stringifyStackTrace(e));
        }
    }        
}
