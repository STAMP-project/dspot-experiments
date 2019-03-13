package org.apereo.cas.support.wsfederation.web;


import CasProtocolConstants.PARAMETER_METHOD;
import HttpRequestUtils.USER_AGENT_HEADER;
import WsFederationCookieManager.WCTX;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.wsfederation.AbstractWsFederationTests;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link WsFederationCookieManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class WsFederationCookieManagerTests extends AbstractWsFederationTests {
    @Test
    public void verifyOperation() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        request.setRemoteAddr("185.86.151.11");
        request.setLocalAddr("185.88.151.11");
        request.addHeader(USER_AGENT_HEADER, "MSIE");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        request.addParameter(PARAMETER_METHOD, "POST");
        request.setAttribute("locale", "en");
        request.setAttribute("theme", "custom");
        val config = wsFederationConfigurations.iterator().next();
        val wctx = config.getId();
        val original = RegisteredServiceTestUtils.getService();
        wsFederationCookieManager.store(request, response, wctx, original, config);
        request.addParameter(WCTX, wctx);
        request.setCookies(response.getCookies());
        val service = wsFederationCookieManager.retrieve(context);
        Assertions.assertNotNull(service);
        Assertions.assertEquals(original.getId(), service.getId());
    }
}

