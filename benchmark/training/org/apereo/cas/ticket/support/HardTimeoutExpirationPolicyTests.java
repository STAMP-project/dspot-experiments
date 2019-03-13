package org.apereo.cas.ticket.support;


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
public class HardTimeoutExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "hardTimeoutExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifySerializeANeverExpiresExpirationPolicyToJson() throws IOException {
        val policyWritten = new HardTimeoutExpirationPolicy();
        HardTimeoutExpirationPolicyTests.MAPPER.writeValue(HardTimeoutExpirationPolicyTests.JSON_FILE, policyWritten);
        val policyRead = HardTimeoutExpirationPolicyTests.MAPPER.readValue(HardTimeoutExpirationPolicyTests.JSON_FILE, HardTimeoutExpirationPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

