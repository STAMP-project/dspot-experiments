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
package org.apache.beam.sdk.transforms.windowing;


import Context.OUTER;
import GlobalWindow.Coder.INSTANCE;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.ByteStreams;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.CountingOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GlobalWindow}.
 */
@RunWith(JUnit4.class)
public class GlobalWindowTest {
    @Test
    public void testCoderBinaryRepresentation() throws Exception {
        CountingOutputStream out = new CountingOutputStream(ByteStreams.nullOutputStream());
        INSTANCE.encode(GlobalWindow.INSTANCE, out, OUTER);
        Assert.assertEquals(0, out.getCount());
        INSTANCE.encode(GlobalWindow.INSTANCE, out);
        Assert.assertEquals(0, out.getCount());
    }

    @Test
    public void testCoderEncodeDecodeEquals() throws Exception {
        CoderProperties.coderDecodeEncodeEqual(INSTANCE, GlobalWindow.INSTANCE);
    }

    @Test
    public void testCoderIsSerializable() {
        CoderProperties.coderSerializable(INSTANCE);
    }

    @Test
    public void testCoderIsDeterministic() throws Exception {
        CoderProperties.coderDeterministic(INSTANCE, GlobalWindow.INSTANCE, GlobalWindow.INSTANCE);
    }
}

