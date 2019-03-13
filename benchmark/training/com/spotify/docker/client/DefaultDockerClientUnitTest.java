/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client;


import DefaultDockerClient.Builder;
import DockerClient.LogsParam;
import RequestEntityProcessing.BUFFERED;
import RequestEntityProcessing.CHUNKED;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;
import com.spotify.docker.FixtureUtil;
import com.spotify.docker.client.DockerClient.Signal;
import com.spotify.docker.client.auth.RegistryAuthSupplier;
import com.spotify.docker.client.exceptions.ConflictException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.NodeNotFoundException;
import com.spotify.docker.client.exceptions.NonSwarmNodeException;
import com.spotify.docker.client.exceptions.NotFoundException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.Distribution;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;
import com.spotify.docker.client.messages.ServiceCreateResponse;
import com.spotify.docker.client.messages.Volume;
import com.spotify.docker.client.messages.swarm.Config;
import com.spotify.docker.client.messages.swarm.ConfigBind;
import com.spotify.docker.client.messages.swarm.ConfigCreateResponse;
import com.spotify.docker.client.messages.swarm.ConfigFile;
import com.spotify.docker.client.messages.swarm.ConfigSpec;
import com.spotify.docker.client.messages.swarm.ContainerSpec;
import com.spotify.docker.client.messages.swarm.EngineConfig;
import com.spotify.docker.client.messages.swarm.Node;
import com.spotify.docker.client.messages.swarm.NodeDescription;
import com.spotify.docker.client.messages.swarm.NodeInfo;
import com.spotify.docker.client.messages.swarm.NodeSpec;
import com.spotify.docker.client.messages.swarm.Placement;
import com.spotify.docker.client.messages.swarm.Preference;
import com.spotify.docker.client.messages.swarm.ResourceRequirements;
import com.spotify.docker.client.messages.swarm.Service;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import com.spotify.docker.client.messages.swarm.Spread;
import com.spotify.docker.client.messages.swarm.SwarmJoin;
import com.spotify.docker.client.messages.swarm.Task;
import com.spotify.docker.client.messages.swarm.TaskSpec;
import com.spotify.docker.client.messages.swarm.Version;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.glassfish.jersey.internal.util.Base64;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


/**
 * Tests DefaultDockerClient against a {@link okhttp3.mockwebserver.MockWebServer} instance, so
 * we can assert what the HTTP requests look like that DefaultDockerClient sends and test how
 * DefaltDockerClient behaves given certain responses from the Docker Remote API.
 * <p>
 * This test may not be a true "unit test", but using a MockWebServer where we can control the HTTP
 * responses sent by the server and capture the HTTP requests sent by the class-under-test is far
 * simpler that attempting to mock the {@link javax.ws.rs.client.Client} instance used by
 * DefaultDockerClient, since the Client has such a rich/fluent interface and many methods/classes
 * that would need to be mocked. Ultimately for testing DefaultDockerClient all we care about is
 * the HTTP requests it sends, rather than what HTTP client library it uses.</p>
 * <p>
 * When adding new functionality to DefaultDockerClient, please consider and prioritize adding unit
 * tests to cover the new functionality in this file rather than integration tests that require a
 * real docker daemon in {@link DefaultDockerClientTest}. While integration tests are valuable,
 * they are more brittle and harder to run than a simple unit test that captures/asserts HTTP
 * requests and responses.</p>
 *
 * @see <a href="https://github.com/square/okhttp/tree/master/mockwebserver">
https://github.com/square/okhttp/tree/master/mockwebserver</a>
 */
public class DefaultDockerClientUnitTest {
    private final MockWebServer server = new MockWebServer();

    private Builder builder;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHostForUnixSocket() {
        final DefaultDockerClient client = DefaultDockerClient.builder().uri("unix:///var/run/docker.sock").build();
        Assert.assertThat(client.getHost(), Matchers.equalTo("localhost"));
    }

    @Test
    public void testHostForLocalHttps() {
        final DefaultDockerClient client = DefaultDockerClient.builder().uri("https://localhost:2375").build();
        Assert.assertThat(client.getHost(), Matchers.equalTo("localhost"));
    }

    @Test
    public void testHostForFqdnHttps() {
        final DefaultDockerClient client = DefaultDockerClient.builder().uri("https://perdu.com:2375").build();
        Assert.assertThat(client.getHost(), Matchers.equalTo("perdu.com"));
    }

