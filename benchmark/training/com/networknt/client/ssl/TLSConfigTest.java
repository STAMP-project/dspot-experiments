/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.networknt.client.ssl;


import com.networknt.client.ssl.TLSConfig.InvalidGroupKeyException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static EndpointIdentificationAlgorithm.APIS;
import static EndpointIdentificationAlgorithm.HTTPS;


public class TLSConfigTest {
    private static final Map<String, Object> tlsMap = new HashMap<>();

    private static final String LOCALHOST = "localhost";

    private static final String SOMEHOST = "somehost";

    private static final String EMPTY = "";

    @Test
    public void trusted_names_can_be_properly_resolved() {
        TLSConfig localConfig = TLSConfig.create(TLSConfigTest.tlsMap, "trustedNames.local");
        Assert.assertTrue((((localConfig.getTrustedNameSet().size()) == 1) && (localConfig.getTrustedNameSet().contains(TLSConfigTest.LOCALHOST))));
        Assert.assertTrue(((APIS) == (localConfig.getEndpointIdentificationAlgorithm())));
        TLSConfig group1Config = TLSConfig.create(TLSConfigTest.tlsMap, "trustedNames.groups.group1");
        Assert.assertTrue((((group1Config.getTrustedNameSet().size()) == 1) && (group1Config.getTrustedNameSet().contains(TLSConfigTest.SOMEHOST))));
        Assert.assertTrue(((APIS) == (group1Config.getEndpointIdentificationAlgorithm())));
        TLSConfig group2Config = TLSConfig.create(TLSConfigTest.tlsMap, "trustedNames.groups.group2");
        Assert.assertTrue(group2Config.getTrustedNameSet().isEmpty());
        Assert.assertTrue(((HTTPS) == (group2Config.getEndpointIdentificationAlgorithm())));
    }

    @Test(expected = InvalidGroupKeyException.class)
    public void incomplete_group_key_throws_exception() {
        TLSConfig.create(TLSConfigTest.tlsMap, "trustedNames.groups");
    }

    @Test(expected = InvalidGroupKeyException.class)
    public void nonexisting_group_key_throws_exception() {
        TLSConfig.create(TLSConfigTest.tlsMap, "trustedNames.something");
    }

    @Test
    public void trustedNames_is_optional() {
        Map<String, Object> map = new HashMap<>();
        map.put("verifyHostname", Boolean.TRUE);
        TLSConfig config = TLSConfig.create(map);
        Assert.assertTrue(config.getTrustedNameSet().isEmpty());
        Assert.assertTrue(((HTTPS) == (config.getEndpointIdentificationAlgorithm())));
    }

    @Test
    public void trustedNames_is_not_resolved_if_not_needed() {
        Map<String, Object> map = new HashMap<>();
        map.put("verifyHostname", Boolean.FALSE);
        map.put("trustedNames", TLSConfigTest.LOCALHOST);
        TLSConfig config = TLSConfig.create(map);
        Assert.assertTrue(config.getTrustedNameSet().isEmpty());
        Assert.assertTrue((null == (config.getEndpointIdentificationAlgorithm())));
    }
}

