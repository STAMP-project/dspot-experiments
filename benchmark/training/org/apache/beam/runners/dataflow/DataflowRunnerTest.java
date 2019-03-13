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
package org.apache.beam.runners.dataflow;


import Create.Values;
import Dataflow.Projects.Locations.Jobs;
import DataflowPipelineOptions.FlexResourceSchedulingGoal.COST_OPTIMIZED;
import DataflowRunner.GCS_UPLOAD_BUFFER_SIZE_BYTES_DEFAULT;
import GcsUtil.StorageObjectOrIOException;
import PipelineVisitor.Defaults;
import StreamingShardedWriteFactory.DEFAULT_NUM_SHARDS;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.api.services.dataflow.Dataflow;
import com.google.api.services.dataflow.model.DataflowPackage;
import com.google.api.services.dataflow.model.Job;
import com.google.auto.service.AutoService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.options.DataflowPipelineWorkerPoolOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.Pipeline.PipelineVisitor;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.extensions.gcp.auth.NoopCredentialFactory;
import org.apache.beam.sdk.extensions.gcp.auth.TestCredential;
import org.apache.beam.sdk.extensions.gcp.storage.NoopPathValidator;
import org.apache.beam.sdk.io.DynamicFileDestinations;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.runners.TransformHierarchy;
import org.apache.beam.sdk.runners.TransformHierarchy.Node;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.GcsUtil;
import org.apache.beam.sdk.util.gcsfs.GcsPath;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Throwables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static CompositeBehavior.ENTER_TRANSFORM;


/**
 * Tests for the {@link DataflowRunner}.
 *
 * <p>Implements {@link Serializable} because it is caught in closures.
 */
@RunWith(JUnit4.class)
public class DataflowRunnerTest implements Serializable {
    private static final String VALID_BUCKET = "valid-bucket";

    private static final String VALID_STAGING_BUCKET = "gs://valid-bucket/staging";

    private static final String VALID_TEMP_BUCKET = "gs://valid-bucket/temp";

    private static final String VALID_PROFILE_BUCKET = "gs://valid-bucket/profiles";

    private static final String NON_EXISTENT_BUCKET = "gs://non-existent-bucket/location";

    private static final String PROJECT_ID = "some-project";

    private static final String REGION_ID = "some-region-1";

    @Rule
    public transient TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Rule
    public transient ExpectedLogs expectedLogs = ExpectedLogs.none(DataflowRunner.class);

    private transient Jobs mockJobs;

    private transient GcsUtil mockGcsUtil;

    @Test
    public void testPathValidation() {
        String[] args = new String[]{ "--runner=DataflowRunner", "--tempLocation=/tmp/not/a/gs/path", "--project=test-project", "--credentialFactoryClass=" + (NoopCredentialFactory.class.getName()) };
        try {
            Pipeline.create(PipelineOptionsFactory.fromArgs(args).create()).run();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(Throwables.getStackTraceAsString(e), Matchers.containsString("DataflowRunner requires gcpTempLocation"));
        }
    }

    @Test
    public void testPathExistsValidation() {
        String[] args = new String[]{ "--runner=DataflowRunner", "--tempLocation=gs://does/not/exist", "--project=test-project", "--credentialFactoryClass=" + (NoopCredentialFactory.class.getName()) };
        try {
            Pipeline.create(PipelineOptionsFactory.fromArgs(args).create()).run();
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertThat(Throwables.getStackTraceAsString(e), Matchers.both(Matchers.containsString("gs://does/not/exist")).and(Matchers.containsString("Unable to verify that GCS bucket gs://does exists")));
        }
    }

    @Test
    public void testPathValidatorOverride() {
        String[] args = new String[]{ "--runner=DataflowRunner", "--tempLocation=/tmp/testing", "--project=test-project", "--credentialFactoryClass=" + (NoopCredentialFactory.class.getName()), "--pathValidatorClass=" + (NoopPathValidator.class.getName()) };
        // Should not crash, because gcpTempLocation should get set from tempLocation
        TestPipeline.fromOptions(PipelineOptionsFactory.fromArgs(args).create());
    }

    @Test
    public void testFromOptionsWithUppercaseConvertsToLowercase() throws Exception {
        String mixedCase = "ThisJobNameHasMixedCase";
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setJobName(mixedCase);
        DataflowRunner.fromOptions(options);
        Assert.assertThat(options.getJobName(), Matchers.equalTo(mixedCase.toLowerCase()));
    }

