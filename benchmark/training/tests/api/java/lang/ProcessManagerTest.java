/**
 * Copyright (C) 2007 The Android Open Source Project
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
package tests.api.java.lang;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;


public class ProcessManagerTest extends TestCase {
    Thread thread = null;

    Process process = null;

    boolean isThrown = false;

    public void testCat() throws IOException, InterruptedException {
        String[] commands = new String[]{ "cat" };
        Process process = Runtime.getRuntime().exec(commands, null, null);
        OutputStream out = process.getOutputStream();
        String greeting = "Hello, World!";
        out.write(greeting.getBytes());
        out.write('\n');
        out.close();
        TestCase.assertEquals(greeting, readLine(process));
    }

    public void testPwd() throws IOException, InterruptedException {
        String[] commands = new String[]{ "sh", "-c", "pwd" };
        Process process = Runtime.getRuntime().exec(commands, null, new File("/"));
        logErrors(process);
        TestCase.assertEquals("/", readLine(process));
    }

    public void testEnvironment() throws IOException, InterruptedException {
        String[] commands = new String[]{ "sh", "-c", "echo $FOO" };
        // Remember to set the path so we can find sh.
        String[] environment = new String[]{ "FOO=foo", "PATH=" + (System.getenv("PATH")) };
        Process process = Runtime.getRuntime().exec(commands, environment, null);
        logErrors(process);
        TestCase.assertEquals("foo", readLine(process));
    }

    public void testHeavyLoad() {
        int i;
        for (i = 0; i < 100; i++)
            ProcessManagerTest.stuff();

    }

    InputStream in;

    public void testCloseNonStandardFds() throws IOException, InterruptedException {
        // RoboVM note: This test is Linux specific
        if ((System.getProperty("os.name").contains("iOS")) || (System.getProperty("os.name").contains("Mac"))) {
            return;
        }
        String[] commands = new String[]{ "ls", "/proc/self/fd" };
        Process process = Runtime.getRuntime().exec(commands, null, null);
        int before = countLines(process);
        // Open a new fd.
        this.in = new FileInputStream("/proc/version");
        try {
            process = Runtime.getRuntime().exec(commands, null, null);
            int after = countLines(process);
            // Assert that the new fd wasn't open in the second run.
            TestCase.assertEquals(before, after);
        } finally {
            this.in = null;
        }
    }

    public void testInvalidCommand() throws IOException, InterruptedException {
        try {
            String[] commands = new String[]{ "doesnotexist" };
            Runtime.getRuntime().exec(commands, null, null);
        } catch (IOException e) {
            /* expected */
        }
    }
}

