package org.apereo.cas.consent;


import ConsentReminderOptions.ALWAYS;
import ConsentReminderOptions.ATTRIBUTE_NAME;
import java.time.temporal.ChronoUnit;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasConsentCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.consent.DefaultRegisteredServiceConsentPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link DefaultConsentEngineTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasRegisteredServicesTestConfiguration.class, CasConsentCoreConfiguration.class, CasCoreAuditConfiguration.class, RefreshAutoConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class })
@DirtiesContext
public class DefaultConsentEngineTests {
    @Autowired
    @Qualifier("consentEngine")
    private ConsentEngine consentEngine;

    @Test
    public void verifyConsentIsAlwaysRequired() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        Mockito.when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService, authentication, 14, ChronoUnit.DAYS, ALWAYS);
        Assertions.assertNotNull(decision);
        val result = this.consentEngine.isConsentRequiredFor(service, consentService, authentication);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isRequired());
        Assertions.assertEquals(decision, result.getConsentDecision());
    }

    @Test
    public void verifyConsentIsRequiredByAttributeName() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        Mockito.when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService, authentication, 14, ChronoUnit.DAYS, ATTRIBUTE_NAME);
        Assertions.assertNotNull(decision);
        val result = this.consentEngine.isConsentRequiredFor(service, consentService, authentication);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isRequired());
    }

    @Test
    public void verifyConsentFound() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        Mockito.when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService, authentication, 14, ChronoUnit.DAYS, ATTRIBUTE_NAME);
        Assertions.assertNotNull(decision);
        val decision2 = this.consentEngine.findConsentDecision(service, consentService, authentication);
        Assertions.assertEquals(decision, decision2);
    }
}

