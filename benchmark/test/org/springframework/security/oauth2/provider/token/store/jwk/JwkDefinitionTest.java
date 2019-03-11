/**
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.token.store.jwk;


import JwkDefinition.CryptoAlgorithm;
import JwkDefinition.CryptoAlgorithm.RS256;
import JwkDefinition.CryptoAlgorithm.RS384;
import JwkDefinition.CryptoAlgorithm.RS512;
import JwkDefinition.KeyType;
import JwkDefinition.PublicKeyUse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joe Grandja
 */
public class JwkDefinitionTest {
    @Test
    public void constructorWhenArgumentsPassedThenAttributesAreCorrectlySet() throws Exception {
        String keyId = "key-id-1";
        JwkDefinition.KeyType keyType = KeyType.RSA;
        JwkDefinition.PublicKeyUse publicKeyUse = PublicKeyUse.SIG;
        JwkDefinition.CryptoAlgorithm algorithm = CryptoAlgorithm.RS512;
        JwkDefinition jwkDefinition = new JwkDefinition(keyId, keyType, publicKeyUse, algorithm) {};
        Assert.assertEquals(keyId, jwkDefinition.getKeyId());
        Assert.assertEquals(keyType, jwkDefinition.getKeyType());
        Assert.assertEquals(publicKeyUse, jwkDefinition.getPublicKeyUse());
        Assert.assertEquals(algorithm, jwkDefinition.getAlgorithm());
    }

    @Test
    public void cryptoAlgorithmWhenAttributesAccessedThenCorrectValuesReturned() {
        Assert.assertEquals("RS256", RS256.headerParamValue());
        Assert.assertEquals("SHA256withRSA", RS256.standardName());
        Assert.assertEquals("RS384", RS384.headerParamValue());
        Assert.assertEquals("SHA384withRSA", RS384.standardName());
        Assert.assertEquals("RS512", RS512.headerParamValue());
        Assert.assertEquals("SHA512withRSA", RS512.standardName());
    }
}

