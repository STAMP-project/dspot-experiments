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
package org.apache.zeppelin.interpreter;


import InterpreterResult.Code;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LazyOpenInterpreterTest {
    Interpreter interpreter = Mockito.mock(Interpreter.class);

    @Test
    public void isOpenTest() throws InterpreterException {
        InterpreterResult interpreterResult = new InterpreterResult(Code.SUCCESS, "");
        Mockito.when(interpreter.interpret(ArgumentMatchers.any(String.class), ArgumentMatchers.any(InterpreterContext.class))).thenReturn(interpreterResult);
        LazyOpenInterpreter lazyOpenInterpreter = new LazyOpenInterpreter(interpreter);
        Assert.assertFalse("Interpreter is not open", lazyOpenInterpreter.isOpen());
        InterpreterContext interpreterContext = Mockito.mock(InterpreterContext.class);
        lazyOpenInterpreter.interpret("intp 1", interpreterContext);
        Assert.assertTrue("Interpeter is open", lazyOpenInterpreter.isOpen());
    }
}

