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
package org.apache.beam.sdk.testing;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for {@link SerializableMatchers}.
 *
 * <p>Since the only new matchers are those for {@link KV}, only those are tested here, to avoid
 * tediously repeating all of hamcrest's tests.
 *
 * <p>A few wrappers of a hamcrest matchers are tested for serializability. Beyond that, the
 * boilerplate that is identical to each is considered thoroughly tested.
 */
@RunWith(JUnit4.class)
public class SerializableMatchersTest implements Serializable {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAnythingSerializable() throws Exception {
        SerializableUtils.ensureSerializable(SerializableMatchers.anything());
    }

    @Test
    public void testAllOfSerializable() throws Exception {
        SerializableUtils.ensureSerializable(SerializableMatchers.allOf(SerializableMatchers.anything()));
    }

    @Test
    public void testContainsInAnyOrderSerializable() throws Exception {
        Assert.assertThat(ImmutableList.of(2, 1, 3), SerializableUtils.ensureSerializable(SerializableMatchers.containsInAnyOrder(1, 2, 3)));
    }

    @Test
    public void testContainsInAnyOrderNotSerializable() throws Exception {
        Assert.assertThat(ImmutableList.of(new SerializableMatchersTest.NotSerializableClass()), SerializableUtils.ensureSerializable(SerializableMatchers.containsInAnyOrder(new SerializableMatchersTest.NotSerializableClassCoder(), new SerializableMatchersTest.NotSerializableClass())));
    }

    @Test
    public void testKvKeyMatcherSerializable() throws Exception {
        Assert.assertThat(KV.of("hello", 42L), SerializableUtils.ensureSerializable(SerializableMatchers.kvWithKey("hello")));
    }

    @Test
    public void testKvMatcherBasicSuccess() throws Exception {
        Assert.assertThat(KV.of(1, 2), SerializableMatchers.kv(SerializableMatchers.anything(), SerializableMatchers.anything()));
    }

    @Test
    public void testKvMatcherKeyFailure() throws Exception {
        AssertionError exc = SerializableMatchersTest.assertionShouldFail(() -> Assert.assertThat(KV.of(1, 2), SerializableMatchers.kv(SerializableMatchers.not(SerializableMatchers.anything()), SerializableMatchers.anything())));
        Assert.assertThat(exc.getMessage(), Matchers.containsString("key did not match"));
    }

    @Test
    public void testKvMatcherValueFailure() throws Exception {
        AssertionError exc = SerializableMatchersTest.assertionShouldFail(() -> Assert.assertThat(KV.of(1, 2), SerializableMatchers.kv(SerializableMatchers.anything(), SerializableMatchers.not(SerializableMatchers.anything()))));
        Assert.assertThat(exc.getMessage(), Matchers.containsString("value did not match"));
    }

    @Test
    public void testKvMatcherGBKLikeSuccess() throws Exception {
        Assert.assertThat(KV.of("key", ImmutableList.of(1, 2, 3)), SerializableMatchers.<Object, Iterable<Integer>>kv(SerializableMatchers.anything(), SerializableMatchers.containsInAnyOrder(3, 2, 1)));
    }

    @Test
    public void testKvMatcherGBKLikeFailure() throws Exception {
        AssertionError exc = SerializableMatchersTest.assertionShouldFail(() -> Assert.assertThat(KV.of("key", ImmutableList.of(1, 2, 3)), SerializableMatchers.<String, Iterable<Integer>>kv(SerializableMatchers.anything(), SerializableMatchers.containsInAnyOrder(1, 2, 3, 4))));
        Assert.assertThat(exc.getMessage(), Matchers.containsString("value did not match"));
    }

    private static class NotSerializableClass {
        @Override
        public boolean equals(Object other) {
            return other instanceof SerializableMatchersTest.NotSerializableClass;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    private static class NotSerializableClassCoder extends AtomicCoder<SerializableMatchersTest.NotSerializableClass> {
        @Override
        public void encode(SerializableMatchersTest.NotSerializableClass value, OutputStream outStream) {
        }

        @Override
        public SerializableMatchersTest.NotSerializableClass decode(InputStream inStream) {
            return new SerializableMatchersTest.NotSerializableClass();
        }
    }
}

