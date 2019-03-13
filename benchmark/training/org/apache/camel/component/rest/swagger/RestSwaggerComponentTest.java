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
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RestSwaggerComponentTest extends CamelTestSupport {
    @ClassRule
    public static WireMockRule petstore = new WireMockRule(wireMockConfig().dynamicPort());

    static final Object NO_BODY = null;

    @Parameterized.Parameter
    public String componentName;

    @Test
    public void shouldBeAddingPets() {
        final Pet pet = new Pet();
        pet.name = "Jean-Luc Picard";
        final Pet created = template.requestBody("direct:addPet", pet, Pet.class);
        assertNotNull(created);
        assertEquals(Integer.valueOf(14), created.id);
        RestSwaggerComponentTest.petstore.verify(postRequestedFor(urlEqualTo("/v2/pet")).withHeader("Accept", equalTo("application/xml, application/json")).withHeader("Content-Type", equalTo("application/xml")));
    }

    @Test
    public void shouldBeGettingPetsById() {
        final Pet pet = template.requestBodyAndHeader("direct:getPetById", RestSwaggerComponentTest.NO_BODY, "petId", 14, Pet.class);
        assertNotNull(pet);
        assertEquals(Integer.valueOf(14), pet.id);
        assertEquals("Olafur Eliason Arnalds", pet.name);
        RestSwaggerComponentTest.petstore.verify(getRequestedFor(urlEqualTo("/v2/pet/14")).withHeader("Accept", equalTo("application/xml, application/json")));
    }

    @Test
    public void shouldBeGettingPetsByIdSpecifiedInEndpointParameters() {
        final Pet pet = template.requestBody("direct:getPetByIdWithEndpointParams", RestSwaggerComponentTest.NO_BODY, Pet.class);
        assertNotNull(pet);
        assertEquals(Integer.valueOf(14), pet.id);
        assertEquals("Olafur Eliason Arnalds", pet.name);
        RestSwaggerComponentTest.petstore.verify(getRequestedFor(urlEqualTo("/v2/pet/14")).withHeader("Accept", equalTo("application/xml, application/json")));
    }

    @Test
    public void shouldBeGettingPetsByStatus() {
        final Pets pets = template.requestBodyAndHeader("direct:findPetsByStatus", RestSwaggerComponentTest.NO_BODY, "status", "available", Pets.class);
        assertNotNull(pets);
        assertNotNull(pets.pets);
        assertEquals(2, pets.pets.size());
        RestSwaggerComponentTest.petstore.verify(getRequestedFor(urlPathEqualTo("/v2/pet/findByStatus")).withQueryParam("status", equalTo("available")).withHeader("Accept", equalTo("application/xml, application/json")));
    }
}

