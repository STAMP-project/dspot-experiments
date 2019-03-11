/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.hbase;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.TEXT;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for HBase Interpreter.
 */
public class HbaseInterpreterTest {
    private static Logger logger = LoggerFactory.getLogger(HbaseInterpreterTest.class);

    private static HbaseInterpreter hbaseInterpreter;

    @Test
    public void newObject() {
        MatcherAssert.assertThat(HbaseInterpreterTest.hbaseInterpreter, Matchers.notNullValue());
    }

    @Test
    public void putsTest() {
        InterpreterResult result = HbaseInterpreterTest.hbaseInterpreter.interpret("puts \"Hello World\"", null);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(result.message().get(0).getType(), TEXT);
        Assert.assertEquals("Hello World\n", result.message().get(0).getData());
    }

    @Test
    public void testException() {
        InterpreterResult result = HbaseInterpreterTest.hbaseInterpreter.interpret("plot practical joke", null);
        Assert.assertEquals(ERROR, result.code());
        Assert.assertEquals("(NameError) undefined local variable or method `joke' for main:Object", result.message().get(0).getData());
    }
}

