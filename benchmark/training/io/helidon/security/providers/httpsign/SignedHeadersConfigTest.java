/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.providers.httpsign;


import io.helidon.config.Config;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link SignedHeadersConfig}.
 */
public class SignedHeadersConfigTest {
    private static Config config;

    @Test
    public void testFromConfig() {
        SignedHeadersConfig shc = SignedHeadersConfigTest.config.get("0.http-signatures.sign-headers").as(SignedHeadersConfig.class).get();
        testThem(shc);
    }

    @Test
    public void testFromBuilder() {
        SignedHeadersConfig shc = SignedHeadersConfig.builder().defaultConfig(SignedHeadersConfig.HeadersConfig.create(io.helidon.common.CollectionsHelper.listOf("date"))).config("get", SignedHeadersConfig.HeadersConfig.create(io.helidon.common.CollectionsHelper.listOf("date", SignedHeadersConfig.REQUEST_TARGET, "host"), io.helidon.common.CollectionsHelper.listOf("authorization"))).build();
        testThem(shc);
    }
}

