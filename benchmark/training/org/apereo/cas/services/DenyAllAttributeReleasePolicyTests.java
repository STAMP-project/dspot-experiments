package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class DenyAllAttributeReleasePolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "denyAllAttributeReleasePolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeADenyAllAttributeReleasePolicyToJson() throws IOException {
        val policyWritten = new DenyAllAttributeReleasePolicy();
        DenyAllAttributeReleasePolicyTests.MAPPER.writeValue(DenyAllAttributeReleasePolicyTests.JSON_FILE, policyWritten);
        val policyRead = DenyAllAttributeReleasePolicyTests.MAPPER.readValue(DenyAllAttributeReleasePolicyTests.JSON_FILE, DenyAllAttributeReleasePolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

