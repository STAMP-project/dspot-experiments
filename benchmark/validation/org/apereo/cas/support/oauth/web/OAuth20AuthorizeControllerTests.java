package org.apereo.cas.support.oauth.web;


import HttpMethod.GET;
import OAuth20Constants.BYPASS_APPROVAL_PROMPT;
import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.CONFIRM_VIEW;
import OAuth20Constants.ERROR_VIEW;
import OAuth20Constants.REDIRECT_URI;
import OAuth20Constants.RESPONSE_TYPE;
import OAuth20Constants.STATE;
import OAuth20ResponseTypes.CODE;
import OAuth20ResponseTypes.TOKEN;
import Pac4jConstants.USER_PROFILES;
import java.util.HashMap;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.web.endpoints.OAuth20AuthorizeEndpointController;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.code.OAuthCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.cas.profile.CasProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.view.RedirectView;


/**
 * This class tests the {@link OAuth20AuthorizeEndpointController} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */
public class OAuth20AuthorizeControllerTests extends AbstractOAuth20Tests {
    private static final String ID = "id";

    private static final String FIRST_NAME_ATTRIBUTE = "firstName";

    private static final String FIRST_NAME = "jerome";

    private static final String LAST_NAME_ATTRIBUTE = "lastName";

    private static final String LAST_NAME = "LELEU";

    private static final String CONTEXT = "/oauth2.0/";

    private static final String CLIENT_ID = "1";

    private static final String REDIRECT_URI = "http://someurl";

    private static final String OTHER_REDIRECT_URI = "http://someotherurl";

    private static final String CAS_SERVER = "casserver";

    private static final String CAS_SCHEME = "https";

    private static final int CAS_PORT = 443;

    private static final String AUTHORIZE_URL = ((((OAuth20AuthorizeControllerTests.CAS_SCHEME) + "://") + (OAuth20AuthorizeControllerTests.CAS_SERVER)) + (OAuth20AuthorizeControllerTests.CONTEXT)) + "authorize";

    private static final String SERVICE_NAME = "serviceName";

    private static final String STATE = "state";

    @Autowired
    @Qualifier("authorizeController")
    private OAuth20AuthorizeEndpointController oAuth20AuthorizeEndpointController;

