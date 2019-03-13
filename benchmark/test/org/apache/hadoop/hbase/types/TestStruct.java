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
package org.apache.hadoop.hbase.types;


import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Order;
import org.apache.hadoop.hbase.util.PositionedByteRange;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static RawBytes.DESCENDING;


/**
 * This class both tests and demonstrates how to construct compound rowkeys
 * from a POJO. The code under test is {@link Struct}.
 * {@link SpecializedPojo1Type1} demonstrates how one might create their own
 * custom data type extension for an application POJO.
 */
@RunWith(Parameterized.class)
@Category({ MiscTests.class, SmallTests.class })
public class TestStruct {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStruct.class);

    @Parameterized.Parameter(0)
    public Struct generic;

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameter(1)
    public DataType specialized;

    @Parameterized.Parameter(2)
    public Object[][] constructorArgs;

    static final Comparator<byte[]> NULL_SAFE_BYTES_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] o1, byte[] o2) {
            if (o1 == o2)
                return 0;

            if (null == o1)
                return -1;

            if (null == o2)
                return 1;

            return Bytes.compareTo(o1, o2);
        }
    };

    /**
     * A simple object to serialize.
     */
    private static class Pojo1 implements Comparable<TestStruct.Pojo1> {
        final String stringFieldAsc;

        final int intFieldAsc;

        final double doubleFieldAsc;

        final transient String str;

        public Pojo1(Object... argv) {
            stringFieldAsc = ((String) (argv[0]));
            intFieldAsc = ((Integer) (argv[1]));
            doubleFieldAsc = ((Double) (argv[2]));
            str = new StringBuilder().append("{ ").append((null == (stringFieldAsc) ? "" : "\"")).append(stringFieldAsc).append((null == (stringFieldAsc) ? "" : "\"")).append(", ").append(intFieldAsc).append(", ").append(doubleFieldAsc).append(" }").toString();
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public int compareTo(TestStruct.Pojo1 o) {
            int cmp = stringFieldAsc.compareTo(o.stringFieldAsc);
            if (cmp != 0) {
                return cmp;
            }
            cmp = Integer.valueOf(intFieldAsc).compareTo(Integer.valueOf(o.intFieldAsc));
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(doubleFieldAsc, o.doubleFieldAsc);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(doubleFieldAsc);
            result = (prime * result) + ((int) (temp ^ (temp >>> 32)));
            result = (prime * result) + (intFieldAsc);
            result = (prime * result) + ((stringFieldAsc) == null ? 0 : stringFieldAsc.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            TestStruct.Pojo1 other = ((TestStruct.Pojo1) (obj));
            if ((Double.doubleToLongBits(doubleFieldAsc)) != (Double.doubleToLongBits(other.doubleFieldAsc))) {
                return false;
            }
            if ((intFieldAsc) != (other.intFieldAsc)) {
                return false;
            }
            if ((stringFieldAsc) == null) {
                if ((other.stringFieldAsc) != null) {
                    return false;
                }
            } else
                if (!(stringFieldAsc.equals(other.stringFieldAsc))) {
                    return false;
                }

            return true;
        }
    }

    /**
     * A simple object to serialize.
     */
    private static class Pojo2 implements Comparable<TestStruct.Pojo2> {
        final byte[] byteField1Asc;

        final byte[] byteField2Dsc;

        final String stringFieldDsc;

        final byte[] byteField3Dsc;

        final transient String str;

        public Pojo2(Object... vals) {
            byteField1Asc = ((vals.length) > 0) ? ((byte[]) (vals[0])) : null;
            byteField2Dsc = ((vals.length) > 1) ? ((byte[]) (vals[1])) : null;
            stringFieldDsc = ((vals.length) > 2) ? ((String) (vals[2])) : null;
            byteField3Dsc = ((vals.length) > 3) ? ((byte[]) (vals[3])) : null;
            str = new StringBuilder().append("{ ").append(Bytes.toStringBinary(byteField1Asc)).append(", ").append(Bytes.toStringBinary(byteField2Dsc)).append(", ").append((null == (stringFieldDsc) ? "" : "\"")).append(stringFieldDsc).append((null == (stringFieldDsc) ? "" : "\"")).append(", ").append(Bytes.toStringBinary(byteField3Dsc)).append(" }").toString();
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public int compareTo(TestStruct.Pojo2 o) {
            int cmp = TestStruct.NULL_SAFE_BYTES_COMPARATOR.compare(byteField1Asc, o.byteField1Asc);
            if (cmp != 0) {
                return cmp;
            }
            cmp = -(TestStruct.NULL_SAFE_BYTES_COMPARATOR.compare(byteField2Dsc, o.byteField2Dsc));
            if (cmp != 0) {
                return cmp;
            }
            if (null == (stringFieldDsc)) {
                cmp = 1;
            } else
                if (null == (o.stringFieldDsc)) {
                    cmp = -1;
                } else
                    if (stringFieldDsc.equals(o.stringFieldDsc)) {
                        cmp = 0;
                    } else
                        cmp = -(stringFieldDsc.compareTo(o.stringFieldDsc));



            if (cmp != 0) {
                return cmp;
            }
            return -(TestStruct.NULL_SAFE_BYTES_COMPARATOR.compare(byteField3Dsc, o.byteField3Dsc));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (Arrays.hashCode(byteField1Asc));
            result = (prime * result) + (Arrays.hashCode(byteField2Dsc));
            result = (prime * result) + (Arrays.hashCode(byteField3Dsc));
            result = (prime * result) + ((stringFieldDsc) == null ? 0 : stringFieldDsc.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            TestStruct.Pojo2 other = ((TestStruct.Pojo2) (obj));
            if (!(Arrays.equals(byteField1Asc, other.byteField1Asc))) {
                return false;
            }
            if (!(Arrays.equals(byteField2Dsc, other.byteField2Dsc))) {
                return false;
            }
            if (!(Arrays.equals(byteField3Dsc, other.byteField3Dsc))) {
                return false;
            }
            if ((stringFieldDsc) == null) {
                if ((other.stringFieldDsc) != null) {
                    return false;
                }
            } else
                if (!(stringFieldDsc.equals(other.stringFieldDsc))) {
                    return false;
                }

            return true;
        }
    }

    /**
     * A custom data type implementation specialized for {@link Pojo1}.
     */
    private static class SpecializedPojo1Type1 implements DataType<TestStruct.Pojo1> {
        private static final RawStringTerminated stringField = new RawStringTerminated("/");

        private static final RawInteger intField = new RawInteger();

        private static final RawDouble doubleField = new RawDouble();

        /**
         * The {@link Struct} equivalent of this type.
         */
        public static Struct GENERIC = new StructBuilder().add(TestStruct.SpecializedPojo1Type1.stringField).add(TestStruct.SpecializedPojo1Type1.intField).add(TestStruct.SpecializedPojo1Type1.doubleField).toStruct();

        @Override
        public boolean isOrderPreserving() {
            return true;
        }

        @Override
        public Order getOrder() {
            return null;
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public boolean isSkippable() {
            return true;
        }

        @Override
        public int encodedLength(TestStruct.Pojo1 val) {
            return ((TestStruct.SpecializedPojo1Type1.stringField.encodedLength(val.stringFieldAsc)) + (TestStruct.SpecializedPojo1Type1.intField.encodedLength(val.intFieldAsc))) + (TestStruct.SpecializedPojo1Type1.doubleField.encodedLength(val.doubleFieldAsc));
        }

        @Override
        public Class<TestStruct.Pojo1> encodedClass() {
            return TestStruct.Pojo1.class;
        }

        @Override
        public int skip(PositionedByteRange src) {
            int skipped = TestStruct.SpecializedPojo1Type1.stringField.skip(src);
            skipped += TestStruct.SpecializedPojo1Type1.intField.skip(src);
            skipped += TestStruct.SpecializedPojo1Type1.doubleField.skip(src);
            return skipped;
        }

        @Override
        public TestStruct.Pojo1 decode(PositionedByteRange src) {
            Object[] ret = new Object[3];
            ret[0] = TestStruct.SpecializedPojo1Type1.stringField.decode(src);
            ret[1] = TestStruct.SpecializedPojo1Type1.intField.decode(src);
            ret[2] = TestStruct.SpecializedPojo1Type1.doubleField.decode(src);
            return new TestStruct.Pojo1(ret);
        }

        @Override
        public int encode(PositionedByteRange dst, TestStruct.Pojo1 val) {
            int written = TestStruct.SpecializedPojo1Type1.stringField.encode(dst, val.stringFieldAsc);
            written += TestStruct.SpecializedPojo1Type1.intField.encode(dst, val.intFieldAsc);
            written += TestStruct.SpecializedPojo1Type1.doubleField.encode(dst, val.doubleFieldAsc);
            return written;
        }
    }

    /**
     * A custom data type implementation specialized for {@link Pojo2}.
     */
    private static class SpecializedPojo2Type1 implements DataType<TestStruct.Pojo2> {
        private static RawBytesTerminated byteField1 = new RawBytesTerminated("/");

        private static RawBytesTerminated byteField2 = new RawBytesTerminated(Order.DESCENDING, "/");

        private static RawStringTerminated stringField = new RawStringTerminated(Order.DESCENDING, new byte[]{ 0 });

        private static RawBytes byteField3 = DESCENDING;

        /**
         * The {@link Struct} equivalent of this type.
         */
        public static Struct GENERIC = new StructBuilder().add(TestStruct.SpecializedPojo2Type1.byteField1).add(TestStruct.SpecializedPojo2Type1.byteField2).add(TestStruct.SpecializedPojo2Type1.stringField).add(TestStruct.SpecializedPojo2Type1.byteField3).toStruct();

        @Override
        public boolean isOrderPreserving() {
            return true;
        }

        @Override
        public Order getOrder() {
            return null;
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public boolean isSkippable() {
            return true;
        }

        @Override
        public int encodedLength(TestStruct.Pojo2 val) {
            return (((TestStruct.SpecializedPojo2Type1.byteField1.encodedLength(val.byteField1Asc)) + (TestStruct.SpecializedPojo2Type1.byteField2.encodedLength(val.byteField2Dsc))) + (TestStruct.SpecializedPojo2Type1.stringField.encodedLength(val.stringFieldDsc))) + (TestStruct.SpecializedPojo2Type1.byteField3.encodedLength(val.byteField3Dsc));
        }

        @Override
        public Class<TestStruct.Pojo2> encodedClass() {
            return TestStruct.Pojo2.class;
        }

        @Override
        public int skip(PositionedByteRange src) {
            int skipped = TestStruct.SpecializedPojo2Type1.byteField1.skip(src);
            skipped += TestStruct.SpecializedPojo2Type1.byteField2.skip(src);
            skipped += TestStruct.SpecializedPojo2Type1.stringField.skip(src);
            skipped += TestStruct.SpecializedPojo2Type1.byteField3.skip(src);
            return skipped;
        }

        @Override
        public TestStruct.Pojo2 decode(PositionedByteRange src) {
            Object[] ret = new Object[4];
            ret[0] = TestStruct.SpecializedPojo2Type1.byteField1.decode(src);
            ret[1] = TestStruct.SpecializedPojo2Type1.byteField2.decode(src);
            ret[2] = TestStruct.SpecializedPojo2Type1.stringField.decode(src);
            ret[3] = TestStruct.SpecializedPojo2Type1.byteField3.decode(src);
            return new TestStruct.Pojo2(ret);
        }

        @Override
        public int encode(PositionedByteRange dst, TestStruct.Pojo2 val) {
            int written = TestStruct.SpecializedPojo2Type1.byteField1.encode(dst, val.byteField1Asc);
            written += TestStruct.SpecializedPojo2Type1.byteField2.encode(dst, val.byteField2Dsc);
            written += TestStruct.SpecializedPojo2Type1.stringField.encode(dst, val.stringFieldDsc);
            written += TestStruct.SpecializedPojo2Type1.byteField3.encode(dst, val.byteField3Dsc);
            return written;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrderPreservation() throws Exception {
        Object[] vals = new Object[constructorArgs.length];
        PositionedByteRange[] encodedGeneric = new PositionedByteRange[constructorArgs.length];
        PositionedByteRange[] encodedSpecialized = new PositionedByteRange[constructorArgs.length];
        Constructor<?> ctor = specialized.encodedClass().getConstructor(Object[].class);
        for (int i = 0; i < (vals.length); i++) {
            vals[i] = ctor.newInstance(new Object[]{ constructorArgs[i] });
            encodedGeneric[i] = new org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange(generic.encodedLength(constructorArgs[i]));
            encodedSpecialized[i] = new org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange(specialized.encodedLength(vals[i]));
        }
        // populate our arrays
        for (int i = 0; i < (vals.length); i++) {
            generic.encode(encodedGeneric[i], constructorArgs[i]);
            encodedGeneric[i].setPosition(0);
            specialized.encode(encodedSpecialized[i], vals[i]);
            encodedSpecialized[i].setPosition(0);
            Assert.assertArrayEquals(encodedGeneric[i].getBytes(), encodedSpecialized[i].getBytes());
        }
        Arrays.sort(vals);
        Arrays.sort(encodedGeneric);
        Arrays.sort(encodedSpecialized);
        for (int i = 0; i < (vals.length); i++) {
            Assert.assertEquals(("Struct encoder does not preserve sort order at position " + i), vals[i], ctor.newInstance(new Object[]{ generic.decode(encodedGeneric[i]) }));
            Assert.assertEquals(("Specialized encoder does not preserve sort order at position " + i), vals[i], specialized.decode(encodedSpecialized[i]));
        }
    }
}

