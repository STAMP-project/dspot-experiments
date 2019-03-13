/**
 * Copyright 2006-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.provider.endpoint;


import OAuth2Utils.REDIRECT_URI;
import OAuth2Utils.USER_OAUTH_APPROVAL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;


/**
 *
 *
 * @author Dave Syer
 */
public class AuthorizationEndpointTests {
    private AuthorizationEndpoint endpoint = new AuthorizationEndpoint();

    private HashMap<String, Object> model = new HashMap<String, Object>();

    private SimpleSessionStatus sessionStatus = new SimpleSessionStatus();

    private UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("foo", "bar", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

    private BaseClientDetails client;

    @Test(expected = IllegalStateException.class)
    public void testMandatoryProperties() throws Exception {
        endpoint = new AuthorizationEndpoint();
        endpoint.afterPropertiesSet();
    }

    @Test
    public void testStartAuthorizationCodeFlow() throws Exception {
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", null, null, "read", Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        Assert.assertEquals("forward:/oauth/confirm_access", result.getViewName());
    }

    @Test
    public void testApprovalStoreAddsScopes() throws Exception {
        ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
        userApprovalHandler.setApprovalStore(new InMemoryApprovalStore());
        endpoint.setUserApprovalHandler(userApprovalHandler);
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", null, null, "read", Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        Assert.assertEquals("forward:/oauth/confirm_access", result.getViewName());
        Assert.assertTrue(result.getModel().containsKey("scopes"));
    }

    @Test(expected = OAuth2Exception.class)
    public void testStartAuthorizationCodeFlowForClientCredentialsFails() throws Exception {
        client.setAuthorizedGrantTypes(Collections.singleton("client_credentials"));
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", null, null, null, Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        Assert.assertEquals("forward:/oauth/confirm_access", result.getViewName());
    }

    @Test
    public void testAuthorizationCodeWithFragment() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com#bar", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere.com?code=thecode#bar", getUrl());
    }

