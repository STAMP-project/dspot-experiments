/**
 * ========================================================================
 */
/**
 * Copyright (C) 2016 Alex Shvid
 */
/**
 * ------------------------------------------------------------------------
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
 * http://www.apache.org/licenses/LICENSE-2.0
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
/**
 * ========================================================================
 */
package io.protostuff;


import io.protostuff.runtime.RuntimeSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for ignoring unknown fields during de-seralization
 */
public class MsgpackUnknownFieldTest {
    protected static boolean numeric = false;

    public static final Schema<MsgpackUnknownFieldTest.TestMessage> SCHEMA = RuntimeSchema.getSchema(MsgpackUnknownFieldTest.TestMessage.class);

    public static final Schema<MsgpackUnknownFieldTest.TestMessageExtended> EXTENDED_SCHEMA = RuntimeSchema.getSchema(MsgpackUnknownFieldTest.TestMessageExtended.class);

    private MsgpackUnknownFieldTest.TestMessage fixture;

    private MsgpackUnknownFieldTest.TestMessageExtended fixtureExtended;

    @Test
    public void normalMessage() throws Exception {
        byte[] message = MsgpackIOUtil.toByteArray(fixture, MsgpackUnknownFieldTest.SCHEMA, MsgpackUnknownFieldTest.numeric);
        MsgpackUnknownFieldTest.TestMessage instance = MsgpackUnknownFieldTest.SCHEMA.newMessage();
        MsgpackIOUtil.mergeFrom(message, instance, MsgpackUnknownFieldTest.SCHEMA, MsgpackUnknownFieldTest.numeric);
        checkKnownFields(instance);
    }

    @Test
    public void normalExtendedMessage() throws Exception {
        byte[] message = MsgpackIOUtil.toByteArray(fixtureExtended, MsgpackUnknownFieldTest.EXTENDED_SCHEMA, MsgpackUnknownFieldTest.numeric);
        MsgpackUnknownFieldTest.TestMessageExtended instance = MsgpackUnknownFieldTest.EXTENDED_SCHEMA.newMessage();
        MsgpackIOUtil.mergeFrom(message, instance, MsgpackUnknownFieldTest.EXTENDED_SCHEMA, MsgpackUnknownFieldTest.numeric);
        checkKnownFields(instance);
    }

    @Test
    public void unknownField() throws Exception {
        byte[] extendedMessage = MsgpackIOUtil.toByteArray(fixtureExtended, MsgpackUnknownFieldTest.EXTENDED_SCHEMA, MsgpackUnknownFieldTest.numeric);
        MsgpackUnknownFieldTest.TestMessage instance = MsgpackUnknownFieldTest.SCHEMA.newMessage();
        // unknown field3
        MsgpackIOUtil.mergeFrom(extendedMessage, instance, MsgpackUnknownFieldTest.SCHEMA, MsgpackUnknownFieldTest.numeric);
        checkKnownFields(instance);
    }

    @Test
    public void missingField() throws Exception {
        byte[] message = MsgpackIOUtil.toByteArray(fixture, MsgpackUnknownFieldTest.SCHEMA, MsgpackUnknownFieldTest.numeric);
        MsgpackUnknownFieldTest.TestMessageExtended instance = MsgpackUnknownFieldTest.EXTENDED_SCHEMA.newMessage();
        // missing field3
        MsgpackIOUtil.mergeFrom(message, instance, MsgpackUnknownFieldTest.EXTENDED_SCHEMA, MsgpackUnknownFieldTest.numeric);
        Assert.assertEquals(fixtureExtended.field1, instance.field1);
        Assert.assertEquals(fixtureExtended.field2, instance.field2);
        Assert.assertNull(instance.field3);
    }

    static class TestMessage {
        public int field1;

        public String field2;
    }

    static class TestMessageExtended {
        public int field1;

        public String field2;

        public byte[] field3;
    }
}

