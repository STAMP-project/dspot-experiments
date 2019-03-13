package org.apereo.cas.logout;


import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.val;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.logout.slo.SingleLogoutMessageCreator;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
public class SamlCompliantLogoutMessageCreatorTests {
    private static final String CONST_TEST_URL = "https://google.com";

    private final SingleLogoutMessageCreator builder = new DefaultSingleLogoutMessageCreator();

    @Test
    public void verifyMessageBuilding() throws Exception {
        val service = Mockito.mock(WebApplicationService.class);
        Mockito.when(service.getOriginalUrl()).thenReturn(SamlCompliantLogoutMessageCreatorTests.CONST_TEST_URL);
        val logoutUrl = new URL(service.getOriginalUrl());
        val request = DefaultSingleLogoutRequest.builder().ticketId("TICKET-ID").service(service).logoutUrl(logoutUrl).registeredService(Mockito.mock(RegisteredService.class)).ticketGrantingTicket(new MockTicketGrantingTicket("casuser")).build();
        val msg = builder.create(request);
        val factory = DocumentBuilderFactory.newInstance();
        val documentBuilder = factory.newDocumentBuilder();
        try (val is = new ByteArrayInputStream(msg.getPayload().getBytes(StandardCharsets.UTF_8))) {
            val document = documentBuilder.parse(is);
            val list = document.getDocumentElement().getElementsByTagName("samlp:SessionIndex");
            Assertions.assertEquals(1, list.getLength());
            Assertions.assertEquals(list.item(0).getTextContent(), request.getTicketId());
        }
    }
}

