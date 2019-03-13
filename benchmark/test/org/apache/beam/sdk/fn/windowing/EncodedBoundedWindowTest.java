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
package org.apache.beam.sdk.fn.windowing;


import Coder.INSTANCE;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link EncodedBoundedWindow}.
 */
@RunWith(JUnit4.class)
public class EncodedBoundedWindowTest {
    @Test
    public void testCoder() throws Exception {
        CoderProperties.coderSerializable(INSTANCE);
        CoderProperties.coderConsistentWithEquals(INSTANCE, EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })), EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })));
        CoderProperties.coderDecodeEncodeEqual(INSTANCE, EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })));
        CoderProperties.coderDeterministic(INSTANCE, EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })), EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })));
        CoderProperties.structuralValueDecodeEncodeEqual(INSTANCE, EncodedBoundedWindow.forEncoding(ByteString.copyFrom(new byte[]{ 1, 2, 3 })));
    }
}

