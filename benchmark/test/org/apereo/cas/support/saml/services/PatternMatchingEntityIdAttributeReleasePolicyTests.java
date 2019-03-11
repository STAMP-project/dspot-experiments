package org.apereo.cas.support.saml.services;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link PatternMatchingEntityIdAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("FileSystem")
public class PatternMatchingEntityIdAttributeReleasePolicyTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyPatternDoesNotMatch() {
        val filter = new PatternMatchingEntityIdAttributeReleasePolicy();
        val registeredService = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        registeredService.setAttributeReleasePolicy(filter);
        val attributes = filter.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), registeredService);
        Assertions.assertTrue(attributes.isEmpty());
    }

    @Test
    public void verifyPatternDoesMatch() {
        val filter = new PatternMatchingEntityIdAttributeReleasePolicy();
        filter.setEntityIds("https://sp.+");
        filter.setAllowedAttributes(CollectionUtils.wrapList("uid", "givenName", "displayName"));
        val registeredService = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        registeredService.setAttributeReleasePolicy(filter);
        val attributes = filter.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), registeredService);
        Assertions.assertFalse(attributes.isEmpty());
    }
}

