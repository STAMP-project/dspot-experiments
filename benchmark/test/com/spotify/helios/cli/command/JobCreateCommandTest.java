/**
 * -
 * -\-\-
 * Helios Tools
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
package com.spotify.helios.cli.command;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spotify.helios.client.HeliosClient;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobCreateCommandTest {
    private static final Logger log = LoggerFactory.getLogger(JobCreateCommandTest.class);

    private static final String JOB_NAME = "foo";

    private static final String JOB_ID = (JobCreateCommandTest.JOB_NAME) + ":123";

    private static final String EXEC_HEALTH_CHECK = "touch /this";

    private static final List<String> SECURITY_OPT = ImmutableList.of("label:user:dxia", "apparmor:foo");

    private static final String NETWORK_MODE = "host";

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private JobCreateCommand command;

    private Map<String, String> envVars = Maps.newHashMap();

    @Test
    public void testValidJobCreateCommand() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("spotify/busybox:latest");
        Mockito.when(options.getString("exec_check")).thenReturn(JobCreateCommandTest.EXEC_HEALTH_CHECK);
        // For some reason the mocked options.getInt() returns 0 by default.
        // Explicitly return null to check that the value from the JSON file doesn't get overwritten.
        Mockito.when(options.getInt("grace_period")).thenReturn(null);
        // TODO (mbrown): this path is weird when running from IntelliJ, should be changed to not
        // care about CWD
        Mockito.doReturn(new File("src/test/resources/job_config.json")).when(options).get("file");
        Mockito.doReturn(JobCreateCommandTest.SECURITY_OPT).when(options).getList("security_opt");
        Mockito.when(options.getString("network_mode")).thenReturn(JobCreateCommandTest.NETWORK_MODE);
        Mockito.when(options.getList("metadata")).thenReturn(Lists.<Object>newArrayList("a=1", "b=2"));
        Mockito.when(options.getList("labels")).thenReturn(Lists.<Object>newArrayList("a=b", "c=d"));
        Mockito.doReturn(ImmutableList.of("cap1", "cap2")).when(options).getList("add_capability");
        Mockito.doReturn(ImmutableList.of("cap3", "cap4")).when(options).getList("drop_capability");
        Mockito.when(options.getString("runtime")).thenReturn("nvidia");
        Mockito.when(options.getList("rollout_options")).thenReturn(Lists.<Object>newArrayList("overlap=true", "parallelism=2", "foo=bar"));
        final int ret = runCommand();
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"created\":null"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"env\":{\"JVM_ARGS\":\"-Ddw.feature.randomFeatureFlagEnabled=true\"}"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"metadata\":{\"a\":\"1\",\"b\":\"2\"},"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"gracePeriod\":100"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("\"healthCheck\":{\"type\":\"exec\"," + "\"command\":[\"touch\",\"/this\"],\"type\":\"exec\"},")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"securityOpt\":[\"label:user:dxia\",\"apparmor:foo\"]"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"networkMode\":\"host\""));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"expires\":null"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"addCapabilities\":[\"cap1\",\"cap2\"]"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"dropCapabilities\":[\"cap3\",\"cap4\"]"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"labels\":{\"a\":\"b\",\"c\":\"d\"}"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"runtime\":\"nvidia\""));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("rolloutOptions\":{\"ignoreFailures\":null,\"migrate\":null," + "\"overlap\":true,\"parallelism\":2,\"timeout\":null,\"token\":null}")));
    }

    @Test
    public void testJobCreateCommandFailsWithInvalidJobId() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_NAME);
        Mockito.when(options.getString("image")).thenReturn("spotify/busybox:latest");
        final int ret = runCommand();
        Assert.assertEquals(1, ret);
    }

    @Test
    public void testJobCreateCommandFailsWithInvalidPortProtocol() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("spotify/busybox:latest");
        Mockito.doReturn(ImmutableList.of("dns=53:53/http")).when(options).getList("port");
        final int ret = runCommand(true);
        Assert.assertEquals(1, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"status\":\"INVALID_JOB_DEFINITION\"}"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"errors\":[\"Invalid port mapping protocol: http\"]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJobCreateCommandFailsWithInvalidFilePath() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("spotify/busybox:latest");
        Mockito.doReturn(new File("non/existant/file")).when(options).get("file");
        runCommand();
    }

    /**
     * Test that when the environment variables we have defaults for are set, they are picked up in
     * the job metadata.
     */
    @Test
    public void testMetadataPicksUpEnvVars() throws Exception {
        envVars.put("GIT_COMMIT", "abcdef1234");
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("spotify/busybox:latest");
        Mockito.when(options.getList("metadata")).thenReturn(Lists.<Object>newArrayList("foo=bar"));
        final int ret = runCommand();
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        final String expectedOutputPrefix = "Creating job: ";
        MatcherAssert.assertThat(output, Matchers.startsWith(expectedOutputPrefix));
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonFromOutput = output.split("\n")[0].substring(expectedOutputPrefix.length());
        final JsonNode jsonNode = objectMapper.readTree(jsonFromOutput);
        final ArrayList<String> fieldNames = Lists.newArrayList(jsonNode.fieldNames());
        MatcherAssert.assertThat(fieldNames, CoreMatchers.hasItem("metadata"));
        MatcherAssert.assertThat(jsonNode.get("metadata").isObject(), Matchers.equalTo(true));
        final ObjectNode metadataNode = ((ObjectNode) (jsonNode.get("metadata")));
        MatcherAssert.assertThat(metadataNode.get("foo").asText(), Matchers.equalTo("bar"));
        MatcherAssert.assertThat(metadataNode.get("GIT_COMMIT").asText(), Matchers.equalTo("abcdef1234"));
    }

    /**
     * Ensure that creating a job from a json file which has added and dropped capabilities is not
     * overwritten by empty arguments in the CLI switches.
     */
    @Test
    public void testAddCapabilitiesFromJsonFile() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("foobar");
        Mockito.when(options.get("file")).thenReturn(new File("src/test/resources/job_config_extra_capabilities.json"));
        Mockito.when(options.getList("add-capability")).thenReturn(Collections.emptyList());
        Mockito.when(options.getList("drop-capability")).thenReturn(Collections.emptyList());
        Assert.assertEquals(0, runCommand());
        Mockito.verify(client).createJob(ArgumentMatchers.argThat(hasCapabilities(ImmutableSet.of("cap_one", "cap_two"), ImmutableSet.of("cap_three", "cap_four"))));
    }

    /**
     * Check that metadata from JSON file and CLI switch is merged correctly. Metadata from CLI
     * switch takes precedence over those from JSON file.
     */
    @Test
    public void testAddMetadataFromJsonFile() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("foobar");
        Mockito.when(options.get("file")).thenReturn(new File("src/test/resources/job_config_extra_metadata.json"));
        final List<Object> value = new ArrayList<>();
        value.add("baz=qux2");
        Mockito.when(options.getList("metadata")).thenReturn(value);
        Assert.assertEquals(0, runCommand());
        final Map<String, String> of = ImmutableMap.of("foo", "bar", "baz", "qux2");
        Mockito.verify(client).createJob(ArgumentMatchers.argThat(hasMetadata(Matchers.equalTo(of))));
    }

    @Test
    public void testLabelsFromJsonFile() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("foobar");
        Mockito.when(options.get("file")).thenReturn(new File("src/test/resources/job_config_extra_labels.json"));
        Mockito.when(options.getList("labels")).thenReturn(Collections.emptyList());
        Assert.assertEquals(0, runCommand());
        Mockito.verify(client).createJob(ArgumentMatchers.argThat(hasLabels(ImmutableMap.of("foo", "bar", "baz", "qux"))));
    }

    @Test
    public void testRuntimeFromJsonFile() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("foobar");
        Mockito.when(options.get("file")).thenReturn(new File("src/test/resources/job_config_with_runtime.json"));
        Assert.assertEquals(0, runCommand());
        Mockito.verify(client).createJob(ArgumentMatchers.argThat(hasRuntime("nvidia")));
    }

    @Test
    public void testRuntimeFromCliOverridesJsonFile() throws Exception {
        Mockito.when(options.getString("id")).thenReturn(JobCreateCommandTest.JOB_ID);
        Mockito.when(options.getString("image")).thenReturn("foobar");
        Mockito.when(options.get("file")).thenReturn(new File("src/test/resources/job_config_with_runtime.json"));
        Mockito.when(options.getString("runtime")).thenReturn("rkt");
        Assert.assertEquals(0, runCommand());
        Mockito.verify(client).createJob(ArgumentMatchers.argThat(hasRuntime("rkt")));
    }
}