    @Test
    public void testFromOptionsUserAgentFromPipelineInfo() throws Exception {
        DataflowPipelineOptions options = buildPipelineOptions();
        DataflowRunner.fromOptions(options);
        String expectedName = DataflowRunnerInfo.getDataflowRunnerInfo().getName().replace(" ", "_");
        Assert.assertThat(options.getUserAgent(), Matchers.containsString(expectedName));
        String expectedVersion = DataflowRunnerInfo.getDataflowRunnerInfo().getVersion();
        Assert.assertThat(options.getUserAgent(), Matchers.containsString(expectedVersion));
    }

    /**
     * Invasive mock-based test for checking that the JSON generated for the pipeline options has not
     * had vital fields pruned.
     */
    @Test
    public void testSettingOfSdkPipelineOptions() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        // These options are important only for this test, and need not be global to the test class
        options.setAppName(DataflowRunnerTest.class.getSimpleName());
        options.setJobName("some-job-name");
        Pipeline p = Pipeline.create(options);
        p.run();
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        Map<String, Object> sdkPipelineOptions = jobCaptor.getValue().getEnvironment().getSdkPipelineOptions();
        Assert.assertThat(sdkPipelineOptions, Matchers.hasKey("options"));
        Map<String, Object> optionsMap = ((Map<String, Object>) (sdkPipelineOptions.get("options")));
        Assert.assertThat(optionsMap, Matchers.hasEntry("appName", ((Object) (options.getAppName()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("project", ((Object) (options.getProject()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("pathValidatorClass", ((Object) (options.getPathValidatorClass().getName()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("runner", ((Object) (options.getRunner().getName()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("jobName", ((Object) (options.getJobName()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("tempLocation", ((Object) (options.getTempLocation()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("stagingLocation", ((Object) (options.getStagingLocation()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("stableUniqueNames", ((Object) (options.getStableUniqueNames().toString()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("streaming", ((Object) (options.isStreaming()))));
        Assert.assertThat(optionsMap, Matchers.hasEntry("numberOfWorkerHarnessThreads", ((Object) (options.getNumberOfWorkerHarnessThreads()))));
    }

    @Test
    public void testSettingFlexRS() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setFlexRSGoal(COST_OPTIMIZED);
        Pipeline p = Pipeline.create(options);
        p.run();
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        Assert.assertEquals("FLEXRS_COST_OPTIMIZED", jobCaptor.getValue().getEnvironment().getFlexResourceSchedulingGoal());
    }

    /**
     * PipelineOptions used to test auto registration of Jackson modules.
     */
    public interface JacksonIncompatibleOptions extends PipelineOptions {
        DataflowRunnerTest.JacksonIncompatible getJacksonIncompatible();

        void setJacksonIncompatible(DataflowRunnerTest.JacksonIncompatible value);
    }

    /**
     * A Jackson {@link Module} to test auto-registration of modules.
     */
    @AutoService(Module.class)
    public static class RegisteredTestModule extends SimpleModule {
        public RegisteredTestModule() {
            super("RegisteredTestModule");
            setMixInAnnotation(DataflowRunnerTest.JacksonIncompatible.class, DataflowRunnerTest.JacksonIncompatibleMixin.class);
        }
    }

    /**
     * A class which Jackson does not know how to serialize/deserialize.
     */
    public static class JacksonIncompatible {
        private final String value;

        public JacksonIncompatible(String value) {
            this.value = value;
        }
    }

    /**
     * A Jackson mixin used to add annotations to other classes.
     */
    @JsonDeserialize(using = DataflowRunnerTest.JacksonIncompatibleDeserializer.class)
    @JsonSerialize(using = DataflowRunnerTest.JacksonIncompatibleSerializer.class)
    public static final class JacksonIncompatibleMixin {}

    /**
     * A Jackson deserializer for {@link JacksonIncompatible}.
     */
    public static class JacksonIncompatibleDeserializer extends JsonDeserializer<DataflowRunnerTest.JacksonIncompatible> {
        @Override
        public DataflowRunnerTest.JacksonIncompatible deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JsonProcessingException, IOException {
            return new DataflowRunnerTest.JacksonIncompatible(jsonParser.readValueAs(String.class));
        }
    }

    /**
     * A Jackson serializer for {@link JacksonIncompatible}.
     */
    public static class JacksonIncompatibleSerializer extends JsonSerializer<DataflowRunnerTest.JacksonIncompatible> {
        @Override
        public void serialize(DataflowRunnerTest.JacksonIncompatible jacksonIncompatible, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws JsonProcessingException, IOException {
            jsonGenerator.writeString(jacksonIncompatible.value);
        }
    }

    @Test
    public void testSettingOfPipelineOptionsWithCustomUserType() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.as(DataflowRunnerTest.JacksonIncompatibleOptions.class).setJacksonIncompatible(new DataflowRunnerTest.JacksonIncompatible("userCustomTypeTest"));
        Pipeline p = Pipeline.create(options);
        p.run();
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        Map<String, Object> sdkPipelineOptions = jobCaptor.getValue().getEnvironment().getSdkPipelineOptions();
        Assert.assertThat(sdkPipelineOptions, Matchers.hasKey("options"));
        Map<String, Object> optionsMap = ((Map<String, Object>) (sdkPipelineOptions.get("options")));
        Assert.assertThat(optionsMap, Matchers.hasEntry("jacksonIncompatible", ((Object) ("userCustomTypeTest"))));
    }

    @Test
    public void testRun() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        Pipeline p = buildDataflowPipeline(options);
        DataflowPipelineJob job = ((DataflowPipelineJob) (p.run()));
        Assert.assertEquals("newid", job.getJobId());
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    /**
     * Options for testing.
     */
    public interface RuntimeTestOptions extends PipelineOptions {
        ValueProvider<String> getInput();

        void setInput(ValueProvider<String> value);

        ValueProvider<String> getOutput();

        void setOutput(ValueProvider<String> value);
    }

    @Test
    public void testTextIOWithRuntimeParameters() throws IOException {
        DataflowPipelineOptions dataflowOptions = buildPipelineOptions();
        DataflowRunnerTest.RuntimeTestOptions options = dataflowOptions.as(DataflowRunnerTest.RuntimeTestOptions.class);
        Pipeline p = buildDataflowPipeline(dataflowOptions);
        p.apply(TextIO.read().from(options.getInput())).apply(TextIO.write().to(options.getOutput()));
    }

    /**
     * Tests that all reads are consumed by at least one {@link PTransform}.
     */
    @Test
    public void testUnconsumedReads() throws IOException {
        DataflowPipelineOptions dataflowOptions = buildPipelineOptions();
        DataflowRunnerTest.RuntimeTestOptions options = dataflowOptions.as(DataflowRunnerTest.RuntimeTestOptions.class);
        Pipeline p = buildDataflowPipeline(dataflowOptions);
        p.apply(TextIO.read().from(options.getInput()));
        DataflowRunner.fromOptions(dataflowOptions).replaceTransforms(p);
        final AtomicBoolean unconsumedSeenAsInput = new AtomicBoolean();
        p.traverseTopologically(new PipelineVisitor.Defaults() {
            @Override
            public void visitPrimitiveTransform(Node node) {
                unconsumedSeenAsInput.set(true);
            }
        });
        Assert.assertThat(unconsumedSeenAsInput.get(), Matchers.is(true));
    }

    @Test
    public void testRunReturnDifferentRequestId() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        Dataflow mockDataflowClient = options.getDataflowClient();
        Dataflow.Projects.Locations.Jobs.Create mockRequest = Mockito.mock(Create.class);
        Mockito.when(mockDataflowClient.projects().locations().jobs().create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), ArgumentMatchers.any(Job.class))).thenReturn(mockRequest);
        Job resultJob = new Job();
        resultJob.setId("newid");
        // Return a different request id.
        resultJob.setClientRequestId("different_request_id");
        Mockito.when(mockRequest.execute()).thenReturn(resultJob);
        Pipeline p = buildDataflowPipeline(options);
        try {
            p.run();
            Assert.fail("Expected DataflowJobAlreadyExistsException");
        } catch (DataflowJobAlreadyExistsException expected) {
            Assert.assertThat(expected.getMessage(), Matchers.containsString(("If you want to submit a second job, try again by setting a " + "different name using --jobName.")));
            Assert.assertEquals(expected.getJob().getJobId(), resultJob.getId());
        }
    }

    @Test
    public void testUpdate() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setUpdate(true);
        options.setJobName("oldJobName");
        Pipeline p = buildDataflowPipeline(options);
        DataflowPipelineJob job = ((DataflowPipelineJob) (p.run()));
        Assert.assertEquals("newid", job.getJobId());
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testUploadGraph() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setExperiments(Arrays.asList("upload_graph"));
        Pipeline p = buildDataflowPipeline(options);
        DataflowPipelineJob job = ((DataflowPipelineJob) (p.run()));
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
        Assert.assertTrue(jobCaptor.getValue().getSteps().isEmpty());
        Assert.assertTrue(jobCaptor.getValue().getStepsLocation().startsWith("gs://valid-bucket/temp/staging/dataflow_graph"));
    }

    @Test
    public void testUpdateNonExistentPipeline() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Could not find running job named badjobname");
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setUpdate(true);
        options.setJobName("badJobName");
        Pipeline p = buildDataflowPipeline(options);
        p.run();
    }

    @Test
    public void testUpdateAlreadyUpdatedPipeline() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setUpdate(true);
        options.setJobName("oldJobName");
        Dataflow mockDataflowClient = options.getDataflowClient();
        Dataflow.Projects.Locations.Jobs.Create mockRequest = Mockito.mock(Create.class);
        Mockito.when(mockDataflowClient.projects().locations().jobs().create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), ArgumentMatchers.any(Job.class))).thenReturn(mockRequest);
        final Job resultJob = new Job();
        resultJob.setId("newid");
        // Return a different request id.
        resultJob.setClientRequestId("different_request_id");
        Mockito.when(mockRequest.execute()).thenReturn(resultJob);
        Pipeline p = buildDataflowPipeline(options);
        thrown.expect(DataflowJobAlreadyUpdatedException.class);
        thrown.expect(new TypeSafeMatcher<DataflowJobAlreadyUpdatedException>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(("Expected job ID: " + (resultJob.getId())));
            }

            @Override
            protected boolean matchesSafely(DataflowJobAlreadyUpdatedException item) {
                return resultJob.getId().equals(item.getJob().getJobId());
            }
        });
        thrown.expectMessage(("The job named oldjobname with id: oldJobId has already been updated " + "into job id: newid and cannot be updated again."));
        p.run();
    }

    @Test
    public void testRunWithFiles() throws IOException {
        // Test that the function DataflowRunner.stageFiles works as expected.
        final String cloudDataflowDataset = "somedataset";
        // Create some temporary files.
        File temp1 = File.createTempFile("DataflowRunnerTest", "txt");
        temp1.deleteOnExit();
        File temp2 = File.createTempFile("DataflowRunnerTest2", "txt");
        temp2.deleteOnExit();
        String overridePackageName = "alias.txt";
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setFilesToStage(ImmutableList.of(temp1.getAbsolutePath(), ((overridePackageName + "=") + (temp2.getAbsolutePath()))));
        options.setStagingLocation(DataflowRunnerTest.VALID_STAGING_BUCKET);
        options.setTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setTempDatasetId(cloudDataflowDataset);
        options.setProject(DataflowRunnerTest.PROJECT_ID);
        options.setRegion(DataflowRunnerTest.REGION_ID);
        options.setJobName("job");
        options.setDataflowClient(buildMockDataflow());
        options.setGcsUtil(mockGcsUtil);
        options.setGcpCredential(new TestCredential());
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).then(( invocation) -> FileChannel.open(Files.createTempFile("channel-", ".tmp"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE));
        Pipeline p = buildDataflowPipeline(options);
        DataflowPipelineJob job = ((DataflowPipelineJob) (p.run()));
        Assert.assertEquals("newid", job.getJobId());
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        Job workflowJob = jobCaptor.getValue();
        DataflowRunnerTest.assertValidJob(workflowJob);
        Assert.assertEquals(2, workflowJob.getEnvironment().getWorkerPools().get(0).getPackages().size());
        DataflowPackage workflowPackage1 = workflowJob.getEnvironment().getWorkerPools().get(0).getPackages().get(0);
        Assert.assertThat(workflowPackage1.getName(), Matchers.startsWith(temp1.getName()));
        DataflowPackage workflowPackage2 = workflowJob.getEnvironment().getWorkerPools().get(0).getPackages().get(1);
        Assert.assertEquals(overridePackageName, workflowPackage2.getName());
        Assert.assertEquals(GcsPath.fromUri(DataflowRunnerTest.VALID_TEMP_BUCKET).toResourceName(), workflowJob.getEnvironment().getTempStoragePrefix());
        Assert.assertEquals(cloudDataflowDataset, workflowJob.getEnvironment().getDataset());
        Assert.assertEquals(DataflowRunnerInfo.getDataflowRunnerInfo().getName(), workflowJob.getEnvironment().getUserAgent().get("name"));
        Assert.assertEquals(DataflowRunnerInfo.getDataflowRunnerInfo().getVersion(), workflowJob.getEnvironment().getUserAgent().get("version"));
    }

    @Test
    public void runWithDefaultFilesToStage() throws Exception {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setFilesToStage(null);
        DataflowRunner.fromOptions(options);
        Assert.assertTrue((!(options.getFilesToStage().isEmpty())));
    }

    @Test
    public void testGcsStagingLocationInitialization() throws Exception {
        // Set temp location (required), and check that staging location is set.
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setProject(DataflowRunnerTest.PROJECT_ID);
        options.setGcpCredential(new TestCredential());
        options.setGcsUtil(mockGcsUtil);
        options.setRunner(DataflowRunner.class);
        DataflowRunner.fromOptions(options);
        Assert.assertNotNull(options.getStagingLocation());
    }

    @Test
    public void testInvalidGcpTempLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setGcpTempLocation("file://temp/location");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString("Expected a valid 'gs://' path but was given"));
        DataflowRunner.fromOptions(options);
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testNonGcsTempLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setTempLocation("file://temp/location");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("DataflowRunner requires gcpTempLocation, " + "but failed to retrieve a value from PipelineOptions"));
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testInvalidStagingLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setStagingLocation("file://my/staging/location");
        try {
            DataflowRunner.fromOptions(options);
            Assert.fail("fromOptions should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected a valid 'gs://' path but was given"));
        }
        options.setStagingLocation("my/staging/location");
        try {
            DataflowRunner.fromOptions(options);
            Assert.fail("fromOptions should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected a valid 'gs://' path but was given"));
        }
    }

    @Test
    public void testInvalidProfileLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setSaveProfilesToGcs("file://my/staging/location");
        try {
            DataflowRunner.fromOptions(options);
            Assert.fail("fromOptions should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected a valid 'gs://' path but was given"));
        }
        options.setSaveProfilesToGcs("my/staging/location");
        try {
            DataflowRunner.fromOptions(options);
            Assert.fail("fromOptions should have failed");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected a valid 'gs://' path but was given"));
        }
    }

    @Test
    public void testNonExistentTempLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setGcpTempLocation(DataflowRunnerTest.NON_EXISTENT_BUCKET);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString(("Output path does not exist or is not writeable: " + (DataflowRunnerTest.NON_EXISTENT_BUCKET))));
        DataflowRunner.fromOptions(options);
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testNonExistentStagingLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setStagingLocation(DataflowRunnerTest.NON_EXISTENT_BUCKET);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString(("Output path does not exist or is not writeable: " + (DataflowRunnerTest.NON_EXISTENT_BUCKET))));
        DataflowRunner.fromOptions(options);
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testNonExistentProfileLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setSaveProfilesToGcs(DataflowRunnerTest.NON_EXISTENT_BUCKET);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString(("Output path does not exist or is not writeable: " + (DataflowRunnerTest.NON_EXISTENT_BUCKET))));
        DataflowRunner.fromOptions(options);
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testNoProjectFails() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        // Explicitly set to null to prevent the default instance factory from reading credentials
        // from a user's environment, causing this test to fail.
        options.setProject(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Project id");
        thrown.expectMessage("when running a Dataflow in the cloud");
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testProjectId() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setProject("foo-12345");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        options.setGcpCredential(new TestCredential());
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testProjectPrefix() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setProject("google.com:some-project-12345");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        options.setGcpCredential(new TestCredential());
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testProjectNumber() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setProject("12345");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Project ID");
        thrown.expectMessage("project number");
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testProjectDescription() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setProject("some project");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Project ID");
        thrown.expectMessage("project description");
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testInvalidNumberOfWorkerHarnessThreads() throws IOException {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        FileSystems.setDefaultPipelineOptions(options);
        options.setRunner(DataflowRunner.class);
        options.setProject("foo-12345");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        options.as(DataflowPipelineDebugOptions.class).setNumberOfWorkerHarnessThreads((-1));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Number of worker harness threads");
        thrown.expectMessage("Please make sure the value is non-negative.");
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testNoStagingLocationAndNoTempLocationFails() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setProject("foo-project");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("DataflowRunner requires gcpTempLocation, " + "but failed to retrieve a value from PipelineOption"));
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testGcpTempAndNoTempLocationSucceeds() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setGcpCredential(new TestCredential());
        options.setProject("foo-project");
        options.setGcpTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testTempLocationAndNoGcpTempLocationSucceeds() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DataflowRunner.class);
        options.setGcpCredential(new TestCredential());
        options.setProject("foo-project");
        options.setTempLocation(DataflowRunnerTest.VALID_TEMP_BUCKET);
        options.setGcsUtil(mockGcsUtil);
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testValidProfileLocation() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        options.setSaveProfilesToGcs(DataflowRunnerTest.VALID_PROFILE_BUCKET);
        DataflowRunner.fromOptions(options);
    }

    @Test
    public void testInvalidJobName() throws IOException {
        List<String> invalidNames = Arrays.asList("invalid_name", "0invalid", "invalid-");
        List<String> expectedReason = Arrays.asList("JobName invalid", "JobName invalid", "JobName invalid");
        for (int i = 0; i < (invalidNames.size()); ++i) {
            DataflowPipelineOptions options = buildPipelineOptions();
            options.setJobName(invalidNames.get(i));
            try {
                DataflowRunner.fromOptions(options);
                Assert.fail(("Expected IllegalArgumentException for jobName " + (options.getJobName())));
            } catch (IllegalArgumentException e) {
                Assert.assertThat(e.getMessage(), Matchers.containsString(expectedReason.get(i)));
            }
        }
    }

    @Test
    public void testValidJobName() throws IOException {
        List<String> names = Arrays.asList("ok", "Ok", "A-Ok", "ok-123", "this-one-is-fairly-long-01234567890123456789");
        for (String name : names) {
            DataflowPipelineOptions options = buildPipelineOptions();
            options.setJobName(name);
            DataflowRunner runner = DataflowRunner.fromOptions(options);
            Assert.assertNotNull(runner);
        }
    }

    @Test
    public void testGcsUploadBufferSizeIsUnsetForBatchWhenDefault() throws IOException {
        DataflowPipelineOptions batchOptions = buildPipelineOptions();
        batchOptions.setRunner(DataflowRunner.class);
        Pipeline.create(batchOptions);
        Assert.assertNull(batchOptions.getGcsUploadBufferSizeBytes());
    }

    @Test
    public void testGcsUploadBufferSizeIsSetForStreamingWhenDefault() throws IOException {
        DataflowPipelineOptions streamingOptions = buildPipelineOptions();
        streamingOptions.setStreaming(true);
        streamingOptions.setRunner(DataflowRunner.class);
        Pipeline p = Pipeline.create(streamingOptions);
        // Instantiation of a runner prior to run() currently has a side effect of mutating the options.
        // This could be tested by DataflowRunner.fromOptions(streamingOptions) but would not ensure
        // that the pipeline itself had the expected options set.
        p.run();
        Assert.assertEquals(GCS_UPLOAD_BUFFER_SIZE_BYTES_DEFAULT, streamingOptions.getGcsUploadBufferSizeBytes().intValue());
    }

    @Test
    public void testGcsUploadBufferSizeUnchangedWhenNotDefault() throws IOException {
        int gcsUploadBufferSizeBytes = 12345678;
        DataflowPipelineOptions batchOptions = buildPipelineOptions();
        batchOptions.setGcsUploadBufferSizeBytes(gcsUploadBufferSizeBytes);
        batchOptions.setRunner(DataflowRunner.class);
        Pipeline.create(batchOptions);
        Assert.assertEquals(gcsUploadBufferSizeBytes, batchOptions.getGcsUploadBufferSizeBytes().intValue());
        DataflowPipelineOptions streamingOptions = buildPipelineOptions();
        streamingOptions.setStreaming(true);
        streamingOptions.setGcsUploadBufferSizeBytes(gcsUploadBufferSizeBytes);
        streamingOptions.setRunner(DataflowRunner.class);
        Pipeline.create(streamingOptions);
        Assert.assertEquals(gcsUploadBufferSizeBytes, streamingOptions.getGcsUploadBufferSizeBytes().intValue());
    }

    /**
     * A fake PTransform for testing.
     */
    public static class TestTransform extends PTransform<PCollection<Integer>, PCollection<Integer>> {
        public boolean translated = false;

        @Override
        public PCollection<Integer> expand(PCollection<Integer> input) {
            return PCollection.createPrimitiveOutputInternal(input.getPipeline(), WindowingStrategy.globalDefault(), input.isBounded(), input.getCoder());
        }
    }

    @Test
    public void testTransformTranslatorMissing() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        Pipeline p = Pipeline.create(options);
        p.apply(Create.of(Arrays.asList(1, 2, 3))).apply(new DataflowRunnerTest.TestTransform());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(Matchers.containsString("no translator registered"));
        DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList());
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(mockJobs).create(ArgumentMatchers.eq(DataflowRunnerTest.PROJECT_ID), ArgumentMatchers.eq(DataflowRunnerTest.REGION_ID), jobCaptor.capture());
        DataflowRunnerTest.assertValidJob(jobCaptor.getValue());
    }

    @Test
    public void testTransformTranslator() throws IOException {
        // Test that we can provide a custom translation
        DataflowPipelineOptions options = buildPipelineOptions();
        Pipeline p = Pipeline.create(options);
        DataflowRunnerTest.TestTransform transform = new DataflowRunnerTest.TestTransform();
        p.apply(Create.of(Arrays.asList(1, 2, 3)).withCoder(BigEndianIntegerCoder.of())).apply(transform);
        DataflowPipelineTranslator translator = DataflowRunner.fromOptions(options).getTranslator();
        DataflowPipelineTranslator.registerTransformTranslator(DataflowRunnerTest.TestTransform.class, ( transform1, context) -> {
            transform1.translated = true;
            // Note: This is about the minimum needed to fake out a
            // translation. This obviously isn't a real translation.
            TransformTranslator.StepTranslationContext stepContext = context.addStep(transform1, "TestTranslate");
            stepContext.addOutput(PropertyNames.OUTPUT, context.getOutput(transform1));
        });
        translator.translate(p, DataflowRunner.fromOptions(options), Collections.emptyList());
        Assert.assertTrue(transform.translated);
    }

    @Test
    public void testMapStateUnsupportedInBatch() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(false);
        verifyMapStateUnsupported(options);
    }

    @Test
    public void testMapStateUnsupportedInStreaming() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(true);
        verifyMapStateUnsupported(options);
    }

    @Test
    public void testSetStateUnsupportedInBatch() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(false);
        Pipeline.create(options);
        verifySetStateUnsupported(options);
    }

    @Test
    public void testSetStateUnsupportedInStreaming() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(true);
        verifySetStateUnsupported(options);
    }

    /**
     * Records all the composite transforms visited within the Pipeline.
     */
    private static class CompositeTransformRecorder extends PipelineVisitor.Defaults {
        private List<PTransform<?, ?>> transforms = new ArrayList<>();

        @Override
        public CompositeBehavior enterCompositeTransform(TransformHierarchy.Node node) {
            if ((node.getTransform()) != null) {
                transforms.add(node.getTransform());
            }
            return ENTER_TRANSFORM;
        }

        public List<PTransform<?, ?>> getCompositeTransforms() {
            return transforms;
        }
    }

    @Test
    public void testApplyIsScopedToExactClass() throws IOException {
        DataflowPipelineOptions options = buildPipelineOptions();
        Pipeline p = Pipeline.create(options);
        Create.TimestampedValues<String> transform = Create.timestamped(Arrays.asList(TimestampedValue.of("TestString", Instant.now())));
        p.apply(transform);
        DataflowRunnerTest.CompositeTransformRecorder recorder = new DataflowRunnerTest.CompositeTransformRecorder();
        p.traverseTopologically(recorder);
        // The recorder will also have seen a Create.Values composite as well, but we can't obtain that
        // transform.
        Assert.assertThat("Expected to have seen CreateTimestamped composite transform.", recorder.getCompositeTransforms(), Matchers.hasItem(transform));
        Assert.assertThat("Expected to have two composites, CreateTimestamped and Create.Values", recorder.getCompositeTransforms(), Matchers.hasItem(Matchers.<PTransform<?, ?>>isA(((Class) (Values.class)))));
    }

    @Test
    public void testToString() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setJobName("TestJobName");
        options.setProject("test-project");
        options.setTempLocation("gs://test/temp/location");
        options.setGcpCredential(new TestCredential());
        options.setPathValidatorClass(NoopPathValidator.class);
        options.setRunner(DataflowRunner.class);
        Assert.assertEquals("DataflowRunner#testjobname", DataflowRunner.fromOptions(options).toString());
    }

    /**
     * Tests that the {@link DataflowRunner} with {@code --templateLocation} returns normally when the
     * runner is successfully run.
     */
    @Test
    public void testTemplateRunnerFullCompletion() throws Exception {
        File existingFile = tmpFolder.newFile();
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setJobName("TestJobName");
        options.setGcpCredential(new TestCredential());
        options.setPathValidatorClass(NoopPathValidator.class);
        options.setProject("test-project");
        options.setRunner(DataflowRunner.class);
        options.setTemplateLocation(existingFile.getPath());
        options.setTempLocation(tmpFolder.getRoot().getPath());
        Pipeline p = Pipeline.create(options);
        p.run();
        expectedLogs.verifyInfo("Template successfully created");
    }

    /**
     * Tests that the {@link DataflowRunner} with {@code --templateLocation} throws the appropriate
     * exception when an output file is not writable.
     */
    @Test
    public void testTemplateRunnerLoggedErrorForFile() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setJobName("TestJobName");
        options.setRunner(DataflowRunner.class);
        options.setTemplateLocation("//bad/path");
        options.setProject("test-project");
        options.setTempLocation(tmpFolder.getRoot().getPath());
        options.setGcpCredential(new TestCredential());
        options.setPathValidatorClass(NoopPathValidator.class);
        Pipeline p = Pipeline.create(options);
        thrown.expectMessage("Cannot create output file at");
        thrown.expect(RuntimeException.class);
        p.run();
    }

    @Test
    public void testWorkerHarnessContainerImage() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        // default image set
        options.setWorkerHarnessContainerImage("some-container");
        Assert.assertThat(DataflowRunner.getContainerImageForJob(options), Matchers.equalTo("some-container"));
        // batch, legacy
        options.setWorkerHarnessContainerImage("gcr.io/IMAGE/foo");
        options.setExperiments(null);
        options.setStreaming(false);
        Assert.assertThat(DataflowRunner.getContainerImageForJob(options), Matchers.equalTo("gcr.io/beam-java-batch/foo"));
        // streaming, legacy
        options.setStreaming(true);
        Assert.assertThat(DataflowRunner.getContainerImageForJob(options), Matchers.equalTo("gcr.io/beam-java-streaming/foo"));
        // streaming, fnapi
        options.setExperiments(ImmutableList.of("experiment1", "beam_fn_api"));
        Assert.assertThat(DataflowRunner.getContainerImageForJob(options), Matchers.equalTo("gcr.io/java/foo"));
    }

    @Test
    public void testStreamingWriteWithNoShardingReturnsNewTransform() {
        PipelineOptions options = TestPipeline.testingPipelineOptions();
        options.as(DataflowPipelineWorkerPoolOptions.class).setMaxNumWorkers(10);
        testStreamingWriteOverride(options, 20);
    }

    @Test
    public void testStreamingWriteWithNoShardingReturnsNewTransformMaxWorkersUnset() {
        PipelineOptions options = TestPipeline.testingPipelineOptions();
        testStreamingWriteOverride(options, DEFAULT_NUM_SHARDS);
    }

    @Test
    public void testMergingStatefulRejectedInStreaming() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(true);
        verifyMergingStatefulParDoRejected(options);
    }

    @Test
    public void testMergingStatefulRejectedInBatch() throws Exception {
        PipelineOptions options = buildPipelineOptions();
        options.as(StreamingOptions.class).setStreaming(false);
        verifyMergingStatefulParDoRejected(options);
    }

    private static class TestSink extends FileBasedSink<Object, Void, Object> {
        @Override
        public void validate(PipelineOptions options) {
        }

        TestSink(String tmpFolder) {
            super(StaticValueProvider.of(FileSystems.matchNewResource(tmpFolder, true)), DynamicFileDestinations.constant(new FilenamePolicy() {
                @Override
                public ResourceId windowedFilename(int shardNumber, int numShards, BoundedWindow window, PaneInfo paneInfo, OutputFileHints outputFileHints) {
                    throw new UnsupportedOperationException("should not be called");
                }

                @Nullable
                @Override
                public ResourceId unwindowedFilename(int shardNumber, int numShards, OutputFileHints outputFileHints) {
                    throw new UnsupportedOperationException("should not be called");
                }
            }, SerializableFunctions.identity()));
        }

        @Override
        public WriteOperation<Void, Object> createWriteOperation() {
            return new WriteOperation<Void, Object>(this) {
                @Override
                public Writer<Void, Object> createWriter() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

