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


import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;


/**
 * Basic tests for ZipFile.
 */
public class OldAndroidZipFileTest extends TestCase {
    private static final int SAMPLE_SIZE = 128 * 1024;

    public void testZipFile() throws Exception {
        File file = File.createTempFile("ZipFileTest", ".zip");
        try {
            // create a test file; assume it's not going to collide w/anything
            FileOutputStream outStream = new FileOutputStream(file);
            OldAndroidZipFileTest.createCompressedZip(outStream);
            OldAndroidZipFileTest.scanZip(file.getPath());
            OldAndroidZipFileTest.read2(file.getPath());
        } finally {
            file.delete();
        }
    }
}

