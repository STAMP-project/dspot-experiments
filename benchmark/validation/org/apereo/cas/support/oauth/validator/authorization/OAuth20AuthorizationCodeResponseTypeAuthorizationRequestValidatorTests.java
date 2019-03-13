package org.apereo.cas.support.oauth.validator.authorization;


import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.GRANT_TYPE;
import OAuth20Constants.REDIRECT_URI;
import OAuth20Constants.RESPONSE_TYPE;
import OAuth20GrantTypes.AUTHORIZATION_CODE;
import OAuth20ResponseTypes.CODE;
import OAuth20ResponseTypes.TOKEN;
import java.util.Collection;
import java.util.LinkedHashSet;
import lombok.val;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.services.RegisteredServiceAccessStrategyAuditableEnforcer;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20AuthorizationCodeResponseTypeAuthorizationRequestValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OAuth20AuthorizationCodeResponseTypeAuthorizationRequestValidatorTests {
    @Test
    public void verifyValidator() {
        val serviceManager = Mockito.mock(ServicesManager.class);
        val service = new OAuthRegisteredService();
        service.setName("OAuth");
        service.setClientId("client");
        service.setClientSecret("secret");
        service.setServiceId("https://callback.example.org");
        Mockito.when(serviceManager.getAllServices()).thenReturn(((Collection) (CollectionUtils.toCollection(service))));
        val v = new OAuth20AuthorizationCodeResponseTypeAuthorizationRequestValidator(serviceManager, new WebApplicationServiceFactory(), new RegisteredServiceAccessStrategyAuditableEnforcer());
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        Assertions.assertFalse(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.addParameter(GRANT_TYPE, AUTHORIZATION_CODE.getType());
        Assertions.assertFalse(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.addParameter(CLIENT_ID, "client");
        Assertions.assertFalse(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.addParameter(REDIRECT_URI, service.getServiceId());
        Assertions.assertFalse(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.addParameter(RESPONSE_TYPE, CODE.getType());
        service.setSupportedResponseTypes(new LinkedHashSet());
        Assertions.assertTrue(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        service.setSupportedResponseTypes(CollectionUtils.wrapHashSet(CODE.getType()));
        Assertions.assertTrue(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        service.setSupportedResponseTypes(CollectionUtils.wrapHashSet(TOKEN.getType()));
        Assertions.assertFalse(v.validate(new org.pac4j.core.context.J2EContext(request, response)));
        Assertions.assertTrue(v.supports(new org.pac4j.core.context.J2EContext(request, response)));
    }
}

