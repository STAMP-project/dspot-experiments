/**
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.nio;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import junit.framework.TestCase;


/**
 * Tests for some buffers from the java.nio package.
 */
public class OldAndroidNIOTest extends TestCase {
    public void testNIO_byte_array() throws Exception {
        // Test byte array-based buffer
        byteBufferTest(ByteBuffer.allocate(12));
    }

    public void testNIO_direct() throws Exception {
        // Test native heap-allocated buffer
        byteBufferTest(ByteBuffer.allocateDirect(12));
    }

    public void testNIO_short_array() throws Exception {
        // Test short array-based buffer
        short[] shortArray = new short[8];
        ShortBuffer sb = ShortBuffer.wrap(shortArray);
        shortBufferTest(sb);
    }

    public void testNIO_int_array() throws Exception {
        // Test int array-based buffer
        int[] intArray = new int[8];
        IntBuffer ib = IntBuffer.wrap(intArray);
        intBufferTest(ib);
    }

    public void testNIO_float_array() throws Exception {
        // Test float array-based buffer
        float[] floatArray = new float[8];
        FloatBuffer fb = FloatBuffer.wrap(floatArray);
        floatBufferTest(fb);
    }
}

