/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.flink;


import RestOptions.PORT;
import java.io.IOException;
import java.util.Collections;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.LocalEnvironment;
import org.apache.flink.api.java.RemoteEnvironment;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.RemoteStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.internal.util.reflection.Whitebox;


/**
 * Tests for {@link FlinkExecutionEnvironments}.
 */
public class FlinkExecutionEnvironmentsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSetParallelismBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setParallelism(42);
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(options.getParallelism(), Is.is(42));
        Assert.assertThat(bev.getParallelism(), Is.is(42));
    }

    @Test
    public void shouldSetParallelismStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setParallelism(42);
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(options.getParallelism(), Is.is(42));
        Assert.assertThat(sev.getParallelism(), Is.is(42));
    }

    @Test
    public void shouldSetMaxParallelismStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setMaxParallelism(42);
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(options.getMaxParallelism(), Is.is(42));
        Assert.assertThat(sev.getMaxParallelism(), Is.is(42));
    }

    @Test
    public void shouldInferParallelismFromEnvironmentBatch() throws IOException {
        String flinkConfDir = extractFlinkConfig();
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setFlinkMaster("host:80");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList(), flinkConfDir);
        Assert.assertThat(options.getParallelism(), Is.is(23));
        Assert.assertThat(bev.getParallelism(), Is.is(23));
    }

    @Test
    public void shouldInferParallelismFromEnvironmentStreaming() throws IOException {
        String confDir = extractFlinkConfig();
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setFlinkMaster("host:80");
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList(), confDir);
        Assert.assertThat(options.getParallelism(), Is.is(23));
        Assert.assertThat(sev.getParallelism(), Is.is(23));
    }

    @Test
    public void shouldFallbackToDefaultParallelismBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setFlinkMaster("host:80");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(options.getParallelism(), Is.is(1));
        Assert.assertThat(bev.getParallelism(), Is.is(1));
    }

    @Test
    public void shouldFallbackToDefaultParallelismStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setFlinkMaster("host:80");
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(options.getParallelism(), Is.is(1));
        Assert.assertThat(sev.getParallelism(), Is.is(1));
    }

    @Test
    public void useDefaultParallelismFromContextBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(bev, Matchers.instanceOf(LocalEnvironment.class));
        Assert.assertThat(options.getParallelism(), Is.is(LocalStreamEnvironment.getDefaultLocalParallelism()));
        Assert.assertThat(bev.getParallelism(), Is.is(LocalStreamEnvironment.getDefaultLocalParallelism()));
    }

    @Test
    public void useDefaultParallelismFromContextStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(sev, Matchers.instanceOf(LocalStreamEnvironment.class));
        Assert.assertThat(options.getParallelism(), Is.is(LocalStreamEnvironment.getDefaultLocalParallelism()));
        Assert.assertThat(sev.getParallelism(), Is.is(LocalStreamEnvironment.getDefaultLocalParallelism()));
    }

    @Test
    public void shouldParsePortForRemoteEnvironmentBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host:1234");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(bev, Matchers.instanceOf(RemoteEnvironment.class));
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("host"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(1234));
    }

    @Test
    public void shouldParsePortForRemoteEnvironmentStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host:1234");
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(sev, Matchers.instanceOf(RemoteStreamEnvironment.class));
        Assert.assertThat(Whitebox.getInternalState(sev, "host"), Is.is("host"));
        Assert.assertThat(Whitebox.getInternalState(sev, "port"), Is.is(1234));
    }

    @Test
    public void shouldAllowPortOmissionForRemoteEnvironmentBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(bev, Matchers.instanceOf(RemoteEnvironment.class));
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("host"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldAllowPortOmissionForRemoteEnvironmentStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host");
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(sev, Matchers.instanceOf(RemoteStreamEnvironment.class));
        Assert.assertThat(Whitebox.getInternalState(sev, "host"), Is.is("host"));
        Assert.assertThat(Whitebox.getInternalState(sev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldTreatAutoAndEmptyHostTheSameBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        ExecutionEnvironment sev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        options.setFlinkMaster("[auto]");
        ExecutionEnvironment sev2 = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertEquals(sev.getClass(), sev2.getClass());
    }

    @Test
    public void shouldTreatAutoAndEmptyHostTheSameStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        options.setFlinkMaster("[auto]");
        StreamExecutionEnvironment sev2 = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertEquals(sev.getClass(), sev2.getClass());
    }

    @Test
    public void shouldDetectMalformedPortBatch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host:p0rt");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unparseable port number");
        FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
    }

    @Test
    public void shouldDetectMalformedPortStreaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("host:p0rt");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unparseable port number");
        FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
    }

    @Test
    public void shouldSupportIPv4Batch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("192.168.1.1:1234");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("192.168.1.1"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(1234));
        options.setFlinkMaster("192.168.1.1");
        bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("192.168.1.1"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldSupportIPv4Streaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("192.168.1.1:1234");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("192.168.1.1"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(1234));
        options.setFlinkMaster("192.168.1.1");
        bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("192.168.1.1"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldSupportIPv6Batch() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("[FE80:CD00:0000:0CDE:1257:0000:211E:729C]:1234");
        ExecutionEnvironment bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("FE80:CD00:0000:0CDE:1257:0000:211E:729C"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(1234));
        options.setFlinkMaster("FE80:CD00:0000:0CDE:1257:0000:211E:729C");
        bev = FlinkExecutionEnvironments.createBatchExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(bev, "host"), Is.is("FE80:CD00:0000:0CDE:1257:0000:211E:729C"));
        Assert.assertThat(Whitebox.getInternalState(bev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldSupportIPv6Streaming() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(FlinkRunner.class);
        options.setFlinkMaster("[FE80:CD00:0000:0CDE:1257:0000:211E:729C]:1234");
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(sev, "host"), Is.is("FE80:CD00:0000:0CDE:1257:0000:211E:729C"));
        Assert.assertThat(Whitebox.getInternalState(sev, "port"), Is.is(1234));
        options.setFlinkMaster("FE80:CD00:0000:0CDE:1257:0000:211E:729C");
        sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        Assert.assertThat(Whitebox.getInternalState(sev, "host"), Is.is("FE80:CD00:0000:0CDE:1257:0000:211E:729C"));
        Assert.assertThat(Whitebox.getInternalState(sev, "port"), Is.is(PORT.defaultValue()));
    }

    @Test
    public void shouldSetSavepointRestoreForRemoteStreaming() {
        String path = "fakePath";
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setFlinkMaster("host:80");
        options.setSavepointPath(path);
        StreamExecutionEnvironment sev = FlinkExecutionEnvironments.createStreamExecutionEnvironment(options, Collections.emptyList());
        // subject to change with https://issues.apache.org/jira/browse/FLINK-11048
        Assert.assertThat(sev, Matchers.instanceOf(RemoteStreamEnvironment.class));
        Assert.assertThat(Whitebox.getInternalState(sev, "restoreSettings"), Is.is(SavepointRestoreSettings.forPath(path)));
    }
}

