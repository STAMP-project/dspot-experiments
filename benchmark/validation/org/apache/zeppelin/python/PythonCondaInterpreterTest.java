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
package org.apache.zeppelin.python;


import InterpreterResult.Code.SUCCESS;
import PythonCondaInterpreter.PATTERN_COMMAND_ENV;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PythonCondaInterpreterTest {
    private PythonCondaInterpreter conda;

    private PythonInterpreter python;

    @Test
    public void testListEnv() throws IOException, InterruptedException, InterpreterException {
        setMockCondaEnvList();
        // list available env
        InterpreterContext context = getInterpreterContext();
        InterpreterResult result = conda.interpret("env list", context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertTrue(result.toString().contains(">env1<"));
        Assert.assertTrue(result.toString().contains("/path1<"));
        Assert.assertTrue(result.toString().contains(">env2<"));
        Assert.assertTrue(result.toString().contains("/path2<"));
    }

    @Test
    public void testActivateEnv() throws IOException, InterruptedException, InterpreterException {
        setMockCondaEnvList();
        String envname = "env1";
        InterpreterContext context = getInterpreterContext();
        conda.interpret(("activate " + envname), context);
        Mockito.verify(python, Mockito.times(1)).open();
        Mockito.verify(python, Mockito.times(1)).close();
        Mockito.verify(python).setPythonExec("/path1/bin/python");
        Assert.assertTrue(envname.equals(conda.getCurrentCondaEnvName()));
    }

    @Test
    public void testDeactivate() throws InterpreterException {
        InterpreterContext context = getInterpreterContext();
        conda.interpret("deactivate", context);
        Mockito.verify(python, Mockito.times(1)).open();
        Mockito.verify(python, Mockito.times(1)).close();
        Mockito.verify(python).setPythonExec("python");
        Assert.assertTrue(conda.getCurrentCondaEnvName().isEmpty());
    }

    @Test
    public void testParseCondaCommonStdout() throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder().append("# comment1\n").append("# comment2\n").append("env1     /location1\n").append("env2     /location2\n");
        Map<String, String> locationPerEnv = PythonCondaInterpreter.parseCondaCommonStdout(sb.toString());
        Assert.assertEquals("/location1", locationPerEnv.get("env1"));
        Assert.assertEquals("/location2", locationPerEnv.get("env2"));
    }

    @Test
    public void testGetRestArgsFromMatcher() {
        Matcher m = PATTERN_COMMAND_ENV.matcher("env remove --name test --yes");
        m.matches();
        List<String> restArgs = PythonCondaInterpreter.getRestArgsFromMatcher(m);
        List<String> expected = Arrays.asList(new String[]{ "remove", "--name", "test", "--yes" });
        Assert.assertEquals(expected, restArgs);
    }
}

