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


import Cluster.Builder;
import ClusterStatus.OUT_OF_SERVICE;
import ClusterStatus.TERMINATED;
import ClusterStatus.UP;
import CommandStatus.INACTIVE;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.PRECONDITION_FAILED;
import MediaType.APPLICATION_JSON_VALUE;
import MediaTypes.HAL_JSON_UTF8_VALUE;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.genie.common.dto.Cluster;
import com.netflix.genie.common.dto.ClusterStatus;
import com.netflix.genie.common.dto.CommandStatus;
import com.netflix.genie.common.util.GenieObjectMapper;
import com.netflix.genie.web.hateoas.resources.ClusterResource;
import io.restassured.RestAssured;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.util.URLEncoder;
import org.apache.http.client.utils.URLEncodedUtils;
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
 * Integration tests for the Commands REST API.
 *
 * @author tgianos
 * @since 3.0.0
 */
// TODO: Add tests for error conditions
public class ClusterRestControllerIntegrationTests extends RestControllerIntegrationTestsBase {
    private static final String ID = UUID.randomUUID().toString();

    private static final String NAME = "h2prod";

    private static final String USER = "genie";

    private static final String VERSION = "2.7.1";

    private static final String CLUSTERS_LIST_PATH = (RestControllerIntegrationTestsBase.EMBEDDED_PATH) + ".clusterList";

