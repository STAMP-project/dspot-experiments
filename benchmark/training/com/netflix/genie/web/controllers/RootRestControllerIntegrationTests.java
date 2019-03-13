/**
 * Copyright 2015 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.controllers;


import HttpStatus.OK;
import MediaTypes.HAL_JSON_UTF8_VALUE;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Integration tests for the Root REST API.
 *
 * @author tgianos
 * @since 3.0.0
 */
public class RootRestControllerIntegrationTests extends RestControllerIntegrationTestsBase {
    /**
     * Make sure we can get the root resource.
     */
    @Test
    public void canGetRootResource() {
        RestAssured.given(getRequestSpecification()).when().port(this.port).get("/api/v3").then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("content.description", Matchers.notNullValue()).body(((RestControllerIntegrationTestsBase.LINKS_PATH) + ".keySet().size()"), Matchers.is(5)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.SELF_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.APPLICATIONS_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.CLUSTERS_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.JOBS_LINK_KEY));
    }
}

