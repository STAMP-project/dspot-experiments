package org.apereo.cas.support.saml.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlIdPTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * This is {@link MetadataRequestedAttributesAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("FileSystem")
public class MetadataRequestedAttributesAttributeReleasePolicyTests extends BaseSamlIdPConfigurationTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "MetadataRequestedAttributesAttributeReleasePolicyTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyMatch() {
        val filter = new MetadataRequestedAttributesAttributeReleasePolicy();
        filter.setUseFriendlyName(true);
        val registeredService = SamlIdPTestUtils.getSamlRegisteredService();
        registeredService.setAttributeReleasePolicy(filter);
        val attributes = filter.getAttributes(CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("eduPersonPrincipalName", "cas-eduPerson-user")), CoreAuthenticationTestUtils.getService(), registeredService);
        Assertions.assertFalse(attributes.isEmpty());
        Assertions.assertTrue(attributes.containsKey("eduPersonPrincipalName"));
    }

    @Test
    public void verifySerializationToJson() throws IOException {
        val filter = new MetadataRequestedAttributesAttributeReleasePolicy();
        filter.setUseFriendlyName(true);
        MetadataRequestedAttributesAttributeReleasePolicyTests.MAPPER.writeValue(MetadataRequestedAttributesAttributeReleasePolicyTests.JSON_FILE, filter);
        val strategyRead = MetadataRequestedAttributesAttributeReleasePolicyTests.MAPPER.readValue(MetadataRequestedAttributesAttributeReleasePolicyTests.JSON_FILE, MetadataRequestedAttributesAttributeReleasePolicy.class);
        Assertions.assertEquals(filter, strategyRead);
    }
}

