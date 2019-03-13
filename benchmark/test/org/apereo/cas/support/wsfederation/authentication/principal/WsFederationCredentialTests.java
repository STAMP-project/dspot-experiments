package org.apereo.cas.support.wsfederation.authentication.principal;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.val;
import org.apereo.cas.support.wsfederation.AbstractWsFederationTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link WsFederationCredential}.
 *
 * @author John Gasper
 * @since 4.2.0
 */
public class WsFederationCredentialTests extends AbstractWsFederationTests {
    private static final String ISSUER = "http://adfs.example.com/adfs/services/trust";

    private static final String AUDIENCE = "urn:federation:cas";

    @Test
    public void verifyIsValidAllGood() {
        Assertions.assertTrue(WsFederationCredentialTests.getCredential().isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidAllGood() - True");
    }

    @Test
    public void verifyIsValidBadAudience() {
        val standardCred = WsFederationCredentialTests.getCredential();
        standardCred.setAudience("urn:NotUs");
        Assertions.assertFalse(standardCred.isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidBadAudeience() - False");
    }

    @Test
    public void verifyIsValidBadIssuer() {
        val standardCred = WsFederationCredentialTests.getCredential();
        standardCred.setIssuer("urn:NotThem");
        Assertions.assertFalse(standardCred.isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidBadIssuer() - False");
    }

    @Test
    public void verifyIsValidEarlyToken() {
        val standardCred = WsFederationCredentialTests.getCredential();
        standardCred.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1));
        standardCred.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).plusDays(1));
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1));
        Assertions.assertFalse(standardCred.isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidEarlyToken() - False");
    }

    @Test
    public void verifyIsValidOldToken() {
        val standardCred = WsFederationCredentialTests.getCredential();
        standardCred.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1));
        standardCred.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).minusDays(1));
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1));
        Assertions.assertFalse(standardCred.isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidOldToken() - False");
    }

    @Test
    public void verifyIsValidExpiredIssuedOn() {
        val standardCred = WsFederationCredentialTests.getCredential();
        standardCred.setIssuedOn(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(3));
        Assertions.assertFalse(standardCred.isValid(WsFederationCredentialTests.AUDIENCE, WsFederationCredentialTests.ISSUER, 2000), "testIsValidOldToken() - False");
    }
}

