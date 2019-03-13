package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.scim2.common.types.Meta;
import com.unboundid.scim2.common.types.Name;
import com.unboundid.scim2.common.types.UserResource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasScimConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link PrincipalScimV2ProvisionerActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasScimConfiguration.class, CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasCoreServicesConfiguration.class, CasCoreUtilConfiguration.class, CasCoreConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = "cas.scim.target=http://localhost:8218")
public class PrincipalScimV2ProvisionerActionTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("principalScimProvisionerAction")
    private Action principalScimProvisionerAction;

    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        val user = new UserResource();
        user.setActive(true);
        user.setDisplayName("CASUser");
        user.setId("casuser");
        val name = new Name();
        name.setGivenName("casuser");
        user.setName(name);
        val meta = new Meta();
        meta.setResourceType("User");
        meta.setCreated(Calendar.getInstance());
        meta.setLocation(new URI("http://localhost:8218"));
        user.setMeta(meta);
        val data = PrincipalScimV2ProvisionerActionTests.MAPPER.writeValueAsString(user);
        try (val webServer = new org.apereo.cas.util.MockWebServer(8218, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            Assertions.assertEquals(TRANSITION_ID_SUCCESS, principalScimProvisionerAction.execute(context).getId());
        }
    }
}

