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
package org.apache.camel.component.rest.swagger;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public abstract class HttpsTest extends CamelTestSupport {
    @ClassRule
    public static WireMockRule petstore = new WireMockRule(wireMockConfig().httpServerFactory(new Jetty94ServerFactory()).containerThreads(13).dynamicPort().dynamicHttpsPort().keystorePath(Resources.getResource("localhost.p12").toString()).keystoreType("PKCS12").keystorePassword("password"));

    static final Object NO_BODY = null;

    @Parameterized.Parameter
    public String componentName;

    @Test
    public void shouldBeConfiguredForHttps() throws Exception {
        final Pet pet = template.requestBodyAndHeader("direct:getPetById", HttpsTest.NO_BODY, "petId", 14, Pet.class);
        assertNotNull(pet);
        assertEquals(Integer.valueOf(14), pet.id);
        assertEquals("Olafur Eliason Arnalds", pet.name);
        HttpsTest.petstore.verify(getRequestedFor(urlEqualTo("/v2/pet/14")).withHeader("Accept", equalTo("application/xml, application/json")));
    }
}

