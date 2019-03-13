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
import InterpreterResult.Type.HTML;
import InterpreterResult.Type.TABLE;
import InterpreterResult.Type.TEXT;
import org.junit.Assert;
import org.junit.Test;


public class InterpreterResultTest {
    @Test
    public void testTextType() {
        InterpreterResult result = new InterpreterResult(Code.SUCCESS, "this is a TEXT type");
        Assert.assertEquals("No magic", TEXT, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "%this is a TEXT type");
        Assert.assertEquals("No magic", TEXT, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "%\n");
        Assert.assertEquals("No magic", TEXT, result.message().get(0).getType());
    }

    @Test
    public void testSimpleMagicType() {
        InterpreterResult result = null;
        result = new InterpreterResult(Code.SUCCESS, "%table col1\tcol2\naaa\t123\n");
        Assert.assertEquals(TABLE, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "%table\ncol1\tcol2\naaa\t123\n");
        Assert.assertEquals(TABLE, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "some text before magic word\n%table col1\tcol2\naaa\t123\n");
        Assert.assertEquals(TABLE, result.message().get(1).getType());
    }

    @Test
    public void testComplexMagicType() {
        InterpreterResult result = null;
        result = new InterpreterResult(Code.SUCCESS, "some text before %table col1\tcol2\naaa\t123\n");
        Assert.assertEquals("some text before magic return magic", TEXT, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "some text before\n%table col1\tcol2\naaa\t123\n");
        Assert.assertEquals("some text before magic return magic", TEXT, result.message().get(0).getType());
        Assert.assertEquals("some text before magic return magic", TABLE, result.message().get(1).getType());
        result = new InterpreterResult(Code.SUCCESS, "%html  <h3> This is a hack </h3> %table\n col1\tcol2\naaa\t123\n");
        Assert.assertEquals("magic A before magic B return magic A", HTML, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, ("some text before magic word %table col1\tcol2\naaa\t123\n %html  " + "<h3> This is a hack </h3>"));
        Assert.assertEquals("text & magic A before magic B return magic A", TEXT, result.message().get(0).getType());
        result = new InterpreterResult(Code.SUCCESS, "%table col1\tcol2\naaa\t123\n %html  <h3> This is a hack </h3> %table col1\naaa\n123\n");
        Assert.assertEquals("magic A, magic B, magic a' return magic A", TABLE, result.message().get(0).getType());
    }

    @Test
    public void testSimpleMagicData() {
        InterpreterResult result = null;
        result = new InterpreterResult(Code.SUCCESS, "%table col1\tcol2\naaa\t123\n");
        Assert.assertEquals("%table col1\tcol2\naaa\t123\n", "col1\tcol2\naaa\t123\n", result.message().get(0).getData());
        result = new InterpreterResult(Code.SUCCESS, "%table\ncol1\tcol2\naaa\t123\n");
        Assert.assertEquals("%table\ncol1\tcol2\naaa\t123\n", "col1\tcol2\naaa\t123\n", result.message().get(0).getData());
        result = new InterpreterResult(Code.SUCCESS, "some text before magic word\n%table col1\tcol2\naaa\t123\n");
        Assert.assertEquals("some text before magic word\n%table col1\tcol2\naaa\t123\n", "col1\tcol2\naaa\t123\n", result.message().get(1).getData());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("%html hello", new InterpreterResult(Code.SUCCESS, "%html hello").toString());
    }
}