    @Test
    public void testAuthorizationCodeWithQueryParams() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com?foo=bar", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere.com?foo=bar&code=thecode", getUrl());
    }

    @Test
    public void testAuthorizationCodeWithTrickyState() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com", " =?s", null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere.com?code=thecode&state=%20%3D?s", getUrl());
    }

    @Test
    public void testAuthorizationCodeWithMultipleQueryParams() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com?foo=bar&bar=foo", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere.com?foo=bar&bar=foo&code=thecode", getUrl());
    }

    @Test
    public void testAuthorizationCodeWithTrickyQueryParams() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com?foo=b =&bar=f $", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        String url = ((RedirectView) (result)).getUrl();
        Assert.assertEquals("http://anywhere.com?foo=b%20=&bar=f%20$&code=thecode", url);
        MultiValueMap<String, String> params = UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
        Assert.assertEquals("[b%20=]", params.get("foo").toString());
        Assert.assertEquals("[f%20$]", params.get("bar").toString());
    }

    @Test
    public void testAuthorizationCodeWithTrickyEncodedQueryParams() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com/path?foo=b%20%3D&bar=f%20$", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere.com/path?foo=b%20%3D&bar=f%20$&code=thecode", getUrl());
    }

    @Test
    public void testAuthorizationCodeWithMoreTrickyEncodedQueryParams() throws Exception {
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices());
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere?t=a%3Db%26ep%3Dtest%2540test.me", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(Collections.singletonMap(USER_OAUTH_APPROVAL, "true"), model, sessionStatus, principal);
        Assert.assertEquals("http://anywhere?t=a%3Db%26ep%3Dtest%2540test.me&code=thecode", getUrl());
    }

    @Test
    public void testAuthorizationCodeError() throws Exception {
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        endpoint.setAuthorizationCodeServices(new AuthorizationEndpointTests.StubAuthorizationCodeServices() {
            @Override
            public String createAuthorizationCode(OAuth2Authentication authentication) {
                throw new InvalidScopeException("FOO");
            }
        });
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong view: " + result), url.startsWith("http://anywhere.com"));
        Assert.assertTrue(("No error: " + result), url.contains("?error="));
        Assert.assertTrue(("Wrong state: " + result), url.contains("&state=mystate"));
    }

    @Test
    public void testAuthorizationCodeWithMultipleResponseTypes() throws Exception {
        Set<String> responseTypes = new HashSet<String>();
        responseTypes.add("code");
        responseTypes.add("other");
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", null, null, "read", responseTypes).getRequestParameters(), sessionStatus, principal);
        Assert.assertEquals("forward:/oauth/confirm_access", result.getViewName());
    }

    @Test
    public void testImplicitPreApproved() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
                token.setAdditionalInformation(Collections.singletonMap("foo", ((Object) ("bar"))));
                return token;
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong view: " + result), url.startsWith("http://anywhere.com"));
        Assert.assertTrue(("Wrong state: " + result), url.contains("&state=mystate"));
        Assert.assertTrue(("Wrong token: " + result), url.contains("access_token="));
        Assert.assertTrue(("Wrong token: " + result), url.contains("foo=bar"));
    }

    @Test
    public void testImplicitAppendsScope() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
                token.setScope(Collections.singleton("read"));
                return token;
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong scope: " + result), url.contains("&scope=read"));
    }

    @Test
    public void testImplicitWithQueryParam() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
                return token;
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com?foo=bar", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong url: " + result), url.contains("foo=bar"));
    }

    @Test
    public void testImplicitWithAdditionalInfo() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
                token.setAdditionalInformation(Collections.<String, Object>singletonMap("foo", "bar"));
                return token;
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong url: " + result), url.contains("foo=bar"));
    }

    @Test
    public void testImplicitAppendsScopeWhenDefaulting() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
                token.setScope(new LinkedHashSet<String>(Arrays.asList("read", "write")));
                return token;
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }

            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }
        });
        client.setScope(Collections.singleton("read"));
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", null, Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong scope: " + result), url.contains("&scope=read%20write"));
    }

    @Test(expected = InvalidScopeException.class)
    public void testImplicitPreApprovedButInvalid() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                throw new IllegalStateException("Shouldn't be called");
            }
        });
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }

            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }
        });
        client.setScope(Collections.singleton("smallscope"));
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "bigscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong view: " + result), url.startsWith("http://anywhere.com"));
    }

    @Test
    public void testImplicitUnapproved() throws Exception {
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                return null;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        Assert.assertEquals("forward:/oauth/confirm_access", result.getViewName());
    }

    @Test
    public void testImplicitError() throws Exception {
        endpoint.setUserApprovalHandler(new DefaultUserApprovalHandler() {
            public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return authorizationRequest;
            }

            public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
                return true;
            }
        });
        endpoint.setTokenGranter(new TokenGranter() {
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                return null;
            }
        });
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "mystate", "myscope", Collections.singleton("token"));
        ModelAndView result = endpoint.authorize(model, authorizationRequest.getRequestParameters(), sessionStatus, principal);
        String url = ((RedirectView) (result.getView())).getUrl();
        Assert.assertTrue(("Wrong view: " + result), url.startsWith("http://anywhere.com"));
        Assert.assertTrue(("No error: " + result), url.contains("#error="));
        Assert.assertTrue(("Wrong state: " + result), url.contains("&state=mystate"));
    }

    @Test
    public void testApproveOrDeny() throws Exception {
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com", null, null, Collections.singleton("code"));
        request.setApproved(true);
        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        View result = endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
        Assert.assertTrue(("Wrong view: " + result), getUrl().startsWith("http://anywhere.com"));
    }

    @Test
    public void testApprovalDenied() throws Exception {
        AuthorizationRequest request = getAuthorizationRequest("foo", "http://anywhere.com", null, null, Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "false");
        View result = endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
        String url = ((RedirectView) (result)).getUrl();
        Assert.assertTrue(("Wrong view: " + result), url.startsWith("http://anywhere.com"));
        Assert.assertTrue(("Wrong view: " + result), url.contains("error=access_denied"));
    }

    @Test
    public void testDirectApproval() throws Exception {
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", "http://anywhere.com", null, "read", Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        // Should go to approval page (SECOAUTH-191)
        Assert.assertFalse(((result.getView()) instanceof RedirectView));
    }

    @Test
    public void testRedirectUriOptionalForAuthorization() throws Exception {
        ModelAndView result = endpoint.authorize(model, getAuthorizationRequest("foo", null, null, "read", Collections.singleton("code")).getRequestParameters(), sessionStatus, principal);
        // RedirectUri parameter should be null (SECOAUTH-333), however the resolvedRedirectUri not
        AuthorizationRequest authorizationRequest = ((AuthorizationRequest) (result.getModelMap().get(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME)));
        Assert.assertNull(authorizationRequest.getRequestParameters().get(REDIRECT_URI));
        Assert.assertEquals("http://anywhere.com", authorizationRequest.getRedirectUri());
    }

    /**
     * Ensure that if the approval endpoint is called without a resolved redirect URI, the request fails.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = InvalidRequestException.class)
    public void testApproveOrDenyWithOAuth2RequestWithoutRedirectUri() throws Exception {
        AuthorizationRequest request = getAuthorizationRequest("foo", null, null, null, Collections.singleton("code"));
        request.setApproved(true);
        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, request);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(request));
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedClientId() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setClientId("bar");// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedState() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setState("state-5678");// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedRedirectUri() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setRedirectUri("http://somewhere.com");// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedResponseTypes() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setResponseTypes(Collections.singleton("implicit"));// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedScope() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setScope(Arrays.asList("read", "write"));// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedApproved() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        authorizationRequest.setApproved(false);
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setApproved(true);// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedResourceIds() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setResourceIds(Collections.singleton("resource-other"));// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    @Test(expected = InvalidRequestException.class)
    public void testApproveWithModifiedAuthorities() throws Exception {
        AuthorizationRequest authorizationRequest = getAuthorizationRequest("foo", "http://anywhere.com", "state-1234", "read", Collections.singleton("code"));
        model.put(AuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        model.put(AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, endpoint.unmodifiableMap(authorizationRequest));
        authorizationRequest.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList("authority-other"));// Modify authorization request

        Map<String, String> approvalParameters = new HashMap<String, String>();
        approvalParameters.put("user_oauth_approval", "true");
        endpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
    }

    private class StubAuthorizationCodeServices implements AuthorizationCodeServices {
        private OAuth2Authentication authentication;

        public String createAuthorizationCode(OAuth2Authentication authentication) {
            this.authentication = authentication;
            return "thecode";
        }

        public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
            return authentication;
        }
    }
}

