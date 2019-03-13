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
package org.apache.zeppelin.ignite;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import org.apache.ignite.Ignite;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Apache Ignite interpreter ({@link IgniteInterpreter}).
 */
public class IgniteInterpreterTest {
    private static final String HOST = "127.0.0.1:47500..47509";

    private static final InterpreterContext INTP_CONTEXT = InterpreterContext.builder().build();

    private IgniteInterpreter intp;

    private Ignite ignite;

    @Test
    public void testInterpret() {
        String sizeVal = "size";
        InterpreterResult result = intp.interpret(((("import org.apache.ignite.IgniteCache\n" + "val ") + sizeVal) + " = ignite.cluster().nodes().size()"), IgniteInterpreterTest.INTP_CONTEXT);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertTrue(result.message().get(0).getData().contains(((sizeVal + ": Int = ") + (ignite.cluster().nodes().size()))));
        result = intp.interpret("\"123\"\n  .toInt", IgniteInterpreterTest.INTP_CONTEXT);
        Assert.assertEquals(SUCCESS, result.code());
    }

    @Test
    public void testInterpretInvalidInput() {
        InterpreterResult result = intp.interpret("invalid input", IgniteInterpreterTest.INTP_CONTEXT);
        Assert.assertEquals(ERROR, result.code());
    }
}

