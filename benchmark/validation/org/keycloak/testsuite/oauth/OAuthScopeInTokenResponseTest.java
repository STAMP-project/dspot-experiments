package org.keycloak.testsuite.oauth;


import OAuth2Constants.CODE;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import javax.ws.rs.core.Response;
import org.junit.Test;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;


// OIDC Financial API Read Only Profile : scope MUST be returned in the response from Token Endpoint
public class OAuthScopeInTokenResponseTest extends AbstractKeycloakTest {
    @Test
    public void specifyNoScopeTest() throws Exception {
        String loginUser = "john-doh@localhost";
        String loginPassword = "password";
        String clientSecret = "password";
        String expectedScope = "openid profile email";
        oauth.doLogin(loginUser, loginPassword);
        String code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(code, expectedScope, clientSecret);
    }

    @Test
    public void specifySingleNotExistingScopeTest() throws Exception {
        String loginUser = "john-doh@localhost";
        String loginPassword = "password";
        String clientSecret = "password";
        String requestedScope = "user";
        String expectedScope = "openid profile email";
        oauth.scope(requestedScope);
        oauth.doLogin(loginUser, loginPassword);
        String code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(code, expectedScope, clientSecret);
    }

    @Test
    public void specifyMultipleScopeTest() throws Exception {
        String loginUser = "rich.roles@redhat.com";
        String loginPassword = "password";
        String clientSecret = "password";
        String requestedScope = "user address";
        String expectedScope = "openid profile email address";
        oauth.scope(requestedScope);
        oauth.doLogin(loginUser, loginPassword);
        String code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(code, expectedScope, clientSecret);
    }

    @Test
    public void specifyMultipleExistingScopesTest() throws Exception {
        // Create client scope and add it as optional scope
        ClientScopeRepresentation userScope = new ClientScopeRepresentation();
        userScope.setName("user");
        userScope.setProtocol(LOGIN_PROTOCOL);
        Response response = realmsResouce().realm("test").clientScopes().create(userScope);
        String userScopeId = ApiUtil.getCreatedId(response);
        getCleanup().addClientScopeId(userScopeId);
        ApiUtil.findClientResourceByClientId(realmsResouce().realm("test"), "test-app").addOptionalClientScope(userScopeId);
        String loginUser = "john-doh@localhost";
        String loginPassword = "password";
        String clientSecret = "password";
        // Login without 'user' scope
        String requestedScope = "address phone";
        String expectedScope = "openid profile email address phone";
        oauth.scope(requestedScope);
        oauth.doLogin(loginUser, loginPassword);
        String code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(code, expectedScope, clientSecret);
        // Login with 'user' scope
        requestedScope = "user address phone";
        expectedScope = "openid profile email user address phone";
        oauth.scope(requestedScope);
        oauth.doLogin(loginUser, loginPassword);
        code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(code, expectedScope, clientSecret);
        // Cleanup
        ApiUtil.findClientResourceByClientId(realmsResouce().realm("test"), "test-app").removeOptionalClientScope(userScopeId);
    }
}

