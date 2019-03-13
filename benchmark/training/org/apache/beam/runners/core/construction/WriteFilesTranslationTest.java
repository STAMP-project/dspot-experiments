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
package org.apache.beam.runners.core.construction;


import FileBasedSink.Writer;
import RunnerApi.WriteFilesPayload;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.sdk.io.DynamicFileDestinations;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.FileBasedSink.FilenamePolicy;
import org.apache.beam.sdk.io.FileBasedSink.OutputFileHints;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.WriteFiles;
import org.apache.beam.sdk.io.WriteFilesResult;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link WriteFilesTranslation}.
 */
@RunWith(Parameterized.class)
public class WriteFilesTranslationTest {
    @Parameterized.Parameter(0)
    public WriteFiles<String, Void, String> writeFiles;

    public static TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void testEncodedProto() throws Exception {
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.WriteFilesPayload payload = WriteFilesTranslation.payloadForWriteFiles(writeFiles, components);
        Assert.assertThat(payload.getRunnerDeterminedSharding(), Matchers.equalTo((((writeFiles.getNumShardsProvider()) == null) && ((writeFiles.getComputeNumShards()) == null))));
        Assert.assertThat(payload.getWindowedWrites(), Matchers.equalTo(writeFiles.getWindowedWrites()));
        Assert.assertThat(((FileBasedSink<String, Void, String>) (WriteFilesTranslation.sinkFromProto(payload.getSink()))), Matchers.equalTo(writeFiles.getSink()));
    }

    @Test
    public void testExtractionDirectFromTransform() throws Exception {
        PCollection<String> input = WriteFilesTranslationTest.p.apply(Create.of("hello"));
        WriteFilesResult<Void> output = input.apply(writeFiles);
        AppliedPTransform<PCollection<String>, WriteFilesResult<Void>, WriteFiles<String, Void, String>> appliedPTransform = AppliedPTransform.of("foo", input.expand(), output.expand(), writeFiles, WriteFilesTranslationTest.p);
        Assert.assertThat(WriteFilesTranslation.isRunnerDeterminedSharding(appliedPTransform), Matchers.equalTo((((writeFiles.getNumShardsProvider()) == null) && ((writeFiles.getComputeNumShards()) == null))));
        Assert.assertThat(WriteFilesTranslation.isWindowedWrites(appliedPTransform), Matchers.equalTo(writeFiles.getWindowedWrites()));
        Assert.assertThat(WriteFilesTranslation.<String, Void, String>getSink(appliedPTransform), Matchers.equalTo(writeFiles.getSink()));
    }

    /**
     * A simple {@link FileBasedSink} for testing serialization/deserialization. Not mocked to avoid
     * any issues serializing mocks.
     */
    private static class DummySink extends FileBasedSink<Object, Void, Object> {
        DummySink() {
            super(StaticValueProvider.of(FileSystems.matchNewResource("nowhere", false)), DynamicFileDestinations.constant(new WriteFilesTranslationTest.DummyFilenamePolicy(), SerializableFunctions.constant(null)));
        }

        @Override
        public WriteOperation<Void, Object> createWriteOperation() {
            return new WriteFilesTranslationTest.DummyWriteOperation(this);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof WriteFilesTranslationTest.DummySink)) {
                return false;
            }
            WriteFilesTranslationTest.DummySink that = ((WriteFilesTranslationTest.DummySink) (other));
            return ((getTempDirectoryProvider().isAccessible()) && (getTempDirectoryProvider().isAccessible())) && (getTempDirectoryProvider().get().equals(getTempDirectoryProvider().get()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(WriteFilesTranslationTest.DummySink.class, (getTempDirectoryProvider().isAccessible() ? getTempDirectoryProvider().get() : null));
        }
    }

    private static class DummyWriteOperation extends FileBasedSink.WriteOperation<Void, Object> {
        public DummyWriteOperation(FileBasedSink<Object, Void, Object> sink) {
            super(sink);
        }

        @Override
        public Writer<Void, Object> createWriter() throws Exception {
            throw new UnsupportedOperationException("Should never be called.");
        }
    }

    private static class DummyFilenamePolicy extends FilenamePolicy {
        @Override
        public ResourceId windowedFilename(int shardNumber, int numShards, BoundedWindow window, PaneInfo paneInfo, OutputFileHints outputFileHints) {
            throw new UnsupportedOperationException("Should never be called.");
        }

        @Nullable
        @Override
        public ResourceId unwindowedFilename(int shardNumber, int numShards, OutputFileHints outputFileHints) {
            throw new UnsupportedOperationException("Should never be called.");
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof WriteFilesTranslationTest.DummyFilenamePolicy;
        }

        @Override
        public int hashCode() {
            return WriteFilesTranslationTest.DummyFilenamePolicy.class.hashCode();
        }
    }
}

