package org.apereo.cas.support.openid.authentication.principal;


import OpenIdProtocolConstants.OPENID_ASSOCHANDLE;
import OpenIdProtocolConstants.OPENID_IDENTITY;
import OpenIdProtocolConstants.OPENID_MODE;
import OpenIdProtocolConstants.OPENID_RETURNTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.DefaultServicesManager;
import org.apereo.cas.services.ServiceRegistry;
import org.apereo.cas.support.openid.AbstractOpenIdTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openid4java.association.Association;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Slf4j
public class OpenIdServiceTests extends AbstractOpenIdTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "openIdService.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String OPEN_ID_PREFIX_URL = "http://openid.ja-sig.org/battags";

    private static final String RETURN_TO_URL = "http://www.ja-sig.org/?service=fa";

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private OpenIdService openIdService;

    private Association association;

    @Test
    public void verifySerializeAOpenIdServiceToJson() throws IOException {
        request.removeParameter(OPENID_ASSOCHANDLE);
        request.addParameter(OPENID_ASSOCHANDLE, association.getHandle());
        openIdService = openIdServiceFactory.createService(request);
        OpenIdServiceTests.MAPPER.writeValue(OpenIdServiceTests.JSON_FILE, openIdService);
        val serviceRead = OpenIdServiceTests.MAPPER.readValue(OpenIdServiceTests.JSON_FILE, OpenIdService.class);
        Assertions.assertEquals(openIdService, serviceRead);
    }

    @Test
    public void verifyGetResponse() {
        try {
            request.removeParameter(OPENID_ASSOCHANDLE);
            request.addParameter(OPENID_ASSOCHANDLE, association.getHandle());
            openIdService = openIdServiceFactory.createService(request);
            val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), openIdService);
            val tgt = centralAuthenticationService.createTicketGrantingTicket(ctx).getId();
            val st = centralAuthenticationService.grantServiceTicket(tgt, openIdService, ctx).getId();
            centralAuthenticationService.validateServiceTicket(st, openIdService);
            val response = new OpenIdServiceResponseBuilder(OpenIdServiceTests.OPEN_ID_PREFIX_URL, serverManager, centralAuthenticationService, new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(openIdService, "something", CoreAuthenticationTestUtils.getAuthentication());
            Assertions.assertNotNull(response);
            Assertions.assertEquals(association.getHandle(), response.getAttributes().get(OPENID_ASSOCHANDLE));
            Assertions.assertEquals(OpenIdServiceTests.RETURN_TO_URL, response.getAttributes().get(OPENID_RETURNTO));
            Assertions.assertEquals(OpenIdServiceTests.OPEN_ID_PREFIX_URL, response.getAttributes().get(OPENID_IDENTITY));
            val response2 = new OpenIdServiceResponseBuilder(OpenIdServiceTests.OPEN_ID_PREFIX_URL, serverManager, centralAuthenticationService, new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(openIdService, null, CoreAuthenticationTestUtils.getAuthentication());
            Assertions.assertEquals("cancel", response2.getAttributes().get(OPENID_MODE));
        } catch (final Exception e) {
            LOGGER.debug("Exception during verification of service ticket", e);
        }
    }

    @Test
    public void verifyExpiredAssociationGetResponse() {
        try {
            request.removeParameter(OPENID_ASSOCHANDLE);
            request.addParameter(OPENID_ASSOCHANDLE, association.getHandle());
            openIdService = openIdServiceFactory.createService(request);
            val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), openIdService);
            val tgt = centralAuthenticationService.createTicketGrantingTicket(ctx).getId();
            val st = centralAuthenticationService.grantServiceTicket(tgt, openIdService, ctx).getId();
            centralAuthenticationService.validateServiceTicket(st, openIdService);
            synchronized(this) {
                try {
                    this.wait(3000);
                } catch (final InterruptedException e) {
                    throw new AssertionError("Could not wait long enough to check association expiry date");
                }
            }
            val response = new OpenIdServiceResponseBuilder(OpenIdServiceTests.OPEN_ID_PREFIX_URL, serverManager, centralAuthenticationService, new DefaultServicesManager(Mockito.mock(ServiceRegistry.class), Mockito.mock(ApplicationEventPublisher.class), new HashSet())).build(openIdService, st, CoreAuthenticationTestUtils.getAuthentication());
            Assertions.assertNotNull(response);
            Assertions.assertEquals(2, response.getAttributes().size());
            Assertions.assertEquals("cancel", response.getAttributes().get(OPENID_MODE));
        } catch (final Exception e) {
            LOGGER.debug("Exception during verification of service ticket", e);
        }
    }

    @Test
    public void verifyEquals() {
        val request1 = new MockHttpServletRequest();
        request1.addParameter("openid.identity", OpenIdServiceTests.OPEN_ID_PREFIX_URL);
        request1.addParameter("openid.return_to", OpenIdServiceTests.RETURN_TO_URL);
        request1.addParameter("openid.mode", "openid.checkid_setup");
        val request2 = new MockHttpServletRequest();
        request2.addParameter("openid.identity", OpenIdServiceTests.OPEN_ID_PREFIX_URL);
        request2.addParameter("openid.return_to", OpenIdServiceTests.RETURN_TO_URL);
        val o1 = openIdServiceFactory.createService(request);
        val o2 = openIdServiceFactory.createService(request);
        Assertions.assertTrue(o1.equals(o2));
        Assertions.assertFalse(o1.equals(new Object()));
    }
}

