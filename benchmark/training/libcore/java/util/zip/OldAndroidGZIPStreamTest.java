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
package libcore.java.util.zip;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;


/**
 * Deflates and inflates some test data with GZipStreams
 */
public class OldAndroidGZIPStreamTest extends TestCase {
    public void testGZIPStream() throws Exception {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        OldAndroidGZIPStreamTest.createGZIP(bytesOut);
        byte[] zipData;
        zipData = bytesOut.toByteArray();
        /* FileOutputStream outFile = new FileOutputStream("/tmp/foo.gz");
        outFile.write(zipData, 0, zipData.length);
        outFile.close();
         */
        /* FileInputStream inFile = new FileInputStream("/tmp/foo.gz");
        int inputLength = inFile.available();
        zipData = new byte[inputLength];
        if (inFile.read(zipData) != inputLength)
        throw new RuntimeException();
        inFile.close();
         */
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(zipData);
        OldAndroidGZIPStreamTest.scanGZIP(bytesIn);
    }
}

