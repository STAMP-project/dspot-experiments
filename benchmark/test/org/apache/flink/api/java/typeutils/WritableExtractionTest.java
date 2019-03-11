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
package org.apache.flink.api.java.typeutils;


import BasicTypeInfo.STRING_TYPE_INFO;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import org.apache.flink.api.common.functions.InvalidTypesException;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the type extraction of {@link Writable}.
 */
@SuppressWarnings("serial")
public class WritableExtractionTest {
    @Test
    public void testDetectWritable() {
        // writable interface itself must not be writable
        Assert.assertFalse(TypeExtractor.isHadoopWritable(Writable.class));
        // various forms of extension
        Assert.assertTrue(TypeExtractor.isHadoopWritable(WritableExtractionTest.DirectWritable.class));
        Assert.assertTrue(TypeExtractor.isHadoopWritable(WritableExtractionTest.ViaInterfaceExtension.class));
        Assert.assertTrue(TypeExtractor.isHadoopWritable(WritableExtractionTest.ViaAbstractClassExtension.class));
        // some non-writables
        Assert.assertFalse(TypeExtractor.isHadoopWritable(String.class));
        Assert.assertFalse(TypeExtractor.isHadoopWritable(List.class));
        Assert.assertFalse(TypeExtractor.isHadoopWritable(WritableComparator.class));
    }

    @Test
    public void testCreateWritableInfo() {
        TypeInformation<WritableExtractionTest.DirectWritable> info1 = TypeExtractor.createHadoopWritableTypeInfo(WritableExtractionTest.DirectWritable.class);
        Assert.assertEquals(WritableExtractionTest.DirectWritable.class, info1.getTypeClass());
        TypeInformation<WritableExtractionTest.ViaInterfaceExtension> info2 = TypeExtractor.createHadoopWritableTypeInfo(WritableExtractionTest.ViaInterfaceExtension.class);
        Assert.assertEquals(WritableExtractionTest.ViaInterfaceExtension.class, info2.getTypeClass());
        TypeInformation<WritableExtractionTest.ViaAbstractClassExtension> info3 = TypeExtractor.createHadoopWritableTypeInfo(WritableExtractionTest.ViaAbstractClassExtension.class);
        Assert.assertEquals(WritableExtractionTest.ViaAbstractClassExtension.class, info3.getTypeClass());
    }

    @Test
    public void testValidateTypeInfo() {
        // validate unrelated type info
        TypeExtractor.validateIfWritable(STRING_TYPE_INFO, String.class);
        // validate writable type info correctly
        TypeExtractor.validateIfWritable(new WritableTypeInfo(WritableExtractionTest.DirectWritable.class), WritableExtractionTest.DirectWritable.class);
        TypeExtractor.validateIfWritable(new WritableTypeInfo(WritableExtractionTest.ViaInterfaceExtension.class), WritableExtractionTest.ViaInterfaceExtension.class);
        TypeExtractor.validateIfWritable(new WritableTypeInfo(WritableExtractionTest.ViaAbstractClassExtension.class), WritableExtractionTest.ViaAbstractClassExtension.class);
        // incorrect case: not writable at all
        try {
            TypeExtractor.validateIfWritable(new WritableTypeInfo(WritableExtractionTest.DirectWritable.class), String.class);
            Assert.fail("should have failed with an exception");
        } catch (InvalidTypesException e) {
            // expected
        }
        // incorrect case: wrong writable
        try {
            TypeExtractor.validateIfWritable(new WritableTypeInfo(WritableExtractionTest.ViaInterfaceExtension.class), WritableExtractionTest.DirectWritable.class);
            Assert.fail("should have failed with an exception");
        } catch (InvalidTypesException e) {
            // expected
        }
    }

    @Test
    public void testExtractFromFunction() {
        RichMapFunction<WritableExtractionTest.DirectWritable, WritableExtractionTest.DirectWritable> function = new RichMapFunction<WritableExtractionTest.DirectWritable, WritableExtractionTest.DirectWritable>() {
            @Override
            public WritableExtractionTest.DirectWritable map(WritableExtractionTest.DirectWritable value) throws Exception {
                return null;
            }
        };
        TypeInformation<WritableExtractionTest.DirectWritable> outType = TypeExtractor.getMapReturnTypes(function, new WritableTypeInfo(WritableExtractionTest.DirectWritable.class));
        Assert.assertTrue((outType instanceof WritableTypeInfo));
        Assert.assertEquals(WritableExtractionTest.DirectWritable.class, outType.getTypeClass());
    }

    @Test
    public void testExtractAsPartOfPojo() {
        PojoTypeInfo<WritableExtractionTest.PojoWithWritable> pojoInfo = ((PojoTypeInfo<WritableExtractionTest.PojoWithWritable>) (TypeExtractor.getForClass(WritableExtractionTest.PojoWithWritable.class)));
        boolean foundWritable = false;
        for (int i = 0; i < (pojoInfo.getArity()); i++) {
            PojoField field = pojoInfo.getPojoFieldAt(i);
            String name = field.getField().getName();
            if (name.equals("hadoopCitizen")) {
                if (foundWritable) {
                    Assert.fail("already seen");
                }
                foundWritable = true;
                Assert.assertEquals(new WritableTypeInfo(WritableExtractionTest.DirectWritable.class), field.getTypeInformation());
                Assert.assertEquals(WritableExtractionTest.DirectWritable.class, field.getTypeInformation().getTypeClass());
            }
        }
        Assert.assertTrue("missed the writable type", foundWritable);
    }

    @Test
    public void testInputValidationError() {
        RichMapFunction<Writable, String> function = new RichMapFunction<Writable, String>() {
            @Override
            public String map(Writable value) throws Exception {
                return null;
            }
        };
        @SuppressWarnings("unchecked")
        TypeInformation<Writable> inType = ((TypeInformation<Writable>) ((TypeInformation<?>) (new WritableTypeInfo(WritableExtractionTest.DirectWritable.class))));
        try {
            TypeExtractor.getMapReturnTypes(function, inType);
            Assert.fail("exception expected");
        } catch (InvalidTypesException e) {
            // right
        }
    }

    // ------------------------------------------------------------------------
    // test type classes
    // ------------------------------------------------------------------------
    private interface ExtendedWritable extends Writable {}

    private abstract static class AbstractWritable implements Writable {}

    private static class DirectWritable implements Writable {
        @Override
        public void write(DataOutput dataOutput) throws IOException {
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
        }
    }

    private static class ViaInterfaceExtension implements WritableExtractionTest.ExtendedWritable {
        @Override
        public void write(DataOutput dataOutput) throws IOException {
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
        }
    }

    private static class ViaAbstractClassExtension extends WritableExtractionTest.AbstractWritable {
        @Override
        public void write(DataOutput dataOutput) throws IOException {
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
        }
    }

    /**
     * Test Pojo containing a {@link DirectWritable}.
     */
    public static class PojoWithWritable {
        public String str;

        public WritableExtractionTest.DirectWritable hadoopCitizen;
    }
}

