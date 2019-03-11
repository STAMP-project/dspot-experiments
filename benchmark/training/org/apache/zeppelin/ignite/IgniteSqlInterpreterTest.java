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


import Code.ERROR;
import Code.SUCCESS;
import Type.TABLE;
import org.apache.ignite.Ignite;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Apache Ignite SQL interpreter ({@link IgniteSqlInterpreter}).
 */
public class IgniteSqlInterpreterTest {
    private static final String HOST = "127.0.0.1:47500..47509";

    private static final InterpreterContext INTP_CONTEXT = InterpreterContext.builder().build();

    private Ignite ignite;

    private IgniteSqlInterpreter intp;

    @Test
    public void testSql() {
        InterpreterResult result = intp.interpret("select name, age from person where age > 10", IgniteSqlInterpreterTest.INTP_CONTEXT);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(TABLE, result.message().get(0).getType());
        Assert.assertEquals("NAME\tAGE\nsun\t100\nmoon\t50\n", result.message().get(0).getData());
    }

    @Test
    public void testInvalidSql() throws Exception {
        InterpreterResult result = intp.interpret("select * hrom person", IgniteSqlInterpreterTest.INTP_CONTEXT);
        Assert.assertEquals(ERROR, result.code());
    }
}