    @Test
    public void testHostForIpHttps() {
        final DefaultDockerClient client = DefaultDockerClient.builder().uri("https://192.168.53.103:2375").build();
        Assert.assertThat(client.getHost(), Matchers.equalTo("192.168.53.103"));
    }

    @Test
    public void testHostWithProxy() {
        try {
            System.setProperty("http.proxyHost", "gmodules.com");
            System.setProperty("http.proxyPort", "80");
            final DefaultDockerClient client = DefaultDockerClient.builder().uri("https://192.168.53.103:2375").build();
            Assert.assertThat(client.getClient().getConfiguration().getProperty("jersey.config.client.proxy.uri"), Matchers.equalTo("http://gmodules.com:80"));
        } finally {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
        }
    }

    @Test
    public void testHostWithNonProxyHost() {
        try {
            System.setProperty("http.proxyHost", "gmodules.com");
            System.setProperty("http.proxyPort", "80");
            System.setProperty("http.nonProxyHosts", "127.0.0.1|localhost|192.168.*");
            final DefaultDockerClient client = DefaultDockerClient.builder().uri("https://192.168.53.103:2375").build();
            Assert.assertThat(((String) (client.getClient().getConfiguration().getProperty("jersey.config.client.proxy.uri"))), Matchers.isEmptyOrNullString());
        } finally {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("http.nonProxyHosts");
        }
    }

    @Test
    public void testCustomHeaders() throws Exception {
        builder.header("int", 1);
        builder.header("string", "2");
        builder.header("list", Lists.newArrayList("a", "b", "c"));
        server.enqueue(new MockResponse());
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        dockerClient.info();
        final RecordedRequest recordedRequest = takeRequestImmediately();
        Assert.assertThat(recordedRequest.getMethod(), Matchers.is("GET"));
        Assert.assertThat(recordedRequest.getPath(), Matchers.is("/info"));
        Assert.assertThat(recordedRequest.getHeader("int"), Matchers.is("1"));
        Assert.assertThat(recordedRequest.getHeader("string"), Matchers.is("2"));
        // TODO (mbrown): this seems like incorrect behavior - the client should send 3 headers with
        // name "list", not one header with a value of "[a, b, c]"
        Assert.assertThat(recordedRequest.getHeaders().values("list"), Matchers.contains("[a, b, c]"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroupAdd() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final HostConfig hostConfig = HostConfig.builder().groupAdd("63", "65").build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        final JsonNode groupAdd = DefaultDockerClientUnitTest.toJson(recordedRequest.getBody()).get("HostConfig").get("GroupAdd");
        Assert.assertThat(groupAdd.isArray(), Matchers.is(true));
        Assert.assertThat(DefaultDockerClientUnitTest.childrenTextNodes(((ArrayNode) (groupAdd))), Matchers.containsInAnyOrder("63", "65"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCapAddAndDrop() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final HostConfig hostConfig = HostConfig.builder().capAdd(ImmutableList.of("foo", "bar")).capAdd(ImmutableList.of("baz", "qux")).build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        Assert.assertThat(recordedRequest.getMethod(), Matchers.is("POST"));
        Assert.assertThat(recordedRequest.getPath(), Matchers.is("/containers/create"));
        Assert.assertThat(recordedRequest.getHeader("Content-Type"), Matchers.is("application/json"));
        final JsonNode requestJson = DefaultDockerClientUnitTest.toJson(recordedRequest.getBody());
        Assert.assertThat(requestJson, Matchers.is(jsonObject().where("HostConfig", Matchers.is(jsonObject().where("CapAdd", Matchers.is(jsonArray(Matchers.containsInAnyOrder(jsonText("baz"), jsonText("qux")))))))));
    }

    @Test
    @SuppressWarnings("deprecated")
    public void buildThrowsIfRegistryAuthandRegistryAuthSupplierAreBothSpecified() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("LOGIC ERROR");
        final RegistryAuthSupplier authSupplier = Mockito.mock(RegistryAuthSupplier.class);
        // noinspection deprecation
        DefaultDockerClient.builder().registryAuth(RegistryAuth.builder().identityToken("hello").build()).registryAuthSupplier(authSupplier).build();
    }

    @Test
    public void testBuildPassesMultipleRegistryConfigs() throws Exception {
        final RegistryConfigs registryConfigs = RegistryConfigs.create(ImmutableMap.of("server1", RegistryAuth.builder().serverAddress("server1").username("u1").password("p1").email("e1").build(), "server2", RegistryAuth.builder().serverAddress("server2").username("u2").password("p2").email("e2").build()));
        final RegistryAuthSupplier authSupplier = Mockito.mock(RegistryAuthSupplier.class);
        Mockito.when(authSupplier.authForBuild()).thenReturn(registryConfigs);
        final DefaultDockerClient client = builder.registryAuthSupplier(authSupplier).build();
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.20");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.22/build.json")));
        final Path path = Paths.get(Resources.getResource("dockerDirectory").toURI());
        client.build(path);
        final RecordedRequest versionRequest = takeRequestImmediately();
        Assert.assertThat(versionRequest.getMethod(), Matchers.is("GET"));
        Assert.assertThat(versionRequest.getPath(), Matchers.is("/version"));
        final RecordedRequest buildRequest = takeRequestImmediately();
        Assert.assertThat(buildRequest.getMethod(), Matchers.is("POST"));
        Assert.assertThat(buildRequest.getPath(), Matchers.is("/build"));
        final String registryConfigHeader = buildRequest.getHeader("X-Registry-Config");
        Assert.assertThat(registryConfigHeader, Matchers.is(Matchers.not(Matchers.nullValue())));
        // check that the JSON in the header is equivalent to what we mocked out above from
        // the registryAuthSupplier
        final JsonNode headerJsonNode = DefaultDockerClientUnitTest.toJson(BaseEncoding.base64().decode(registryConfigHeader));
        Assert.assertThat(headerJsonNode, Matchers.is(DefaultDockerClientUnitTest.toJson(registryConfigs.configs())));
    }

