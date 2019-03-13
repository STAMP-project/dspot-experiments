/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.shell;


import Code.INCOMPLETE;
import InterpreterResult.Code.SUCCESS;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


public class ShellInterpreterTest {
    private ShellInterpreter shell;

    private InterpreterContext context;

    private InterpreterResult result;

    @Test
    public void test() throws InterpreterException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            result = shell.interpret("dir", context);
        } else {
            result = shell.interpret("ls", context);
        }
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertTrue(shell.executors.isEmpty());
        // it should be fine to cancel a statement that has been completed.
        shell.cancel(context);
        Assert.assertTrue(shell.executors.isEmpty());
    }

    @Test
    public void testInvalidCommand() throws InterpreterException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            result = shell.interpret("invalid_command\ndir", context);
        } else {
            result = shell.interpret("invalid_command\nls", context);
        }
        Assert.assertEquals(Code.SUCCESS, result.code());
        Assert.assertTrue(shell.executors.isEmpty());
    }

    @Test
    public void testShellTimeout() throws InterpreterException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            result = shell.interpret("timeout 4", context);
        } else {
            result = shell.interpret("sleep 4", context);
        }
        Assert.assertEquals(INCOMPLETE, result.code());
        Assert.assertTrue(result.message().get(0).getData().contains("Paragraph received a SIGTERM"));
    }
}

