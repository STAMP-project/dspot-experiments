package org.apereo.cas.support.wsfederation;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Setter;
import lombok.val;
import org.apereo.cas.support.wsfederation.authentication.principal.WsFederationCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;


/**
 * Test cases for {@link WsFederationHelper}.
 *
 * @author John Gasper
 * @since 4.2.0
 */
@Setter
public class WsFederationHelperTests extends AbstractWsFederationTests {
    private static final String GOOD_TOKEN = "goodToken";

    @Autowired
    private HashMap<String, String> testTokens;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void verifyParseTokenString() {
        val wresult = testTokens.get(WsFederationHelperTests.GOOD_TOKEN);
        val result = wsFederationHelper.buildAndVerifyAssertion(wsFederationHelper.getRequestSecurityTokenFromResult(wresult), wsFederationConfigurations);
        Assertions.assertNotNull(result, "testParseTokenString() - Not null");
    }

    @Test
    public void verifyCreateCredentialFromToken() {
        val wresult = testTokens.get(WsFederationHelperTests.GOOD_TOKEN);
        val assertion = wsFederationHelper.buildAndVerifyAssertion(wsFederationHelper.getRequestSecurityTokenFromResult(wresult), wsFederationConfigurations);
        val expResult = new WsFederationCredential();
        expResult.setIssuedOn(ZonedDateTime.parse("2014-02-26T22:51:16.504Z"));
        expResult.setNotBefore(ZonedDateTime.parse("2014-02-26T22:51:16.474Z"));
        expResult.setNotOnOrAfter(ZonedDateTime.parse("2014-02-26T23:51:16.474Z"));
        expResult.setIssuer("http://adfs.example.com/adfs/services/trust");
        expResult.setAudience("urn:federation:cas");
        expResult.setId("_6257b2bf-7361-4081-ae1f-ec58d4310f61");
        val result = wsFederationHelper.createCredentialFromToken(assertion.getKey());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expResult.getIssuedOn(), result.getIssuedOn());
        Assertions.assertEquals(expResult.getNotBefore(), result.getNotBefore());
        Assertions.assertEquals(expResult.getNotOnOrAfter(), result.getNotOnOrAfter());
        Assertions.assertEquals(expResult.getIssuer(), result.getIssuer());
        Assertions.assertEquals(expResult.getAudience(), result.getAudience());
        Assertions.assertEquals(expResult.getId(), result.getId());
    }

    @Test
    public void verifyGetSigningCredential() {
        val result = wsFederationConfigurations.iterator().next().getSigningWallet().iterator().next();
        Assertions.assertNotNull(result);
    }

    @Test
    public void verifyValidateSignatureGoodToken() {
        val wresult = testTokens.get(WsFederationHelperTests.GOOD_TOKEN);
        val assertion = wsFederationHelper.buildAndVerifyAssertion(wsFederationHelper.getRequestSecurityTokenFromResult(wresult), wsFederationConfigurations);
        val result = wsFederationHelper.validateSignature(assertion);
        Assertions.assertTrue(result);
    }

    @Test
    public void verifyValidateSignatureModifiedAttribute() {
        val wresult = testTokens.get("badTokenModifiedAttribute");
        val assertion = wsFederationHelper.buildAndVerifyAssertion(wsFederationHelper.getRequestSecurityTokenFromResult(wresult), wsFederationConfigurations);
        val result = wsFederationHelper.validateSignature(assertion);
        Assertions.assertFalse(result);
    }

    @Test
    @DirtiesContext
    public void verifyValidateSignatureBadKey() {
        val cfg = new WsFederationConfiguration();
        cfg.setSigningCertificateResources(ctx.getResource("classpath:bad-signing.crt"));
        val signingWallet = new ArrayList<org.opensaml.security.credential.Credential>(cfg.getSigningWallet());
        val wResult = testTokens.get(WsFederationHelperTests.GOOD_TOKEN);
        val requestSecurityTokenFromResult = wsFederationHelper.getRequestSecurityTokenFromResult(wResult);
        val assertion = wsFederationHelper.buildAndVerifyAssertion(requestSecurityTokenFromResult, wsFederationConfigurations);
        val wallet = assertion.getValue().getSigningWallet();
        wallet.clear();
        wallet.addAll(signingWallet);
        val result = wsFederationHelper.validateSignature(assertion);
        Assertions.assertFalse(result);
    }

    @Test
    public void verifyValidateSignatureModifiedSignature() {
        val wresult = testTokens.get("badTokenModifiedSignature");
        val assertion = wsFederationHelper.buildAndVerifyAssertion(wsFederationHelper.getRequestSecurityTokenFromResult(wresult), wsFederationConfigurations);
        val result = wsFederationHelper.validateSignature(assertion);
        Assertions.assertFalse(result);
    }
}

