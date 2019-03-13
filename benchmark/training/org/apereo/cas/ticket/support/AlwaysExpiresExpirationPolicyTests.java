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
 * @since 3.0
 */
public class AlwaysExpiresExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "alwaysExpiresExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeAnAlwaysExpiresExpirationPolicyToJson() throws IOException {
        val policyWritten = new AlwaysExpiresExpirationPolicy();
        AlwaysExpiresExpirationPolicyTests.MAPPER.writeValue(AlwaysExpiresExpirationPolicyTests.JSON_FILE, policyWritten);
        val policyRead = AlwaysExpiresExpirationPolicyTests.MAPPER.readValue(AlwaysExpiresExpirationPolicyTests.JSON_FILE, AlwaysExpiresExpirationPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

