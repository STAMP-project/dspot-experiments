package org.apereo.cas.oidc.web.flow;


import java.io.Serializable;
import lombok.val;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.web.flow.services.DefaultRegisteredServiceUserInterfaceInfo;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link OidcRegisteredServiceUIActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class OidcRegisteredServiceUIActionTests extends AbstractOidcTests {
    @Test
    public void verifyOidcActionWithoutMDUI() throws Exception {
        val ctx = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(ctx, null);
        val event = oidcRegisteredServiceUIAction.execute(ctx);
        Assertions.assertEquals("success", event.getId());
        Assertions.assertNull(WebUtils.getServiceUserInterfaceMetadata(ctx, Serializable.class));
    }

    @Test
    public void verifyOidcActionWithMDUI() throws Exception {
        val ctx = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(ctx, RegisteredServiceTestUtils.getService("https://www.example.org?client_id=id&client_secret=secret&redirect_uri=https://oauth.example.org"));
        val event = oidcRegisteredServiceUIAction.execute(ctx);
        Assertions.assertEquals("success", event.getId());
        val mdui = WebUtils.getServiceUserInterfaceMetadata(ctx, DefaultRegisteredServiceUserInterfaceInfo.class);
        Assertions.assertNotNull(mdui);
        val svc = AbstractOidcTests.getOidcRegisteredService();
        Assertions.assertEquals(mdui.getDisplayName(), svc.getName());
        Assertions.assertEquals(mdui.getInformationURL(), svc.getInformationUrl());
        Assertions.assertEquals(mdui.getDescription(), svc.getDescription());
        Assertions.assertEquals(mdui.getPrivacyStatementURL(), svc.getPrivacyUrl());
        Assertions.assertEquals(mdui.getLogoUrl(), svc.getLogo());
    }
}

