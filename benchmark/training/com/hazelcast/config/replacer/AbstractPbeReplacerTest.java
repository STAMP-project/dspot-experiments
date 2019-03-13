/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config.replacer;


import AbstractPbeReplacer.PROPERTY_CIPHER_ALGORITHM;
import AbstractPbeReplacer.PROPERTY_KEY_LENGTH_BITS;
import AbstractPbeReplacer.PROPERTY_SALT_LENGTH_BYTES;
import AbstractPbeReplacer.PROPERTY_SECRET_KEY_ALGORITHM;
import AbstractPbeReplacer.PROPERTY_SECRET_KEY_FACTORY_ALGORITHM;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Unit tests for {@link AbstractPbeReplacer}.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractPbeReplacerTest {
    @Test
    public void testDefaultEncryptDecrypt() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer("test", new Properties());
        assertReplacerWorks(replacer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptionFailWithEmptyPassword() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer("", new Properties());
        replacer.encrypt("test", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptionFailWithNullPassword() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer(null, new Properties());
        replacer.encrypt("test", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecryptionFailWithEmptyPassword() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer("", new Properties());
        replacer.decrypt("aSalt1xx:1:test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecryptionFailWithNullPassword() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer(null, new Properties());
        replacer.decrypt("aSalt1xx:1:test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySalt() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_SALT_LENGTH_BYTES, "0");
        createAndInitReplacer(null, properties);
    }

    @Test
    public void testMinimalSaltLength() throws Exception {
        assumeDefaultAlgorithmsSupported();
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_SALT_LENGTH_BYTES, "1");
        AbstractPbeReplacer replacer = createAndInitReplacer("test", properties);
        assertReplacerWorks(replacer);
    }

    @Test
    public void testLegacyCipher() throws Exception {
        assumeAlgorithmsSupported("PBKDF2WithHmacSHA1", "DES");
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_KEY_LENGTH_BITS, "64");
        properties.setProperty(PROPERTY_SALT_LENGTH_BYTES, "8");
        properties.setProperty(PROPERTY_CIPHER_ALGORITHM, "DES");
        properties.setProperty(PROPERTY_SECRET_KEY_FACTORY_ALGORITHM, "PBKDF2WithHmacSHA1");
        properties.setProperty(PROPERTY_SECRET_KEY_ALGORITHM, "DES");
        AbstractPbeReplacer replacer = createAndInitReplacer("This is a password", properties);
        assertReplacerWorks(replacer);
    }

    @Test
    public void testWrongVariables() throws Exception {
        assumeDefaultAlgorithmsSupported();
        AbstractPbeReplacer replacer = createAndInitReplacer("test", new Properties());
        Assert.assertNull(replacer.getReplacement(null));
        Assert.assertNull(replacer.getReplacement("WronglyFormatted"));
        Assert.assertNull(replacer.getReplacement("aSalt:1:EncryptedValue"));
        // following incorrect value was generated using replacer.encrypt("test", 777).replace(":777:", ":1:");
        Assert.assertNull("Null value expected as javax.crypto.BadPaddingException should be thrown in the AbstractPbeReplacer", replacer.getReplacement("IVJXCMo0XBE=:1:NnZjBhX7sB/IT0sTFZ2eIA=="));
    }

    private static class TestReplacer extends AbstractPbeReplacer {
        private final char[] password;

        private TestReplacer(char[] password) {
            this.password = password;
        }

        public String getPrefix() {
            return "TEST";
        }

        @Override
        protected char[] getPassword() {
            return password;
        }
    }
}

