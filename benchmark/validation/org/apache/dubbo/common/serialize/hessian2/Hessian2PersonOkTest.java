/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.serialize.hessian2;


import java.io.ByteArrayInputStream;
import java.util.Arrays;
import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.base.AbstractSerializationPersonOkTest;
import org.apache.dubbo.common.serialize.base.AbstractSerializationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * JdkPersonOkTest
 */
public class Hessian2PersonOkTest extends AbstractSerializationPersonOkTest {
    {
        serialization = new Hessian2Serialization();
    }

    @Test
    public void test_boolArray_withType() throws Exception {
        boolean[] data = new boolean[]{ true, false, true };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertTrue(Arrays.equals(data, ((boolean[]) (deserialize.readObject(boolean[].class)))));
        try {
            deserialize.readObject(boolean[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_shortArray_withType() throws Exception {
        short[] data = new short[]{ 37, 39, 12 };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, ((short[]) (deserialize.readObject(short[].class))));
        try {
            deserialize.readObject(short[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_intArray_withType() throws Exception {
        int[] data = new int[]{ 234, 0, -1 };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, ((int[]) (deserialize.readObject())));
        try {
            deserialize.readObject(int[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_longArray_withType() throws Exception {
        long[] data = new long[]{ 234, 0, -1 };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, ((long[]) (deserialize.readObject())));
        try {
            deserialize.readObject(long[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_floatArray_withType() throws Exception {
        float[] data = new float[]{ 37.0F, -3.14F, 123456.7F };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, ((float[]) (deserialize.readObject())), 1.0E-4F);
        try {
            deserialize.readObject(float[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_doubleArray_withType() throws Exception {
        double[] data = new double[]{ 37.0, -3.14, 123456.7 };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, ((double[]) (deserialize.readObject(double[].class))), 1.0E-4);
        try {
            deserialize.readObject(double[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }

    @Test
    public void test_StringArray_withType() throws Exception {
        String[] data = new String[]{ "1", "b" };
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeObject(data);
        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);
        Assertions.assertArrayEquals(data, deserialize.readObject(String[].class));
        try {
            deserialize.readObject(String[].class);
            Assertions.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // NOTE: Hessian2 throws ArrayIndexOutOfBoundsException instead of IOException, let's live with this.
    }
}

