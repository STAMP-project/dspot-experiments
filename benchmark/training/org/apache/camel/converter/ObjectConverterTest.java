/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.converter;


import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class ObjectConverterTest extends Assert {
    @Test
    public void testIterator() {
        Iterator<?> it = ObjectConverter.iterator("Claus,Jonathan");
        Assert.assertEquals("Claus", it.next());
        Assert.assertEquals("Jonathan", it.next());
        Assert.assertEquals(false, it.hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIterable() {
        for (final String name : ((Iterable<String>) (ObjectConverter.iterable("Claus,Jonathan")))) {
            switch (name) {
                case "Claus" :
                case "Jonathan" :
                    break;
                default :
                    Assert.fail();
            }
        }
    }

    @Test
    public void testToByte() {
        Assert.assertEquals(Byte.valueOf("4"), ObjectConverter.toByte(Byte.valueOf("4")));
        Assert.assertEquals(Byte.valueOf("4"), ObjectConverter.toByte(Integer.valueOf("4")));
        Assert.assertEquals(Byte.valueOf("4"), ObjectConverter.toByte("4"));
    }

    @Test
    public void testToClass() {
        Assert.assertEquals(String.class, ObjectConverter.toClass("java.lang.String", null));
        Assert.assertEquals(null, ObjectConverter.toClass("foo.Bar", null));
    }

    @Test
    public void testToShort() {
        Assert.assertEquals(Short.valueOf("4"), ObjectConverter.toShort(Short.valueOf("4")));
        Assert.assertEquals(Short.valueOf("4"), ObjectConverter.toShort(Integer.valueOf("4")));
        Assert.assertEquals(Short.valueOf("4"), ObjectConverter.toShort("4"));
        Assert.assertEquals(null, ObjectConverter.toShort(Double.NaN));
        Assert.assertEquals(null, ObjectConverter.toShort(Float.NaN));
        Assert.assertEquals(Short.valueOf("4"), ObjectConverter.toShort(Short.valueOf("4")));
    }

    @Test
    public void testToInteger() {
        Assert.assertEquals(Integer.valueOf("4"), ObjectConverter.toInteger(Integer.valueOf("4")));
        Assert.assertEquals(Integer.valueOf("4"), ObjectConverter.toInteger(Long.valueOf("4")));
        Assert.assertEquals(Integer.valueOf("4"), ObjectConverter.toInteger("4"));
        Assert.assertEquals(null, ObjectConverter.toInteger(Double.NaN));
        Assert.assertEquals(null, ObjectConverter.toInteger(Float.NaN));
        Assert.assertEquals(Integer.valueOf("4"), ObjectConverter.toInteger(Integer.valueOf("4")));
    }

    @Test
    public void testToLong() {
        Assert.assertEquals(Long.valueOf("4"), ObjectConverter.toLong(Long.valueOf("4")));
        Assert.assertEquals(Long.valueOf("4"), ObjectConverter.toLong(Integer.valueOf("4")));
        Assert.assertEquals(Long.valueOf("4"), ObjectConverter.toLong("4"));
        Assert.assertEquals(null, ObjectConverter.toLong(Double.NaN));
        Assert.assertEquals(null, ObjectConverter.toLong(Float.NaN));
        Assert.assertEquals(Long.valueOf("4"), ObjectConverter.toLong(Long.valueOf("4")));
    }

    @Test
    public void testToFloat() {
        Assert.assertEquals(Float.valueOf("4"), ObjectConverter.toFloat(Float.valueOf("4")));
        Assert.assertEquals(Float.valueOf("4"), ObjectConverter.toFloat(Integer.valueOf("4")));
        Assert.assertEquals(Float.valueOf("4"), ObjectConverter.toFloat("4"));
        Assert.assertEquals(((Float) (Float.NaN)), ObjectConverter.toFloat(Double.NaN));
        Assert.assertEquals(((Float) (Float.NaN)), ObjectConverter.toFloat(Float.NaN));
        Assert.assertEquals(Float.valueOf("4"), ObjectConverter.toFloat(Float.valueOf("4")));
    }

    @Test
    public void testToDouble() {
        Assert.assertEquals(Double.valueOf("4"), ObjectConverter.toDouble(Double.valueOf("4")));
        Assert.assertEquals(Double.valueOf("4"), ObjectConverter.toDouble(Integer.valueOf("4")));
        Assert.assertEquals(Double.valueOf("4"), ObjectConverter.toDouble("4"));
        Assert.assertEquals(((Double) (Double.NaN)), ObjectConverter.toDouble(Double.NaN));
        Assert.assertEquals(((Double) (Double.NaN)), ObjectConverter.toDouble(Float.NaN));
        Assert.assertEquals(Double.valueOf("4"), ObjectConverter.toDouble(Double.valueOf("4")));
    }

    @Test
    public void testToBigInteger() {
        Assert.assertEquals(BigInteger.valueOf(4), ObjectConverter.toBigInteger(Long.valueOf("4")));
        Assert.assertEquals(BigInteger.valueOf(4), ObjectConverter.toBigInteger(Integer.valueOf("4")));
        Assert.assertEquals(BigInteger.valueOf(4), ObjectConverter.toBigInteger("4"));
        Assert.assertEquals(BigInteger.valueOf(123456789L), ObjectConverter.toBigInteger("123456789"));
        Assert.assertEquals(null, ObjectConverter.toBigInteger(new Date()));
        Assert.assertEquals(null, ObjectConverter.toBigInteger(Double.NaN));
        Assert.assertEquals(null, ObjectConverter.toBigInteger(Float.NaN));
        Assert.assertEquals(BigInteger.valueOf(4), ObjectConverter.toBigInteger(Long.valueOf("4")));
        Assert.assertEquals(new BigInteger("14350442579497085228"), ObjectConverter.toBigInteger("14350442579497085228"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("ABC", ObjectConverter.toString(new StringBuffer("ABC")));
        Assert.assertEquals("ABC", ObjectConverter.toString(new StringBuilder("ABC")));
        Assert.assertEquals("", ObjectConverter.toString(new StringBuffer("")));
        Assert.assertEquals("", ObjectConverter.toString(new StringBuilder("")));
    }

    @Test
    public void testToChar() {
        Assert.assertEquals('A', ObjectConverter.toChar("A"));
        Assert.assertEquals(Character.valueOf('A'), ObjectConverter.toCharacter("A"));
    }

    @Test
    public void testNaN() throws Exception {
        Assert.assertEquals(((Double) (Double.NaN)), ObjectConverter.toDouble(Double.NaN));
        Assert.assertEquals(((Double) (Double.NaN)), ObjectConverter.toDouble(Float.NaN));
        Assert.assertEquals(((Float) (Float.NaN)), ObjectConverter.toFloat(Double.NaN));
        Assert.assertEquals(((Float) (Float.NaN)), ObjectConverter.toFloat(Float.NaN));
    }
}

