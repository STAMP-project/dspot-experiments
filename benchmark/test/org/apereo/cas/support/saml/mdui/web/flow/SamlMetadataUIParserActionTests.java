package org.apereo.cas.support.saml.mdui.web.flow;


import CasProtocolConstants.PARAMETER_SERVICE;
import SamlProtocolConstants.PARAMETER_ENTITY_ID;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.apereo.cas.support.saml.mdui.SamlMetadataUIInfo;
import org.apereo.cas.support.saml.mdui.config.SamlMetadataUIConfiguration;
import org.apereo.cas.support.saml.mdui.config.SamlMetadataUIWebflowConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SamlMetadataUIParserActionTests}.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@Import({ SamlMetadataUIConfiguration.class, SamlMetadataUIWebflowConfiguration.class })
@TestPropertySource(properties = { "cas.samlMetadataUi.resources=classpath:sample-metadata.xml::classpath:inc-md-pub.pem" })
public class SamlMetadataUIParserActionTests extends AbstractOpenSamlTests {
    @Autowired
    @Qualifier("samlMetadataUIParserAction")
    private Action samlMetadataUIParserAction;

    @Test
    public void verifyEntityIdUIInfoExists() throws Exception {
        val ctx = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ENTITY_ID, "https://carmenwiki.osu.edu/shibboleth");
        val response = new MockHttpServletResponse();
        val sCtx = new MockServletContext();
        ctx.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(sCtx, request, response));
        ctx.getFlowScope().put(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService());
        samlMetadataUIParserAction.execute(ctx);
        Assertions.assertNotNull(WebUtils.getServiceUserInterfaceMetadata(ctx, SamlMetadataUIInfo.class));
    }

    @Test
    public void verifyEntityIdUIInfoNoParam() throws Exception {
        val ctx = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter("somethingelse", "https://carmenwiki.osu.edu/shibboleth");
        val response = new MockHttpServletResponse();
        val sCtx = new MockServletContext();
        ctx.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(sCtx, request, response));
        samlMetadataUIParserAction.execute(ctx);
        Assertions.assertNull(WebUtils.getServiceUserInterfaceMetadata(ctx, SamlMetadataUIInfo.class));
    }
}

