package org.apereo.cas;


import HttpRequestUtils.USER_AGENT_HEADER;
import TestMultifactorAuthenticationProvider.ID;
import lombok.val;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationRequest;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationResponse;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationService;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link AdaptiveMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = "cas.authn.adaptive.requireMultifactor.mfa-dummy=MSIE")
public class AdaptiveMultifactorAuthenticationPolicyEventResolverTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier("adaptiveAuthenticationPolicyWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    private MockRequestContext context;

    private MockHttpServletRequest request;

    @Test
    public void verifyOperationNeedsMfa() {
        request.addHeader(USER_AGENT_HEADER, "MSIE");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }

    @Test
    public void verifyOperationNeedsMfaByGeo() {
        request.addHeader(USER_AGENT_HEADER, "FIREFOX");
        request.addParameter("geolocation", "1000,1000,1000,1000");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }

    @TestConfiguration
    public static class GeoLocationServiceTestConfiguration {
        @Bean
        public GeoLocationService geoLocationService() {
            val service = Mockito.mock(GeoLocationService.class);
            val response = new GeoLocationResponse();
            response.addAddress("MSIE");
            Mockito.when(service.locate(ArgumentMatchers.anyString(), ArgumentMatchers.any(GeoLocationRequest.class))).thenReturn(response);
            return service;
        }
    }
}

