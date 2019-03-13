package org.pac4j.cas.client.rest;


import java.util.concurrent.TimeUnit;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestClientIT implements TestsConstants {
    private static final String CAS_PREFIX_URL = "http://casserverpac4j.herokuapp.com/";

    private static final String USER = "jleleu";

    @Test
    public void testRestForm() {
        internalTestRestForm(new org.pac4j.cas.credentials.authenticator.CasRestAuthenticator(getConfig()));
    }

    @Test
    public void testRestFormWithCaching() {
        internalTestRestForm(new org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator(new org.pac4j.cas.credentials.authenticator.CasRestAuthenticator(getConfig()), 100, 100, TimeUnit.SECONDS));
    }

    @Test
    public void testRestBasic() {
        internalTestRestBasic(new CasRestBasicAuthClient(getConfig(), VALUE, NAME), 3);
    }

    @Test
    public void testRestBasicWithCas20TicketValidator() {
        final CasConfiguration config = getConfig();
        config.setDefaultTicketValidator(new Cas20ServiceTicketValidator(CasRestClientIT.CAS_PREFIX_URL));
        internalTestRestBasic(new CasRestBasicAuthClient(config, VALUE, NAME), 0);
    }
}

