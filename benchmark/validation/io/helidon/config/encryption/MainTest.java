/**
 * Copyright (c) 2018,2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.encryption;


import EncryptionFilter.PREFIX_AES;
import EncryptionFilter.PREFIX_RSA;
import Main.Algorithm.aes;
import Main.Algorithm.rsa;
import Main.EncryptionCliProcessor;
import io.helidon.common.configurable.Resource;
import io.helidon.common.pki.KeyConfig;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Base64;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test Main class (cli).
 */
public class MainTest {
    @Test
    public void testAesEncryption() {
        String masterPassword = "BigMasterPassowrd!!!";
        String secret = "some secret to encrypt";
        String[] args = new String[]{ "aes", masterPassword, secret };
        Main.EncryptionCliProcessor ecp = new Main.EncryptionCliProcessor();
        ecp.parse(args);
        Assertions.assertEquals(aes, ecp.getAlgorithm());
        Assertions.assertEquals(masterPassword, ecp.getMasterPassword());
        Assertions.assertEquals(secret, ecp.getSecret());
        String encrypted = ecp.encrypt();
        Assertions.assertAll(() -> MatcherAssert.assertThat(("Encrypted string should contain aes prefix: " + encrypted), encrypted.startsWith(PREFIX_AES)), () -> MatcherAssert.assertThat(("Encrypted string should contain suffix \"}\": " + encrypted), encrypted.endsWith("}")));
        String orig = EncryptionUtil.decryptAes(ecp.getMasterPassword().toCharArray(), encrypted.substring(PREFIX_AES.length(), ((encrypted.length()) - 1)));
        Assertions.assertEquals(secret, orig);
        Main.main(args);
    }

    @Test
    public void testRsaEncryption() {
        String keystorePath = "src/test/resources/.ssh/keystore.p12";
        String keystorePass = "j4c";
        String secret = "some secret to encrypt";
        String certAlias = "1";
        String[] args = new String[]{ "rsa", keystorePath, keystorePass, certAlias, secret };
        PrivateKey pk = KeyConfig.keystoreBuilder().keystore(Resource.create(Paths.get(keystorePath))).keyAlias("1").keystorePassphrase(keystorePass.toCharArray()).build().privateKey().orElseThrow(AssertionError::new);
        Main.EncryptionCliProcessor ecp = new Main.EncryptionCliProcessor();
        ecp.parse(args);
        Assertions.assertEquals(rsa, ecp.getAlgorithm());
        Assertions.assertNotNull(ecp.getPublicKey());
        Assertions.assertEquals(secret, ecp.getSecret());
        String encrypted = ecp.encrypt();
        Assertions.assertAll(() -> MatcherAssert.assertThat(("Encrypted string should start with rsa prefix: " + encrypted), encrypted.startsWith(PREFIX_RSA)), () -> MatcherAssert.assertThat(("Encrypted string should end with \"}\": " + encrypted), encrypted.endsWith("}")));
        String base64 = encrypted.substring(PREFIX_RSA.length(), ((encrypted.length()) - 1));
        Base64.getDecoder().decode(base64);
        String orig = EncryptionUtil.decryptRsa(pk, base64);
        Assertions.assertEquals(secret, orig);
        Main.main(args);
    }
}

