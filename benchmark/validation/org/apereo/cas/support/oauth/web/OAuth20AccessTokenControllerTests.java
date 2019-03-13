package org.apereo.cas.support.oauth.web;


import HttpMethod.GET;
import OAuth20Constants.ACCESS_TOKEN;
import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.CLIENT_SECRET;
import OAuth20Constants.CODE;
import OAuth20Constants.DEVICE_INTERVAL;
import OAuth20Constants.DEVICE_USER_CODE;
import OAuth20Constants.DEVICE_VERIFICATION_URI;
import OAuth20Constants.ERROR;
import OAuth20Constants.EXPIRES_IN;
import OAuth20Constants.GRANT_TYPE;
import OAuth20Constants.INVALID_REQUEST;
import OAuth20Constants.REDIRECT_URI;
import OAuth20Constants.RESPONSE_TYPE;
import OAuth20Constants.TOKEN_TYPE;
import OAuth20DeviceUserCodeApprovalEndpointController.PARAMETER_USER_CODE;
import OAuth20GrantTypes.AUTHORIZATION_CODE;
import OAuth20GrantTypes.PASSWORD;
import OAuth20GrantTypes.REFRESH_TOKEN;
import OAuth20ResponseTypes.DEVICE_CODE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.support.AlwaysExpiresExpirationPolicy;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;


/**
 * This class tests the {@link OAuth20AccessTokenEndpointController} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */
