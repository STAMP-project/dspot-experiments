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
package org.apache.zeppelin.file;


import CompletionType.command;
import InterpreterResult.Type.TEXT;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.junit.Test;


/**
 * Tests Interpreter by running pre-determined commands against mock file system.
 */
public class HDFSFileInterpreterTest extends TestCase {
    @Test
    public void testMaxLength() {
        HDFSFileInterpreter t = new MockHDFSFileInterpreter(new Properties());
        t.open();
        InterpreterResult result = t.interpret("ls -l /", null);
        String lineSeparator = "\n";
        int fileStatusLength = MockFileSystem.FILE_STATUSES.split(lineSeparator).length;
        TestCase.assertEquals(result.message().get(0).getData().split(lineSeparator).length, fileStatusLength);
        t.close();
        Properties properties = new Properties();
        final int maxLength = fileStatusLength - 2;
        properties.setProperty("hdfs.maxlength", String.valueOf(maxLength));
        HDFSFileInterpreter t1 = new MockHDFSFileInterpreter(properties);
        t1.open();
        InterpreterResult result1 = t1.interpret("ls -l /", null);
        TestCase.assertEquals(result1.message().get(0).getData().split(lineSeparator).length, maxLength);
        t1.close();
    }

    @Test
    public void test() {
        HDFSFileInterpreter t = new MockHDFSFileInterpreter(new Properties());
        t.open();
        // We have info for /, /user, /tmp, /mr-history/done
        // Ensure
        // 1. ls -l works
        // 2. paths (. and ..) are correctly handled
        // 3. flags and arguments to commands are correctly handled
        InterpreterResult result1 = t.interpret("ls -l /", null);
        TestCase.assertEquals(result1.message().get(0).getType(), TEXT);
        InterpreterResult result2 = t.interpret("ls -l /./user/..", null);
        TestCase.assertEquals(result2.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result1.message().get(0).getData(), result2.message().get(0).getData());
        // Ensure you can do cd and after that the ls uses current directory correctly
        InterpreterResult result3 = t.interpret("cd user", null);
        TestCase.assertEquals(result3.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result3.message().get(0).getData(), "OK");
        InterpreterResult result4 = t.interpret("ls", null);
        TestCase.assertEquals(result4.message().get(0).getType(), TEXT);
        InterpreterResult result5 = t.interpret("ls /user", null);
        TestCase.assertEquals(result5.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result4.message().get(0).getData(), result5.message().get(0).getData());
        // Ensure pwd works correctly
        InterpreterResult result6 = t.interpret("pwd", null);
        TestCase.assertEquals(result6.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result6.message().get(0).getData(), "/user");
        // Move a couple of levels and check we're in the right place
        InterpreterResult result7 = t.interpret("cd ../mr-history/done", null);
        TestCase.assertEquals(result7.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result7.message().get(0).getData(), "OK");
        InterpreterResult result8 = t.interpret("ls -l ", null);
        TestCase.assertEquals(result8.message().get(0).getType(), TEXT);
        InterpreterResult result9 = t.interpret("ls -l /mr-history/done", null);
        TestCase.assertEquals(result9.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result8.message().get(0).getData(), result9.message().get(0).getData());
        InterpreterResult result10 = t.interpret("cd ../..", null);
        TestCase.assertEquals(result10.message().get(0).getType(), TEXT);
        TestCase.assertEquals(result7.message().get(0).getData(), "OK");
        InterpreterResult result11 = t.interpret("ls -l ", null);
        TestCase.assertEquals(result11.message().get(0).getType(), TEXT);
        // we should be back to first result after all this navigation
        TestCase.assertEquals(result1.message().get(0).getData(), result11.message().get(0).getData());
        // auto completion test
        List expectedResultOne = Arrays.asList(new InterpreterCompletion("ls", "ls", command.name()));
        List expectedResultTwo = Arrays.asList(new InterpreterCompletion("pwd", "pwd", command.name()));
        List<InterpreterCompletion> resultOne = t.completion("l", 0, null);
        List<InterpreterCompletion> resultTwo = t.completion("p", 0, null);
        TestCase.assertEquals(expectedResultOne, resultOne);
        TestCase.assertEquals(expectedResultTwo, resultTwo);
        t.close();
    }
}

