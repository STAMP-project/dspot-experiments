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
public class ReturnAllAttributeReleasePolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "returnAllAttributeReleasePolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifySerializeAReturnAllAttributeReleasePolicyToJson() throws IOException {
        val policyWritten = new ReturnAllAttributeReleasePolicy();
        ReturnAllAttributeReleasePolicyTests.MAPPER.writeValue(ReturnAllAttributeReleasePolicyTests.JSON_FILE, policyWritten);
        val policyRead = ReturnAllAttributeReleasePolicyTests.MAPPER.readValue(ReturnAllAttributeReleasePolicyTests.JSON_FILE, ReturnAllAttributeReleasePolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

