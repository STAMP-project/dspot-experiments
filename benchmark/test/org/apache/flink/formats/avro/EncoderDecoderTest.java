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
package org.apache.flink.formats.avro;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.flink.formats.avro.generated.Address;
import org.apache.flink.formats.avro.generated.Colors;
import org.apache.flink.formats.avro.generated.Fixed16;
import org.apache.flink.formats.avro.generated.Fixed2;
import org.apache.flink.formats.avro.generated.User;
import org.apache.flink.formats.avro.utils.DataInputDecoder;
import org.apache.flink.formats.avro.utils.DataOutputEncoder;
import org.apache.flink.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link DataOutputEncoder} and {@link DataInputDecoder} classes for Avro serialization.
 */
public class EncoderDecoderTest {
    @Test
    public void testComplexStringsDirecty() {
        try {
            Random rnd = new Random(349712539451944123L);
            for (int i = 0; i < 10; i++) {
                String testString = StringUtils.getRandomString(rnd, 10, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
                {
                    DataOutputStream dataOut = new DataOutputStream(baos);
                    DataOutputEncoder encoder = new DataOutputEncoder();
                    encoder.setOut(dataOut);
                    encoder.writeString(testString);
                    dataOut.flush();
                    dataOut.close();
                }
                byte[] data = baos.toByteArray();
                // deserialize
                {
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    DataInputStream dataIn = new DataInputStream(bais);
                    DataInputDecoder decoder = new DataInputDecoder();
                    decoder.setIn(dataIn);
                    String deserialized = decoder.readString();
                    Assert.assertEquals(testString, deserialized);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test failed due to an exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testPrimitiveTypes() {
        EncoderDecoderTest.testObjectSerialization(Boolean.TRUE);
        EncoderDecoderTest.testObjectSerialization(Boolean.FALSE);
        EncoderDecoderTest.testObjectSerialization(((byte) (0)));
        EncoderDecoderTest.testObjectSerialization(((byte) (1)));
        EncoderDecoderTest.testObjectSerialization(((byte) (-1)));
        EncoderDecoderTest.testObjectSerialization(Byte.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Byte.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(((short) (0)));
        EncoderDecoderTest.testObjectSerialization(((short) (1)));
        EncoderDecoderTest.testObjectSerialization(((short) (-1)));
        EncoderDecoderTest.testObjectSerialization(Short.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Short.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(0);
        EncoderDecoderTest.testObjectSerialization(1);
        EncoderDecoderTest.testObjectSerialization((-1));
        EncoderDecoderTest.testObjectSerialization(Integer.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Integer.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(0L);
        EncoderDecoderTest.testObjectSerialization(1L);
        EncoderDecoderTest.testObjectSerialization(((long) (-1)));
        EncoderDecoderTest.testObjectSerialization(Long.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Long.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(0.0F);
        EncoderDecoderTest.testObjectSerialization(1.0F);
        EncoderDecoderTest.testObjectSerialization(((float) (-1)));
        EncoderDecoderTest.testObjectSerialization(((float) (Math.E)));
        EncoderDecoderTest.testObjectSerialization(((float) (Math.PI)));
        EncoderDecoderTest.testObjectSerialization(Float.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Float.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(Float.MIN_NORMAL);
        EncoderDecoderTest.testObjectSerialization(Float.NaN);
        EncoderDecoderTest.testObjectSerialization(Float.NEGATIVE_INFINITY);
        EncoderDecoderTest.testObjectSerialization(Float.POSITIVE_INFINITY);
        EncoderDecoderTest.testObjectSerialization(0.0);
        EncoderDecoderTest.testObjectSerialization(1.0);
        EncoderDecoderTest.testObjectSerialization(((double) (-1)));
        EncoderDecoderTest.testObjectSerialization(Math.E);
        EncoderDecoderTest.testObjectSerialization(Math.PI);
        EncoderDecoderTest.testObjectSerialization(Double.MIN_VALUE);
        EncoderDecoderTest.testObjectSerialization(Double.MAX_VALUE);
        EncoderDecoderTest.testObjectSerialization(Double.MIN_NORMAL);
        EncoderDecoderTest.testObjectSerialization(Double.NaN);
        EncoderDecoderTest.testObjectSerialization(Double.NEGATIVE_INFINITY);
        EncoderDecoderTest.testObjectSerialization(Double.POSITIVE_INFINITY);
        EncoderDecoderTest.testObjectSerialization("");
        EncoderDecoderTest.testObjectSerialization("abcdefg");
        EncoderDecoderTest.testObjectSerialization("ab\u1535\u0155xyz\u706f");
        EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.SimpleTypes(3637, 54876486548L, ((byte) (65)), "We're out looking for astronauts", ((short) (9095)), 2.65767523));
        EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.SimpleTypes(705608724, (-1L), ((byte) (-65)), "Serve me the sky with a big slice of lemon", ((short) (Byte.MIN_VALUE)), 1.0E-7));
    }

    @Test
    public void testArrayTypes() {
        {
            int[] array = new int[]{ 1, 2, 3, 4, 5 };
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            long[] array = new long[]{ 1, 2, 3, 4, 5 };
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            float[] array = new float[]{ 1, 2, 3, 4, 5 };
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            double[] array = new double[]{ 1, 2, 3, 4, 5 };
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            String[] array = new String[]{ "Oh", "my", "what", "do", "we", "have", "here", "?" };
            EncoderDecoderTest.testObjectSerialization(array);
        }
    }

    @Test
    public void testEmptyArray() {
        {
            int[] array = new int[0];
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            long[] array = new long[0];
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            float[] array = new float[0];
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            double[] array = new double[0];
            EncoderDecoderTest.testObjectSerialization(array);
        }
        {
            String[] array = new String[0];
            EncoderDecoderTest.testObjectSerialization(array);
        }
    }

    @Test
    public void testObjects() {
        // simple object containing only primitives
        {
            EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.Book(976243875L, "The Serialization Odysse", 42));
        }
        // object with collection
        {
            ArrayList<String> list = new ArrayList<>();
            list.add("A");
            list.add("B");
            list.add("C");
            list.add("D");
            list.add("E");
            EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.BookAuthor(976243875L, list, "Arno Nym"));
        }
        // object with empty collection
        {
            ArrayList<String> list = new ArrayList<>();
            EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.BookAuthor(987654321L, list, "The Saurus"));
        }
    }

    @Test
    public void testNestedObjectsWithCollections() {
        EncoderDecoderTest.testObjectSerialization(new EncoderDecoderTest.ComplexNestedObject2(true));
    }

    @Test
    public void testGeneratedObjectWithNullableFields() {
        List<CharSequence> strings = Arrays.asList(new CharSequence[]{ "These", "strings", "should", "be", "recognizable", "as", "a", "meaningful", "sequence" });
        List<Boolean> bools = Arrays.asList(true, true, false, false, true, false, true, true);
        Map<CharSequence, Long> map = new HashMap<>();
        map.put("1", 1L);
        map.put("2", 2L);
        map.put("3", 3L);
        byte[] b = new byte[16];
        new Random().nextBytes(b);
        Fixed16 f = new Fixed16(b);
        Address addr = new Address(239, "6th Main", "Bangalore", "Karnataka", "560075");
        User user = // 20.00
        new User("Freudenreich", 1337, "macintosh gray", 1234567890L, 3.1415926, null, true, strings, bools, null, Colors.GREEN, map, f, Boolean.TRUE, addr, ByteBuffer.wrap(b), LocalDate.parse("2014-03-01"), LocalTime.parse("12:12:12"), 123456, DateTime.parse("2014-03-01T12:12:12.321Z"), 123456L, ByteBuffer.wrap(BigDecimal.valueOf(2000, 2).unscaledValue().toByteArray()), new Fixed2(BigDecimal.valueOf(2000, 2).unscaledValue().toByteArray()));// 20.00

        EncoderDecoderTest.testObjectSerialization(user);
    }

    @Test
    public void testVarLenCountEncoding() {
        try {
            long[] values = new long[]{ 0, 1, 2, 3, 4, 0, 574, 45236, 0, 234623462, 23462462346L, 0, 9734028767869761L, 9223372036854775807L };
            // write
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            {
                DataOutputStream dataOut = new DataOutputStream(baos);
                for (long val : values) {
                    DataOutputEncoder.writeVarLongCount(dataOut, val);
                }
                dataOut.flush();
                dataOut.close();
            }
            // read
            {
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                DataInputStream dataIn = new DataInputStream(bais);
                for (long val : values) {
                    long read = DataInputDecoder.readVarLongCount(dataIn);
                    Assert.assertEquals("Wrong var-len encoded value read.", val, read);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Test failed due to an exception: " + (e.getMessage())));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Test Objects
    // --------------------------------------------------------------------------------------------
    private static final class SimpleTypes {
        private final int iVal;

        private final long lVal;

        private final byte bVal;

        private final String sVal;

        private final short rVal;

        private final double dVal;

        public SimpleTypes() {
            this(0, 0, ((byte) (0)), "", ((short) (0)), 0);
        }

        public SimpleTypes(int iVal, long lVal, byte bVal, String sVal, short rVal, double dVal) {
            this.iVal = iVal;
            this.lVal = lVal;
            this.bVal = bVal;
            this.sVal = sVal;
            this.rVal = rVal;
            this.dVal = dVal;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (EncoderDecoderTest.SimpleTypes.class)) {
                EncoderDecoderTest.SimpleTypes other = ((EncoderDecoderTest.SimpleTypes) (obj));
                return ((((((other.iVal) == (this.iVal)) && ((other.lVal) == (this.lVal))) && ((other.bVal) == (this.bVal))) && (other.sVal.equals(this.sVal))) && ((other.rVal) == (this.rVal))) && ((other.dVal) == (this.dVal));
            } else {
                return false;
            }
        }
    }

    private static class ComplexNestedObject1 {
        private double doubleValue;

        private List<String> stringList;

        public ComplexNestedObject1() {
        }

        public ComplexNestedObject1(int offInit) {
            this.doubleValue = 6293485.6723 + offInit;
            this.stringList = new ArrayList<>();
            this.stringList.add(("A" + offInit));
            this.stringList.add(("somewhat" + offInit));
            this.stringList.add(("random" + offInit));
            this.stringList.add(("collection" + offInit));
            this.stringList.add(("of" + offInit));
            this.stringList.add(("strings" + offInit));
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (EncoderDecoderTest.ComplexNestedObject1.class)) {
                EncoderDecoderTest.ComplexNestedObject1 other = ((EncoderDecoderTest.ComplexNestedObject1) (obj));
                return ((other.doubleValue) == (this.doubleValue)) && (this.stringList.equals(other.stringList));
            } else {
                return false;
            }
        }
    }

    private static class ComplexNestedObject2 {
        private long longValue;

        private Map<String, EncoderDecoderTest.ComplexNestedObject1> theMap;

        public ComplexNestedObject2() {
        }

        public ComplexNestedObject2(boolean init) {
            this.longValue = 46547;
            this.theMap = new HashMap<>();
            this.theMap.put("36354L", new EncoderDecoderTest.ComplexNestedObject1(43546543));
            this.theMap.put("785611L", new EncoderDecoderTest.ComplexNestedObject1(45784568));
            this.theMap.put("43L", new EncoderDecoderTest.ComplexNestedObject1(9876543));
            this.theMap.put("-45687L", new EncoderDecoderTest.ComplexNestedObject1(7897615));
            this.theMap.put("1919876876896L", new EncoderDecoderTest.ComplexNestedObject1(27154));
            this.theMap.put("-868468468L", new EncoderDecoderTest.ComplexNestedObject1(546435));
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (EncoderDecoderTest.ComplexNestedObject2.class)) {
                EncoderDecoderTest.ComplexNestedObject2 other = ((EncoderDecoderTest.ComplexNestedObject2) (obj));
                return ((other.longValue) == (this.longValue)) && (this.theMap.equals(other.theMap));
            } else {
                return false;
            }
        }
    }

    private static class Book {
        private long bookId;

        private String title;

        private long authorId;

        public Book() {
        }

        public Book(long bookId, String title, long authorId) {
            this.bookId = bookId;
            this.title = title;
            this.authorId = authorId;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (EncoderDecoderTest.Book.class)) {
                EncoderDecoderTest.Book other = ((EncoderDecoderTest.Book) (obj));
                return (((other.bookId) == (this.bookId)) && ((other.authorId) == (this.authorId))) && (this.title.equals(other.title));
            } else {
                return false;
            }
        }
    }

    private static class BookAuthor {
        private long authorId;

        private List<String> bookTitles;

        private String authorName;

        public BookAuthor() {
        }

        public BookAuthor(long authorId, List<String> bookTitles, String authorName) {
            this.authorId = authorId;
            this.bookTitles = bookTitles;
            this.authorName = authorName;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj.getClass()) == (EncoderDecoderTest.BookAuthor.class)) {
                EncoderDecoderTest.BookAuthor other = ((EncoderDecoderTest.BookAuthor) (obj));
                return ((other.authorName.equals(this.authorName)) && ((other.authorId) == (this.authorId))) && (other.bookTitles.equals(this.bookTitles));
            } else {
                return false;
            }
        }
    }
}

