/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class CryptoTest {
    @Mock
    private NinjaProperties ninjaProperties;

    private Crypto crypto;

    @Test
    public void testSigning() {
        Assert.assertEquals("f0f591a35650937c9559ee8f98cc29dac46c3fcb", crypto.signHmacSha1("Sentence to sign"));
        Assert.assertEquals("ba864c24a2a80a639d4f76bb44fd71650dcd4904", crypto.signHmacSha1("Another sentence to sign"));
        Assert.assertEquals("4ad5fb0895dbc0c7172f9fc85d59f74b69f99b8b", crypto.signHmacSha1("Yet another sentence to sign"));
    }
}

