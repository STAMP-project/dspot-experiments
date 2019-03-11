package org.apereo.cas.support.openid.authentication.principal;


import OpenIdProtocolConstants.OPENID_IDENTITY;
import OpenIdProtocolConstants.OPENID_RETURNTO;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Test cases for {@link OpenIdServiceFactory}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public class OpenIdServiceFactoryTests {
    @Test
    public void verifyServiceCreationSuccessfullyById() {
        val request = new MockHttpServletRequest();
        request.addParameter(OPENID_RETURNTO, "test");
        request.addParameter(OPENID_IDENTITY, "identity");
        val factory = new OpenIdServiceFactory("");
        val service = factory.createService(request);
        Assertions.assertNotNull(service);
    }

    @Test
    public void verifyServiceCreationMissingReturn() {
        val request = new MockHttpServletRequest();
        request.addParameter(OPENID_IDENTITY, "identity");
        val factory = new OpenIdServiceFactory("");
        val service = factory.createService(request);
        Assertions.assertNull(service);
    }

    @Test
    public void verifyServiceCreationMissingId() {
        val request = new MockHttpServletRequest();
        request.addParameter(OPENID_RETURNTO, "test");
        val factory = new OpenIdServiceFactory("");
        val service = factory.createService(request);
        Assertions.assertNull(service);
    }
}

