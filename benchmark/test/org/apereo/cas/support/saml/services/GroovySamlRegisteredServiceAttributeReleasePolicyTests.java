package org.apereo.cas.support.saml.services;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link GroovySamlRegisteredServiceAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
public class GroovySamlRegisteredServiceAttributeReleasePolicyTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyScriptReleasesSamlAttributes() {
        val filter = new GroovySamlRegisteredServiceAttributeReleasePolicy();
        filter.setGroovyScript("classpath:saml-groovy-attrs.groovy");
        filter.setAllowedAttributes(CollectionUtils.wrapList("uid", "givenName", "displayName"));
        val registeredService = BaseSamlIdPConfigurationTests.getSamlRegisteredServiceForTestShib();
        registeredService.setAttributeReleasePolicy(filter);
        val attributes = filter.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), registeredService);
        Assertions.assertFalse(attributes.isEmpty());
    }
}

