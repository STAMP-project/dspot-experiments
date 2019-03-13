/**
 * Copyright (C) 2009 The Android Open Source Project
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
package libcore.java.lang;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import libcore.dalvik.system.CloseGuardTester;
import tests.support.Support_Exec;


public class ProcessBuilderTest extends TestCase {
    public void test_redirectErrorStream_true() throws Exception {
        ProcessBuilderTest.assertRedirectErrorStream(true, "out\nerr\n", "");
    }

    public void test_redirectErrorStream_false() throws Exception {
        ProcessBuilderTest.assertRedirectErrorStream(false, "out\n", "err\n");
    }

    public void testEnvironment() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(ProcessBuilderTest.shell(), "-c", "echo $A");
        pb.environment().put("A", "android");
        Support_Exec.execAndCheckOutput(pb, "android\n", "");
    }

    public void testDestroyClosesEverything() throws IOException {
        Process process = new ProcessBuilder(ProcessBuilderTest.shell(), "-c", "echo out; echo err 1>&2").start();
        InputStream in = process.getInputStream();
        InputStream err = process.getErrorStream();
        OutputStream out = process.getOutputStream();
        process.destroy();
        try {
            in.read();
            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            err.read();
            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            /* We test write+flush because the RI returns a wrapped stream, but
            only bothers to close the underlying stream.
             */
            out.write(1);
            out.flush();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testDestroyDoesNotLeak() throws IOException {
        CloseGuardTester closeGuardTester = new CloseGuardTester();
        try {
            Process process = new ProcessBuilder(ProcessBuilderTest.shell(), "-c", "echo out; echo err 1>&2").start();
            process.destroy();
            closeGuardTester.assertEverythingWasClosed();
        } finally {
            closeGuardTester.close();
        }
    }

    public void testEnvironmentMapForbidsNulls() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(ProcessBuilderTest.shell(), "-c", "echo $A");
        Map<String, String> environment = pb.environment();
        Map<String, String> before = new HashMap<String, String>(environment);
        try {
            environment.put("A", null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            environment.put(null, "android");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(before, environment);
    }
}

