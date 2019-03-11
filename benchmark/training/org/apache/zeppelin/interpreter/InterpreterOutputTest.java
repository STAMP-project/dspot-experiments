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


import InterpreterResult.Type.HTML;
import InterpreterResult.Type.TABLE;
import InterpreterResult.Type.TEXT;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static Constants.ZEPPELIN_INTERPRETER_OUTPUT_LIMIT;
import static InterpreterOutput.limit;


public class InterpreterOutputTest implements InterpreterOutputListener {
    private InterpreterOutput out;

    int numAppendEvent;

    int numUpdateEvent;

    @Test
    public void testDetectNewline() throws IOException {
        out.write("hello\nworld");
        Assert.assertEquals(1, out.size());
        Assert.assertEquals(TEXT, out.getOutputAt(0).getType());
        Assert.assertEquals("hello\n", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(1, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
        out.write("\n");
        Assert.assertEquals("hello\nworld\n", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(2, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
    }

    @Test
    public void testFlush() throws IOException {
        out.write("hello\nworld");
        Assert.assertEquals("hello\n", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(1, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
        out.flush();
        Assert.assertEquals("hello\nworld", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(2, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
        out.clear();
        out.write("%html div");
        Assert.assertEquals("", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(HTML, out.getOutputAt(0).getType());
        out.flush();
        Assert.assertEquals("div", new String(out.getOutputAt(0).toByteArray()));
    }

    @Test
    public void testType() throws IOException {
        // default output stream type is TEXT
        out.write("Text\n");
        Assert.assertEquals(TEXT, out.getOutputAt(0).getType());
        Assert.assertEquals("Text\n", new String(out.getOutputAt(0).toByteArray()));
        Assert.assertEquals(1, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
        // change type
        out.write("%html\n");
        Assert.assertEquals(HTML, out.getOutputAt(1).getType());
        Assert.assertEquals("", new String(out.getOutputAt(1).toByteArray()));
        Assert.assertEquals(1, numAppendEvent);
        Assert.assertEquals(1, numUpdateEvent);
        // none TEXT type output stream does not generate append event
        out.write("<div>html</div>\n");
        Assert.assertEquals(HTML, out.getOutputAt(1).getType());
        Assert.assertEquals(1, numAppendEvent);
        Assert.assertEquals(2, numUpdateEvent);
        out.flush();
        Assert.assertEquals("<div>html</div>\n", new String(out.getOutputAt(1).toByteArray()));
        // change type to text again
        out.write("%text hello\n");
        Assert.assertEquals(TEXT, out.getOutputAt(2).getType());
        Assert.assertEquals(2, numAppendEvent);
        Assert.assertEquals(4, numUpdateEvent);
        Assert.assertEquals("hello\n", new String(out.getOutputAt(2).toByteArray()));
    }

    @Test
    public void testChangeTypeInTheBeginning() throws IOException {
        out.write("%html\nHello");
        Assert.assertEquals(HTML, out.getOutputAt(0).getType());
    }

    @Test
    public void testChangeTypeWithMultipleNewline() throws IOException {
        out.write("%html\n");
        Assert.assertEquals(HTML, out.getOutputAt(0).getType());
        out.write("%text\n");
        Assert.assertEquals(TEXT, out.getOutputAt(1).getType());
        out.write("\n%html\n");
        Assert.assertEquals(HTML, out.getOutputAt(2).getType());
        out.write("\n\n%text\n");
        Assert.assertEquals(TEXT, out.getOutputAt(3).getType());
        out.write("\n\n\n%html\n");
        Assert.assertEquals(HTML, out.getOutputAt(4).getType());
    }

    @Test
    public void testChangeTypeWithoutData() throws IOException {
        out.write("%html\n%table\n");
        Assert.assertEquals(HTML, out.getOutputAt(0).getType());
        Assert.assertEquals(TABLE, out.getOutputAt(1).getType());
    }

    @Test
    public void testMagicData() throws IOException {
        out.write("%table col1\tcol2\n\n%html <h3> This is a hack </h3>\t234\n".getBytes());
        Assert.assertEquals(TABLE, out.getOutputAt(0).getType());
        Assert.assertEquals(HTML, out.getOutputAt(1).getType());
        Assert.assertEquals("col1\tcol2\n", new String(out.getOutputAt(0).toByteArray()));
        out.flush();
        Assert.assertEquals("<h3> This is a hack </h3>\t234\n", new String(out.getOutputAt(1).toByteArray()));
    }

    @Test
    public void testTableCellFormatting() throws IOException {
        out.write("%table col1\tcol2\n\n%html val1\tval2\n".getBytes());
        Assert.assertEquals(TABLE, out.getOutputAt(0).getType());
        Assert.assertEquals("col1\tcol2\n", new String(out.getOutputAt(0).toByteArray()));
        out.flush();
        Assert.assertEquals("val1\tval2\n", new String(out.getOutputAt(1).toByteArray()));
    }

    @Test
    public void testTruncate() throws IOException {
        // output is truncated after the new line
        limit = 3;
        out = new InterpreterOutput(this);
        // truncate text
        out.write("%text hello\nworld\n");
        Assert.assertEquals("hello", new String(out.getOutputAt(0).toByteArray()));
        out.getOutputAt(1).flush();
        Assert.assertTrue(new String(out.getOutputAt(1).toByteArray()).contains("truncated"));
        // truncate table
        out = new InterpreterOutput(this);
        out.write("%table key\tvalue\nhello\t100\nworld\t200\n");
        Assert.assertEquals("key\tvalue", new String(out.getOutputAt(0).toByteArray()));
        out.getOutputAt(1).flush();
        Assert.assertTrue(new String(out.getOutputAt(1).toByteArray()).contains("truncated"));
        // does not truncate html
        out = new InterpreterOutput(this);
        out.write("%html hello\nworld\n");
        out.flush();
        Assert.assertEquals("hello\nworld\n", new String(out.getOutputAt(0).toByteArray()));
        // restore default
        limit = ZEPPELIN_INTERPRETER_OUTPUT_LIMIT;
    }
}

