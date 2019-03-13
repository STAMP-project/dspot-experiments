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
package org.apache.flink.types;


import java.util.ArrayList;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.junit.Assert;
import org.junit.Test;


public class JavaToValueConverterTest {
    @Test
    public void testJavaToValueConversion() {
        try {
            Assert.assertNull(JavaToValueConverter.convertBoxedJavaType(null));
            Assert.assertEquals(new StringValue("123Test"), JavaToValueConverter.convertBoxedJavaType("123Test"));
            Assert.assertEquals(new ByteValue(((byte) (44))), JavaToValueConverter.convertBoxedJavaType(((byte) (44))));
            Assert.assertEquals(new ShortValue(((short) (10000))), JavaToValueConverter.convertBoxedJavaType(((short) (10000))));
            Assert.assertEquals(new IntValue(3567564), JavaToValueConverter.convertBoxedJavaType(3567564));
            Assert.assertEquals(new LongValue(767692734), JavaToValueConverter.convertBoxedJavaType(767692734L));
            Assert.assertEquals(new FloatValue(17.5F), JavaToValueConverter.convertBoxedJavaType(17.5F));
            Assert.assertEquals(new DoubleValue(3.1415926), JavaToValueConverter.convertBoxedJavaType(3.1415926));
            Assert.assertEquals(new BooleanValue(true), JavaToValueConverter.convertBoxedJavaType(true));
            Assert.assertEquals(new CharValue('@'), JavaToValueConverter.convertBoxedJavaType('@'));
            try {
                JavaToValueConverter.convertBoxedJavaType(new ArrayList<Object>());
                Assert.fail("Accepted invalid type.");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testValueToJavaConversion() {
        try {
            Assert.assertNull(JavaToValueConverter.convertValueType(null));
            Assert.assertEquals("123Test", JavaToValueConverter.convertValueType(new StringValue("123Test")));
            Assert.assertEquals(((byte) (44)), JavaToValueConverter.convertValueType(new ByteValue(((byte) (44)))));
            Assert.assertEquals(((short) (10000)), JavaToValueConverter.convertValueType(new ShortValue(((short) (10000)))));
            Assert.assertEquals(3567564, JavaToValueConverter.convertValueType(new IntValue(3567564)));
            Assert.assertEquals(767692734L, JavaToValueConverter.convertValueType(new LongValue(767692734)));
            Assert.assertEquals(17.5F, JavaToValueConverter.convertValueType(new FloatValue(17.5F)));
            Assert.assertEquals(3.1415926, JavaToValueConverter.convertValueType(new DoubleValue(3.1415926)));
            Assert.assertEquals(true, JavaToValueConverter.convertValueType(new BooleanValue(true)));
            Assert.assertEquals('@', JavaToValueConverter.convertValueType(new CharValue('@')));
            try {
                JavaToValueConverter.convertValueType(new JavaToValueConverterTest.MyValue());
                Assert.fail("Accepted invalid type.");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private static final class MyValue implements Value {
        private static final long serialVersionUID = 1L;

        @Override
        public void write(DataOutputView out) {
        }

        @Override
        public void read(DataInputView in) {
        }
    }
}

