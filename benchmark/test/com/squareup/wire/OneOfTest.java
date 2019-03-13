/**
 * Copyright 2015 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.wire;


import OneOfMessage.Builder;
import com.squareup.wire.protos.oneof.OneOfMessage;
import org.junit.Assert;
import org.junit.Test;


public class OneOfTest {
    private static final byte[] INITIAL_BYTES = new byte[]{  };

    // (Tag #1 << 3 | VARINT) = 8.
    private static final byte[] FOO_BYTES = new byte[]{ 8, 17 };

    // (Tag #3 << 3 | LENGTH_DELIMITED) = 26, string length = 6.
    private static final byte[] BAR_BYTES = new byte[]{ 26, 6, 'b', 'a', 'r', 'b', 'a', 'r' };

    private final ProtoAdapter<OneOfMessage> adapter = OneOfMessage.ADAPTER;

    @Test
    public void testOneOf() throws Exception {
        OneOfMessage.Builder builder = new OneOfMessage.Builder();
        validate(builder, null, null, OneOfTest.INITIAL_BYTES);
        builder.foo(17);
        validate(builder, 17, null, OneOfTest.FOO_BYTES);
        builder.bar("barbar");
        validate(builder, null, "barbar", OneOfTest.BAR_BYTES);
        builder.bar(null);
        validate(builder, null, null, OneOfTest.INITIAL_BYTES);
        builder.bar("barbar");
        validate(builder, null, "barbar", OneOfTest.BAR_BYTES);
        builder.foo(17);
        validate(builder, 17, null, OneOfTest.FOO_BYTES);
        builder.foo(null);
        validate(builder, null, null, OneOfTest.INITIAL_BYTES);
    }

    @Test
    public void buildFailsWhenBothFieldsAreNonNull() throws Exception {
        OneOfMessage.Builder builder = new OneOfMessage.Builder();
        builder.foo = 1;
        builder.bar = "two";
        try {
            builder.build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("at most one of foo, bar, baz may be non-null");
        }
    }

    @Test
    public void constructorFailsWhenBothFieldsAreNonNull() throws Exception {
        try {
            new OneOfMessage(1, "two", null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("at most one of foo, bar, baz may be non-null");
        }
    }
}

