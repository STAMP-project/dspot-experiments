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


import Application.Builder;
import ApplicationStatus.ACTIVE;
import ApplicationStatus.DEPRECATED;
import ApplicationStatus.INACTIVE;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.PRECONDITION_FAILED;
import MediaType.APPLICATION_JSON_VALUE;
import MediaTypes.HAL_JSON_UTF8_VALUE;
import com.github.fge.jsonpatch.JsonPatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.Application;
import com.netflix.genie.common.dto.ApplicationStatus;
import com.netflix.genie.common.dto.Command;
import com.netflix.genie.common.dto.CommandStatus;
import com.netflix.genie.common.util.GenieObjectMapper;
import com.netflix.genie.web.hateoas.resources.ApplicationResource;
import io.restassured.RestAssured;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import org.springframework.restdocs.snippet.Attributes;

import static com.netflix.genie.web.controllers.RestControllerIntegrationTestsBase.EntitiesLinksMatcher.matchUrisAnyOrder;
import static com.netflix.genie.web.controllers.RestControllerIntegrationTestsBase.EntityLinkMatcher.matchUri;


/**
 * Integration tests for the Applications REST API.
 *
 * @author tgianos
 * @since 3.0.0
 */
// TODO: Add tests for error conditions
public class ApplicationRestControllerIntegrationTests extends RestControllerIntegrationTestsBase {
    // Use a `.` to ensure that the Spring prefix matcher is turned off
    // see: https://tinyurl.com/yblzglk8
    private static final String ID = ((UUID.randomUUID().toString()) + ".") + (UUID.randomUUID().toString());

    private static final String NAME = "spark";

    private static final String USER = "genie";

    private static final String VERSION = "1.5.1";

    private static final String TYPE = "spark";

    private static final String TYPE_PATH = "type";

    private static final String APPLICATIONS_LIST_PATH = (RestControllerIntegrationTestsBase.EMBEDDED_PATH) + ".applicationList";

