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
package org.apache.zeppelin.interpreter.remote;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.ANGULAR;
import InterpreterResult.Type.HTML;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for remote interpreter output stream
 */
public class RemoteInterpreterOutputTestStream extends AbstractInterpreterTest implements RemoteInterpreterProcessListener {
    private InterpreterSetting interpreterSetting;

    @Test
    public void testInterpreterResultOnly() throws InterpreterException {
        RemoteInterpreter intp = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock_stream")));
        InterpreterResult ret = intp.interpret("SUCCESS::staticresult", createInterpreterContext());
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals("staticresult", ret.message().get(0).getData());
        ret = intp.interpret("SUCCESS::staticresult2", createInterpreterContext());
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals("staticresult2", ret.message().get(0).getData());
        ret = intp.interpret("ERROR::staticresult3", createInterpreterContext());
        Assert.assertEquals(ERROR, ret.code());
        Assert.assertEquals("staticresult3", ret.message().get(0).getData());
    }

    @Test
    public void testInterpreterOutputStreamOnly() throws InterpreterException {
        RemoteInterpreter intp = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock_stream")));
        InterpreterResult ret = intp.interpret("SUCCESS:streamresult:", createInterpreterContext());
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals("streamresult", ret.message().get(0).getData());
        ret = intp.interpret("ERROR:streamresult2:", createInterpreterContext());
        Assert.assertEquals(ERROR, ret.code());
        Assert.assertEquals("streamresult2", ret.message().get(0).getData());
    }

    @Test
    public void testInterpreterResultOutputStreamMixed() throws InterpreterException {
        RemoteInterpreter intp = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock_stream")));
        InterpreterResult ret = intp.interpret("SUCCESS:stream:static", createInterpreterContext());
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals("stream", ret.message().get(0).getData());
        Assert.assertEquals("static", ret.message().get(1).getData());
    }

    @Test
    public void testOutputType() throws InterpreterException {
        RemoteInterpreter intp = ((RemoteInterpreter) (interpreterSetting.getInterpreter("user1", "note1", "mock_stream")));
        InterpreterResult ret = intp.interpret("SUCCESS:%html hello:", createInterpreterContext());
        Assert.assertEquals(HTML, ret.message().get(0).getType());
        Assert.assertEquals("hello", ret.message().get(0).getData());
        ret = intp.interpret("SUCCESS:%html\nhello:", createInterpreterContext());
        Assert.assertEquals(HTML, ret.message().get(0).getType());
        Assert.assertEquals("hello", ret.message().get(0).getData());
        ret = intp.interpret("SUCCESS:%html hello:%angular world", createInterpreterContext());
        Assert.assertEquals(HTML, ret.message().get(0).getType());
        Assert.assertEquals("hello", ret.message().get(0).getData());
        Assert.assertEquals(ANGULAR, ret.message().get(1).getType());
        Assert.assertEquals("world", ret.message().get(1).getData());
    }
}

