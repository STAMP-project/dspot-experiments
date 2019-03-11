/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.lang;


import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;


public class Process2Test extends TestCase {
    public void test_getErrorStream() {
        String[] commands = new String[]{ "ls" };
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commands, null, null);
            InputStream is = process.getErrorStream();
            StringBuffer msg = new StringBuffer("");
            while (true) {
                int c = is.read();
                if (c == (-1))
                    break;

                msg.append(((char) (c)));
            } 
            TestCase.assertEquals("", msg.toString());
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        } finally {
            process.destroy();
        }
        String[] unknownCommands = new String[]{ "mkdir", "-u", "test" };
        Process erProcess = null;
        try {
            erProcess = Runtime.getRuntime().exec(unknownCommands, null, null);
            InputStream is = erProcess.getErrorStream();
            StringBuffer msg = new StringBuffer("");
            while (true) {
                int c = is.read();
                if (c == (-1))
                    break;

                msg.append(((char) (c)));
            } 
            TestCase.assertTrue("Error stream should not be empty", (!("".equals(msg.toString()))));
        } catch (IOException e) {
            TestCase.fail("IOException was thrown.");
        } finally {
            erProcess.destroy();
        }
    }
}

