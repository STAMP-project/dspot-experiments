package org.apereo.cas.authentication.support;


import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import lombok.val;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.ProtocolAttributeEncoder;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link DefaultCasProtocolAttributeEncoderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class DefaultCasProtocolAttributeEncoderTests {
    private final ProtocolAttributeEncoder encoder = new DefaultCasProtocolAttributeEncoder(Mockito.mock(ServicesManager.class), CipherExecutor.noOpOfStringToString());

    private RegisteredService registeredService;

    @Test
    public void verifyEncodeNamesCorrectly() {
        val attributes = new LinkedHashMap<String, Object>();
        attributes.put("user@name", "casuser");
        attributes.put("user:name", "casuser");
        val results = encoder.encodeAttributes(attributes, registeredService);
        Assertions.assertFalse(results.containsKey("user@name"));
        Assertions.assertFalse(results.containsKey("user:name"));
    }

    @Test
    public void verifyEncodeBinaryValuesCorrectly() {
        val attributes = new LinkedHashMap<String, Object>();
        attributes.put("user", "casuser".getBytes(StandardCharsets.UTF_8));
        val results = encoder.encodeAttributes(attributes, registeredService);
        Assertions.assertTrue(results.containsKey("user"));
        val user = results.get("user");
        Assertions.assertTrue(user.getClass().isAssignableFrom(String.class));
    }
}

