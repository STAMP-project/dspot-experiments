package org.apereo.cas.authentication.mfa;


import AuthenticationHandler.SUCCESSFUL_AUTHENTICATION_HANDLERS;
import AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE;
import lombok.val;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.configuration.model.support.mfa.MultifactorAuthenticationProviderBypassProperties;
import org.apereo.cas.services.RegisteredServiceMultifactorPolicy;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link MultifactorAuthenticationProviderBypassTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@DirtiesContext
@SpringBootTest(classes = AopAutoConfiguration.class)
public class MultifactorAuthenticationProviderBypassTests {
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyMultifactorAuthenticationBypassByPrincipalAttributes() {
        val request = new MockHttpServletRequest();
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setPrincipalAttributeName("givenName");
        props.setPrincipalAttributeValue("CAS");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("givenName", "CAS"));
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal, CollectionUtils.wrap("authnFlag", "bypass"));
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.PrincipalMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByAuthenticationAttributes() {
        val request = new MockHttpServletRequest();
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setAuthenticationAttributeName("authnFlag");
        props.setAuthenticationAttributeValue("bypass");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("givenName", "CAS"));
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal, CollectionUtils.wrap("authnFlag", "bypass"));
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.AuthenticationMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByAuthenticationMethod() {
        val request = new MockHttpServletRequest();
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setAuthenticationMethodName("simpleAuthentication");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("givenName", "CAS"));
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal, CollectionUtils.wrap(AUTHENTICATION_METHOD_ATTRIBUTE, "simpleAuthentication"));
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.AuthenticationMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByAuthenticationHandler() {
        val request = new MockHttpServletRequest();
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setAuthenticationHandlerName("SimpleAuthenticationHandler");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("givenName", "CAS"));
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal, CollectionUtils.wrap(SUCCESSFUL_AUTHENTICATION_HANDLERS, "SimpleAuthenticationHandler"));
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.AuthenticationMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByAuthenticationCredentialClass() {
        val request = new MockHttpServletRequest();
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setCredentialClassType(Credential.class.getName());
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.CredentialMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByHttpRequestHeader() {
        val request = new MockHttpServletRequest();
        request.addHeader("headerbypass", "true");
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setHttpRequestHeaders("headerbypass");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.HttpRequestMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByHttpRequestRemoteAddress() {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("123.456.789.000");
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setHttpRequestRemoteAddress("123.+");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.HttpRequestMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByHttpRequestRemoteHost() {
        val request = new MockHttpServletRequest();
        request.setRemoteHost("somewhere.example.org");
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setHttpRequestRemoteAddress(".+example\\.org");
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.HttpRequestMultifactorAuthenticationProviderBypass(props, provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassByService() {
        val request = new MockHttpServletRequest();
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.RegisteredServiceMultifactorAuthenticationProviderBypass(provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        val policy = Mockito.mock(RegisteredServiceMultifactorPolicy.class);
        Mockito.when(policy.isBypassEnabled()).thenReturn(true);
        Mockito.when(service.getMultifactorPolicy()).thenReturn(policy);
        Assertions.assertFalse(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }

    @Test
    public void verifyMultifactorAuthenticationBypassIgnored() {
        val request = new MockHttpServletRequest();
        val principal = MultifactorAuthenticationTestUtils.getPrincipal("casuser");
        val authentication = MultifactorAuthenticationTestUtils.getAuthentication(principal);
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val bypass = new org.apereo.cas.authentication.bypass.RegisteredServiceMultifactorAuthenticationProviderBypass(provider.getId());
        val service = MultifactorAuthenticationTestUtils.getRegisteredService();
        Assertions.assertTrue(bypass.shouldMultifactorAuthenticationProviderExecute(authentication, service, provider, request));
    }
}

