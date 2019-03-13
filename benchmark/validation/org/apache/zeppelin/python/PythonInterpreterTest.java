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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


public class PythonInterpreterTest extends BasePythonInterpreterTest {
    private class infinityPythonJob implements Runnable {
        @Override
        public void run() {
            String code = "import time\nwhile True:\n  time.sleep(1)";
            InterpreterResult ret = null;
            try {
                ret = interpreter.interpret(code, getInterpreterContext());
            } catch (InterpreterException e) {
                e.printStackTrace();
            }
            Assert.assertNotNull(ret);
            Pattern expectedMessage = Pattern.compile("KeyboardInterrupt");
            Matcher m = expectedMessage.matcher(ret.message().toString());
            Assert.assertTrue(m.find());
        }
    }

    @Test
    public void testCancelIntp() throws InterruptedException, InterpreterException {
        Assert.assertEquals(SUCCESS, interpreter.interpret("a = 1\n", getInterpreterContext()).code());
        Thread t = new Thread(new PythonInterpreterTest.infinityPythonJob());
        t.start();
        Thread.sleep(5000);
        interpreter.cancel(getInterpreterContext());
        Assert.assertTrue(t.isAlive());
        t.join(2000);
        Assert.assertFalse(t.isAlive());
    }
}

