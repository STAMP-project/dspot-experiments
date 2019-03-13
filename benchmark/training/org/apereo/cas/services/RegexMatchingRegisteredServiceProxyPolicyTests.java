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
 * @since 4.0.0
 */
public class RegexMatchingRegisteredServiceProxyPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "regexMatchingRegisteredServiceProxyPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeARegexMatchingRegisteredServiceProxyPolicyToJson() throws IOException {
        val policyWritten = new RegexMatchingRegisteredServiceProxyPolicy("pattern");
        RegexMatchingRegisteredServiceProxyPolicyTests.MAPPER.writeValue(RegexMatchingRegisteredServiceProxyPolicyTests.JSON_FILE, policyWritten);
        val policyRead = RegexMatchingRegisteredServiceProxyPolicyTests.MAPPER.readValue(RegexMatchingRegisteredServiceProxyPolicyTests.JSON_FILE, RegexMatchingRegisteredServiceProxyPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

