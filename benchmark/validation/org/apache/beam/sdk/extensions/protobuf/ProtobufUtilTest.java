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
package org.apache.beam.sdk.extensions.protobuf;


import com.google.protobuf.Any;
import com.google.protobuf.Duration;
import com.google.protobuf.ExtensionRegistry;
import java.util.Set;
import org.apache.beam.sdk.coders.Coder.NonDeterministicException;
import org.apache.beam.sdk.extensions.protobuf.Proto2CoderTestMessages.MessageA;
import org.apache.beam.sdk.extensions.protobuf.Proto2CoderTestMessages.MessageB;
import org.apache.beam.sdk.extensions.protobuf.Proto2CoderTestMessages.MessageC;
import org.apache.beam.sdk.extensions.protobuf.Proto2CoderTestMessages.MessageWithMap;
import org.apache.beam.sdk.extensions.protobuf.Proto2CoderTestMessages.ReferencesMessageWithMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ProtobufUtil}.
 */
@RunWith(JUnit4.class)
public class ProtobufUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Set<String> MESSAGE_A_ONLY = ImmutableSet.of("proto2_coder_test_messages.MessageA");

    private static final Set<String> MESSAGE_B_ONLY = ImmutableSet.of("proto2_coder_test_messages.MessageB");

    private static final Set<String> MESSAGE_C_ONLY = ImmutableSet.of("proto2_coder_test_messages.MessageC");

    // map fields are actually represented as a nested Message in generated Java code.
    private static final Set<String> WITH_MAP_ONLY = ImmutableSet.of("proto2_coder_test_messages.MessageWithMap", "proto2_coder_test_messages.MessageWithMap.Field1Entry");

    private static final Set<String> REFERS_MAP_ONLY = ImmutableSet.of("proto2_coder_test_messages.ReferencesMessageWithMap");

    // A references A and B.
    private static final Set<String> MESSAGE_A_ALL = Sets.union(ProtobufUtilTest.MESSAGE_A_ONLY, ProtobufUtilTest.MESSAGE_B_ONLY);

    // C, only with registered extensions, references A.
    private static final Set<String> MESSAGE_C_EXT = Sets.union(ProtobufUtilTest.MESSAGE_C_ONLY, ProtobufUtilTest.MESSAGE_A_ALL);

    // MessageWithMap references A.
    private static final Set<String> WITH_MAP_ALL = Sets.union(ProtobufUtilTest.WITH_MAP_ONLY, ProtobufUtilTest.MESSAGE_A_ALL);

    // ReferencesMessageWithMap references MessageWithMap.
    private static final Set<String> REFERS_MAP_ALL = Sets.union(ProtobufUtilTest.REFERS_MAP_ONLY, ProtobufUtilTest.WITH_MAP_ALL);

    @Test
    public void testRecursiveDescriptorsMessageA() {
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(MessageA.class), Matchers.equalTo(ProtobufUtilTest.MESSAGE_A_ALL));
    }

    @Test
    public void testRecursiveDescriptorsMessageB() {
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(MessageB.class), Matchers.equalTo(ProtobufUtilTest.MESSAGE_B_ONLY));
    }

    @Test
    public void testRecursiveDescriptorsMessageC() {
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(MessageC.class), Matchers.equalTo(ProtobufUtilTest.MESSAGE_C_ONLY));
    }

    @Test
    public void testRecursiveDescriptorsMessageCWithExtensions() {
        // With extensions, Message C has a reference to Message A and Message B.
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        Proto2CoderTestMessages.registerAllExtensions(registry);
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(MessageC.class, registry), Matchers.equalTo(ProtobufUtilTest.MESSAGE_C_EXT));
    }

    @Test
    public void testRecursiveDescriptorsMessageWithMap() {
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(MessageWithMap.class), Matchers.equalTo(ProtobufUtilTest.WITH_MAP_ALL));
    }

    @Test
    public void testRecursiveDescriptorsReferencesMessageWithMap() {
        Assert.assertThat(ProtobufUtilTest.getRecursiveDescriptorFullNames(ReferencesMessageWithMap.class), Matchers.equalTo(ProtobufUtilTest.REFERS_MAP_ALL));
    }

    @Test
    public void testVerifyProto2() {
        ProtobufUtil.checkProto2Syntax(MessageA.class, ExtensionRegistry.getEmptyRegistry());
        ProtobufUtil.checkProto2Syntax(MessageB.class, ExtensionRegistry.getEmptyRegistry());
        ProtobufUtil.checkProto2Syntax(MessageC.class, ExtensionRegistry.getEmptyRegistry());
        ProtobufUtil.checkProto2Syntax(MessageWithMap.class, ExtensionRegistry.getEmptyRegistry());
        ProtobufUtil.checkProto2Syntax(ReferencesMessageWithMap.class, ExtensionRegistry.getEmptyRegistry());
    }

    @Test
    public void testAnyIsNotProto2() {
        // Any is a core Protocol Buffers type that uses proto3 syntax.
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Any.class.getCanonicalName());
        thrown.expectMessage(("in file " + (Any.getDescriptor().getFile().getName())));
        ProtobufUtil.checkProto2Syntax(Any.class, ExtensionRegistry.getEmptyRegistry());
    }

    @Test
    public void testDurationIsNotProto2() {
        // Duration is a core Protocol Buffers type that uses proto3 syntax.
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Duration.class.getCanonicalName());
        thrown.expectMessage(("in file " + (Duration.getDescriptor().getFile().getName())));
        ProtobufUtil.checkProto2Syntax(Duration.class, ExtensionRegistry.getEmptyRegistry());
    }

    @Test
    public void testDurationIsDeterministic() throws NonDeterministicException {
        // Duration can be encoded deterministically.
        ProtobufUtil.verifyDeterministic(ProtoCoder.of(Duration.class));
    }

    @Test
    public void testMessageWithMapIsNotDeterministic() throws NonDeterministicException {
        String mapFieldName = MessageWithMap.getDescriptor().findFieldByNumber(1).getFullName();
        thrown.expect(NonDeterministicException.class);
        thrown.expectMessage(MessageWithMap.class.getName());
        thrown.expectMessage(("transitively includes Map field " + mapFieldName));
        thrown.expectMessage(("file " + (MessageWithMap.getDescriptor().getFile().getName())));
        ProtobufUtil.verifyDeterministic(ProtoCoder.of(MessageWithMap.class));
    }

    @Test
    public void testMessageWithTransitiveMapIsNotDeterministic() throws NonDeterministicException {
        String mapFieldName = MessageWithMap.getDescriptor().findFieldByNumber(1).getFullName();
        thrown.expect(NonDeterministicException.class);
        thrown.expectMessage(ReferencesMessageWithMap.class.getName());
        thrown.expectMessage(("transitively includes Map field " + mapFieldName));
        thrown.expectMessage(("file " + (MessageWithMap.getDescriptor().getFile().getName())));
        ProtobufUtil.verifyDeterministic(ProtoCoder.of(ReferencesMessageWithMap.class));
    }
}

