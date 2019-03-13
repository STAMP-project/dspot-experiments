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
package org.apache.zeppelin.scio;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.INCOMPLETE;
import InterpreterResult.Code.SUCCESS;
import org.junit.Assert;
import org.junit.Test;


public class ScioInterpreterTest {
    private static ScioInterpreter repl;

    private static InterpreterGroup intpGroup;

    private InterpreterContext context;

    private final String newline = "\n";

    @Test
    public void testBasicSuccess() {
        Assert.assertEquals(SUCCESS, ScioInterpreterTest.repl.interpret((("val a = 1" + (newline)) + "val b = 2"), context).code());
    }

    @Test
    public void testBasicSyntaxError() {
        InterpreterResult error = ScioInterpreterTest.repl.interpret("val a:Int = 'ds'", context);
        Assert.assertEquals(ERROR, error.code());
        Assert.assertEquals("Interpreter error", error.message().get(0).getData());
    }

    @Test
    public void testBasicIncomplete() {
        InterpreterResult incomplete = ScioInterpreterTest.repl.interpret("val a = \"\"\"", context);
        Assert.assertEquals(INCOMPLETE, incomplete.code());
        Assert.assertEquals("Incomplete expression", incomplete.message().get(0).getData());
    }

    @Test
    public void testBasicPipeline() {
        Assert.assertEquals(SUCCESS, ScioInterpreterTest.repl.interpret((("val (sc, _) = ContextAndArgs(argz)" + (newline)) + "sc.parallelize(1 to 10).closeAndCollect().toList"), context).code());
    }

    @Test
    public void testBasicMultiStepPipeline() {
        final StringBuilder code = new StringBuilder();
        code.append("val (sc, _) = ContextAndArgs(argz)").append(newline).append("val numbers = sc.parallelize(1 to 10)").append(newline).append("val results = numbers.closeAndCollect().toList").append(newline).append("println(results)");
        Assert.assertEquals(SUCCESS, ScioInterpreterTest.repl.interpret(code.toString(), context).code());
    }

    @Test
    public void testException() {
        InterpreterResult exception = ScioInterpreterTest.repl.interpret((("val (sc, _) = ContextAndArgs(argz)" + (newline)) + "throw new Exception(\"test\")"), context);
        Assert.assertEquals(ERROR, exception.code());
        Assert.assertTrue(((exception.message().get(0).getData().length()) > 0));
    }
}

