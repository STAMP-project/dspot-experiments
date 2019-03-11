package org.apereo.cas.ticket.support;


import RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.DefaultAuthenticationResultBuilder;
import org.apereo.cas.authentication.DefaultPrincipalElectionStrategy;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for RememberMeDelegatingExpirationPolicy.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 */
public class RememberMeDelegatingExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "rememberMeDelegatingExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final Long REMEMBER_ME_TTL = 20000L;

    private static final Long DEFAULT_TTL = 10000L;

    /**
     * Factory to create the principal type.
     */
    protected PrincipalFactory principalFactory = new DefaultPrincipalFactory();

    private RememberMeDelegatingExpirationPolicy expirationPolicy;

    @Test
    public void verifyTicketExpirationWithRememberMe() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication(this.principalFactory.createPrincipal("test"), Collections.singletonMap(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, true));
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl("111", authentication, this.expirationPolicy);
        Assertions.assertFalse(t.isExpired());
        t.grantServiceTicket("55", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false, true);
        Assertions.assertTrue(t.isExpired());
    }

    @Test
    public void verifyTicketExpirationWithRememberMeBuiltAuthn() {
        val builder = new DefaultAuthenticationResultBuilder();
        val p1 = CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("uid", "casuser"));
        val authn1 = CoreAuthenticationTestUtils.getAuthentication(p1, CollectionUtils.wrap(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, true));
        val result = builder.collect(authn1).build(new DefaultPrincipalElectionStrategy());
        val authentication = result.getAuthentication();
        Assertions.assertNotNull(authentication);
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl("111", authentication, this.expirationPolicy);
        Assertions.assertFalse(t.isExpired());
        t.grantServiceTicket("55", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false, true);
        Assertions.assertTrue(t.isExpired());
    }

    @Test
    public void verifyTicketExpirationWithoutRememberMe() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication();
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl("111", authentication, this.expirationPolicy);
        Assertions.assertFalse(t.isExpired());
        t.grantServiceTicket("55", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false, true);
        Assertions.assertFalse(t.isExpired());
    }

    @Test
    public void verifyTicketTTLWithRememberMe() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication(this.principalFactory.createPrincipal("test"), Collections.singletonMap(AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, true));
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl("111", authentication, this.expirationPolicy);
        Assertions.assertEquals(RememberMeDelegatingExpirationPolicyTests.REMEMBER_ME_TTL, expirationPolicy.getTimeToLive(t));
    }

    @Test
    public void verifyTicketTTLWithoutRememberMe() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication();
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl("111", authentication, this.expirationPolicy);
        Assertions.assertEquals(RememberMeDelegatingExpirationPolicyTests.DEFAULT_TTL, expirationPolicy.getTimeToLive(t));
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        RememberMeDelegatingExpirationPolicyTests.MAPPER.writeValue(RememberMeDelegatingExpirationPolicyTests.JSON_FILE, expirationPolicy);
        val policyRead = RememberMeDelegatingExpirationPolicyTests.MAPPER.readValue(RememberMeDelegatingExpirationPolicyTests.JSON_FILE, RememberMeDelegatingExpirationPolicy.class);
        Assertions.assertEquals(expirationPolicy, policyRead);
    }
}

