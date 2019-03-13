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
 * This is {@link InCommonRSAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("FileSystem")
public class InCommonRSAttributeReleasePolicyTests extends BaseSamlIdPConfigurationTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "InCommonRSAttributeReleasePolicyTests.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyMatch() {
        val filter = new InCommonRSAttributeReleasePolicy();
        val registeredService = SamlIdPTestUtils.getSamlRegisteredService();
        registeredService.setAttributeReleasePolicy(filter);
        val attributes = filter.getAttributes(CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("eduPersonPrincipalName", "cas-eduPerson-user")), CoreAuthenticationTestUtils.getService(), registeredService);
        Assertions.assertFalse(attributes.isEmpty());
        Assertions.assertTrue(attributes.containsKey("eduPersonPrincipalName"));
    }

    @Test
    public void verifySerializationToJson() throws IOException {
        val filter = new InCommonRSAttributeReleasePolicy();
        InCommonRSAttributeReleasePolicyTests.MAPPER.writeValue(InCommonRSAttributeReleasePolicyTests.JSON_FILE, filter);
        val strategyRead = InCommonRSAttributeReleasePolicyTests.MAPPER.readValue(InCommonRSAttributeReleasePolicyTests.JSON_FILE, InCommonRSAttributeReleasePolicy.class);
        Assertions.assertEquals(filter, strategyRead);
    }
}

