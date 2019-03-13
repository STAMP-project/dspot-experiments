package org.apereo.cas.support.oauth.web;


import HttpMethod.GET;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON_VALUE;
import OAuth20Constants.ACCESS_TOKEN;
import OAuth20Constants.EXPIRED_ACCESS_TOKEN;
import OAuth20Constants.MISSING_ACCESS_TOKEN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.web.endpoints.OAuth20UserProfileEndpointController;
import org.apereo.cas.support.oauth.web.response.accesstoken.response.OAuth20JwtAccessTokenCipherExecutor;
import org.apereo.cas.support.oauth.web.response.accesstoken.response.RegisteredServiceJWTAccessTokenCipherExecutor;
import org.apereo.cas.ticket.accesstoken.AccessTokenFactory;
import org.apereo.cas.ticket.support.AlwaysExpiresExpirationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This class tests the {@link OAuth20UserProfileEndpointController} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */
public class OAuth20ProfileControllerTests extends AbstractOAuth20Tests {
    @Autowired
    @Qualifier("defaultAccessTokenFactory")
    private AccessTokenFactory accessTokenFactory;

    @Autowired
    @Qualifier("profileController")
    private OAuth20UserProfileEndpointController oAuth20ProfileController;

    @Test
    public void verifyNoGivenAccessToken() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(UNAUTHORIZED, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        Assertions.assertNotNull(entity.getBody());
        Assertions.assertTrue(entity.getBody().contains(MISSING_ACCESS_TOKEN));
    }

    @Test
    public void verifyNoExistingAccessToken() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        mockRequest.setParameter(ACCESS_TOKEN, "DOES NOT EXIST");
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(UNAUTHORIZED, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        Assertions.assertNotNull(entity.getBody());
        Assertions.assertTrue(entity.getBody().contains(EXPIRED_ACCESS_TOKEN));
    }

    @Test
    public void verifyExpiredAccessToken() throws Exception {
        val principal = CoreAuthenticationTestUtils.getPrincipal(AbstractOAuth20Tests.ID, new HashMap());
        val authentication = OAuth20ProfileControllerTests.getAuthentication(principal);
        val jwtBuilder = new org.apereo.cas.token.JWTBuilder("cas.example.org", new OAuth20JwtAccessTokenCipherExecutor(), servicesManager, new RegisteredServiceJWTAccessTokenCipherExecutor());
        val expiringAccessTokenFactory = new org.apereo.cas.ticket.accesstoken.DefaultAccessTokenFactory(new AlwaysExpiresExpirationPolicy(), jwtBuilder);
        val accessToken = expiringAccessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication, new MockTicketGrantingTicket("casuser"), new ArrayList(), null);
        this.ticketRegistry.addTicket(accessToken);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        mockRequest.setParameter(ACCESS_TOKEN, accessToken.getId());
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(UNAUTHORIZED, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        Assertions.assertNotNull(entity.getBody());
        Assertions.assertTrue(entity.getBody().contains(EXPIRED_ACCESS_TOKEN));
    }

