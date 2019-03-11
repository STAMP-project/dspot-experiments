/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.util.zip;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DeflaterInputStream;
import junit.framework.TestCase;


public final class DeflaterInputStreamTest extends TestCase {
    public void testReadByteByByte() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        InputStream in = new DeflaterInputStream(new ByteArrayInputStream(data));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TestCase.assertEquals(1, in.available());
        int b;
        while ((b = in.read()) != (-1)) {
            out.write(b);
        } 
        TestCase.assertEquals(0, in.available());
        TestCase.assertEquals(Arrays.toString(data), Arrays.toString(inflate(out.toByteArray())));
        in.close();
        try {
            in.available();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testReadWithBuffer() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        byte[] buffer = new byte[8];
        InputStream in = new DeflaterInputStream(new ByteArrayInputStream(data));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TestCase.assertEquals(1, in.available());
        int count;
        while ((count = in.read(buffer, 0, 5)) != (-1)) {
            TestCase.assertTrue((count <= 5));
            out.write(buffer, 0, count);
        } 
        TestCase.assertEquals(0, in.available());
        TestCase.assertEquals(Arrays.toString(data), Arrays.toString(inflate(out.toByteArray())));
        in.close();
        try {
            in.available();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testReadExceptions() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        byte[] buffer = new byte[8];
        InputStream in = new DeflaterInputStream(new ByteArrayInputStream(data));
        try {
            in.read(buffer, 0, 10);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            in.read(null, 0, 5);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            in.read(buffer, (-1), 5);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        in.close();
        try {
            in.read(buffer, 0, 5);
            TestCase.fail();
        } catch (IOException expected) {
        }
    }
}

