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
package org.apache.hadoop.io;


import ArrayPrimitiveWritable.Internal;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ArrayPrimitiveWritable}
 */
public class TestArrayPrimitiveWritable {
    static final boolean[] b = new boolean[]{ true, true, false };

    static final char[] c = new char[]{ 'a', 'b', 'c' };

    static final byte[] by = new byte[]{ 1, 2, 3 };

    static final short[] sh = new short[]{ 1, 2, 3 };

    static final int[] i = new int[]{ 1, 2, 3 };

    static final long[] lo = new long[]{ 1L, 2L, 3L };

    static final float[] f = new float[]{ ((float) (1.0)), ((float) (2.5)), ((float) (3.3)) };

    static final double[] d = new double[]{ 1.0, 2.5, 3.3 };

    static final Object[] bigSet = new Object[]{ TestArrayPrimitiveWritable.b, TestArrayPrimitiveWritable.c, TestArrayPrimitiveWritable.by, TestArrayPrimitiveWritable.sh, TestArrayPrimitiveWritable.i, TestArrayPrimitiveWritable.lo, TestArrayPrimitiveWritable.f, TestArrayPrimitiveWritable.d };

    static final Object[] expectedResultSet = new Object[]{ TestArrayPrimitiveWritable.b, TestArrayPrimitiveWritable.b, TestArrayPrimitiveWritable.c, TestArrayPrimitiveWritable.c, TestArrayPrimitiveWritable.by, TestArrayPrimitiveWritable.by, TestArrayPrimitiveWritable.sh, TestArrayPrimitiveWritable.sh, TestArrayPrimitiveWritable.i, TestArrayPrimitiveWritable.i, TestArrayPrimitiveWritable.lo, TestArrayPrimitiveWritable.lo, TestArrayPrimitiveWritable.f, TestArrayPrimitiveWritable.f, TestArrayPrimitiveWritable.d, TestArrayPrimitiveWritable.d };

    final Object[] resultSet = new Object[(TestArrayPrimitiveWritable.bigSet.length) * 2];

    final DataOutputBuffer out = new DataOutputBuffer();

    final DataInputBuffer in = new DataInputBuffer();

    @Test
    public void testMany() throws IOException {
        // Write a big set of data, one of each primitive type array
        for (Object x : TestArrayPrimitiveWritable.bigSet) {
            // write each test object two ways
            // First, transparently via ObjectWritable
            ObjectWritable.writeObject(out, x, x.getClass(), null, true);
            // Second, explicitly via ArrayPrimitiveWritable
            new ArrayPrimitiveWritable(x).write(out);
        }
        // Now read the data back in
        in.reset(out.getData(), out.getLength());
        for (int x = 0; x < (resultSet.length);) {
            // First, transparently
            resultSet[(x++)] = ObjectWritable.readObject(in, null);
            // Second, explicitly
            ArrayPrimitiveWritable apw = new ArrayPrimitiveWritable();
            apw.readFields(in);
            resultSet[(x++)] = apw.get();
        }
        // validate data structures and values
        Assert.assertEquals(TestArrayPrimitiveWritable.expectedResultSet.length, resultSet.length);
        for (int x = 0; x < (resultSet.length); x++) {
            Assert.assertEquals(("ComponentType of array " + x), TestArrayPrimitiveWritable.expectedResultSet[x].getClass().getComponentType(), resultSet[x].getClass().getComponentType());
        }
        Assert.assertTrue("In and Out arrays didn't match values", Arrays.deepEquals(TestArrayPrimitiveWritable.expectedResultSet, resultSet));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testObjectLabeling() throws IOException {
        // Do a few tricky experiments to make sure things are being written
        // the way we expect
        // Write the data array with ObjectWritable
        // which will indirectly write it using APW.Internal
        ObjectWritable.writeObject(out, TestArrayPrimitiveWritable.i, TestArrayPrimitiveWritable.i.getClass(), null, true);
        // Write the corresponding APW directly with ObjectWritable
        ArrayPrimitiveWritable apw = new ArrayPrimitiveWritable(TestArrayPrimitiveWritable.i);
        ObjectWritable.writeObject(out, apw, apw.getClass(), null, true);
        // Get ready to read it back
        in.reset(out.getData(), out.getLength());
        // Read the int[] object as written by ObjectWritable, but
        // "going around" ObjectWritable
        String className = UTF8.readString(in);
        Assert.assertEquals(("The int[] written by ObjectWritable was not labelled as " + "an ArrayPrimitiveWritable.Internal"), Internal.class.getName(), className);
        ArrayPrimitiveWritable.Internal apwi = new ArrayPrimitiveWritable.Internal();
        apwi.readFields(in);
        Assert.assertEquals("The ArrayPrimitiveWritable.Internal component type was corrupted", int.class, apw.getComponentType());
        Assert.assertTrue(("The int[] written by ObjectWritable as " + "ArrayPrimitiveWritable.Internal was corrupted"), Arrays.equals(TestArrayPrimitiveWritable.i, ((int[]) (apwi.get()))));
        // Read the APW object as written by ObjectWritable, but
        // "going around" ObjectWritable
        String declaredClassName = UTF8.readString(in);
        Assert.assertEquals(("The APW written by ObjectWritable was not labelled as " + "declaredClass ArrayPrimitiveWritable"), ArrayPrimitiveWritable.class.getName(), declaredClassName);
        className = UTF8.readString(in);
        Assert.assertEquals(("The APW written by ObjectWritable was not labelled as " + "class ArrayPrimitiveWritable"), ArrayPrimitiveWritable.class.getName(), className);
        ArrayPrimitiveWritable apw2 = new ArrayPrimitiveWritable();
        apw2.readFields(in);
        Assert.assertEquals("The ArrayPrimitiveWritable component type was corrupted", int.class, apw2.getComponentType());
        Assert.assertTrue(("The int[] written by ObjectWritable as " + "ArrayPrimitiveWritable was corrupted"), Arrays.equals(TestArrayPrimitiveWritable.i, ((int[]) (apw2.get()))));
    }

    @Test
    public void testOldFormat() throws IOException {
        // Make sure we still correctly write the old format if desired.
        // Write the data array with old ObjectWritable API
        // which will set allowCompactArrays false.
        ObjectWritable.writeObject(out, TestArrayPrimitiveWritable.i, TestArrayPrimitiveWritable.i.getClass(), null);
        // Get ready to read it back
        in.reset(out.getData(), out.getLength());
        // Read the int[] object as written by ObjectWritable, but
        // "going around" ObjectWritable
        @SuppressWarnings("deprecation")
        String className = UTF8.readString(in);
        Assert.assertEquals(("The int[] written by ObjectWritable as a non-compact array " + "was not labelled as an array of int"), TestArrayPrimitiveWritable.i.getClass().getName(), className);
        int length = in.readInt();
        Assert.assertEquals(("The int[] written by ObjectWritable as a non-compact array " + "was not expected length"), TestArrayPrimitiveWritable.i.length, length);
        int[] readValue = new int[length];
        try {
            for (int i = 0; i < length; i++) {
                readValue[i] = ((int) ((Integer) (ObjectWritable.readObject(in, null))));
            }
        } catch (Exception e) {
            Assert.fail((((("The int[] written by ObjectWritable as a non-compact array " + "was corrupted.  Failed to correctly read int[] of length ") + length) + ". Got exception:\n") + (StringUtils.stringifyException(e))));
        }
        Assert.assertTrue(("The int[] written by ObjectWritable as a non-compact array " + "was corrupted."), Arrays.equals(TestArrayPrimitiveWritable.i, readValue));
    }
}

