package org.apereo.cas.support.saml.web.support;


import CasProtocolConstants.PARAMETER_SERVICE;
import java.util.Collections;
import lombok.val;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.support.saml.authentication.principal.SamlServiceFactory;
import org.apereo.cas.util.HttpRequestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class WebUtilTests {
    @Test
    public void verifyFindService() {
        val casArgumentExtractor = new org.apereo.cas.web.support.DefaultArgumentExtractor(new WebApplicationServiceFactory());
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, "test");
        val service = HttpRequestUtils.getService(Collections.singletonList(casArgumentExtractor), request);
        Assertions.assertNotNull(service);
        Assertions.assertEquals("test", service.getId());
    }

    @Test
    public void verifyFoundNoService() {
        val casArgumentExtractor = new org.apereo.cas.web.support.DefaultArgumentExtractor(new SamlServiceFactory());
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, "test");
        val service = HttpRequestUtils.getService(Collections.singletonList(casArgumentExtractor), request);
        Assertions.assertNull(service);
    }
}

