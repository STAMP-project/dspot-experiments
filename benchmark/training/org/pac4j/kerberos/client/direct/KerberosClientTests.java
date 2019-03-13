package org.pac4j.kerberos.client.direct;


import HttpConstants.AUTHORIZATION_HEADER;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidation;
import org.pac4j.kerberos.credentials.authenticator.KerberosTicketValidator;


/**
 * Tests the Kerberos client with mocked Kerberos authenticator/validator (i.e. no real tickets used)
 *
 * @author Garry Boyce
 * @author Vidmantas Zemleris
 * @since 2.1.0
 */
public class KerberosClientTests implements TestsConstants {
    private KerberosAuthenticator kerberosAuthenticator;

    private KerberosTicketValidator krbValidator;

    private static final byte[] KERBEROS_TICKET = Base64.getEncoder().encode("Test Kerberos".getBytes(StandardCharsets.UTF_8));

    @Test
    public void testMissingKerberosAuthenticator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(null);
        TestsHelper.initShouldFail(kerberosClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.setProfileCreator(null);
        TestsHelper.initShouldFail(kerberosClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectKerberosClient kerberosClient = new DirectKerberosClient(kerberosAuthenticator);
        kerberosClient.init();
    }

    @Test
    public void testMissingKerberosHeader() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final DirectKerberosClient client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        final Optional<KerberosCredentials> credentials = client.getCredentials(new org.pac4j.core.context.JEEContext(request, response));
        Assert.assertFalse(credentials.isPresent());
    }

    @Test
    public void testAuthentication() {
        Mockito.when(krbValidator.validateTicket(ArgumentMatchers.any())).thenReturn(new KerberosTicketValidation("garry", null, null, null));
        final DirectKerberosClient client = new DirectKerberosClient(new KerberosAuthenticator(krbValidator));
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(AUTHORIZATION_HEADER, ("Negotiate " + (new String(KerberosClientTests.KERBEROS_TICKET, StandardCharsets.UTF_8))));
        final KerberosCredentials credentials = client.getCredentials(context).get();
        Assert.assertEquals(new String(Base64.getDecoder().decode(KerberosClientTests.KERBEROS_TICKET), StandardCharsets.UTF_8), new String(credentials.getKerberosTicket(), StandardCharsets.UTF_8));
        final CommonProfile profile = ((CommonProfile) (client.getUserProfile(credentials, context).get()));
        Assert.assertEquals("garry", profile.getId());
    }
}