    private static final String APPLICATIONS_ID_LIST_PATH = (ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + ".id";

    private static final String APPLICATION_COMMANDS_LINK_PATH = "_links.commands.href";

    private static final String APPLICATIONS_COMMANDS_LINK_PATH = (ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "._links.commands.href";

    /**
     * Test creating an application without an ID.
     *
     * @throws Exception
     * 		on configuration issue
     */
    @Test
    public void canCreateApplicationWithoutId() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final RestDocumentationFilter creationResultFilter = // Request headers
        // Request fields
        // Response headers
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.getApplicationRequestPayload(), Snippets.LOCATION_HEADER);
        final String id = this.createConfigResource(withType(ApplicationRestControllerIntegrationTests.TYPE).withDependencies(Sets.newHashSet((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark.tar.gz"))).withSetupFile((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/setup-spark.sh")).withConfigs(Sets.newHashSet((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark-env.sh"))).withDescription("Spark for Genie").withTags(Sets.newHashSet(("type:" + (ApplicationRestControllerIntegrationTests.TYPE)), ("ver:" + (ApplicationRestControllerIntegrationTests.VERSION)))).build(), creationResultFilter);
        final RestDocumentationFilter getResultFilter = // path parameters
        // response headers
        // response payload
        // response links
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.HAL_CONTENT_TYPE_HEADER, Snippets.getApplicationResponsePayload(), Snippets.APPLICATION_LINKS);
        RestAssured.given(getRequestSpecification()).filter(getResultFilter).when().port(this.port).get(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}"), id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(id)).body(RestControllerIntegrationTestsBase.UPDATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.CREATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.NAME)).body(RestControllerIntegrationTestsBase.VERSION_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.VERSION)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.USER)).body(RestControllerIntegrationTestsBase.DESCRIPTION_PATH, Matchers.is("Spark for Genie")).body(RestControllerIntegrationTestsBase.SETUP_FILE_PATH, Matchers.is((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/setup-spark.sh"))).body(RestControllerIntegrationTestsBase.CONFIGS_PATH, Matchers.hasItem((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark-env.sh"))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasSize(4)).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.id:" + id))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.name:" + (ApplicationRestControllerIntegrationTests.NAME)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("ver:" + (ApplicationRestControllerIntegrationTests.VERSION)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("type:" + (ApplicationRestControllerIntegrationTests.TYPE)))).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(ACTIVE.toString())).body(RestControllerIntegrationTestsBase.DEPENDENCIES_PATH, Matchers.hasItem((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark.tar.gz"))).body(ApplicationRestControllerIntegrationTests.TYPE_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.TYPE)).body(((RestControllerIntegrationTestsBase.LINKS_PATH) + ".keySet().size()"), Matchers.is(2)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.SELF_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY)).body(ApplicationRestControllerIntegrationTests.APPLICATION_COMMANDS_LINK_PATH, matchUri(RestControllerIntegrationTestsBase.APPLICATIONS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, id));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(1L));
    }

    /**
     * Test creating an application with an ID.
     *
     * @throws Exception
     * 		When issue in creation
     */
    @Test
    public void canCreateApplicationWithId() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(withType(ApplicationRestControllerIntegrationTests.TYPE).withDependencies(Sets.newHashSet((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark.tar.gz"))).withSetupFile((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/setup-spark.sh")).withConfigs(Sets.newHashSet((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark-env.sh"))).withDescription("Spark for Genie").withTags(Sets.newHashSet(("type:" + (ApplicationRestControllerIntegrationTests.TYPE)), ("ver:" + (ApplicationRestControllerIntegrationTests.VERSION)))).build(), null);
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}"), ApplicationRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.ID)).body(RestControllerIntegrationTestsBase.UPDATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.CREATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.NAME)).body(RestControllerIntegrationTestsBase.VERSION_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.VERSION)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.USER)).body(RestControllerIntegrationTestsBase.DESCRIPTION_PATH, Matchers.is("Spark for Genie")).body(RestControllerIntegrationTestsBase.SETUP_FILE_PATH, Matchers.is((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/setup-spark.sh"))).body(RestControllerIntegrationTestsBase.CONFIGS_PATH, Matchers.hasItem((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark-env.sh"))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasSize(4)).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.id:" + (ApplicationRestControllerIntegrationTests.ID)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.name:" + (ApplicationRestControllerIntegrationTests.NAME)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("ver:" + (ApplicationRestControllerIntegrationTests.VERSION)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("type:" + (ApplicationRestControllerIntegrationTests.TYPE)))).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(ACTIVE.toString())).body(RestControllerIntegrationTestsBase.DEPENDENCIES_PATH, Matchers.hasItem((("s3://mybucket/spark/" + (ApplicationRestControllerIntegrationTests.VERSION)) + "/spark.tar.gz"))).body(ApplicationRestControllerIntegrationTests.TYPE_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.TYPE)).body(((RestControllerIntegrationTestsBase.LINKS_PATH) + ".keySet().size()"), Matchers.is(2)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.SELF_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY)).body(ApplicationRestControllerIntegrationTests.APPLICATION_COMMANDS_LINK_PATH, matchUri(RestControllerIntegrationTestsBase.APPLICATIONS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, ApplicationRestControllerIntegrationTests.ID));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(1L));
    }

    /**
     * Test to make sure the post API can handle bad input.
     *
     * @throws Exception
     * 		on issue
     */
    @Test
    public void canHandleBadInputToCreateApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final Application app = build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(app)).when().port(this.port).post(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
    }

    /**
     * Test to make sure that you can search for applications by various parameters.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canFindApplications() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final Application spark151 = build();
        final Application spark150 = build();
        final Application spark141 = build();
        final Application spark140 = build();
        final Application spark131 = build();
        final Application pig = build();
        final Application hive = build();
        final String spark151Id = this.createConfigResource(spark151, null);
        final String spark150Id = this.createConfigResource(spark150, null);
        final String spark141Id = this.createConfigResource(spark141, null);
        final String spark140Id = this.createConfigResource(spark140, null);
        final String spark131Id = this.createConfigResource(spark131, null);
        final String pigId = this.createConfigResource(pig, null);
        final String hiveId = this.createConfigResource(hive, null);
        final List<String> appIds = Lists.newArrayList(spark151Id, spark150Id, spark141Id, spark140Id, spark131Id, pigId, hiveId);
        final RestDocumentationFilter findFilter = // Request query parameters
        // Response headers
        // Result fields
        // HAL Links
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.APPLICATION_SEARCH_QUERY_PARAMETERS, Snippets.HAL_CONTENT_TYPE_HEADER, Snippets.APPLICATION_SEARCH_RESULT_FIELDS, Snippets.SEARCH_LINKS);
        // Test finding all applications
        RestAssured.given(getRequestSpecification()).filter(findFilter).when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(7)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_ID_LIST_PATH, Matchers.hasSize(7)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_ID_LIST_PATH, Matchers.containsInAnyOrder(spark151Id, spark150Id, spark141Id, spark140Id, spark131Id, pigId, hiveId)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_COMMANDS_LINK_PATH, matchUrisAnyOrder(RestControllerIntegrationTestsBase.APPLICATIONS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, appIds));
        // Limit the size
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("size", 2).when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(2));
        // Query by name
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("name", "hive").when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(1)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[0].id"), Matchers.is(hiveId));
        // Query by user
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("user", "genieUser3").when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(1)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[0].id"), Matchers.is(spark141Id));
        // Query by statuses
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("status", ACTIVE.toString(), DEPRECATED.toString()).when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(6)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[0].id"), Matchers.is(hiveId)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[1].id"), Matchers.is(pigId)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[2].id"), Matchers.is(spark131Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[3].id"), Matchers.is(spark140Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[4].id"), Matchers.is(spark150Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[5].id"), Matchers.is(spark151Id));
        // Query by tags
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("tag", ("genie.id:" + spark131Id)).when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(1)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[0].id"), Matchers.is(spark131Id));
        // Query by type
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("type", "spark").when().port(this.port).get(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH, Matchers.hasSize(5)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[0].id"), Matchers.is(spark131Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[1].id"), Matchers.is(spark140Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[2].id"), Matchers.is(spark141Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[3].id"), Matchers.is(spark150Id)).body(((ApplicationRestControllerIntegrationTests.APPLICATIONS_LIST_PATH) + "[4].id"), Matchers.is(spark151Id));
        // TODO: Add tests for sort, orderBy etc
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(7L));
    }

    /**
     * Test to make sure that an application can be updated.
     *
     * @throws Exception
     * 		on configuration errors
     */
    @Test
    public void canUpdateApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final String id = this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String applicationResource = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}";
        final Application createdApp = GenieObjectMapper.getMapper().readValue(RestAssured.given(getRequestSpecification()).when().port(this.port).get(applicationResource, ApplicationRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).extract().asByteArray(), ApplicationResource.class).getContent();
        Assert.assertThat(createdApp.getStatus(), Matchers.is(ACTIVE));
        final Application.Builder newStatusApp = new Application.Builder(createdApp.getName(), createdApp.getUser(), createdApp.getVersion(), ApplicationStatus.INACTIVE).withId(createdApp.getId().orElseThrow(IllegalArgumentException::new)).withCreated(createdApp.getCreated().orElseThrow(IllegalArgumentException::new)).withUpdated(createdApp.getUpdated().orElseThrow(IllegalArgumentException::new)).withTags(createdApp.getTags()).withConfigs(createdApp.getConfigs()).withDependencies(createdApp.getDependencies());
        createdApp.getDescription().ifPresent(newStatusApp::withDescription);
        createdApp.getSetupFile().ifPresent(newStatusApp::withSetupFile);
        final RestDocumentationFilter updateResultFilter = // request header
        // path parameters
        // payload fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, Snippets.getApplicationRequestPayload());
        RestAssured.given(getRequestSpecification()).filter(updateResultFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(newStatusApp.build())).when().port(this.port).put(applicationResource, id).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(applicationResource, ApplicationRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(INACTIVE.toString()));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(1L));
    }

    /**
     * Test to make sure that an application can be patched.
     *
     * @throws Exception
     * 		on configuration errors
     */
    @Test
    public void canPatchApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final String id = this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String applicationResource = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}";
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(applicationResource, id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(ApplicationRestControllerIntegrationTests.USER));
        final String newUser = UUID.randomUUID().toString();
        final String patchString = ("[{ \"op\": \"replace\", \"path\": \"/user\", \"value\": \"" + newUser) + "\" }]";
        final JsonPatch patch = JsonPatch.fromJson(GenieObjectMapper.getMapper().readTree(patchString));
        final RestDocumentationFilter patchFilter = // request headers
        // path params
        // request payload
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, Snippets.PATCH_FIELDS);
        RestAssured.given(getRequestSpecification()).filter(patchFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(patch)).when().port(this.port).patch(applicationResource, id).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(applicationResource, id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(newUser));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(1L));
    }

    /**
     * Make sure can successfully delete all applications.
     *
     * @throws Exception
     * 		on a configuration error
     */
    @Test
    public void canDeleteAllApplications() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).build(), null);
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.DEPRECATED).build(), null);
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.INACTIVE).build(), null);
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(3L));
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/");
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(NO_CONTENT.value()));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
    }

    /**
     * Test to make sure that you can delete an application.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canDeleteAnApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String id3 = UUID.randomUUID().toString();
        final String name1 = UUID.randomUUID().toString();
        final String name2 = UUID.randomUUID().toString();
        final String name3 = UUID.randomUUID().toString();
        final String user1 = UUID.randomUUID().toString();
        final String user2 = UUID.randomUUID().toString();
        final String user3 = UUID.randomUUID().toString();
        final String version1 = UUID.randomUUID().toString();
        final String version2 = UUID.randomUUID().toString();
        final String version3 = UUID.randomUUID().toString();
        this.createConfigResource(new Application.Builder(name1, user1, version1, ApplicationStatus.ACTIVE).withId(id1).build(), null);
        this.createConfigResource(new Application.Builder(name2, user2, version2, ApplicationStatus.DEPRECATED).withId(id2).build(), null);
        this.createConfigResource(new Application.Builder(name3, user3, version3, ApplicationStatus.INACTIVE).withId(id3).build(), null);
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(3L));
        final RestDocumentationFilter deleteFilter = // path parameters
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}"), id2).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}"), id2).then().statusCode(Matchers.is(NOT_FOUND.value()));
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(2L));
    }

    /**
     * Test to make sure we can add configurations to the application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddConfigsToApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter addFilter = // request headers
        // path parameters
        // request payload fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.CONFIG_FIELDS));
        final RestDocumentationFilter getFilter = // path parameters
        // response headers
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.CONFIG_FIELDS));
        this.canAddElementsToResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/configs"), ApplicationRestControllerIntegrationTests.ID, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the configurations for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateConfigsForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.CONFIG_FIELDS));
        this.canUpdateElementsForResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/configs"), ApplicationRestControllerIntegrationTests.ID, updateFilter);
    }

    /**
     * Test to make sure we can delete the configurations for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteConfigsForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter deleteFilter = // Path parameters
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        this.canDeleteElementsFromResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/configs"), ApplicationRestControllerIntegrationTests.ID, deleteFilter);
    }

    /**
     * Test to make sure we can add dependencies to the application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddDependenciesToApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter addFilter = // path params
        // request header
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.CONTENT_TYPE_HEADER, PayloadDocumentation.requestFields(Snippets.DEPENDENCIES_FIELDS));
        final RestDocumentationFilter getFilter = // path params
        // response headers
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.DEPENDENCIES_FIELDS));
        this.canAddElementsToResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/dependencies"), ApplicationRestControllerIntegrationTests.ID, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the dependencies for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateDependenciesForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.DEPENDENCIES_FIELDS));
        this.canUpdateElementsForResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/dependencies"), ApplicationRestControllerIntegrationTests.ID, updateFilter);
    }

    /**
     * Test to make sure we can delete the dependencies for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteDependenciesForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final RestDocumentationFilter deleteFilter = // Path variables
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        this.canDeleteElementsFromResource(((RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/dependencies"), ApplicationRestControllerIntegrationTests.ID, deleteFilter);
    }

    /**
     * Test to make sure we can add tags to the application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddTagsToApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String api = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/tags";
        final RestDocumentationFilter addFilter = // path params
        // request header
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.CONTENT_TYPE_HEADER, PayloadDocumentation.requestFields(Snippets.TAGS_FIELDS));
        final RestDocumentationFilter getFilter = // path parameters
        // response headers
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.TAGS_FIELDS));
        this.canAddTagsToResource(api, ApplicationRestControllerIntegrationTests.ID, ApplicationRestControllerIntegrationTests.NAME, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the tags for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateTagsForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String api = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/tags";
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.TAGS_FIELDS));
        this.canUpdateTagsForResource(api, ApplicationRestControllerIntegrationTests.ID, ApplicationRestControllerIntegrationTests.NAME, updateFilter);
    }

    /**
     * Test to make sure we can delete the tags for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteTagsForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String api = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/tags";
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        this.canDeleteTagsForResource(api, ApplicationRestControllerIntegrationTests.ID, ApplicationRestControllerIntegrationTests.NAME, deleteFilter);
    }

    /**
     * Test to make sure we can delete a tag for an application after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteTagForApplication() throws Exception {
        Assert.assertThat(this.applicationRepository.count(), Matchers.is(0L));
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String api = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/tags";
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM.and(RequestDocumentation.parameterWithName("tag").description("The tag to remove")));
        this.canDeleteTagForResource(api, ApplicationRestControllerIntegrationTests.ID, ApplicationRestControllerIntegrationTests.NAME, deleteFilter);
    }

    /**
     * Make sure can get all the commands which use a given application.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canGetCommandsForApplication() throws Exception {
        this.createConfigResource(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(ApplicationRestControllerIntegrationTests.ID).build(), null);
        final String placeholder = UUID.randomUUID().toString();
        final String command1Id = UUID.randomUUID().toString();
        final String command2Id = UUID.randomUUID().toString();
        final String command3Id = UUID.randomUUID().toString();
        this.createConfigResource(new Command.Builder(placeholder, placeholder, placeholder, CommandStatus.ACTIVE, placeholder, 1000L).withId(command1Id).build(), null);
        this.createConfigResource(new Command.Builder(placeholder, placeholder, placeholder, CommandStatus.INACTIVE, placeholder, 1100L).withId(command2Id).build(), null);
        this.createConfigResource(new Command.Builder(placeholder, placeholder, placeholder, CommandStatus.DEPRECATED, placeholder, 1200L).withId(command3Id).build(), null);
        final Set<String> appIds = Sets.newHashSet(ApplicationRestControllerIntegrationTests.ID);
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(appIds)).when().port(this.port).post(((RestControllerIntegrationTestsBase.COMMANDS_API) + "/{id}/applications"), command1Id).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(appIds)).when().port(this.port).post(((RestControllerIntegrationTestsBase.COMMANDS_API) + "/{id}/applications"), command3Id).then().statusCode(Matchers.is(NO_CONTENT.value()));
        final String applicationCommandsAPI = (RestControllerIntegrationTestsBase.APPLICATIONS_API) + "/{id}/commands";
        Arrays.asList(GenieObjectMapper.getMapper().readValue(RestAssured.given(getRequestSpecification()).when().port(this.port).get(applicationCommandsAPI, ApplicationRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).extract().asByteArray(), Command[].class)).forEach(( command) -> {
            if ((!(command.getId().orElseThrow(IllegalArgumentException::new).equals(command1Id))) && (!(command.getId().orElseThrow(IllegalArgumentException::new).equals(command3Id)))) {
                Assert.fail();
            }
        });
        // Filter by status
        final RestDocumentationFilter getFilter = // Path parameters
        // Query Parameters
        // Response Headers
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("status").description("The status of commands to search for").attributes(Attributes.key(Snippets.CONSTRAINTS).value(CommandStatus.values())).optional()), Snippets.HAL_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(PayloadDocumentation.subsectionWithPath("[]").description("The list of commands found").attributes(Snippets.EMPTY_CONSTRAINTS)));
        RestAssured.given(getRequestSpecification()).param("status", CommandStatus.ACTIVE.toString(), CommandStatus.INACTIVE.toString()).filter(getFilter).when().port(this.port).get(applicationCommandsAPI, ApplicationRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(1)).body("[0].id", Matchers.is(command1Id));
    }

    /**
     * Test creating an application with blank files and tag resources.
     *
     * @throws Exception
     * 		when an unexpected error is encountered
     */
    @Test
    public void canCreateApplicationWithBlankFields() throws Exception {
        final Set<String> stringSetWithBlank = Sets.newHashSet("foo", " ");
        final List<Application> invalidApplicationResources = Lists.newArrayList(new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(UUID.randomUUID().toString()).withSetupFile(" ").build(), new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(UUID.randomUUID().toString()).withConfigs(stringSetWithBlank).build(), new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(UUID.randomUUID().toString()).withDependencies(stringSetWithBlank).build(), new Application.Builder(ApplicationRestControllerIntegrationTests.NAME, ApplicationRestControllerIntegrationTests.USER, ApplicationRestControllerIntegrationTests.VERSION, ApplicationStatus.ACTIVE).withId(UUID.randomUUID().toString()).withTags(stringSetWithBlank).build());
        long i = 0L;
        for (final Application invalidApplicationResource : invalidApplicationResources) {
            Assert.assertThat(this.applicationRepository.count(), Matchers.is(i));
            RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(invalidApplicationResource)).when().port(this.port).post(RestControllerIntegrationTestsBase.APPLICATIONS_API).then().statusCode(Matchers.is(CREATED.value()));
            Assert.assertThat(this.applicationRepository.count(), Matchers.is((++i)));
        }
    }
}

