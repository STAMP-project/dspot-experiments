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
package io.helidon.security.examples.signatures;


import io.helidon.common.CollectionsHelper;
import javax.ws.rs.client.Client;
import org.junit.jupiter.api.Test;


/**
 * Actual unit tests are shared by config and builder example.
 */
public abstract class SignatureExampleTest {
    private static Client client;

    private static Client authFeatureClient;

    @Test
    public void testService1Hmac() {
        testProtected((("http://localhost:" + (getService1Port())) + "/service1"), "jack", "password", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf(), "Service1 - HMAC signature");
    }

    @Test
    public void testService1Rsa() {
        testProtected((("http://localhost:" + (getService1Port())) + "/service1-rsa"), "jack", "password", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf(), "Service1 - RSA signature");
    }
}

