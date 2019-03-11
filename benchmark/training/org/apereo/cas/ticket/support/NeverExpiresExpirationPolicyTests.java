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
public class NeverExpiresExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "neverExpiresExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeANeverExpiresExpirationPolicyToJson() throws IOException {
        val policyWritten = new NeverExpiresExpirationPolicy();
        NeverExpiresExpirationPolicyTests.MAPPER.writeValue(NeverExpiresExpirationPolicyTests.JSON_FILE, policyWritten);
        val policyRead = NeverExpiresExpirationPolicyTests.MAPPER.readValue(NeverExpiresExpirationPolicyTests.JSON_FILE, NeverExpiresExpirationPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

