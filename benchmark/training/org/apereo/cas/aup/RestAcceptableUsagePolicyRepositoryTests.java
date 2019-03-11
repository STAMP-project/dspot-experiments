package org.apereo.cas.aup;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.configuration.model.support.aup.AcceptableUsagePolicyProperties;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link RestAcceptableUsagePolicyRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
public class RestAcceptableUsagePolicyRepositoryTests {
    @Test
    public void verify() {
        val ticketRegistrySupport = Mockito.mock(TicketRegistrySupport.class);
        val props = new AcceptableUsagePolicyProperties.Rest();
        props.setUrl("http://localhost:9298");
        val r = new RestAcceptableUsagePolicyRepository(ticketRegistrySupport, "givenName", props);
        val data = "";
        try (val webServer = new org.apereo.cas.util.MockWebServer(9298, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            Assertions.assertFalse(r.isUsagePolicyAcceptedBy(CoreAuthenticationTestUtils.getPrincipal()));
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertTrue(r.submit(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

