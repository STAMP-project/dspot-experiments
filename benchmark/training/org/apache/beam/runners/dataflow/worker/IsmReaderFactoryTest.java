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
package org.apache.beam.runners.dataflow.worker;


import GlobalWindow.Coder.INSTANCE;
import java.io.Closeable;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecordCoder;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.WindowedValue.WindowedValueCoder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.cache.Cache;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link IsmReaderFactory}.
 */
@RunWith(JUnit4.class)
public class IsmReaderFactoryTest {
    private DataflowPipelineOptions options;

    private Cache<Object, Object> logicalReferenceCache;

    private BatchModeExecutionContext executionContext;

    private Closeable stateCloseable;

    private DataflowOperationContext operationContext;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testFactory() throws Exception {
        WindowedValueCoder<?> coder = WindowedValue.getFullCoder(IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of()), VarLongCoder.of()), INSTANCE);
        String tmpFile = tmpFolder.newFile().getPath();
        ResourceId tmpResourceId = FileSystems.matchSingleFileSpec(tmpFile).resourceId();
        @SuppressWarnings("rawtypes")
        IsmReader<?> ismReader = ((IsmReader) (new IsmReaderFactory().create(createSpecForFilename(tmpFile), coder, options, executionContext, operationContext)));
        Assert.assertEquals(coder.getValueCoder(), ismReader.getCoder());
        Assert.assertEquals(tmpResourceId, ismReader.getResourceId());
    }

    @Test
    public void testFactoryReturnsCachedInstance() throws Exception {
        Coder<?> coder = WindowedValue.getFullCoder(IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of()), VarLongCoder.of()), INSTANCE);
        String tmpFile = tmpFolder.newFile().getPath();
        String anotherTmpFile = tmpFolder.newFile().getPath();
        @SuppressWarnings("rawtypes")
        IsmReader<?> ismReader = ((IsmReader) (new IsmReaderFactory().create(createSpecForFilename(tmpFile), coder, options, executionContext, operationContext)));
        Assert.assertSame(ismReader, new IsmReaderFactory().create(createSpecForFilename(tmpFile), coder, options, executionContext, operationContext));
        Assert.assertNotSame(ismReader, new IsmReaderFactory().create(createSpecForFilename(anotherTmpFile), coder, options, executionContext, operationContext));
    }
}

