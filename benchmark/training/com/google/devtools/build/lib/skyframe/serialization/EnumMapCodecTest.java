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


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import com.google.devtools.build.lib.skyframe.serialization.testutils.TestUtils;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.util.EnumMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link EnumMapCodec}.
 */
@RunWith(JUnit4.class)
public class EnumMapCodecTest {
    @Test
    public void smoke() throws Exception {
        new SerializationTester(new EnumMap(ImmutableMap.of(EnumMapCodecTest.TestEnum.FIRST, "first", EnumMapCodecTest.TestEnum.THIRD, "third", EnumMapCodecTest.TestEnum.SECOND, "second")), new EnumMap(EnumMapCodecTest.TestEnum.class), new EnumMap(EnumMapCodecTest.EmptyEnum.class)).runTests();
    }

    @Test
    public void throwsOnSubclass() {
        SerializationException exception = MoreAsserts.assertThrows(SerializationException.class, () -> TestUtils.toBytes(new EnumMapCodecTest.SubEnum(EnumMapCodecTest.TestEnum.class), ImmutableMap.of()));
        assertThat(exception).hasMessageThat().contains("Cannot serialize subclasses of EnumMap");
    }

    private enum TestEnum {

        FIRST,
        SECOND,
        THIRD;}

    private enum EmptyEnum {
        ;
    }

    private static class SubEnum<E extends Enum<E>, V> extends EnumMap<E, V> {
        public SubEnum(Class<E> keyType) {
            super(keyType);
        }
    }
}

