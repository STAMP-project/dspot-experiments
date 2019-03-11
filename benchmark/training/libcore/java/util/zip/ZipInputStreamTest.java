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


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import junit.framework.TestCase;


public final class ZipInputStreamTest extends TestCase {
    public void testShortMessage() throws IOException {
        byte[] data = "Hello World".getBytes("UTF-8");
        byte[] zipped = ZipOutputStreamTest.zip("short", data);
        TestCase.assertEquals(Arrays.toString(data), Arrays.toString(ZipInputStreamTest.unzip("short", zipped)));
    }

    public void testLongMessage() throws IOException {
        byte[] data = new byte[1024 * 1024];
        new Random().nextBytes(data);
        TestCase.assertTrue(Arrays.equals(data, ZipInputStreamTest.unzip("r", ZipOutputStreamTest.zip("r", data))));
    }
}

