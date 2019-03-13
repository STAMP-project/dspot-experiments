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
package org.apache.hadoop.mapred.nativetask.handlers;


import NativeCollectorOnlyHandler.GET_COMBINE_HANDLER;
import NativeCollectorOnlyHandler.GET_OUTPUT_INDEX_PATH;
import NativeCollectorOnlyHandler.GET_OUTPUT_PATH;
import NativeCollectorOnlyHandler.GET_SPILL_PATH;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.nativetask.Command;
import org.apache.hadoop.mapred.nativetask.ICombineHandler;
import org.apache.hadoop.mapred.nativetask.INativeHandler;
import org.apache.hadoop.mapred.nativetask.TaskContext;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.apache.hadoop.mapred.nativetask.util.ReadWriteBuffer;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class TestNativeCollectorOnlyHandler {
    private NativeCollectorOnlyHandler handler;

    private INativeHandler nativeHandler;

    private BufferPusher pusher;

    private ICombineHandler combiner;

    private TaskContext taskContext;

    private static final String LOCAL_DIR = (TestConstants.NATIVETASK_TEST_DIR) + "/local";

    @Test
    public void testCollect() throws IOException {
        this.handler = new NativeCollectorOnlyHandler(taskContext, nativeHandler, pusher, combiner);
        handler.collect(new BytesWritable(), new BytesWritable(), 100);
        handler.close();
        handler.close();
        Mockito.verify(pusher, Mockito.times(1)).collect(ArgumentMatchers.any(BytesWritable.class), ArgumentMatchers.any(BytesWritable.class), ArgumentMatchers.anyInt());
        Mockito.verify(pusher, Mockito.times(1)).close();
        Mockito.verify(combiner, Mockito.times(1)).close();
        Mockito.verify(nativeHandler, Mockito.times(1)).close();
    }

    @Test
    public void testGetCombiner() throws IOException {
        this.handler = new NativeCollectorOnlyHandler(taskContext, nativeHandler, pusher, combiner);
        Mockito.when(combiner.getId()).thenReturn(100L);
        final ReadWriteBuffer result = handler.onCall(GET_COMBINE_HANDLER, null);
        Assert.assertEquals(100L, result.readLong());
    }

    @Test
    public void testOnCall() throws IOException {
        this.handler = new NativeCollectorOnlyHandler(taskContext, nativeHandler, pusher, combiner);
        boolean thrown = false;
        try {
            handler.onCall(new Command((-1)), null);
        } catch (final IOException e) {
            thrown = true;
        }
        Assert.assertTrue("exception thrown", thrown);
        final String expectedOutputPath = StringUtils.join(File.separator, new String[]{ TestNativeCollectorOnlyHandler.LOCAL_DIR, "output", "file.out" });
        final String expectedOutputIndexPath = StringUtils.join(File.separator, new String[]{ TestNativeCollectorOnlyHandler.LOCAL_DIR, "output", "file.out.index" });
        final String expectedSpillPath = StringUtils.join(File.separator, new String[]{ TestNativeCollectorOnlyHandler.LOCAL_DIR, "output", "spill0.out" });
        final String outputPath = handler.onCall(GET_OUTPUT_PATH, null).readString();
        Assert.assertEquals(expectedOutputPath, outputPath);
        final String outputIndexPath = handler.onCall(GET_OUTPUT_INDEX_PATH, null).readString();
        Assert.assertEquals(expectedOutputIndexPath, outputIndexPath);
        final String spillPath = handler.onCall(GET_SPILL_PATH, null).readString();
        Assert.assertEquals(expectedSpillPath, spillPath);
    }
}

