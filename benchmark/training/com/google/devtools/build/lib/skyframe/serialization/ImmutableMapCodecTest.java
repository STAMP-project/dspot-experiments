/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.skyframe.serialization;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester.VerificationFunction;
import com.google.devtools.build.lib.skyframe.serialization.testutils.TestUtils;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ImmutableMapCodec}.
 */
@RunWith(JUnit4.class)
public class ImmutableMapCodecTest {
    @Test
    public void smoke() throws Exception {
        // Check for order.
        new SerializationTester(ImmutableMap.of(), ImmutableMap.of("A", "//foo:A"), ImmutableMap.of("B", "//foo:B"), ImmutableSortedMap.of(), ImmutableSortedMap.of("A", "//foo:A"), ImmutableSortedMap.of("B", "//foo:B"), ImmutableSortedMap.reverseOrder().put("a", "b").put("c", "d").build()).setVerificationFunction(((VerificationFunction<ImmutableMap<?, ?>>) (( deserialized, subject) -> {
            assertThat(deserialized).isEqualTo(subject);
            assertThat(deserialized).containsExactlyEntriesIn(subject).inOrder();
        }))).runTests();
    }

    @Test
    public void unnaturallySortedMapComesBackUnsortedInCorrectOrder() throws Exception {
        ImmutableMap<?, ?> deserialized = TestUtils.roundTrip(ImmutableSortedMap.reverseOrder().put("a", "b").put("c", "d").build());
        assertThat(deserialized).isInstanceOf(ImmutableMap.class);
        assertThat(deserialized).isNotInstanceOf(ImmutableSortedMap.class);
        assertThat(deserialized).containsExactly("c", "d", "a", "b").inOrder();
    }

    @Test
    public void serializingErrorIncludesKeyStringAndValueClass() {
        SerializationException expected = MoreAsserts.assertThrows(SerializationException.class, () -> TestUtils.toBytesMemoized(ImmutableMap.of("a", new ImmutableMapCodecTest.Dummy()), AutoRegistry.get().getBuilder().add(/* throwsOnSerialization= */
        new ImmutableMapCodecTest.DummyThrowingCodec(true)).build()));
        assertThat(expected).hasMessageThat().containsMatch("Exception while serializing value of type .*\\$Dummy for key \'a\'");
    }

    @Test
    public void deserializingErrorIncludesKeyString() throws Exception {
        ObjectCodecRegistry registry = AutoRegistry.get().getBuilder().add(/* throwsOnSerialization= */
        new ImmutableMapCodecTest.DummyThrowingCodec(false)).build();
        ByteString data = TestUtils.toBytes(new SerializationContext(registry, ImmutableMap.of()), ImmutableMap.of("a", new ImmutableMapCodecTest.Dummy()));
        SerializationException expected = MoreAsserts.assertThrows(SerializationException.class, () -> TestUtils.fromBytes(new DeserializationContext(registry, ImmutableMap.of()), data));
        assertThat(expected).hasMessageThat().contains("Exception while deserializing value for key 'a'");
    }

    private static class Dummy {}

    private static class DummyThrowingCodec implements ObjectCodec<ImmutableMapCodecTest.Dummy> {
        private final boolean throwsOnSerialization;

        private DummyThrowingCodec(boolean throwsOnSerialization) {
            this.throwsOnSerialization = throwsOnSerialization;
        }

        @Override
        public Class<ImmutableMapCodecTest.Dummy> getEncodedClass() {
            return ImmutableMapCodecTest.Dummy.class;
        }

        @Override
        public void serialize(SerializationContext context, ImmutableMapCodecTest.Dummy value, CodedOutputStream codedOut) throws SerializationException {
            if (throwsOnSerialization) {
                throw new SerializationException("Expected failure");
            }
        }

        @Override
        public ImmutableMapCodecTest.Dummy deserialize(DeserializationContext context, CodedInputStream codedIn) throws SerializationException {
            Preconditions.checkState((!(throwsOnSerialization)));
            throw new SerializationException("Expected failure");
        }
    }
}

