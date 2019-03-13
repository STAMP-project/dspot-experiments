package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is test cases for
 * {@link DefaultRegisteredServiceAccessStrategy}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultRegisteredServiceAccessStrategyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "x509CertificateCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String TEST = "test";

    private static final String PHONE = "phone";

    private static final String GIVEN_NAME = "givenName";

    private static final String CAS = "cas";

    private static final String KAZ = "KAZ";

    private static final String CN = "cn";

    @Test
    public void checkDefaultImpls() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        Assertions.assertEquals(0, authz.getOrder());
    }

    @Test
    public void checkDefaultInterfaceImpls() {
        val authz = new RegisteredServiceAccessStrategy() {
            private static final long serialVersionUID = -6993120869616143038L;
        };
        Assertions.assertEquals(Integer.MAX_VALUE, authz.getOrder());
        Assertions.assertTrue(authz.isServiceAccessAllowed());
        Assertions.assertTrue(authz.isServiceAccessAllowedForSso());
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(null, null));
        Assertions.assertNull(authz.getUnauthorizedRedirectUrl());
    }

    @Test
    public void checkDefaultAuthzStrategyConfig() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        Assertions.assertTrue(authz.isServiceAccessAllowed());
        Assertions.assertTrue(authz.isServiceAccessAllowedForSso());
    }

    @Test
    public void checkDisabledAuthzStrategyConfig() {
        val authz = new DefaultRegisteredServiceAccessStrategy(false, true);
        Assertions.assertFalse(authz.isServiceAccessAllowed());
        Assertions.assertTrue(authz.isServiceAccessAllowedForSso());
    }

    @Test
    public void checkDisabledSsoAuthzStrategyConfig() {
        val authz = new DefaultRegisteredServiceAccessStrategy(true, false);
        Assertions.assertTrue(authz.isServiceAccessAllowed());
        Assertions.assertFalse(authz.isServiceAccessAllowedForSso());
    }

    @Test
    public void setAuthzStrategyConfig() {
        val authz = new DefaultRegisteredServiceAccessStrategy(false, false);
        authz.setEnabled(true);
        authz.setSsoEnabled(true);
        Assertions.assertTrue(authz.isServiceAccessAllowed());
        Assertions.assertTrue(authz.isServiceAccessAllowedForSso());
        Assertions.assertTrue(authz.isRequireAllAttributes());
    }

    @Test
    public void checkAuthzPrincipalNoAttrRequirements() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, new HashMap()));
    }

    @Test
    public void checkAuthzPrincipalWithAttrRequirementsEmptyPrincipal() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequiredAttributes(DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes());
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, new HashMap()));
    }

    @Test
    public void checkAuthzPrincipalWithAttrRequirementsAll() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequiredAttributes(DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes());
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes()));
    }

    @Test
    public void checkAuthzPrincipalWithAttrRequirementsMissingOne() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequiredAttributes(DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes());
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.remove(DefaultRegisteredServiceAccessStrategyTests.CN);
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkAuthzPrincipalWithAttrRequirementsMissingOneButNotAllNeeded() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequiredAttributes(DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes());
        authz.setRequireAllAttributes(false);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.remove(DefaultRegisteredServiceAccessStrategyTests.CN);
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkAuthzPrincipalWithAttrRequirementsNoValueMatch() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.PHONE);
        authz.setRequiredAttributes(reqs);
        authz.setRequireAllAttributes(false);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.remove(DefaultRegisteredServiceAccessStrategyTests.CN);
        pAttrs.put(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME, "theName");
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    /**
     * It is important that the non-matching attribute is not the first one, due to the
     * use of anyMatch and allMatch in the access strategy.
     */
    @Test
    public void checkAuthzPrincipalWithAttrRequirementsWrongValue() {
        val reqAttrs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        reqAttrs.put(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME, Collections.singleton("not present"));
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequireAllAttributes(true);
        authz.setRequiredAttributes(reqAttrs);
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes()));
    }

    @Test
    public void checkAuthzPrincipalWithAttrValueCaseSensitiveComparison() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.PHONE);
        authz.setRequiredAttributes(reqs);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.put(DefaultRegisteredServiceAccessStrategyTests.CN, "CAS");
        pAttrs.put(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME, "kaz");
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkRejectedAttributesNotAvailable() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        authz.setRequiredAttributes(reqs);
        val rejectedAttributes = DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes();
        authz.setRejectedAttributes(rejectedAttributes);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkRejectedAttributesAvailable() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val rejectedAttributes = DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes();
        authz.setRejectedAttributes(rejectedAttributes);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.put("address", "1234 Main Street");
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkRejectedAttributesAvailableRequireAll() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequireAllAttributes(true);
        val rejectedAttributes = DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes();
        authz.setRejectedAttributes(rejectedAttributes);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.put("address", "1234 Main Street");
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkRejectedAttributesAvailableRequireAll3() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequireAllAttributes(false);
        val rejectedAttributes = DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes();
        authz.setRejectedAttributes(rejectedAttributes);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.put("role", "nomatch");
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkRejectedAttributesAvailableRequireAll2() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        authz.setRequireAllAttributes(false);
        val rejectedAttributes = DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes();
        authz.setRejectedAttributes(rejectedAttributes);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        pAttrs.put("role", "staff");
        Assertions.assertFalse(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkAuthzPrincipalWithAttrValueCaseInsensitiveComparison() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        authz.setRequiredAttributes(reqs);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        authz.setCaseInsensitive(true);
        pAttrs.put(DefaultRegisteredServiceAccessStrategyTests.CN, DefaultRegisteredServiceAccessStrategyTests.CAS);
        pAttrs.put(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME, "kaz");
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void checkAuthzPrincipalWithAttrValuePatternComparison() {
        val authz = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.CN);
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME);
        authz.setRequiredAttributes(reqs);
        val pAttrs = DefaultRegisteredServiceAccessStrategyTests.getPrincipalAttributes();
        Assertions.assertTrue(authz.doPrincipalAttributesAllowServiceAccess(DefaultRegisteredServiceAccessStrategyTests.TEST, pAttrs));
    }

    @Test
    public void verifySerializeADefaultRegisteredServiceAccessStrategyToJson() throws IOException {
        val strategyWritten = new DefaultRegisteredServiceAccessStrategy();
        val reqs = DefaultRegisteredServiceAccessStrategyTests.getRequiredAttributes();
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.CN);
        reqs.remove(DefaultRegisteredServiceAccessStrategyTests.GIVEN_NAME);
        strategyWritten.setRequiredAttributes(reqs);
        strategyWritten.setRejectedAttributes(DefaultRegisteredServiceAccessStrategyTests.getRejectedAttributes());
        DefaultRegisteredServiceAccessStrategyTests.MAPPER.writeValue(DefaultRegisteredServiceAccessStrategyTests.JSON_FILE, strategyWritten);
        val strategyRead = DefaultRegisteredServiceAccessStrategyTests.MAPPER.readValue(DefaultRegisteredServiceAccessStrategyTests.JSON_FILE, DefaultRegisteredServiceAccessStrategy.class);
        Assertions.assertEquals(strategyWritten, strategyRead);
    }
}

