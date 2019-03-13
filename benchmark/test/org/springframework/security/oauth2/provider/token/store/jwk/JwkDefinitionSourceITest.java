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


import JwkDefinition.CryptoAlgorithm.ES256;
import JwkDefinition.CryptoAlgorithm.RS256;
import JwkDefinition.KeyType.EC;
import JwkDefinition.KeyType.RSA;
import JwkDefinition.PublicKeyUse.SIG;
import MediaType.APPLICATION_JSON_VALUE;
import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Winch
 */
public class JwkDefinitionSourceITest {
    private MockWebServer server;

    private JwkDefinitionSource source;

    @Test
    public void getDefinitionLoadIfNecessaryWhenMultipleUrlsThenBothUrlsAreLoaded() {
        this.server.enqueue(new MockResponse().setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE).setBody(("{\n" + (((((((((("    \"keys\": [\n" + "        {\n") + "            \"kid\": \"key-id-1\",\n") + "            \"kty\": \"RSA\",\n") + "            \"alg\": \"RS256\",\n") + "            \"use\": \"sig\",\n") + "            \"n\": \"rne3dowbQHcFCzg2ejWb6az5QNxWFiv6kRpd34VDzYNMhWeewfeEL5Pf5clE8Xh1KlllrDYSxtnzUQm-t9p92yEBASfV96ydTYG-ITfxfJzKtJUN-iIS5K9WGYXnDNS4eYZ_ygW-zBU_9NwFMXdwSTzRqHeJmLJrfbmmjoIuuWyfh2Ko52KzyidceR5SJxGeW0ckeyWka1lDf4cr7fv-s093Y_sd2wrNvg0-9IAkXotbxWWXcfMgXFyw0qHFT_5LrKmiwkY3HCaiV5NgEFJmC6fBIG2EOZG4rqjBoYV6LZwrfTMHknaeel9MOZesW6SR2bswtuuWN3DGq2zg0KamLw\",\n") + "            \"e\": \"AQAB\"\n") + "        }\n") + "    ]\n") + "}\n"))));
        this.server.enqueue(new MockResponse().setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE).setBody(("{\n" + ((((((((((((((((((("    \"keys\": [\n" + "        {\n") + "            \"kid\": \"key-id-2\",\n") + "            \"kty\": \"RSA\",\n") + "            \"alg\": \"RS256\",\n") + "            \"use\": \"sig\",\n") + "            \"n\": \"t6Q8PWSi1dkJj9hTP8hNYFlvadM7DflW9mWepOJhJ66w7nyoK1gPNqFMSQRyO125Gp-TEkodhWr0iujjHVx7BcV0llS4w5ACGgPrcAd6ZcSR0-Iqom-QFcNP8Sjg086MwoqQU_LYywlAGZ21WSdS_PERyGFiNnj3QQlO8Yns5jCtLCRwLHL0Pb1fEv45AuRIuUfVcPySBWYnDyGxvjYGDSM-AqWS9zIQ2ZilgT-GqUmipg0XOC0Cc20rgLe2ymLHjpHciCKVAbY5-L32-lSeZO-Os6U15_aXrk9Gw8cPUaX1_I8sLGuSiVdt3C_Fn2PZ3Z8i744FPFGGcG1qs2Wz-Q\",\n") + "            \"e\": \"AQAB\"\n") + "        },\n") + "        {\n") + "            \"kid\": \"key-id-3\",\n") + "            \"kty\": \"EC\",\n") + "            \"alg\": \"ES256\",\n") + "            \"use\": \"sig\",\n") + "            \"x\": \"IsxeG33-QlL2u-O38QKwAbw5tJTZ-jtMVSlzjNXhvys\",\n") + "            \"y\": \"FPTFJF1M0sNRlOVZIH4e1DoZ_hdg1OvF6BlP2QHmSCg\",\n") + "            \"crv\": \"P-256\"\n") + "        }\n") + "    ]\n") + "}\n"))));
        this.source = new JwkDefinitionSource(Arrays.asList(serverUrl("/jwk1"), serverUrl("/jkw2")));
        String keyId1 = "key-id-1";
        String keyId2 = "key-id-2";
        String keyId3 = "key-id-3";
        JwkDefinition jwkDef1 = this.source.getDefinitionLoadIfNecessary(keyId1).getJwkDefinition();
        JwkDefinition jwkDef2 = this.source.getDefinitionLoadIfNecessary(keyId2).getJwkDefinition();
        JwkDefinition jwkDef3 = this.source.getDefinitionLoadIfNecessary(keyId3).getJwkDefinition();
        Assert.assertEquals(jwkDef1.getKeyId(), keyId1);
        Assert.assertEquals(jwkDef1.getAlgorithm(), RS256);
        Assert.assertEquals(jwkDef1.getPublicKeyUse(), SIG);
        Assert.assertEquals(jwkDef1.getKeyType(), RSA);
        Assert.assertEquals(jwkDef2.getKeyId(), keyId2);
        Assert.assertEquals(jwkDef2.getAlgorithm(), RS256);
        Assert.assertEquals(jwkDef2.getPublicKeyUse(), SIG);
        Assert.assertEquals(jwkDef2.getKeyType(), RSA);
        Assert.assertEquals(jwkDef3.getKeyId(), keyId3);
        Assert.assertEquals(jwkDef3.getAlgorithm(), ES256);
        Assert.assertEquals(jwkDef3.getPublicKeyUse(), SIG);
        Assert.assertEquals(jwkDef3.getKeyType(), EC);
    }
}