    @Test
    public void verifyOK() throws Exception {
        val map = new HashMap<String, Object>();
        map.put(AbstractOAuth20Tests.NAME, AbstractOAuth20Tests.VALUE);
        val list = Arrays.asList(AbstractOAuth20Tests.VALUE, AbstractOAuth20Tests.VALUE);
        map.put(AbstractOAuth20Tests.NAME2, list);
        val principal = CoreAuthenticationTestUtils.getPrincipal(AbstractOAuth20Tests.ID, map);
        val authentication = OAuth20ProfileControllerTests.getAuthentication(principal);
        val accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication, new MockTicketGrantingTicket("casuser"), new ArrayList(), null);
        this.ticketRegistry.addTicket(accessToken);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        mockRequest.setParameter(ACCESS_TOKEN, accessToken.getId());
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(OK, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        val expected = ((((((((((("{\"id\":\"" + (AbstractOAuth20Tests.ID)) + "\",\"attributes\":[{\"") + (AbstractOAuth20Tests.NAME)) + "\":\"") + (AbstractOAuth20Tests.VALUE)) + "\"},{\"") + (AbstractOAuth20Tests.NAME2)) + "\":[\"") + (AbstractOAuth20Tests.VALUE)) + "\",\"") + (AbstractOAuth20Tests.VALUE)) + "\"]}]}";
        val expectedObj = AbstractOAuth20Tests.MAPPER.readTree(expected);
        val receivedObj = AbstractOAuth20Tests.MAPPER.readTree(entity.getBody());
        Assertions.assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());
        val expectedAttributes = expectedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        val receivedAttributes = receivedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        Assertions.assertEquals(expectedAttributes.findValue(AbstractOAuth20Tests.NAME).asText(), receivedAttributes.findValue(AbstractOAuth20Tests.NAME).asText());
        Assertions.assertEquals(expectedAttributes.findValues(AbstractOAuth20Tests.NAME2), receivedAttributes.findValues(AbstractOAuth20Tests.NAME2));
    }

    @Test
    public void verifyOKWithExpiredTicketGrantingTicket() throws Exception {
        val map = new HashMap<String, Object>();
        map.put(AbstractOAuth20Tests.NAME, AbstractOAuth20Tests.VALUE);
        val list = Arrays.asList(AbstractOAuth20Tests.VALUE, AbstractOAuth20Tests.VALUE);
        map.put(AbstractOAuth20Tests.NAME2, list);
        val principal = CoreAuthenticationTestUtils.getPrincipal(AbstractOAuth20Tests.ID, map);
        val authentication = OAuth20ProfileControllerTests.getAuthentication(principal);
        val accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication, new MockTicketGrantingTicket("casuser"), new ArrayList(), null);
        accessToken.getTicketGrantingTicket().markTicketExpired();
        this.ticketRegistry.addTicket(accessToken);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        mockRequest.setParameter(ACCESS_TOKEN, accessToken.getId());
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(OK, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        val expectedObj = AbstractOAuth20Tests.MAPPER.createObjectNode();
        val attrNode = AbstractOAuth20Tests.MAPPER.createObjectNode();
        attrNode.put(AbstractOAuth20Tests.NAME, AbstractOAuth20Tests.VALUE);
        val values = AbstractOAuth20Tests.MAPPER.createArrayNode();
        values.add(AbstractOAuth20Tests.VALUE);
        values.add(AbstractOAuth20Tests.VALUE);
        attrNode.put(AbstractOAuth20Tests.NAME2, values);
        expectedObj.put("id", AbstractOAuth20Tests.ID);
        expectedObj.put("attributes", attrNode);
        val receivedObj = AbstractOAuth20Tests.MAPPER.readTree(entity.getBody());
        Assertions.assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());
        val expectedAttributes = expectedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        val receivedAttributes = receivedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        Assertions.assertEquals(expectedAttributes.findValue(AbstractOAuth20Tests.NAME).asText(), receivedAttributes.findValue(AbstractOAuth20Tests.NAME).asText());
        Assertions.assertEquals(expectedAttributes.findValues(AbstractOAuth20Tests.NAME2), receivedAttributes.findValues(AbstractOAuth20Tests.NAME2));
    }

    @Test
    public void verifyOKWithAuthorizationHeader() throws Exception {
        val map = new HashMap<String, Object>();
        map.put(AbstractOAuth20Tests.NAME, AbstractOAuth20Tests.VALUE);
        val list = Arrays.asList(AbstractOAuth20Tests.VALUE, AbstractOAuth20Tests.VALUE);
        map.put(AbstractOAuth20Tests.NAME2, list);
        val principal = CoreAuthenticationTestUtils.getPrincipal(AbstractOAuth20Tests.ID, map);
        val authentication = OAuth20ProfileControllerTests.getAuthentication(principal);
        val accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication, new MockTicketGrantingTicket("casuser"), new ArrayList(), null);
        this.ticketRegistry.addTicket(accessToken);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.PROFILE_URL)));
        mockRequest.addHeader("Authorization", (((OAuth20Constants.TOKEN_TYPE_BEARER) + ' ') + (accessToken.getId())));
        val mockResponse = new MockHttpServletResponse();
        val entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(OK, entity.getStatusCode());
        Assertions.assertEquals(APPLICATION_JSON_VALUE, mockResponse.getContentType());
        val expected = ((((((((((("{\"id\":\"" + (AbstractOAuth20Tests.ID)) + "\",\"attributes\":[{\"") + (AbstractOAuth20Tests.NAME)) + "\":\"") + (AbstractOAuth20Tests.VALUE)) + "\"},{\"") + (AbstractOAuth20Tests.NAME2)) + "\":[\"") + (AbstractOAuth20Tests.VALUE)) + "\",\"") + (AbstractOAuth20Tests.VALUE)) + "\"]}]}";
        val expectedObj = AbstractOAuth20Tests.MAPPER.readTree(expected);
        val receivedObj = AbstractOAuth20Tests.MAPPER.readTree(entity.getBody());
        Assertions.assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());
        val expectedAttributes = expectedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        val receivedAttributes = receivedObj.get(AbstractOAuth20Tests.ATTRIBUTES_PARAM);
        Assertions.assertEquals(expectedAttributes.findValue(AbstractOAuth20Tests.NAME).asText(), receivedAttributes.findValue(AbstractOAuth20Tests.NAME).asText());
        Assertions.assertEquals(expectedAttributes.findValues(AbstractOAuth20Tests.NAME2), receivedAttributes.findValues(AbstractOAuth20Tests.NAME2));
    }
}

