package org.apereo.cas.support.rest;


import lombok.val;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.support.rest.resources.ServiceTicketResource;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Unit tests for {@link TicketGrantingTicketResource}.
 *
 * @author Dmitriy Kopylenko
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
public class ServiceTicketResourceTests {
    private static final String TICKETS_RESOURCE_URL = "/cas/v1/tickets";

    private static final String OTHER_EXCEPTION = "Other exception";

    private static final String SERVICE = "service";

    private static final String RENEW = "renew";

    private static final String TEST_VALUE = "test";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    @Mock
    private CentralAuthenticationService casMock;

    @Mock
    private TicketRegistrySupport ticketSupport;

    @InjectMocks
    private ServiceTicketResource serviceTicketResource;

    private MockMvc mockMvc;

    @Test
    public void normalCreationOfST() throws Exception {
        configureCasMockToCreateValidST();
        this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId())).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("ST-1"));
    }

    @Test
    public void normalCreationOfSTWithRenew() throws Exception {
        configureCasMockToCreateValidST();
        val content = this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId()).param(ServiceTicketResourceTests.RENEW, "true").param(ServiceTicketResourceTests.USERNAME, ServiceTicketResourceTests.TEST_VALUE).param(ServiceTicketResourceTests.PASSWORD, ServiceTicketResourceTests.TEST_VALUE)).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("ST-1")).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("ST-1"));
    }

    @Test
    public void creationOfSTWithInvalidTicketException() throws Exception {
        configureCasMockSTCreationToThrow(new InvalidTicketException("TGT-1"));
        this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId())).andExpect(status().isNotFound());
    }

    @Test
    public void creationOfSTWithGeneralException() throws Exception {
        configureCasMockSTCreationToThrow(new RuntimeException(ServiceTicketResourceTests.OTHER_EXCEPTION));
        this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId())).andExpect(status().is5xxServerError()).andExpect(content().string(ServiceTicketResourceTests.OTHER_EXCEPTION));
    }

    @Test
    public void creationOfSTWithBadRequestException() throws Exception {
        configureCasMockToCreateValidST();
        val content = this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId()).param(ServiceTicketResourceTests.RENEW, "true")).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("No credentials"));
    }

    @Test
    public void creationOfSTWithAuthenticationException() throws Exception {
        configureCasMockSTCreationToThrowAuthenticationException();
        val content = this.mockMvc.perform(post(((ServiceTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1")).param(ServiceTicketResourceTests.SERVICE, CoreAuthenticationTestUtils.getService().getId()).param(ServiceTicketResourceTests.RENEW, "true").param(ServiceTicketResourceTests.USERNAME, ServiceTicketResourceTests.TEST_VALUE).param(ServiceTicketResourceTests.PASSWORD, ServiceTicketResourceTests.TEST_VALUE)).andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("LoginException"));
    }
}

