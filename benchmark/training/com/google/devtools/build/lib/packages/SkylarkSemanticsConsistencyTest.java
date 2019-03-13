/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.packages;


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.skyframe.serialization.DeserializationContext;
import com.google.devtools.build.lib.skyframe.serialization.DynamicCodec;
import com.google.devtools.build.lib.skyframe.serialization.SerializationContext;
import com.google.devtools.build.lib.skyframe.serialization.testutils.TestUtils;
import com.google.devtools.build.lib.syntax.StarlarkSemantics;
import com.google.devtools.common.options.Options;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the flow of flags from {@link StarlarkSemanticsOptions} to {@link StarlarkSemantics},
 * and to and from {@code StarlarkSemantics}' serialized representation.
 *
 * <p>When adding a new option, it is trivial to make a transposition error or a copy/paste error.
 * These tests guard against such errors. The following possible bugs are considered:
 *
 * <ul>
 *   <li>If a new option is added to {@code StarlarkSemantics} but not to {@code StarlarkSemanticsOptions}, or vice versa, then the programmer will either be unable to
 *       implement its behavior, or unable to test it from the command line and add user
 *       documentation. We hope that the programmer notices this on their own.
 *   <li>If {@link StarlarkSemanticsOptions#toSkylarkSemantics} is not updated to set all fields of
 *       {@code StarlarkSemantics}, then it will fail immediately because all fields of {@link StarlarkSemantics.Builder} are mandatory.
 *   <li>To catch a copy/paste error where the wrong field's data is threaded through {@code toSkylarkSemantics()} or {@code deserialize(...)}, we repeatedly generate matching random
 *       instances of the input and expected output objects.
 *   <li>The {@link #checkDefaultsMatch} test ensures that there is no divergence between the
 *       default values of the two classes.
 *   <li>There is no test coverage for failing to update the non-generated webpage documentation. So
 *       don't forget that!
 * </ul>
 */
@RunWith(JUnit4.class)
public class SkylarkSemanticsConsistencyTest {
    private static final int NUM_RANDOM_TRIALS = 10;

    /**
     * Checks that a randomly generated {@link StarlarkSemanticsOptions} object can be converted to a
     * {@link StarlarkSemantics} object with the same field values.
     */
    @Test
    public void optionsToSemantics() throws Exception {
        for (int i = 0; i < (SkylarkSemanticsConsistencyTest.NUM_RANDOM_TRIALS); i++) {
            long seed = i;
            StarlarkSemanticsOptions options = SkylarkSemanticsConsistencyTest.buildRandomOptions(new Random(seed));
            StarlarkSemantics semantics = SkylarkSemanticsConsistencyTest.buildRandomSemantics(new Random(seed));
            StarlarkSemantics semanticsFromOptions = options.toSkylarkSemantics();
            assertThat(semanticsFromOptions).isEqualTo(semantics);
        }
    }

    /**
     * Checks that a randomly generated {@link StarlarkSemantics} object can be serialized and
     * deserialized to an equivalent object.
     */
    @Test
    public void serializationRoundTrip() throws Exception {
        DynamicCodec codec = new DynamicCodec(SkylarkSemanticsConsistencyTest.buildRandomSemantics(new Random(2)).getClass());
        for (int i = 0; i < (SkylarkSemanticsConsistencyTest.NUM_RANDOM_TRIALS); i++) {
            StarlarkSemantics semantics = SkylarkSemanticsConsistencyTest.buildRandomSemantics(new Random(i));
            StarlarkSemantics deserialized = ((StarlarkSemantics) (TestUtils.fromBytes(new DeserializationContext(ImmutableMap.of()), codec, TestUtils.toBytes(new SerializationContext(ImmutableMap.of()), codec, semantics))));
            assertThat(deserialized).isEqualTo(semantics);
        }
    }

    @Test
    public void checkDefaultsMatch() {
        StarlarkSemanticsOptions defaultOptions = Options.getDefaults(StarlarkSemanticsOptions.class);
        StarlarkSemantics defaultSemantics = StarlarkSemantics.DEFAULT_SEMANTICS;
        StarlarkSemantics semanticsFromOptions = defaultOptions.toSkylarkSemantics();
        assertThat(semanticsFromOptions).isEqualTo(defaultSemantics);
    }

    @Test
    public void canGetBuilderFromInstance() {
        StarlarkSemantics original = StarlarkSemantics.DEFAULT_SEMANTICS;
        assertThat(original.internalSkylarkFlagTestCanary()).isFalse();
        StarlarkSemantics modified = original.toBuilder().internalSkylarkFlagTestCanary(true).build();
        assertThat(modified.internalSkylarkFlagTestCanary()).isTrue();
    }
}

