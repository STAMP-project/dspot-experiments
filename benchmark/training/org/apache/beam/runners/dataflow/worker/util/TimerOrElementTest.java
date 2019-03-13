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
package org.apache.beam.runners.dataflow.worker.util;


import GlobalWindow.Coder.INSTANCE;
import PropertyNames.COMPONENT_ENCODINGS;
import java.util.Collections;
import java.util.List;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.util.Structs;
import org.apache.beam.runners.dataflow.worker.util.TimerOrElement.TimerOrElementCoder;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TimerOrElement}.
 */
@RunWith(JUnit4.class)
public class TimerOrElementTest {
    @Test
    public void testCoderIsSerializableWithWellKnownCoderType() {
        CoderProperties.coderSerializable(TimerOrElementCoder.of(KvCoder.of(INSTANCE, INSTANCE)));
    }

    @Test
    public void testCoderCanBeDecodedFromCloudObject() {
        CloudObject cloudObject = CloudObject.forClassName("com.google.cloud.dataflow.sdk.util.TimerOrElement$TimerOrElementCoder");
        List<CloudObject> component = Collections.singletonList(/* sdkComponents= */
        CloudObjects.asCloudObject(KvCoder.of(VarLongCoder.of(), ByteArrayCoder.of()), null));
        Structs.addList(cloudObject, COMPONENT_ENCODINGS, component);
        Coder<?> decoded = CloudObjects.coderFromCloudObject(cloudObject);
        Assert.assertThat(decoded, Matchers.instanceOf(TimerOrElementCoder.class));
        TimerOrElementCoder<?> decodedCoder = ((TimerOrElementCoder<?>) (decoded));
        Assert.assertThat(decodedCoder.getKeyCoder(), Matchers.equalTo(VarLongCoder.of()));
        Assert.assertThat(decodedCoder.getElementCoder(), Matchers.equalTo(ByteArrayCoder.of()));
    }
}

