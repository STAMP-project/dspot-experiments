package org.apereo.cas.configuration.support;


import CasConfigurationJasyptCipherExecutor.JasyptEncryptionParameters.PASSWORD;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.env.Environment;

import static CasConfigurationJasyptCipherExecutor.ENCRYPTED_VALUE_PREFIX;


/**
 * This is {@link CasConfigurationJasyptCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
public class CasConfigurationJasyptCipherExecutorTests {
    static {
        System.setProperty(PASSWORD.getPropertyName(), "P@$$w0rd");
    }

    @Autowired
    private Environment environment;

    private CasConfigurationJasyptCipherExecutor jasypt;

    @Test
    public void verifyDecryptionEncryption() {
        val result = jasypt.encryptValue(getClass().getSimpleName());
        Assertions.assertNotNull(result);
        val plain = jasypt.decryptValue(result);
        Assertions.assertEquals(plain, getClass().getSimpleName());
    }

    @Test
    public void verifyDecryptionEncryptionPairNotNeeded() {
        val result = jasypt.decryptValue("keyValue");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("keyValue", result);
    }

    @Test
    public void verifyDecryptionEncryptionPairFails() {
        val encVal = (ENCRYPTED_VALUE_PREFIX) + "keyValue";
        val result = jasypt.decode(encVal, ArrayUtils.EMPTY_OBJECT_ARRAY);
        Assertions.assertNull(result);
    }

    @Test
    public void verifyDecryptionEncryptionPairSuccess() {
        val value = jasypt.encryptValue("Testing");
        val result = jasypt.decode(value, ArrayUtils.EMPTY_OBJECT_ARRAY);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Testing", result);
    }
}

