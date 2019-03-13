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


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.nativeio.NativeIO;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestSecureIOUtils {
    private static String realOwner;

    private static String realGroup;

    private static File testFilePathIs;

    private static File testFilePathRaf;

    private static File testFilePathFadis;

    private static FileSystem fs;

    @Test(timeout = 10000)
    public void testReadUnrestricted() throws IOException {
        SecureIOUtils.openForRead(TestSecureIOUtils.testFilePathIs, null, null).close();
        SecureIOUtils.openFSDataInputStream(TestSecureIOUtils.testFilePathFadis, null, null).close();
        SecureIOUtils.openForRandomRead(TestSecureIOUtils.testFilePathRaf, "r", null, null).close();
    }

    @Test(timeout = 10000)
    public void testReadCorrectlyRestrictedWithSecurity() throws IOException {
        SecureIOUtils.openForRead(TestSecureIOUtils.testFilePathIs, TestSecureIOUtils.realOwner, TestSecureIOUtils.realGroup).close();
        SecureIOUtils.openFSDataInputStream(TestSecureIOUtils.testFilePathFadis, TestSecureIOUtils.realOwner, TestSecureIOUtils.realGroup).close();
        SecureIOUtils.openForRandomRead(TestSecureIOUtils.testFilePathRaf, "r", TestSecureIOUtils.realOwner, TestSecureIOUtils.realGroup).close();
    }

    @Test(timeout = 10000)
    public void testReadIncorrectlyRestrictedWithSecurity() throws IOException {
        // this will only run if libs are available
        Assume.assumeTrue(NativeIO.isAvailable());
        System.out.println("Running test with native libs...");
        String invalidUser = "InvalidUser";
        // We need to make sure that forceSecure.. call works only if
        // the file belongs to expectedOwner.
        // InputStream
        try {
            SecureIOUtils.forceSecureOpenForRead(TestSecureIOUtils.testFilePathIs, invalidUser, TestSecureIOUtils.realGroup).close();
            Assert.fail("Didn't throw expection for wrong user ownership!");
        } catch (IOException ioe) {
            // expected
        }
        // FSDataInputStream
        try {
            SecureIOUtils.forceSecureOpenFSDataInputStream(TestSecureIOUtils.testFilePathFadis, invalidUser, TestSecureIOUtils.realGroup).close();
            Assert.fail("Didn't throw expection for wrong user ownership!");
        } catch (IOException ioe) {
            // expected
        }
        // RandomAccessFile
        try {
            SecureIOUtils.forceSecureOpenForRandomRead(TestSecureIOUtils.testFilePathRaf, "r", invalidUser, TestSecureIOUtils.realGroup).close();
            Assert.fail("Didn't throw expection for wrong user ownership!");
        } catch (IOException ioe) {
            // expected
        }
    }

    @Test(timeout = 10000)
    public void testCreateForWrite() throws IOException {
        try {
            SecureIOUtils.createForWrite(TestSecureIOUtils.testFilePathIs, 511);
            Assert.fail(("Was able to create file at " + (TestSecureIOUtils.testFilePathIs)));
        } catch (SecureIOUtils aee) {
            // expected
        }
    }
}

