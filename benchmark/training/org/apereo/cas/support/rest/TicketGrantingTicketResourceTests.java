package org.apereo.cas.support.rest;


import MediaType.ALL;
import MediaType.APPLICATION_JSON_UTF8;
import MediaType.TEXT_HTML;
import lombok.val;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.support.rest.resources.TicketGrantingTicketResource;
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
public class TicketGrantingTicketResourceTests {
    private static final String TICKETS_RESOURCE_URL = "/cas/v1/tickets";

    private static final String USERNAME = "username";

    private static final String OTHER_EXCEPTION = "Other exception";

    private static final String TEST_VALUE = "test";

    private static final String PASSWORD = "password";

    @Mock
    private CentralAuthenticationService casMock;

    @Mock
    private TicketRegistrySupport ticketSupport;

    @InjectMocks
    private TicketGrantingTicketResource ticketGrantingTicketResourceUnderTest;

    private MockMvc mockMvc;

    @Test
    public void verifyNormalCreationOfTGT() throws Exception {
        val expectedReturnEntityBody = "<!DOCTYPE HTML PUBLIC \\\"-//IETF//DTD HTML 2.0//EN\\\">" + ((("<html><head><title>201 CREATED</title></head><body><h1>TGT Created</h1>" + "<form action=\"http://localhost/cas/v1/tickets/TGT-1\" ") + "method=\"POST\">Service:<input type=\"text\" name=\"service\" value=\"\">") + "<br><input type=\"submit\" value=\"Submit\"></form></body></html>");
        configureCasMockToCreateValidTGT();
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE)).andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/cas/v1/tickets/TGT-1")).andExpect(content().contentType(TEXT_HTML)).andExpect(content().string(expectedReturnEntityBody));
    }

    @Test
    public void defaultCreationOfTGT() throws Throwable {
        val expectedReturnEntityBody = "<!DOCTYPE HTML PUBLIC \\\"-//IETF//DTD HTML 2.0//EN\\\">" + ((("<html><head><title>201 CREATED</title></head><body><h1>TGT Created</h1>" + "<form action=\"http://localhost/cas/v1/tickets/TGT-1\" ") + "method=\"POST\">Service:<input type=\"text\" name=\"service\" value=\"\">") + "<br><input type=\"submit\" value=\"Submit\"></form></body></html>");
        configureCasMockToCreateValidTGT();
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE).accept(ALL)).andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/cas/v1/tickets/TGT-1")).andExpect(content().contentType(TEXT_HTML)).andExpect(content().string(expectedReturnEntityBody));
    }

    @Test
    public void verifyHtmlCreationOfTGT() throws Throwable {
        val expectedReturnEntityBody = "<!DOCTYPE HTML PUBLIC \\\"-//IETF//DTD HTML 2.0//EN\\\">" + ((("<html><head><title>201 CREATED</title></head><body><h1>TGT Created</h1>" + "<form action=\"http://localhost/cas/v1/tickets/TGT-1\" ") + "method=\"POST\">Service:<input type=\"text\" name=\"service\" value=\"\">") + "<br><input type=\"submit\" value=\"Submit\"></form></body></html>");
        configureCasMockToCreateValidTGT();
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE).accept(TEXT_HTML)).andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/cas/v1/tickets/TGT-1")).andExpect(content().contentType(TEXT_HTML)).andExpect(content().string(expectedReturnEntityBody));
    }

    @Test
    public void verifyJsonCreationOfTGT() throws Throwable {
        val expectedReturnEntityBody = "TGT-1";
        configureCasMockToCreateValidTGT();
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE).accept(APPLICATION_JSON_UTF8)).andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/cas/v1/tickets/TGT-1")).andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(content().string(expectedReturnEntityBody));
    }

    @Test
    public void creationOfTGTWithAuthenticationException() throws Exception {
        configureCasMockTGTCreationToThrowAuthenticationException();
        val content = this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE)).andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("LoginException"));
    }

    @Test
    public void creationOfTGTWithUnexpectedRuntimeException() throws Exception {
        configureCasMockTGTCreationToThrow(new RuntimeException(TicketGrantingTicketResourceTests.OTHER_EXCEPTION));
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param(TicketGrantingTicketResourceTests.USERNAME, TicketGrantingTicketResourceTests.TEST_VALUE).param(TicketGrantingTicketResourceTests.PASSWORD, TicketGrantingTicketResourceTests.TEST_VALUE)).andExpect(status().is5xxServerError()).andExpect(content().string(TicketGrantingTicketResourceTests.OTHER_EXCEPTION));
    }

    @Test
    public void creationOfTGTWithBadPayload() throws Exception {
        configureCasMockTGTCreationToThrow(new RuntimeException(TicketGrantingTicketResourceTests.OTHER_EXCEPTION));
        this.mockMvc.perform(post(TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL).param("no_username_param", TicketGrantingTicketResourceTests.TEST_VALUE).param("no_password_param", TicketGrantingTicketResourceTests.TEST_VALUE)).andExpect(status().is4xxClientError());
    }

    @Test
    public void deletionOfTGT() throws Exception {
        this.mockMvc.perform(delete(((TicketGrantingTicketResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1"))).andExpect(status().isOk());
    }
}