    @Test
    public void verifyNoClientId() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        val mockResponse = new MockHttpServletResponse();
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyNoRedirectUri() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        val mockResponse = new MockHttpServletResponse();
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyNoResponseType() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        val mockResponse = new MockHttpServletResponse();
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyBadResponseType() throws Exception {
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, "badvalue");
        val mockResponse = new MockHttpServletResponse();
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyNoCasService() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        val mockResponse = new MockHttpServletResponse();
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyRedirectUriDoesNotStartWithServiceId() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        val mockResponse = new MockHttpServletResponse();
        this.servicesManager.save(OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.OTHER_REDIRECT_URI, OAuth20AuthorizeControllerTests.CLIENT_ID));
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyCodeNoProfile() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, CODE.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(true);
        this.servicesManager.save(service);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(ERROR_VIEW, modelAndView.getViewName());
    }

    @Test
    public void verifyCodeRedirectToClient() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, CODE.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(true);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        session.putValue(USER_PROFILES, profile);
        mockRequest.setSession(session);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "?code=OC-")));
        val code = StringUtils.substringAfter(redirectUrl, "?code=");
        val oAuthCode = ((OAuthCode) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(oAuthCode);
        val principal = oAuthCode.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyTokenRedirectToClient() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, TOKEN.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(true);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(USER_PROFILES, profile);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "#access_token=")));
        val code = StringUtils.substringBetween(redirectUrl, "#access_token=", "&token_type=bearer");
        val accessToken = ((AccessToken) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(accessToken);
        val principal = accessToken.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyCodeRedirectToClientWithState() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, CODE.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        mockRequest.setParameter(OAuth20Constants.STATE, OAuth20AuthorizeControllerTests.STATE);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(true);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(USER_PROFILES, profile);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "?code=OC-")));
        val code = StringUtils.substringBefore(StringUtils.substringAfter(redirectUrl, "?code="), "&state=");
        val oAuthCode = ((OAuthCode) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(oAuthCode);
        val principal = oAuthCode.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyTokenRedirectToClientWithState() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, TOKEN.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        mockRequest.setParameter(OAuth20Constants.STATE, OAuth20AuthorizeControllerTests.STATE);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(true);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(USER_PROFILES, profile);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "#access_token=")));
        Assertions.assertTrue(redirectUrl.contains(((('&' + (OAuth20Constants.STATE)) + '=') + (OAuth20AuthorizeControllerTests.STATE))));
        val code = StringUtils.substringBetween(redirectUrl, "#access_token=", "&token_type=bearer");
        val accessToken = ((AccessToken) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(accessToken);
        val principal = accessToken.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyCodeRedirectToClientApproved() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, CODE.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(false);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(BYPASS_APPROVAL_PROMPT, "true");
        session.putValue(USER_PROFILES, profile);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "?code=OC-")));
        val code = StringUtils.substringAfter(redirectUrl, "?code=");
        val oAuthCode = ((OAuthCode) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(oAuthCode);
        val principal = oAuthCode.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyTokenRedirectToClientApproved() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, TOKEN.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(false);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(USER_PROFILES, profile);
        session.putValue(BYPASS_APPROVAL_PROMPT, "true");
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        val view = modelAndView.getView();
        Assertions.assertTrue((view instanceof RedirectView));
        val redirectView = ((RedirectView) (view));
        val redirectUrl = redirectView.getUrl();
        Assertions.assertTrue(redirectUrl.startsWith(((OAuth20AuthorizeControllerTests.REDIRECT_URI) + "#access_token=")));
        val code = StringUtils.substringBetween(redirectUrl, "#access_token=", "&token_type=bearer");
        val accessToken = ((AccessToken) (this.ticketRegistry.getTicket(code)));
        Assertions.assertNotNull(accessToken);
        val principal = accessToken.getAuthentication().getPrincipal();
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.ID, principal.getId());
        val principalAttributes = principal.getAttributes();
        Assertions.assertEquals(attributes.size(), principalAttributes.size());
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.FIRST_NAME, principalAttributes.get(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE));
    }

    @Test
    public void verifyRedirectToApproval() throws Exception {
        clearAllServices();
        val mockRequest = new org.springframework.mock.web.MockHttpServletRequest(GET.name(), ((OAuth20AuthorizeControllerTests.CONTEXT) + (OAuth20Constants.AUTHORIZE_URL)));
        mockRequest.setParameter(OAuth20Constants.CLIENT_ID, OAuth20AuthorizeControllerTests.CLIENT_ID);
        mockRequest.setParameter(OAuth20Constants.REDIRECT_URI, OAuth20AuthorizeControllerTests.REDIRECT_URI);
        mockRequest.setParameter(RESPONSE_TYPE, CODE.name().toLowerCase());
        mockRequest.setServerName(OAuth20AuthorizeControllerTests.CAS_SERVER);
        mockRequest.setServerPort(OAuth20AuthorizeControllerTests.CAS_PORT);
        mockRequest.setScheme(OAuth20AuthorizeControllerTests.CAS_SCHEME);
        val mockResponse = new MockHttpServletResponse();
        val service = OAuth20AuthorizeControllerTests.getRegisteredService(OAuth20AuthorizeControllerTests.REDIRECT_URI, OAuth20AuthorizeControllerTests.SERVICE_NAME);
        service.setBypassApprovalPrompt(false);
        this.servicesManager.save(service);
        val profile = new CasProfile();
        profile.setId(OAuth20AuthorizeControllerTests.ID);
        val attributes = new HashMap<String, Object>();
        attributes.put(OAuth20AuthorizeControllerTests.FIRST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.FIRST_NAME);
        attributes.put(OAuth20AuthorizeControllerTests.LAST_NAME_ATTRIBUTE, OAuth20AuthorizeControllerTests.LAST_NAME);
        profile.addAttributes(attributes);
        val session = new MockHttpSession();
        mockRequest.setSession(session);
        session.putValue(USER_PROFILES, profile);
        val modelAndView = oAuth20AuthorizeEndpointController.handleRequest(mockRequest, mockResponse);
        Assertions.assertEquals(CONFIRM_VIEW, modelAndView.getViewName());
        val model = modelAndView.getModel();
        Assertions.assertEquals(((((OAuth20AuthorizeControllerTests.AUTHORIZE_URL) + '?') + (OAuth20Constants.BYPASS_APPROVAL_PROMPT)) + "=true"), model.get("callbackUrl"));
        Assertions.assertEquals(OAuth20AuthorizeControllerTests.SERVICE_NAME, model.get("serviceName"));
    }
}

