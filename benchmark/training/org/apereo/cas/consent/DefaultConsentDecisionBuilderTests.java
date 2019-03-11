package org.apereo.cas.consent;


import ConsentReminderOptions.ATTRIBUTE_VALUE;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.config.CasConsentCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link DefaultConsentDecisionBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { CasConsentCoreConfiguration.class, CasCoreAuditConfiguration.class, RefreshAutoConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class })
public class DefaultConsentDecisionBuilderTests {
    @Autowired
    @Qualifier("consentDecisionBuilder")
    private ConsentDecisionBuilder consentDecisionBuilder;

    @Test
    public void verifyNewConsentDecision() {
        val consentDecision = getConsentDecision();
        Assertions.assertNotNull(consentDecision);
        Assertions.assertEquals("casuser", consentDecision.getPrincipal());
        Assertions.assertEquals(consentDecision.getService(), RegisteredServiceTestUtils.getService().getId());
    }

    @Test
    public void verifyAttributesRequireConsent() {
        val consentDecision = getConsentDecision();
        Assertions.assertTrue(consentDecisionBuilder.doesAttributeReleaseRequireConsent(consentDecision, CollectionUtils.wrap("attr2", "value2")));
        Assertions.assertFalse(consentDecisionBuilder.doesAttributeReleaseRequireConsent(consentDecision, CollectionUtils.wrap("attr1", "something")));
    }

    @Test
    public void verifyAttributeValuesRequireConsent() {
        val consentDecision = getConsentDecision();
        consentDecision.setOptions(ATTRIBUTE_VALUE);
        Assertions.assertTrue(consentDecisionBuilder.doesAttributeReleaseRequireConsent(consentDecision, CollectionUtils.wrap("attr1", "value2")));
    }

    @Test
    public void verifyAttributesAreRetrieved() {
        val consentDecision = getConsentDecision();
        val attrs = consentDecisionBuilder.getConsentableAttributesFrom(consentDecision);
        Assertions.assertTrue(attrs.containsKey("attr1"));
        Assertions.assertEquals("value1", attrs.get("attr1"));
    }
}

