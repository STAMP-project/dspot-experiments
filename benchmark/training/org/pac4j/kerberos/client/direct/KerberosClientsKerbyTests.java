package org.pac4j.kerberos.client.direct;


import HttpConstants.AUTHORIZATION_HEADER;
import java.io.File;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;


/**
 * This tests both Direct and Indirect Kerberos clients.
 * Both clients behave the same except then when credentials are invalid or not specified.
 * - .getCredentials() in direct client
 *   * returns NULL
 * - .getCredentials() in indirect client raises an exception:
 *   * raises a HttpAction "401 Authenticate: Negotiate"
 *
 * This is a test with real kerberos ticket validation:
 * - spins up a mini/lightweight Kerberos server (using Apache Kerby)
 * - generates the keytabs for service and client
 * - generates a real service ticket, and passes it to mock context
 * - checks that the ticket is correctly validated, and yields a correct client ID
 *
 * @author Vidmantas Zemleris, at Kensu.io
 * @since 2.1.0
 */
public class KerberosClientsKerbyTests implements TestsConstants {
    private static SimpleKdcServer kerbyServer;

    static String clientPrincipal = "clientPrincipal@MYREALM.LT";

    static String clientPassword = "clientPrincipal";

    static String servicePrincipal = "HTTP/lala.mydomain.de@MYREALM.LT";// i.e. HTTP/full-qualified-domain-name@DOMAIN


    static String serviceName = "HTTP@lala.mydomain.de";

    static String serviceKeyTabFileName = "/tmp/testServiceKeyTabFile";

    static File serviceKeytabFile = new File(KerberosClientsKerbyTests.serviceKeyTabFileName);

    @Test
    public void testDirectNoAuth() {
        // a request without "Authentication: (Negotiate|Kerberos) SomeToken" header, yields NULL credentials
        Assert.assertFalse(setupDirectKerberosClient().getCredentials(MockWebContext.create()).isPresent());
    }

    @Test
    public void testDirectAuthenticationWithRealTicket() throws Exception {
        checkWithGoodTicket(setupDirectKerberosClient());
    }

    // =====================
    // Indirect client below
    // =====================
    @Test
    public void testDirectIncorrectAuth() {
        // a request with an incorrect Kerberos token, yields NULL credentials also
        final MockWebContext context = MockWebContext.create().addRequestHeader(AUTHORIZATION_HEADER, ("Negotiate " + "AAAbbAA123"));
        Assert.assertFalse(setupDirectKerberosClient().getCredentials(context).isPresent());
    }

    @Test
    public void testIndirectNoAuth() {
        // a request without "Authentication: (Negotiate|Kerberos) SomeToken" header
        assertGetCredentialsFailsWithAuthRequired(setupIndirectKerberosClient(), MockWebContext.create(), "Performing a 401 HTTP action");
    }

    @Test
    public void testIndirectIncorrectAuth() {
        // a request with an incorrect Kerberos token, yields NULL credentials also
        final MockWebContext context = MockWebContext.create().addRequestHeader(AUTHORIZATION_HEADER, ("Negotiate " + "AAAbbAA123"));
        assertGetCredentialsFailsWithAuthRequired(setupIndirectKerberosClient(), context, "Performing a 401 HTTP action");
    }

    @Test
    public void testIndirectAuthenticationWithRealTicket() throws Exception {
        checkWithGoodTicket(setupIndirectKerberosClient());
    }
}

