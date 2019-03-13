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
package libcore.java.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import junit.framework.TestCase;
import libcore.java.lang.ref.FinalizationTester;


public final class RandomAccessFileTest extends TestCase {
    private File file;

    public void testSeekTooLarge() throws Exception {
        // RoboVM note: On Darwin this test fails on both RoboVM and the RI since
        // lseek doesn't fail even if Long.MAX_VALUE is passed to it.
        if ((System.getProperty("os.name").contains("iOS")) || (System.getProperty("os.name").contains("Mac"))) {
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        try {
            raf.seek(Long.MAX_VALUE);
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testSetLengthTooLarge() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        try {
            raf.setLength(Long.MAX_VALUE);
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testSetLength64() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(0);
        TestCase.assertEquals(0, file.length());
        long moreThanFourGig = ((long) (Integer.MAX_VALUE)) + 1L;
        raf.setLength(moreThanFourGig);
        TestCase.assertEquals(moreThanFourGig, file.length());
    }

    // http://b/3015023
    public void testRandomAccessFileHasCleanupFinalizer() throws Exception {
        // TODO: this always succeeds on the host because our default open file limit is 32Ki.
        // Add Libcore.os.getrlimit and use that instead of hard-coding.
        int tooManyOpenFiles = 2000;
        File file = File.createTempFile("RandomAccessFileTest", "tmp");
        for (int i = 0; i < tooManyOpenFiles; i++) {
            createRandomAccessFile(file);
            FinalizationTester.induceFinalization();
        }
    }

    public void testDirectories() throws Exception {
        try {
            new RandomAccessFile(".", "r");
            TestCase.fail();
        } catch (FileNotFoundException expected) {
        }
        try {
            new RandomAccessFile(".", "rw");
            TestCase.fail();
        } catch (FileNotFoundException expected) {
        }
    }
}

