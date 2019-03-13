package org.apereo.cas.web.flow;


import CasProtocolConstants.PARAMETER_METHOD;
import CasProtocolConstants.PARAMETER_SERVICE;
import CasWebflowConstants.VAR_ID_CREDENTIAL;
import ClientCredential.NOT_YET_AUTHENTICATED;
import HttpMethod.POST;
import Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;
import ThemeChangeInterceptor.DEFAULT_PARAM_NAME;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.ClientCredential;
import org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This class tests the {@link DelegatedClientAuthenticationAction} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */
public class DelegatedClientAuthenticationActionTests {
    private static final String TGT_ID = "TGT-00-xxxxxxxxxxxxxxxxxxxxxxxxxx.cas0";

    private static final String MY_KEY = "my_key";

    private static final String MY_SECRET = "my_secret";

    private static final String MY_LOGIN_URL = "http://casserver/login";

    private static final String MY_SERVICE = "http://myservice";

    private static final String MY_THEME = "my_theme";

    private static final List<String> CLIENTS = Arrays.asList("FacebookClient", "TwitterClient");

    @Test
    public void verifyStartAuthenticationNoService() {
        assertStartAuthentication(null);
    }

    @Test
    public void verifyStartAuthenticationWithService() {
        val service = RegisteredServiceTestUtils.getService(DelegatedClientAuthenticationActionTests.MY_SERVICE);
        assertStartAuthentication(service);
    }

    @Test
    public void verifyFinishAuthenticationAuthzFailure() {
        val mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter(DEFAULT_CLIENT_NAME_PARAMETER, "FacebookClient");
        val service = CoreAuthenticationTestUtils.getService(DelegatedClientAuthenticationActionTests.MY_SERVICE);
        mockRequest.addParameter(PARAMETER_SERVICE, service.getId());
        val servletExternalContext = Mockito.mock(ServletExternalContext.class);
        Mockito.when(servletExternalContext.getNativeRequest()).thenReturn(mockRequest);
        Mockito.when(servletExternalContext.getNativeResponse()).thenReturn(new MockHttpServletResponse());
        val mockRequestContext = new MockRequestContext();
        mockRequestContext.setExternalContext(servletExternalContext);
        val facebookClient = new FacebookClient() {
            @Override
            protected OAuth20Credentials retrieveCredentials(final WebContext context) {
                return new OAuth20Credentials("fakeVerifier");
            }
        };
        facebookClient.setName(FacebookClient.class.getSimpleName());
        val clients = new org.pac4j.core.client.Clients(DelegatedClientAuthenticationActionTests.MY_LOGIN_URL, facebookClient);
        val strategy = new DefaultRegisteredServiceAccessStrategy();
        strategy.setEnabled(false);
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> getDelegatedClientAction(facebookClient, service, clients, mockRequest, strategy).execute(mockRequestContext));
    }

    @Test
    @SneakyThrows
    public void verifyFinishAuthentication() {
        val mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter(DEFAULT_CLIENT_NAME_PARAMETER, "FacebookClient");
        mockRequest.addParameter(DEFAULT_PARAM_NAME, DelegatedClientAuthenticationActionTests.MY_THEME);
        mockRequest.addParameter(LocaleChangeInterceptor.DEFAULT_PARAM_NAME, Locale.getDefault().getCountry());
        mockRequest.addParameter(PARAMETER_METHOD, POST.name());
        val service = CoreAuthenticationTestUtils.getService(DelegatedClientAuthenticationActionTests.MY_SERVICE);
        mockRequest.addParameter(PARAMETER_SERVICE, service.getId());
        val servletExternalContext = Mockito.mock(ServletExternalContext.class);
        Mockito.when(servletExternalContext.getNativeRequest()).thenReturn(mockRequest);
        Mockito.when(servletExternalContext.getNativeResponse()).thenReturn(new MockHttpServletResponse());
        val mockRequestContext = new MockRequestContext();
        mockRequestContext.setExternalContext(servletExternalContext);
        val facebookClient = new FacebookClient() {
            @Override
            protected OAuth20Credentials retrieveCredentials(final WebContext context) {
                return new OAuth20Credentials("fakeVerifier");
            }
        };
        facebookClient.setName(FacebookClient.class.getSimpleName());
        val clients = new org.pac4j.core.client.Clients(DelegatedClientAuthenticationActionTests.MY_LOGIN_URL, facebookClient);
        val strategy = new DefaultRegisteredServiceAccessStrategy();
        strategy.setDelegatedAuthenticationPolicy(new org.apereo.cas.services.DefaultRegisteredServiceDelegatedAuthenticationPolicy(CollectionUtils.wrapList(facebookClient.getName())));
        val event = getDelegatedClientAction(facebookClient, service, clients, mockRequest, strategy).execute(mockRequestContext);
        Assertions.assertEquals("success", event.getId());
        Assertions.assertEquals(DelegatedClientAuthenticationActionTests.MY_THEME, mockRequest.getAttribute(DEFAULT_PARAM_NAME));
        Assertions.assertEquals(Locale.getDefault().getCountry(), mockRequest.getAttribute(LocaleChangeInterceptor.DEFAULT_PARAM_NAME));
        Assertions.assertEquals(POST.name(), mockRequest.getAttribute(PARAMETER_METHOD));
        Assertions.assertEquals(DelegatedClientAuthenticationActionTests.MY_SERVICE, mockRequest.getAttribute(PARAMETER_SERVICE));
        val flowScope = mockRequestContext.getFlowScope();
        Assertions.assertEquals(service.getId(), getId());
        val credential = ((ClientCredential) (flowScope.get(VAR_ID_CREDENTIAL)));
        Assertions.assertNotNull(credential);
        Assertions.assertTrue(credential.getId().startsWith(NOT_YET_AUTHENTICATED));
    }
}

