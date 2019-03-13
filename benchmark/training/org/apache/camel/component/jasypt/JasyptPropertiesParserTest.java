/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jasypt;


import org.hamcrest.core.Is;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Assert;
import org.junit.Test;


public class JasyptPropertiesParserTest {
    private static final String KEY = "somekey";

    private static final String KNOWN_PASSWORD = "secret";

    private static final String KNOWN_ENCRYPTED = "ENC(bsW9uV37gQ0QHFu7KO03Ww==)";

    private static final String KNOW_DECRYPTED = "tiger";

    private JasyptPropertiesParser jasyptPropertiesParser = new JasyptPropertiesParser();

    private StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    @Test
    public void testNullPropertyIsUntouched() {
        String expected = null;
        String result = jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, expected, null);
        Assert.assertThat(result, Is.is(expected));
    }

    @Test
    public void testPlainPropertyIsUntouched() {
        String expected = "http://somehost?1=someval1&2=someval2";
        String result = jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, expected, null);
        Assert.assertThat(result, Is.is(expected));
    }

    @Test
    public void testDecryptsEncryptedProperty() {
        String decrypted = "tiger";
        String encrypted = String.format("%s%s%s", JasyptPropertiesParser.JASYPT_PREFIX_TOKEN, encryptor.encrypt(decrypted), JasyptPropertiesParser.JASYPT_SUFFIX_TOKEN);
        String result = jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, encrypted, null);
        Assert.assertThat(result, Is.is(decrypted));
    }

    @Test
    public void testDecryptsPartiallyEncryptedProperty() {
        String parmValue = "tiger";
        String encParmValue = String.format("%s%s%s", JasyptPropertiesParser.JASYPT_PREFIX_TOKEN, encryptor.encrypt(parmValue), JasyptPropertiesParser.JASYPT_SUFFIX_TOKEN);
        String expected = String.format("http://somehost:port/?param1=%s&param2=somethingelse", parmValue);
        String propertyValue = String.format("http://somehost:port/?param1=%s&param2=somethingelse", encParmValue);
        String result = jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, propertyValue, null);
        Assert.assertThat(result, Is.is(expected));
    }

    @Test
    public void testDecryptsMultitplePartsOfPartiallyEncryptedProperty() {
        StringBuilder propertyValue = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            propertyValue.append(String.format("param%s=%s%s%s()&", i, JasyptPropertiesParser.JASYPT_PREFIX_TOKEN, encryptor.encrypt(("tiger" + i)), JasyptPropertiesParser.JASYPT_SUFFIX_TOKEN));
            expected.append(String.format("param%s=tiger%s()&", i, i));
        }
        String result = jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, propertyValue.toString(), null);
        Assert.assertThat(result, Is.is(expected.toString()));
    }

    @Test
    public void testUsesProvidedPasswordIfEncryptorIsNotSet() throws Exception {
        jasyptPropertiesParser.setEncryptor(null);
        jasyptPropertiesParser.setPassword(JasyptPropertiesParserTest.KNOWN_PASSWORD);
        Assert.assertEquals(JasyptPropertiesParserTest.KNOW_DECRYPTED, jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, JasyptPropertiesParserTest.KNOWN_ENCRYPTED, null));
    }

    @Test
    public void testUsesProvidedPasswordFromSystemPropertyIfEncryptorIsNotSet() throws Exception {
        System.setProperty("myfoo", JasyptPropertiesParserTest.KNOWN_PASSWORD);
        jasyptPropertiesParser.setEncryptor(null);
        jasyptPropertiesParser.setPassword("sys:myfoo");
        Assert.assertEquals(JasyptPropertiesParserTest.KNOW_DECRYPTED, jasyptPropertiesParser.parseProperty(JasyptPropertiesParserTest.KEY, JasyptPropertiesParserTest.KNOWN_ENCRYPTED, null));
        System.clearProperty("myfoo");
    }
}