public class OAuth20AccessTokenControllerTests extends AbstractOAuth20Tests {
    @Test
    @SneakyThrows
    public void verifyClientNoClientId() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    public void verifyClientNoRedirectUri() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    @SneakyThrows
    public void verifyClientNoAuthorizationCode() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    @SneakyThrows
    public void verifyClientBadGrantType() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, "badValue");
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    public void verifyClientDisallowedGrantType() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, OAuth20GrantTypes.PASSWORD.getType());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    @SneakyThrows
    public void verifyClientNoClientSecret() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get("error"));
    }

    @Test
    public void verifyClientNoCode() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        addCode(principal, service);
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    @SneakyThrows
    public void verifyClientNoCasService() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val registeredService = getRegisteredService(AbstractOAuth20Tests.REDIRECT_URI, AbstractOAuth20Tests.CLIENT_SECRET, CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, registeredService);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyClientRedirectUriDoesNotStartWithServiceId() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.OTHER_REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyClientWrongSecret() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.WRONG_CLIENT_SECRET);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    @SneakyThrows
    public void verifyClientEmptySecret() {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, StringUtils.EMPTY);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE), StringUtils.EMPTY);
        val code = addCode(principal, service);
        mockRequest.setParameter(CODE, code.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
    }

    @Test
    public void verifyClientExpiredCode() throws Exception {
        val registeredService = getRegisteredService(AbstractOAuth20Tests.REDIRECT_URI, AbstractOAuth20Tests.CLIENT_SECRET, CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        servicesManager.save(registeredService);
        val map = new HashMap<String, Object>();
        map.put(AbstractOAuth20Tests.NAME, AbstractOAuth20Tests.VALUE);
        val list = Arrays.asList(AbstractOAuth20Tests.VALUE, AbstractOAuth20Tests.VALUE);
        map.put(AbstractOAuth20Tests.NAME2, list);
        val principal = CoreAuthenticationTestUtils.getPrincipal(AbstractOAuth20Tests.ID, map);
        val authentication = AbstractOAuth20Tests.getAuthentication(principal);
        val expiringOAuthCodeFactory = new org.apereo.cas.ticket.code.DefaultOAuthCodeFactory(new AlwaysExpiresExpirationPolicy());
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(registeredService.getServiceId());
        val code = expiringOAuthCodeFactory.create(service, authentication, new MockTicketGrantingTicket("casuser"), new ArrayList(), null, null);
        this.ticketRegistry.addTicket(code);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, AbstractOAuth20Tests.REDIRECT_URI);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(CODE, code.getId());
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        servicesManager.save(getRegisteredService(AbstractOAuth20Tests.REDIRECT_URI, AbstractOAuth20Tests.CLIENT_SECRET, CollectionUtils.wrapSet(AUTHORIZATION_CODE)));
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyClientAuthByParameter() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        assertClientOK(service, false);
    }

    @Test
    public void verifyDeviceFlowGeneratesCode() throws Exception {
        addRegisteredService();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(RESPONSE_TYPE, DEVICE_CODE.getType());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        val model = mv.getModel();
        Assertions.assertTrue(model.containsKey(OAuth20Constants.DEVICE_CODE));
        Assertions.assertTrue(model.containsKey(DEVICE_VERIFICATION_URI));
        Assertions.assertTrue(model.containsKey(DEVICE_USER_CODE));
        Assertions.assertTrue(model.containsKey(DEVICE_INTERVAL));
        Assertions.assertTrue(model.containsKey(EXPIRES_IN));
        val devCode = model.get(OAuth20Constants.DEVICE_CODE).toString();
        val userCode = model.get(DEVICE_USER_CODE).toString();
        val devReq = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.DEVICE_AUTHZ_URL)));
        devReq.setParameter(PARAMETER_USER_CODE, userCode);
        val devResp = new MockHttpServletResponse();
        val mvDev = deviceController.handlePostRequest(devReq, devResp);
        Assertions.assertNotNull(mvDev);
        val status = mvDev.getStatus();
        Assertions.assertNotNull(status);
        Assertions.assertTrue(status.is2xxSuccessful());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(RESPONSE_TYPE, DEVICE_CODE.getType());
        mockRequest.setParameter(CODE, devCode);
        val approveResp = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, approveResp, null);
        val mvApproved = controller.handleRequest(mockRequest, approveResp);
        Assertions.assertTrue(mvApproved.getModel().containsKey(ACCESS_TOKEN));
        Assertions.assertTrue(mvApproved.getModel().containsKey(EXPIRES_IN));
        Assertions.assertTrue(mvApproved.getModel().containsKey(TOKEN_TYPE));
    }

    @Test
    public void verifyClientAuthByHeader() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        assertClientOK(service, false);
    }

    @Test
    public void verifyClientAuthByParameterWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        service.setGenerateRefreshToken(true);
        assertClientOK(service, true);
    }

    @Test
    public void verifyClientAuthByHeaderWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        service.setGenerateRefreshToken(true);
        assertClientOK(service, true);
    }

    @Test
    public void verifyClientAuthJsonByParameter() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        assertClientOK(service, false);
    }

    @Test
    public void verifyClientAuthJsonByHeader() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        assertClientOK(service, false);
    }

    @Test
    public void verifyClientAuthJsonByParameterWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        service.setGenerateRefreshToken(true);
        assertClientOK(service, true);
    }

    @Test
    public void verifyClientAuthJsonByHeaderWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        service.setGenerateRefreshToken(true);
        assertClientOK(service, true);
    }

    @Test
    @SneakyThrows
    public void ensureOnlyRefreshTokenIsAcceptedForRefreshGrant() {
        addRegisteredService(true, CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD, REFRESH_TOKEN));
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        val mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        mockRequest.setParameter(GRANT_TYPE, OAuth20GrantTypes.PASSWORD.name().toLowerCase());
        mockRequest.setParameter(AbstractOAuth20Tests.USERNAME, AbstractOAuth20Tests.GOOD_USERNAME);
        mockRequest.setParameter(AbstractOAuth20Tests.PASSWORD, AbstractOAuth20Tests.GOOD_PASSWORD);
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        var mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        var mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertTrue(mv.getModel().containsKey(OAuth20Constants.REFRESH_TOKEN));
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
        val refreshToken = mv.getModel().get(OAuth20Constants.REFRESH_TOKEN).toString();
        val accessToken = mv.getModel().get(ACCESS_TOKEN).toString();
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, accessToken);
        mockResponse = new MockHttpServletResponse();
        controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, refreshToken);
        mockResponse = new MockHttpServletResponse();
        mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
    }

    @Test
    public void verifyUserNoClientId() throws Exception {
        addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, OAuth20GrantTypes.PASSWORD.name().toLowerCase());
        mockRequest.setParameter(AbstractOAuth20Tests.USERNAME, AbstractOAuth20Tests.GOOD_USERNAME);
        mockRequest.setParameter(AbstractOAuth20Tests.PASSWORD, AbstractOAuth20Tests.GOOD_PASSWORD);
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyUserNoCasService() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(GRANT_TYPE, OAuth20GrantTypes.PASSWORD.name().toLowerCase());
        mockRequest.setParameter(AbstractOAuth20Tests.USERNAME, AbstractOAuth20Tests.GOOD_USERNAME);
        mockRequest.setParameter(AbstractOAuth20Tests.PASSWORD, AbstractOAuth20Tests.GOOD_PASSWORD);
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyUserBadAuthorizationCode() throws Exception {
        addRegisteredService(CollectionUtils.wrapSet(AUTHORIZATION_CODE));
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.name().toLowerCase());
        mockRequest.setParameter(AbstractOAuth20Tests.USERNAME, AbstractOAuth20Tests.GOOD_USERNAME);
        mockRequest.setParameter(AbstractOAuth20Tests.PASSWORD, AbstractOAuth20Tests.GOOD_PASSWORD);
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyUserBadCredentials() throws Exception {
        addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(GRANT_TYPE, OAuth20GrantTypes.PASSWORD.name().toLowerCase());
        mockRequest.setParameter(AbstractOAuth20Tests.USERNAME, AbstractOAuth20Tests.GOOD_USERNAME);
        mockRequest.setParameter(AbstractOAuth20Tests.PASSWORD, "badPassword");
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyUserAuth() {
        addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        assertUserAuth(false);
    }

    @Test
    public void verifyUserAuthWithRefreshToken() {
        val registeredService = addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        registeredService.setGenerateRefreshToken(true);
        assertUserAuth(true);
    }

    @Test
    public void verifyJsonUserAuth() {
        addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        assertUserAuth(false);
    }

    @Test
    public void verifyJsonUserAuthWithRefreshToken() {
        val registeredService = addRegisteredService(CollectionUtils.wrapSet(OAuth20GrantTypes.PASSWORD));
        registeredService.setGenerateRefreshToken(true);
        assertUserAuth(true);
    }

    @Test
    @SneakyThrows
    public void verifyRefreshTokenExpiredToken() {
        val principal = AbstractOAuth20Tests.createPrincipal();
        val registeredService = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        val authentication = AbstractOAuth20Tests.getAuthentication(principal);
        val factory = new WebApplicationServiceFactory();
        val service = factory.createService(registeredService.getServiceId());
        val expiringRefreshTokenFactory = new org.apereo.cas.ticket.refreshtoken.DefaultRefreshTokenFactory(new AlwaysExpiresExpirationPolicy());
        val refreshToken = expiringRefreshTokenFactory.create(service, authentication, new MockTicketGrantingTicket("casuser"), new ArrayList());
        this.ticketRegistry.addTicket(refreshToken);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, refreshToken.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyRefreshTokenBadCredentials() throws Exception {
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        val refreshToken = addRefreshToken(principal, service);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.WRONG_CLIENT_SECRET);
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, refreshToken.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyRefreshTokenEmptySecret() throws Exception {
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN), StringUtils.EMPTY);
        val refreshToken = addRefreshToken(principal, service);
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, StringUtils.EMPTY);
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, refreshToken.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
    }

    @Test
    public void verifyRefreshTokenMissingToken() throws Exception {
        addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, mockResponse.getStatus());
        Assertions.assertEquals(INVALID_REQUEST, mv.getModel().get(ERROR));
    }

    @Test
    public void verifyRefreshTokenOKWithExpiredTicketGrantingTicket() throws Exception {
        val principal = AbstractOAuth20Tests.createPrincipal();
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        val refreshToken = addRefreshToken(principal, service);
        refreshToken.getTicketGrantingTicket().markTicketExpired();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((AbstractOAuth20Tests.CONTEXT) + (OAuth20Constants.ACCESS_TOKEN_URL)));
        mockRequest.setParameter(GRANT_TYPE, REFRESH_TOKEN.name().toLowerCase());
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, AbstractOAuth20Tests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.CLIENT_SECRET, AbstractOAuth20Tests.CLIENT_SECRET);
        mockRequest.setParameter(OAuth20Constants.REFRESH_TOKEN, refreshToken.getId());
        val mockResponse = new MockHttpServletResponse();
        requiresAuthenticationInterceptor.preHandle(mockRequest, mockResponse, null);
        val mv = controller.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());
        val accessTokenId = mv.getModel().get(ACCESS_TOKEN).toString();
        val accessToken = this.ticketRegistry.getTicket(accessTokenId, AccessToken.class);
        Assertions.assertEquals(principal, accessToken.getAuthentication().getPrincipal());
        val timeLeft = Integer.parseInt(mv.getModel().get(EXPIRES_IN).toString());
        Assertions.assertTrue((timeLeft >= (((AbstractOAuth20Tests.TIMEOUT) - 10) - (AbstractOAuth20Tests.DELTA))));
    }

    @Test
    public void verifyRefreshTokenOK() {
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        assertRefreshTokenOk(service);
    }

    @Test
    public void verifyRefreshTokenOKWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        service.setGenerateRefreshToken(true);
        assertRefreshTokenOk(service);
    }

    @Test
    public void verifyJsonRefreshTokenOK() {
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        assertRefreshTokenOk(service);
    }

    @Test
    public void verifyJsonRefreshTokenOKWithRefreshToken() {
        val service = addRegisteredService(CollectionUtils.wrapSet(REFRESH_TOKEN));
        service.setGenerateRefreshToken(true);
        assertRefreshTokenOk(service);
    }
}