    private static final String CLUSTERS_ID_LIST_PATH = (ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + ".id";

    private static final String CLUSTER_COMMANDS_LINK_PATH = "_links.commands.href";

    private static final String CLUSTERS_COMMANDS_LINK_PATH = (ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + "._links.commands.href";

    /**
     * Test creating a cluster without an ID.
     *
     * @throws Exception
     * 		on configuration issue
     */
    @Test
    public void canCreateClusterWithoutId() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        final RestDocumentationFilter createFilter = // Request headers
        // Request fields
        // Response headers
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.getClusterRequestPayload(), Snippets.LOCATION_HEADER);
        final String id = this.createConfigResource(build(), createFilter);
        final RestDocumentationFilter getFilter = // path parameters
        // response headers
        // response payload
        // response links
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.HAL_CONTENT_TYPE_HEADER, Snippets.getClusterResponsePayload(), Snippets.CLUSTER_LINKS);
        RestAssured.given(getRequestSpecification()).filter(getFilter).when().port(this.port).get(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}"), id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(id)).body(RestControllerIntegrationTestsBase.CREATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.UPDATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(ClusterRestControllerIntegrationTests.NAME)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(ClusterRestControllerIntegrationTests.USER)).body(RestControllerIntegrationTestsBase.VERSION_PATH, Matchers.is(ClusterRestControllerIntegrationTests.VERSION)).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.id:" + id))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.name:" + (ClusterRestControllerIntegrationTests.NAME)))).body(RestControllerIntegrationTestsBase.SETUP_FILE_PATH, Matchers.nullValue()).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(UP.toString())).body(RestControllerIntegrationTestsBase.CONFIGS_PATH, Matchers.hasSize(0)).body(RestControllerIntegrationTestsBase.DEPENDENCIES_PATH, Matchers.hasSize(0)).body(((RestControllerIntegrationTestsBase.LINKS_PATH) + ".keySet().size()"), Matchers.is(2)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.SELF_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY)).body(ClusterRestControllerIntegrationTests.CLUSTER_COMMANDS_LINK_PATH, matchUri(RestControllerIntegrationTestsBase.CLUSTERS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, id));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(1L));
    }

    /**
     * Test creating a Cluster with an ID.
     *
     * @throws Exception
     * 		When issue in creation
     */
    @Test
    public void canCreateClusterWithId() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}"), ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(ClusterRestControllerIntegrationTests.ID)).body(RestControllerIntegrationTestsBase.CREATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.UPDATED_PATH, Matchers.notNullValue()).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(ClusterRestControllerIntegrationTests.NAME)).body(RestControllerIntegrationTestsBase.USER_PATH, Matchers.is(ClusterRestControllerIntegrationTests.USER)).body(RestControllerIntegrationTestsBase.VERSION_PATH, Matchers.is(ClusterRestControllerIntegrationTests.VERSION)).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.id:" + (ClusterRestControllerIntegrationTests.ID)))).body(RestControllerIntegrationTestsBase.TAGS_PATH, Matchers.hasItem(("genie.name:" + (ClusterRestControllerIntegrationTests.NAME)))).body(RestControllerIntegrationTestsBase.SETUP_FILE_PATH, Matchers.nullValue()).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(UP.toString())).body(RestControllerIntegrationTestsBase.CONFIGS_PATH, Matchers.hasSize(0)).body(RestControllerIntegrationTestsBase.DEPENDENCIES_PATH, Matchers.hasSize(0)).body(((RestControllerIntegrationTestsBase.LINKS_PATH) + ".keySet().size()"), Matchers.is(2)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.SELF_LINK_KEY)).body(RestControllerIntegrationTestsBase.LINKS_PATH, Matchers.hasKey(RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY)).body(ClusterRestControllerIntegrationTests.CLUSTER_COMMANDS_LINK_PATH, matchUri(RestControllerIntegrationTestsBase.CLUSTERS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, ClusterRestControllerIntegrationTests.ID));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(1L));
    }

    /**
     * Test to make sure the post API can handle bad input.
     *
     * @throws Exception
     * 		on issue
     */
    @Test
    public void canHandleBadInputToCreateCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        final Cluster cluster = new Cluster.Builder(" ", " ", " ", ClusterStatus.UP).build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(cluster)).when().port(this.port).post(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
    }

    /**
     * Test to make sure that you can search for clusters by various parameters.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canFindClusters() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
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
        this.createConfigResource(build(), null);
        Thread.sleep(1000);
        this.createConfigResource(build(), null);
        Thread.sleep(1000);
        this.createConfigResource(build(), null);
        final RestDocumentationFilter findFilter = // Request query parameters
        // Response headers
        // Result fields
        // HAL Links
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CLUSTER_SEARCH_QUERY_PARAMETERS, Snippets.HAL_CONTENT_TYPE_HEADER, Snippets.CLUSTER_SEARCH_RESULT_FIELDS, Snippets.SEARCH_LINKS);
        // Test finding all clusters
        RestAssured.given(getRequestSpecification()).filter(findFilter).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(3)).body(ClusterRestControllerIntegrationTests.CLUSTERS_ID_LIST_PATH, Matchers.containsInAnyOrder(id1, id2, id3)).body(ClusterRestControllerIntegrationTests.CLUSTERS_COMMANDS_LINK_PATH, matchUrisAnyOrder(RestControllerIntegrationTestsBase.CLUSTERS_API, RestControllerIntegrationTestsBase.COMMANDS_LINK_KEY, RestControllerIntegrationTestsBase.COMMANDS_OPTIONAL_HAL_LINK_PARAMETERS, Lists.newArrayList(id1, id2, id3)));
        // Try to limit the number of results
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("size", 2).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(2));
        // Query by name
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("name", name2).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(1)).body(((ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + "[0].id"), Matchers.is(id2));
        // Query by statuses
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("status", UP.toString(), TERMINATED.toString()).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(2)).body(((ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + "[0].id"), Matchers.is(id3)).body(((ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + "[1].id"), Matchers.is(id1));
        // Query by tags
        RestAssured.given(getRequestSpecification()).filter(findFilter).param("tag", ("genie.id:" + id1)).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(1)).body(((ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH) + "[0].id"), Matchers.is(id1));
        // TODO: Add tests for searching by min and max update time as those are available parameters
        // TODO: Add tests for sort, orderBy etc
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(3L));
    }

    /**
     * Test to make sure that a cluster can be updated.
     *
     * @throws Exception
     * 		on configuration errors
     */
    @Test
    public void canUpdateCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final String clusterResource = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}";
        final Cluster createdCluster = GenieObjectMapper.getMapper().readValue(RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterResource, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).extract().asByteArray(), ClusterResource.class).getContent();
        Assert.assertThat(createdCluster.getStatus(), Matchers.is(UP));
        final Cluster.Builder updateCluster = new Cluster.Builder(createdCluster.getName(), createdCluster.getUser(), createdCluster.getVersion(), ClusterStatus.OUT_OF_SERVICE).withId(createdCluster.getId().orElseThrow(IllegalArgumentException::new)).withCreated(createdCluster.getCreated().orElseThrow(IllegalArgumentException::new)).withUpdated(createdCluster.getUpdated().orElseThrow(IllegalArgumentException::new)).withTags(createdCluster.getTags()).withConfigs(createdCluster.getConfigs()).withDependencies(createdCluster.getDependencies());
        createdCluster.getDescription().ifPresent(updateCluster::withDescription);
        createdCluster.getSetupFile().ifPresent(updateCluster::withSetupFile);
        final RestDocumentationFilter updateFilter = // request header
        // path parameters
        // payload fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, Snippets.getClusterRequestPayload());
        RestAssured.given(getRequestSpecification()).filter(updateFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(updateCluster.build())).when().port(this.port).put(clusterResource, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterResource, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(OUT_OF_SERVICE.toString()));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(1L));
    }

    /**
     * Test to make sure that a cluster can be patched.
     *
     * @throws Exception
     * 		on configuration errors
     */
    @Test
    public void canPatchCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        final String id = this.createConfigResource(build(), null);
        final String clusterResource = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}";
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterResource, id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(ClusterRestControllerIntegrationTests.NAME));
        final String newName = UUID.randomUUID().toString();
        final String patchString = ("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"" + newName) + "\" }]";
        final JsonPatch patch = JsonPatch.fromJson(GenieObjectMapper.getMapper().readTree(patchString));
        final RestDocumentationFilter patchFilter = // request headers
        // path params
        // request payload
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, Snippets.PATCH_FIELDS);
        RestAssured.given(getRequestSpecification()).filter(patchFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(patch)).when().port(this.port).patch(clusterResource, id).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterResource, id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.NAME_PATH, Matchers.is(newName));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(1L));
    }

    /**
     * Make sure can successfully delete all clusters.
     *
     * @throws Exception
     * 		on a configuration error
     */
    @Test
    public void canDeleteAllClusters() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(3L));
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/");
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(NO_CONTENT.value()));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
    }

    /**
     * Test to make sure that you can delete a cluster.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canDeleteACluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
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
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(3L));
        final RestDocumentationFilter deleteFilter = // path parameters
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}"), id2).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}"), id2).then().statusCode(Matchers.is(NOT_FOUND.value()));
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(2L));
    }

    /**
     * Test to make sure we can add configurations to the cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddConfigsToCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter addFilter = // path params
        // request header
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.CONTENT_TYPE_HEADER, PayloadDocumentation.requestFields(Snippets.CONFIG_FIELDS));
        final RestDocumentationFilter getFilter = // path params
        // response headers
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.CONFIG_FIELDS));
        this.canAddElementsToResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/configs"), ClusterRestControllerIntegrationTests.ID, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the configurations for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateConfigsForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.CONFIG_FIELDS));
        this.canUpdateElementsForResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/configs"), ClusterRestControllerIntegrationTests.ID, updateFilter);
    }

    /**
     * Test to make sure we can delete the configurations for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteConfigsForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        this.canDeleteElementsFromResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/configs"), ClusterRestControllerIntegrationTests.ID, deleteFilter);
    }

    /**
     * Test to make sure we can add dependencies to the cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddDependenciesToCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter addFilter = // path params
        // request header
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.CONTENT_TYPE_HEADER, PayloadDocumentation.requestFields(Snippets.DEPENDENCIES_FIELDS));
        final RestDocumentationFilter getFilter = // path params
        // response headers
        // response fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.DEPENDENCIES_FIELDS));
        this.canAddElementsToResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/dependencies"), ClusterRestControllerIntegrationTests.ID, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the dependencies for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateDependenciesForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.DEPENDENCIES_FIELDS));
        this.canUpdateElementsForResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/configs"), ClusterRestControllerIntegrationTests.ID, updateFilter);
    }

    /**
     * Test to make sure we can delete the dependencies for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteDependenciesForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        this.canDeleteElementsFromResource(((RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/dependencies"), ClusterRestControllerIntegrationTests.ID, deleteFilter);
    }

    /**
     * Test to make sure we can add tags to the cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canAddTagsToCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final String api = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/tags";
        final RestDocumentationFilter addFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.TAGS_FIELDS));
        final RestDocumentationFilter getFilter = // Path parameters
        // Response header
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, Snippets.JSON_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(Snippets.TAGS_FIELDS));
        this.canAddTagsToResource(api, ClusterRestControllerIntegrationTests.ID, ClusterRestControllerIntegrationTests.NAME, addFilter, getFilter);
    }

    /**
     * Test to make sure we can update the tags for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canUpdateTagsForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final String api = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/tags";
        final RestDocumentationFilter updateFilter = // Request header
        // Path parameters
        // Request fields
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(Snippets.TAGS_FIELDS));
        this.canUpdateTagsForResource(api, ClusterRestControllerIntegrationTests.ID, ClusterRestControllerIntegrationTests.NAME, updateFilter);
    }

    /**
     * Test to make sure we can delete the tags for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteTagsForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        final String api = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/tags";
        this.canDeleteTagsForResource(api, ClusterRestControllerIntegrationTests.ID, ClusterRestControllerIntegrationTests.NAME, deleteFilter);
    }

    /**
     * Test to make sure we can delete a tag for a cluster after it is created.
     *
     * @throws Exception
     * 		on configuration problems
     */
    @Test
    public void canDeleteTagForCluster() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        this.createConfigResource(build(), null);
        final String api = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/tags";
        final RestDocumentationFilter deleteFilter = RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM.and(RequestDocumentation.parameterWithName("tag").description("The tag to remove")));
        this.canDeleteTagForResource(api, ClusterRestControllerIntegrationTests.ID, ClusterRestControllerIntegrationTests.NAME, deleteFilter);
    }

    /**
     * Make sure can add the commands for a cluster.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canAddCommandsForACluster() throws Exception {
        this.createConfigResource(build(), null);
        final String clusterCommandsAPI = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/commands";
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.empty());
        final String placeholder = UUID.randomUUID().toString();
        final String commandId1 = UUID.randomUUID().toString();
        final String commandId2 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        final RestDocumentationFilter addFilter = // Request Headers
        // Path parameters
        // Request payload
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(PayloadDocumentation.fieldWithPath("[]").description("Array of command ids (in preferred order) to append to the existing list of commands").attributes(Snippets.EMPTY_CONSTRAINTS)));
        RestAssured.given(getRequestSpecification()).filter(addFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList(commandId1, commandId2))).when().port(this.port).post(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(2)).body("[0].id", Matchers.is(commandId1)).body("[1].id", Matchers.is(commandId2));
        // Shouldn't add anything
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList())).when().port(this.port).post(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        final String commandId3 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        RestAssured.given(getRequestSpecification()).filter(addFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList(commandId3))).when().port(this.port).post(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(3)).body("[0].id", Matchers.is(commandId1)).body("[1].id", Matchers.is(commandId2)).body("[2].id", Matchers.is(commandId3));
        // Test the filtering
        final RestDocumentationFilter getFilter = // Path parameters
        // Query Parameters
        // Response Headers
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM, RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("status").description("The status of commands to search for").attributes(Attributes.key(Snippets.CONSTRAINTS).value(CommandStatus.values())).optional()), Snippets.HAL_CONTENT_TYPE_HEADER, PayloadDocumentation.responseFields(PayloadDocumentation.subsectionWithPath("[]").description("The list of commands found").attributes(Snippets.EMPTY_CONSTRAINTS)));
        RestAssured.given(getRequestSpecification()).filter(getFilter).param("status", INACTIVE.toString()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(1)).body("[0].id", Matchers.is(commandId3));
    }

    /**
     * Make sure can set the commands for a cluster.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canSetCommandsForACluster() throws Exception {
        this.createConfigResource(build(), null);
        final String clusterCommandsAPI = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/commands";
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.empty());
        final String placeholder = UUID.randomUUID().toString();
        final String commandId1 = UUID.randomUUID().toString();
        final String commandId2 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        final RestDocumentationFilter setFilter = // Request Headers
        // Path parameters
        // Request payload
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.CONTENT_TYPE_HEADER, Snippets.ID_PATH_PARAM, PayloadDocumentation.requestFields(PayloadDocumentation.fieldWithPath("[]").description("Array of command ids (in preferred order) to replace the existing list of commands").attributes(Snippets.EMPTY_CONSTRAINTS)));
        RestAssured.given(getRequestSpecification()).filter(setFilter).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList(commandId1, commandId2))).when().port(this.port).put(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(2)).body("[0].id", Matchers.is(commandId1)).body("[1].id", Matchers.is(commandId2));
        // Should clear commands
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList())).when().port(this.port).put(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.empty());
    }

    /**
     * Make sure that we can remove all the commands from a cluster.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canRemoveCommandsFromACluster() throws Exception {
        this.createConfigResource(build(), null);
        final String clusterCommandsAPI = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/commands";
        final String placeholder = UUID.randomUUID().toString();
        final String commandId1 = UUID.randomUUID().toString();
        final String commandId2 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList(commandId1, commandId2))).when().port(this.port).post(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        final RestDocumentationFilter deleteFilter = // Path parameters
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM);
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.empty());
    }

    /**
     * Make sure that we can remove a command from a cluster.
     *
     * @throws Exception
     * 		on configuration error
     */
    @Test
    public void canRemoveCommandFromACluster() throws Exception {
        this.createConfigResource(build(), null);
        final String clusterCommandsAPI = (RestControllerIntegrationTestsBase.CLUSTERS_API) + "/{id}/commands";
        final String placeholder = UUID.randomUUID().toString();
        final String commandId1 = UUID.randomUUID().toString();
        final String commandId2 = UUID.randomUUID().toString();
        final String commandId3 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        this.createConfigResource(build(), null);
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(Lists.newArrayList(commandId1, commandId2, commandId3))).when().port(this.port).post(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(NO_CONTENT.value()));
        final RestDocumentationFilter deleteFilter = // Path parameters
        RestAssuredRestDocumentation.document("{class-name}/{method-name}/{step}/", Snippets.ID_PATH_PARAM.and(RequestDocumentation.parameterWithName("commandId").description("The id of the command to remove")));
        RestAssured.given(getRequestSpecification()).filter(deleteFilter).when().port(this.port).delete((clusterCommandsAPI + "/{commandId}"), ClusterRestControllerIntegrationTests.ID, commandId2).then().statusCode(Matchers.is(NO_CONTENT.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(clusterCommandsAPI, ClusterRestControllerIntegrationTests.ID).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(2)).body("[0].id", Matchers.is(commandId1)).body("[1].id", Matchers.is(commandId3));
        // Check reverse side of relationship
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.COMMANDS_API) + "/{id}/clusters"), commandId1).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(1)).body("[0].id", Matchers.is(ClusterRestControllerIntegrationTests.ID));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.COMMANDS_API) + "/{id}/clusters"), commandId2).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.empty());
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.COMMANDS_API) + "/{id}/clusters"), commandId3).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body("$", Matchers.hasSize(1)).body("[0].id", Matchers.is(ClusterRestControllerIntegrationTests.ID));
    }

    /**
     * This test "documents" a known bug in Spring HATEOAS links that results in doubly-encoded pagination links.
     * https://github.com/spring-projects/spring-hateoas/issues/559
     * Currently, we work around this bug in the UI by decoding these elements (see Pagination.js).
     * If this test starts failing, it may be because the behavior has been corrected, and the workaround may be
     * removed.
     *
     * @throws Exception
     * 		on error
     */
    @Test
    public void testPagingDoubleEncoding() throws Exception {
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(0L));
        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String id3 = UUID.randomUUID().toString();
        final String name1 = "Test " + (UUID.randomUUID().toString());
        final String name2 = "Test " + (UUID.randomUUID().toString());
        final String name3 = "Test " + (UUID.randomUUID().toString());
        final String user1 = UUID.randomUUID().toString();
        final String user2 = UUID.randomUUID().toString();
        final String user3 = UUID.randomUUID().toString();
        final String version1 = UUID.randomUUID().toString();
        final String version2 = UUID.randomUUID().toString();
        final String version3 = UUID.randomUUID().toString();
        this.createConfigResource(build(), null);
        Thread.sleep(1000);
        this.createConfigResource(build(), null);
        Thread.sleep(1000);
        this.createConfigResource(build(), null);
        Assert.assertThat(this.clusterRepository.count(), Matchers.is(3L));
        final URLEncoder urlEncoder = new URLEncoder();
        final String unencodedNameQuery = "Test %";
        final String singleEncodedNameQuery = urlEncoder.encode(unencodedNameQuery, StandardCharsets.UTF_8);
        final String doubleEncodedNameQuery = urlEncoder.encode(singleEncodedNameQuery, StandardCharsets.UTF_8);
        // Query by name with wildcard and get the second page containing a single result (out of 3)
        final JsonNode responseJsonNode = GenieObjectMapper.getMapper().readTree(RestAssured.given(getRequestSpecification()).param("name", unencodedNameQuery).param("size", 1).param("page", 1).when().port(this.port).get(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(HAL_JSON_UTF8_VALUE)).body(ClusterRestControllerIntegrationTests.CLUSTERS_LIST_PATH, Matchers.hasSize(1)).extract().asByteArray());
        // Self link is not double-encoded
        Assert.assertTrue(responseJsonNode.get("_links").get("self").get("href").asText().contains(singleEncodedNameQuery));
        // Pagination links are double-encoded
        final String[] doubleEncodedHREFS = new String[]{ "first", "next", "prev", "last" };
        for (String doubleEncodedHref : doubleEncodedHREFS) {
            final String linkString = responseJsonNode.get("_links").get(doubleEncodedHref).get("href").asText();
            Assert.assertNotNull(linkString);
            final HashMap<String, String> params = Maps.newHashMap();
            URLEncodedUtils.parse(new URI(linkString), StandardCharsets.UTF_8).forEach(( nameValuePair) -> params.put(nameValuePair.getName(), nameValuePair.getValue()));
            Assert.assertTrue(params.containsKey("name"));
            // Correct: singleEncodedNameQuery, actual: doubleEncodedNameQuery
            Assert.assertEquals(doubleEncodedNameQuery, params.get("name"));
            final String decoded = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8.name());
            Assert.assertEquals(singleEncodedNameQuery, decoded);
        }
    }

    /**
     * Test creating a cluster with blank files and tag resources.
     *
     * @throws Exception
     * 		when an unexpected error is encountered
     */
    @Test
    public void canCreateClusterWithBlankFields() throws Exception {
        final List<Cluster> invalidClusterResources = Lists.newArrayList(build(), build(), build());
        long i = 0L;
        for (final Cluster invalidClusterResource : invalidClusterResources) {
            Assert.assertThat(this.clusterRepository.count(), Matchers.is(i));
            RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(invalidClusterResource)).when().port(this.port).post(RestControllerIntegrationTestsBase.CLUSTERS_API).then().statusCode(Matchers.is(CREATED.value()));
            Assert.assertThat(this.clusterRepository.count(), Matchers.is((++i)));
        }
    }
}

