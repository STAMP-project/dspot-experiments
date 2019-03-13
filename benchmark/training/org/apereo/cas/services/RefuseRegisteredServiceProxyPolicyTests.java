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
 * @since 4.1
 */
public class RefuseRegisteredServiceProxyPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "refuseRegisteredServiceProxyPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeARefuseRegisteredServiceProxyPolicyToJson() throws IOException {
        val policyWritten = new RefuseRegisteredServiceProxyPolicy();
        RefuseRegisteredServiceProxyPolicyTests.MAPPER.writeValue(RefuseRegisteredServiceProxyPolicyTests.JSON_FILE, policyWritten);
        val policyRead = RefuseRegisteredServiceProxyPolicyTests.MAPPER.readValue(RefuseRegisteredServiceProxyPolicyTests.JSON_FILE, RefuseRegisteredServiceProxyPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