    @Test
    public void testNanoCpus() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final HostConfig hostConfig = HostConfig.builder().nanoCpus(2000000000L).build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        final JsonNode requestJson = DefaultDockerClientUnitTest.toJson(recordedRequest.getBody());
        final JsonNode nanoCpus = requestJson.get("HostConfig").get("NanoCpus");
        Assert.assertThat(hostConfig.nanoCpus(), Matchers.is(nanoCpus.longValue()));
    }

    @Test
    public void testInspectNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.28");
        enqueueServerApiResponse(200, "fixtures/1.28/nodeInfo.json");
        final NodeInfo nodeInfo = dockerClient.inspectNode("24ifsmvkjbyhk");
        Assert.assertThat(nodeInfo, Matchers.notNullValue());
        Assert.assertThat(nodeInfo.id(), Matchers.is("24ifsmvkjbyhk"));
        Assert.assertThat(nodeInfo.status(), Matchers.notNullValue());
        Assert.assertThat(nodeInfo.status().addr(), Matchers.is("172.17.0.2"));
        Assert.assertThat(nodeInfo.managerStatus(), Matchers.notNullValue());
        Assert.assertThat(nodeInfo.managerStatus().addr(), Matchers.is("172.17.0.2:2377"));
        Assert.assertThat(nodeInfo.managerStatus().leader(), Matchers.is(true));
        Assert.assertThat(nodeInfo.managerStatus().reachability(), Matchers.is("reachable"));
    }

    @Test
    public void testInspectNonLeaderNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.27");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.27/nodeInfoNonLeader.json")));
        NodeInfo nodeInfo = dockerClient.inspectNode("24ifsmvkjbyhk");
        Assert.assertThat(nodeInfo, Matchers.notNullValue());
        Assert.assertThat(nodeInfo.id(), Matchers.is("24ifsmvkjbyhk"));
        Assert.assertThat(nodeInfo.status(), Matchers.notNullValue());
        Assert.assertThat(nodeInfo.status().addr(), Matchers.is("172.17.0.2"));
        Assert.assertThat(nodeInfo.managerStatus(), Matchers.notNullValue());
        Assert.assertThat(nodeInfo.managerStatus().addr(), Matchers.is("172.17.0.2:2377"));
        Assert.assertThat(nodeInfo.managerStatus().leader(), Matchers.nullValue());
        Assert.assertThat(nodeInfo.managerStatus().reachability(), Matchers.is("reachable"));
    }

    @Test
    public void testInspectNodeNonManager() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.27");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.27/nodeInfoNonManager.json")));
        NodeInfo nodeInfo = dockerClient.inspectNode("24ifsmvkjbyhk");
        Assert.assertThat(nodeInfo, Matchers.notNullValue());
        Assert.assertThat(nodeInfo.id(), Matchers.is("24ifsmvkjbyhk"));
        Assert.assertThat(nodeInfo.status(), Matchers.notNullValue());
        Assert.assertThat(nodeInfo.status().addr(), Matchers.is("172.17.0.2"));
        Assert.assertThat(nodeInfo.managerStatus(), Matchers.nullValue());
    }

    @Test(expected = NodeNotFoundException.class)
    public void testInspectMissingNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.28");
        enqueueServerApiEmptyResponse(404);
        dockerClient.inspectNode("24ifsmvkjbyhk");
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testInspectNonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.28");
        enqueueServerApiEmptyResponse(503);
        dockerClient.inspectNode("24ifsmvkjbyhk");
    }

    @Test
    public void testUpdateNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        enqueueServerApiResponse(200, "fixtures/1.28/listNodes.json");
        final List<Node> nodes = dockerClient.listNodes();
        Assert.assertThat(nodes.size(), Matchers.is(1));
        final Node node = nodes.get(0);
        Assert.assertThat(node.id(), Matchers.equalTo("24ifsmvkjbyhk"));
        Assert.assertThat(node.version().index(), Matchers.equalTo(8L));
        Assert.assertThat(node.spec().name(), Matchers.equalTo("my-node"));
        Assert.assertThat(node.spec().role(), Matchers.equalTo("manager"));
        Assert.assertThat(node.spec().availability(), Matchers.equalTo("active"));
        Assert.assertThat(node.spec().labels(), Matchers.hasKey(Matchers.equalTo("foo")));
        final NodeSpec updatedNodeSpec = NodeSpec.builder(node.spec()).addLabel("foobar", "foobar").build();
        enqueueServerApiVersion("1.28");
        enqueueServerApiEmptyResponse(200);
        dockerClient.updateNode(node.id(), node.version().index(), updatedNodeSpec);
    }

    @Test(expected = DockerException.class)
    public void testUpdateNodeWithInvalidVersion() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        final ObjectNode errorMessage = DefaultDockerClientUnitTest.createObjectNode().put("message", "invalid node version: '7'");
        enqueueServerApiResponse(500, errorMessage);
        final NodeSpec nodeSpec = NodeSpec.builder().addLabel("foo", "baz").name("foobar").availability("active").role("manager").build();
        dockerClient.updateNode("24ifsmvkjbyhk", 7L, nodeSpec);
    }

    @Test(expected = NodeNotFoundException.class)
    public void testUpdateMissingNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        enqueueServerApiError(404, "Error updating node: '24ifsmvkjbyhk'");
        final NodeSpec nodeSpec = NodeSpec.builder().addLabel("foo", "baz").name("foobar").availability("active").role("manager").build();
        dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec);
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testUpdateNonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        enqueueServerApiError(503, "Error updating node: '24ifsmvkjbyhk'");
        final NodeSpec nodeSpec = NodeSpec.builder().name("foobar").addLabel("foo", "baz").availability("active").role("manager").build();
        dockerClient.updateNode("24ifsmvkjbyhk", 8L, nodeSpec);
    }

    @Test
    public void testJoinSwarm() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiEmptyResponse(200);
        SwarmJoin swarmJoin = SwarmJoin.builder().joinToken("token_foo").listenAddr("0.0.0.0:2377").remoteAddrs(Collections.singletonList("10.0.0.10:2377")).build();
        dockerClient.joinSwarm(swarmJoin);
    }

    @Test
    public void testDeleteNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiEmptyResponse(200);
        dockerClient.deleteNode("node-1234");
    }

    @Test(expected = NodeNotFoundException.class)
    public void testDeleteNode_NodeNotFound() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiEmptyResponse(404);
        dockerClient.deleteNode("node-1234");
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testDeleteNode_NodeNotPartOfSwarm() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiEmptyResponse(503);
        dockerClient.deleteNode("node-1234");
    }

    @Test
    public void testCreateServiceWithWarnings() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.25");
        enqueueServerApiResponse(201, "fixtures/1.25/createServiceResponse.json");
        final TaskSpec taskSpec = TaskSpec.builder().containerSpec(ContainerSpec.builder().image("this_image_is_not_found_in_the_registry").build()).build();
        final ServiceSpec spec = ServiceSpec.builder().name("test").taskTemplate(taskSpec).build();
        final ServiceCreateResponse response = dockerClient.createService(spec);
        Assert.assertThat(response.id(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(response.warnings(), Matchers.is(Matchers.hasSize(1)));
        Assert.assertThat(response.warnings(), Matchers.contains("unable to pin image this_image_is_not_found_in_the_registry to digest"));
    }

    @Test
    public void testServiceLogs() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.25");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "text/plain; charset=utf-8").setBody(FixtureUtil.fixture("fixtures/1.25/serviceLogs.txt")));
        final LogStream stream = dockerClient.serviceLogs("serviceId", LogsParam.stderr());
        Assert.assertThat(stream.readFully(), Matchers.is("Log Statement"));
    }

    @Test
    public void testCreateServiceWithPlacementPreference() throws DockerException, IOException, InterruptedException {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final ImmutableList<Preference> prefs = ImmutableList.of(Preference.create(Spread.create("test")));
        final TaskSpec taskSpec = TaskSpec.builder().placement(Placement.create(null, prefs)).containerSpec(ContainerSpec.builder().image("this_image_is_found_in_the_registry").build()).build();
        final ServiceSpec spec = ServiceSpec.builder().name("test").taskTemplate(taskSpec).build();
        enqueueServerApiVersion("1.30");
        enqueueServerApiResponse(201, "fixtures/1.30/createServiceResponse.json");
        final ServiceCreateResponse response = dockerClient.createService(spec);
        Assert.assertThat(response.id(), Matchers.equalTo("ak7w3gjqoa3kuz8xcpnyy0pvl"));
        enqueueServerApiVersion("1.30");
        enqueueServerApiResponse(200, "fixtures/1.30/inspectCreateResponseWithPlacementPrefs.json");
        final Service service = dockerClient.inspectService("ak7w3gjqoa3kuz8xcpnyy0pvl");
        Assert.assertThat(service.spec().taskTemplate().placement(), Matchers.equalTo(taskSpec.placement()));
    }

    @Test
    public void testCreateServiceWithConfig() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        // build() calls /version to check what format of header to send
        enqueueServerApiVersion("1.30");
        enqueueServerApiResponse(201, "fixtures/1.30/configCreateResponse.json");
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        final ConfigCreateResponse configCreateResponse = dockerClient.createConfig(configSpec);
        Assert.assertThat(configCreateResponse.id(), Matchers.equalTo("ktnbjxoalbkvbvedmg1urrz8h"));
        final ConfigBind configBind = ConfigBind.builder().configName(configSpec.name()).configId(configCreateResponse.id()).file(ConfigFile.builder().gid("1000").uid("1000").mode(600L).name(configSpec.name()).build()).build();
        final TaskSpec taskSpec = TaskSpec.builder().containerSpec(ContainerSpec.builder().image("this_image_is_found_in_the_registry").configs(ImmutableList.of(configBind)).build()).build();
        final ServiceSpec spec = ServiceSpec.builder().name("test").taskTemplate(taskSpec).build();
        enqueueServerApiVersion("1.30");
        enqueueServerApiResponse(201, "fixtures/1.30/createServiceResponse.json");
        final ServiceCreateResponse response = dockerClient.createService(spec);
        Assert.assertThat(response.id(), Matchers.equalTo("ak7w3gjqoa3kuz8xcpnyy0pvl"));
    }

    @Test
    public void testListConfigs() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.30/listConfigs.json")));
        final List<Config> configs = dockerClient.listConfigs();
        Assert.assertThat(configs.size(), Matchers.equalTo(1));
        final Config config = configs.get(0);
        Assert.assertThat(config, Matchers.notNullValue());
        Assert.assertThat(config.id(), Matchers.equalTo("ktnbjxoalbkvbvedmg1urrz8h"));
        Assert.assertThat(config.version().index(), Matchers.equalTo(11L));
        final ConfigSpec configSpec = config.configSpec();
        Assert.assertThat(configSpec.name(), Matchers.equalTo("server.conf"));
    }

    @Test
    public void testCreateConfig() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(201).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.30/inspectConfig.json")));
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        final ConfigCreateResponse configCreateResponse = dockerClient.createConfig(configSpec);
        Assert.assertThat(configCreateResponse.id(), Matchers.equalTo("ktnbjxoalbkvbvedmg1urrz8h"));
    }

    @Test(expected = ConflictException.class)
    public void testCreateConfig_ConflictingName() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(409).addHeader("Content-Type", "application/json"));
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        dockerClient.createConfig(configSpec);
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testCreateConfig_NonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(503).addHeader("Content-Type", "application/json"));
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        dockerClient.createConfig(configSpec);
    }

    @Test
    public void testInspectConfig() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.30/inspectConfig.json")));
        final Config config = dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h");
        Assert.assertThat(config, Matchers.notNullValue());
        Assert.assertThat(config.id(), Matchers.equalTo("ktnbjxoalbkvbvedmg1urrz8h"));
        Assert.assertThat(config.version().index(), Matchers.equalTo(11L));
        final ConfigSpec configSpec = config.configSpec();
        Assert.assertThat(configSpec.name(), Matchers.equalTo("app-dev.crt"));
    }

    @Test(expected = NotFoundException.class)
    public void testInspectConfig_NotFound() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"));
        dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h");
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testInspectConfig_NonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(503).addHeader("Content-Type", "application/json"));
        dockerClient.inspectConfig("ktnbjxoalbkvbvedmg1urrz8h");
    }

    @Test
    public void testDeleteConfig() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(204).addHeader("Content-Type", "application/json"));
        dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteConfig_NotFound() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"));
        dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testDeleteConfig_NonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(503).addHeader("Content-Type", "application/json"));
        dockerClient.deleteConfig("ktnbjxoalbkvbvedmg1urrz8h");
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateConfig_NotFound() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"));
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec);
    }

    @Test(expected = NonSwarmNodeException.class)
    public void testUpdateConfig_NonSwarmNode() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.30");
        server.enqueue(new MockResponse().setResponseCode(503).addHeader("Content-Type", "application/json"));
        final ConfigSpec configSpec = ConfigSpec.builder().data(Base64.encodeAsString("foobar")).name("foo.yaml").build();
        dockerClient.updateConfig("ktnbjxoalbkvbvedmg1urrz8h", 11L, configSpec);
    }

    @Test
    public void testListNodes() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.28/listNodes.json")));
        final List<Node> nodes = dockerClient.listNodes();
        Assert.assertThat(nodes.size(), Matchers.equalTo(1));
        final Node node = nodes.get(0);
        Assert.assertThat(node, Matchers.notNullValue());
        Assert.assertThat(node.id(), Matchers.is("24ifsmvkjbyhk"));
        Assert.assertThat(node.version().index(), Matchers.is(8L));
        final NodeSpec nodeSpec = node.spec();
        Assert.assertThat(nodeSpec.name(), Matchers.is("my-node"));
        Assert.assertThat(nodeSpec.role(), Matchers.is("manager"));
        Assert.assertThat(nodeSpec.availability(), Matchers.is("active"));
        Assert.assertThat(nodeSpec.labels().keySet(), Matchers.contains("foo"));
        final NodeDescription desc = node.description();
        Assert.assertThat(desc.hostname(), Matchers.is("bf3067039e47"));
        Assert.assertThat(desc.platform().architecture(), Matchers.is("x86_64"));
        Assert.assertThat(desc.platform().os(), Matchers.is("linux"));
        Assert.assertThat(desc.resources().memoryBytes(), Matchers.is(8272408576L));
        Assert.assertThat(desc.resources().nanoCpus(), Matchers.is(4000000000L));
        final EngineConfig engine = desc.engine();
        Assert.assertThat(engine.engineVersion(), Matchers.is("17.04.0"));
        Assert.assertThat(engine.labels().keySet(), Matchers.contains("foo"));
        Assert.assertThat(engine.plugins().size(), Matchers.equalTo(4));
        Assert.assertThat(node.status(), Matchers.notNullValue());
        Assert.assertThat(node.status().addr(), Matchers.is("172.17.0.2"));
        Assert.assertThat(node.managerStatus(), Matchers.notNullValue());
        Assert.assertThat(node.managerStatus().addr(), Matchers.is("172.17.0.2:2377"));
        Assert.assertThat(node.managerStatus().leader(), Matchers.is(true));
        Assert.assertThat(node.managerStatus().reachability(), Matchers.is("reachable"));
    }

    @Test(expected = DockerException.class)
    public void testListNodesWithServerError() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.28");
        server.enqueue(new MockResponse().setResponseCode(500).addHeader("Content-Type", "application/json"));
        dockerClient.listNodes();
    }

    @Test
    public void testBindBuilderSelinuxLabeling() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final Bind bindNoSelinuxLabel = HostConfig.Bind.builder().from("noselinux").to("noselinux").build();
        final Bind bindSharedSelinuxContent = HostConfig.Bind.builder().from("shared").to("shared").selinuxLabeling(true).build();
        final Bind bindPrivateSelinuxContent = HostConfig.Bind.builder().from("private").to("private").selinuxLabeling(false).build();
        final HostConfig hostConfig = HostConfig.builder().binds(bindNoSelinuxLabel, bindSharedSelinuxContent, bindPrivateSelinuxContent).build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        final JsonNode requestJson = DefaultDockerClientUnitTest.toJson(recordedRequest.getBody());
        final JsonNode binds = requestJson.get("HostConfig").get("Binds");
        Assert.assertThat(binds.isArray(), Matchers.is(true));
        Set<String> bindSet = DefaultDockerClientUnitTest.childrenTextNodes(((ArrayNode) (binds)));
        Assert.assertThat(bindSet, Matchers.hasSize(3));
        Assert.assertThat(bindSet, Matchers.hasItem(Matchers.allOf(Matchers.containsString("noselinux"), Matchers.not(Matchers.containsString("z")), Matchers.not(Matchers.containsString("Z")))));
        Assert.assertThat(bindSet, Matchers.hasItem(Matchers.allOf(Matchers.containsString("shared"), Matchers.containsString("z"))));
        Assert.assertThat(bindSet, Matchers.hasItem(Matchers.allOf(Matchers.containsString("private"), Matchers.containsString("Z"))));
    }

    @Test
    public void testKillContainer() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        server.enqueue(new MockResponse());
        final Signal signal = Signal.SIGHUP;
        dockerClient.killContainer("1234", signal);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        final HttpUrl requestUrl = recordedRequest.getRequestUrl();
        Assert.assertThat(requestUrl.queryParameter("signal"), Matchers.equalTo(signal.toString()));
    }

    @Test
    public void testInspectVolume() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.33/inspectVolume.json")));
        final Volume volume = dockerClient.inspectVolume("my-volume");
        Assert.assertThat(volume.name(), Matchers.is("tardis"));
        Assert.assertThat(volume.driver(), Matchers.is("custom"));
        Assert.assertThat(volume.mountpoint(), Matchers.is("/var/lib/docker/volumes/tardis"));
        Assert.assertThat(volume.status(), Matchers.is(ImmutableMap.of("hello", "world")));
        Assert.assertThat(volume.labels(), Matchers.is(ImmutableMap.of("com.example.some-label", "some-value", "com.example.some-other-label", "some-other-value")));
        Assert.assertThat(volume.scope(), Matchers.is("local"));
        Assert.assertThat(volume.options(), Matchers.is(ImmutableMap.of("foo", "bar", "baz", "qux")));
    }

    @Test
    public void testBufferedRequestEntityProcessing() throws Exception {
        builder.useRequestEntityProcessing(BUFFERED);
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final HostConfig hostConfig = HostConfig.builder().build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        Assert.assertThat(recordedRequest.getHeader("Content-Length"), Matchers.notNullValue());
        Assert.assertThat(recordedRequest.getHeader("Transfer-Encoding"), Matchers.nullValue());
    }

    @Test
    public void testChunkedRequestEntityProcessing() throws Exception {
        builder.useRequestEntityProcessing(CHUNKED);
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        final HostConfig hostConfig = HostConfig.builder().build();
        final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).build();
        server.enqueue(new MockResponse());
        dockerClient.createContainer(containerConfig);
        final RecordedRequest recordedRequest = takeRequestImmediately();
        Assert.assertThat(recordedRequest.getHeader("Content-Length"), Matchers.nullValue());
        Assert.assertThat(recordedRequest.getHeader("Transfer-Encoding"), Matchers.is("chunked"));
    }

    @Test
    public void testGetDistribution() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json").setBody(FixtureUtil.fixture("fixtures/1.30/distribution.json")));
        final Distribution distribution = dockerClient.getDistribution("my-image");
        Assert.assertThat(distribution, Matchers.notNullValue());
        Assert.assertThat(distribution.platforms().size(), Matchers.is(1));
        Assert.assertThat(distribution.platforms().get(0).architecture(), Matchers.is("amd64"));
        Assert.assertThat(distribution.platforms().get(0).os(), Matchers.is("linux"));
        Assert.assertThat(distribution.platforms().get(0).osVersion(), Matchers.is(""));
        Assert.assertThat(distribution.platforms().get(0).variant(), Matchers.is(""));
        Assert.assertThat(distribution.descriptor().size(), Matchers.is(3987495L));
        Assert.assertThat(distribution.descriptor().digest(), Matchers.is("sha256:c0537ff6a5218ef531ece93d4984efc99bbf3f7497c0a7726c88e2bb7584dc96"));
        Assert.assertThat(distribution.descriptor().mediaType(), Matchers.is("application/vnd.docker.distribution.manifest.v2+json"));
        Assert.assertThat(distribution.platforms().get(0).osFeatures(), Matchers.is(ImmutableList.of("feature1", "feature2")));
        Assert.assertThat(distribution.platforms().get(0).features(), Matchers.is(ImmutableList.of("feature1", "feature2")));
        Assert.assertThat(distribution.descriptor().urls(), Matchers.is(ImmutableList.of("url1", "url2")));
    }

    @Test
    public void testInspectTask() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiResponse(200, "fixtures/1.24/task.json");
        final Task task = dockerClient.inspectTask("0kzzo1i0y4jz6027t0k7aezc7");
        Assert.assertThat(task, Matchers.is(pojo(Task.class).where("id", Matchers.is("0kzzo1i0y4jz6027t0k7aezc7")).where("version", Matchers.is(pojo(Version.class).where("index", Matchers.is(71L)))).where("createdAt", Matchers.is(java.sql.Date.from(Instant.parse("2016-06-07T21:07:31.171892745Z")))).where("updatedAt", Matchers.is(java.sql.Date.from(Instant.parse("2016-06-07T21:07:31.376370513Z")))).where("spec", Matchers.is(pojo(TaskSpec.class).where("containerSpec", Matchers.is(pojo(ContainerSpec.class).where("image", Matchers.is("redis")))).where("resources", Matchers.is(pojo(ResourceRequirements.class).where("limits", Matchers.is(pojo(Resources.class))).where("reservations", Matchers.is(pojo(Resources.class)))))))));
    }

    @Test
    public void testListTaskWithCriteria() throws Exception {
        final DefaultDockerClient dockerClient = new DefaultDockerClient(builder);
        enqueueServerApiVersion("1.24");
        enqueueServerApiResponse(200, "fixtures/1.24/tasks.json");
        final List<Task> tasks = dockerClient.listTasks();
        // Throw away the first request that gets the docker server version
        takeRequestImmediately();
        final RecordedRequest recordedRequest = takeRequestImmediately();
        Assert.assertThat(recordedRequest.getRequestUrl().querySize(), Matchers.is(0));
        Assert.assertThat(tasks, Matchers.contains(pojo(Task.class).where("id", Matchers.is("0kzzo1i0y4jz6027t0k7aezc7")), pojo(Task.class).where("id", Matchers.is("1yljwbmlr8er2waf8orvqpwms"))));
        enqueueServerApiVersion("1.24");
        enqueueServerApiResponse(200, "fixtures/1.24/tasks.json");
        final String taskId = "task-1";
        dockerClient.listTasks(Task.find().taskId(taskId).build());
        takeRequestImmediately();
        final RecordedRequest recordedRequest2 = takeRequestImmediately();
        final HttpUrl requestUrl2 = recordedRequest2.getRequestUrl();
        Assert.assertThat(requestUrl2.querySize(), Matchers.is(1));
        final JsonNode requestJson2 = DefaultDockerClientUnitTest.toJson(recordedRequest2.getRequestUrl().queryParameter("filters"));
        Assert.assertThat(requestJson2, Matchers.is(jsonObject().where("id", Matchers.is(jsonArray(Matchers.contains(jsonText(taskId)))))));
        enqueueServerApiVersion("1.24");
        enqueueServerApiResponse(200, "fixtures/1.24/tasks.json");
        final String serviceName = "service-1";
        dockerClient.listTasks(Task.find().serviceName(serviceName).build());
        takeRequestImmediately();
        final RecordedRequest recordedRequest3 = takeRequestImmediately();
        final HttpUrl requestUrl3 = recordedRequest3.getRequestUrl();
        Assert.assertThat(requestUrl3.querySize(), Matchers.is(1));
        final JsonNode requestJson3 = DefaultDockerClientUnitTest.toJson(recordedRequest3.getRequestUrl().queryParameter("filters"));
        Assert.assertThat(requestJson3, Matchers.is(jsonObject().where("service", Matchers.is(jsonArray(Matchers.contains(jsonText(serviceName)))))));
    }
}

