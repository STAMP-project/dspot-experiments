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


import PropertyNames.COMPONENT_ENCODINGS;
import PropertyNames.OBJECT_TYPE_NAME;
import org.apache.beam.runners.dataflow.internal.IsmFormat;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecordCoder;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RunnerHarnessCoderCloudObjectTranslatorRegistrar}.
 */
@RunWith(JUnit4.class)
public class RunnerHarnessCoderCloudObjectTranslatorRegistrarTest {
    @Test
    public void testCloudObjectToVarIntCoder() {
        Coder<?> coder = CloudObjects.coderFromCloudObject(CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "kind:var_int32")));
        Assert.assertThat(coder, IsInstanceOf.instanceOf(VarIntCoder.class));
    }

    @Test
    public void testCloudObjectToBigEndianIntegerCoder() {
        Coder<?> coder = CloudObjects.coderFromCloudObject(CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "kind:fixed_big_endian_int32")));
        Assert.assertThat(coder, IsInstanceOf.instanceOf(BigEndianIntegerCoder.class));
    }

    @Test
    public void testCloudObjectToBigEndianLongCoder() {
        Coder<?> coder = CloudObjects.coderFromCloudObject(CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "kind:fixed_big_endian_int64")));
        Assert.assertThat(coder, IsInstanceOf.instanceOf(BigEndianLongCoder.class));
    }

    @Test
    public void testCloudObjectToVoidCoder() {
        Coder<?> coder = CloudObjects.coderFromCloudObject(CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "kind:void")));
        Assert.assertThat(coder, IsInstanceOf.instanceOf(VoidCoder.class));
    }

    @Test
    public void testCloudObjectToIsmRecordCoder() {
        Coder<?> coder = CloudObjects.coderFromCloudObject(CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "kind:ism_record", "num_shard_key_coders", 1L, COMPONENT_ENCODINGS, ImmutableList.of(ImmutableMap.of("@type", "kind:var_int32"), ImmutableMap.of("@type", "kind:fixed_big_endian_int32"), ImmutableMap.of("@type", "kind:bytes")))));
        Assert.assertThat(coder, IsInstanceOf.instanceOf(IsmRecordCoder.class));
        IsmRecordCoder<?> ismRecordCoder = ((IsmRecordCoder<?>) (coder));
        Assert.assertEquals(1, ismRecordCoder.getNumberOfShardKeyCoders(ImmutableList.of()));
        // We expect 0 metadata shard key coders if it is unspecified.
        Assert.assertEquals(0, ismRecordCoder.getNumberOfShardKeyCoders(ImmutableList.of(IsmFormat.getMetadataKey())));
        Assert.assertEquals(ImmutableList.of(VarIntCoder.of(), BigEndianIntegerCoder.of(), ByteArrayCoder.of()), ismRecordCoder.getCoderArguments());
    }
}

