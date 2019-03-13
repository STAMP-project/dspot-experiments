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
package org.apache.zeppelin.flink;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.INCOMPLETE;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.TABLE;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.zeppelin.display.ui.CheckBox;
import org.apache.zeppelin.display.ui.Select;
import org.apache.zeppelin.display.ui.TextBox;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResultMessageOutput;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.junit.Assert;
import org.junit.Test;


public class FlinkInterpreterTest {
    private FlinkInterpreter interpreter;

    private InterpreterContext context;

    // catch the streaming output in onAppend
    private volatile String output = "";

    // catch the interpreter output in onUpdate
    private List<InterpreterResultMessageOutput> messageOutput;

    @Test
    public void testBasicScala() throws IOException, InterpreterException {
        InterpreterResult result = interpreter.interpret("val a=\"hello world\"", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals("a: String = hello world\n", output);
        result = interpreter.interpret("print(a)", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals("hello world", output);
        // java stdout
        result = interpreter.interpret("System.out.print(a)", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals("hello world", output);
        // incomplete
        result = interpreter.interpret("println(a", getInterpreterContext());
        Assert.assertEquals(INCOMPLETE, result.code());
        // syntax error
        result = interpreter.interpret("println(b)", getInterpreterContext());
        Assert.assertEquals(ERROR, result.code());
        Assert.assertTrue(output.contains("not found: value b"));
        // multiple line
        result = interpreter.interpret("\"123\".\ntoInt", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // single line comment
        result = interpreter.interpret("/*comment here*/", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        result = interpreter.interpret("/*comment here*/\nprint(\"hello world\")", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // multiple line comment
        result = interpreter.interpret("/*line 1 \n line 2*/", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // test function
        result = interpreter.interpret("def add(x:Int, y:Int)\n{ return x+y }", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        result = interpreter.interpret("print(add(1,2))", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        result = interpreter.interpret("/*line 1 \n line 2*/print(\"hello world\")", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // companion object
        result = interpreter.interpret(("class Counter {\n " + ("var value: Long = 0} \n" + "object Counter {\n def apply(x: Long) = new Counter()\n}")), getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // case class
        result = interpreter.interpret(("case class Bank(age:Integer, job:String, marital : String, education : String," + " balance : Integer)\n"), getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        // ZeppelinContext
        context = getInterpreterContext();
        result = interpreter.interpret("val ds = benv.fromElements(1,2,3)\nz.show(ds)", context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(TABLE, messageOutput.get(0).getType());
        Assert.assertEquals(("f0\n" + (("1\n" + "2\n") + "3\n")), messageOutput.get(0).toInterpreterResultMessage().getData());
        context = getInterpreterContext();
        result = interpreter.interpret("z.input(\"name\", \"default_name\")", context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(1, context.getGui().getForms().size());
        Assert.assertTrue(((context.getGui().getForms().get("name")) instanceof TextBox));
        TextBox textBox = ((TextBox) (context.getGui().getForms().get("name")));
        Assert.assertEquals("name", textBox.getName());
        Assert.assertEquals("default_name", textBox.getDefaultValue());
        context = getInterpreterContext();
        result = interpreter.interpret(("z.checkbox(\"checkbox_1\", " + "Seq(\"value_2\"), Seq((\"value_1\", \"name_1\"), (\"value_2\", \"name_2\")))"), context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(1, context.getGui().getForms().size());
        Assert.assertTrue(((context.getGui().getForms().get("checkbox_1")) instanceof CheckBox));
        CheckBox checkBox = ((CheckBox) (context.getGui().getForms().get("checkbox_1")));
        Assert.assertEquals("checkbox_1", checkBox.getName());
        Assert.assertEquals(1, checkBox.getDefaultValue().length);
        Assert.assertEquals("value_2", checkBox.getDefaultValue()[0]);
        Assert.assertEquals(2, checkBox.getOptions().length);
        Assert.assertEquals("value_1", checkBox.getOptions()[0].getValue());
        Assert.assertEquals("name_1", checkBox.getOptions()[0].getDisplayName());
        Assert.assertEquals("value_2", checkBox.getOptions()[1].getValue());
        Assert.assertEquals("name_2", checkBox.getOptions()[1].getDisplayName());
        context = getInterpreterContext();
        result = interpreter.interpret(("z.select(\"select_1\", Seq(\"value_2\"), " + "Seq((\"value_1\", \"name_1\"), (\"value_2\", \"name_2\")))"), context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(1, context.getGui().getForms().size());
        Assert.assertTrue(((context.getGui().getForms().get("select_1")) instanceof Select));
        Select select = ((Select) (context.getGui().getForms().get("select_1")));
        Assert.assertEquals("select_1", select.getName());
        // TODO(zjffdu) it seems a bug of GUI, the default value should be 'value_2',
        // but it is List(value_2)
        // assertEquals("value_2", select.getDefaultValue());
        Assert.assertEquals(2, select.getOptions().length);
        Assert.assertEquals("value_1", select.getOptions()[0].getValue());
        Assert.assertEquals("name_1", select.getOptions()[0].getDisplayName());
        Assert.assertEquals("value_2", select.getOptions()[1].getValue());
        Assert.assertEquals("name_2", select.getOptions()[1].getDisplayName());
    }

    @Test
    public void testCompletion() throws InterpreterException {
        InterpreterResult result = interpreter.interpret("val a=\"hello world\"", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals("a: String = hello world\n", output);
        List<InterpreterCompletion> completions = interpreter.completion("a.", 2, getInterpreterContext());
        Assert.assertTrue(((completions.size()) > 0));
    }

    // Disable it for now as there's extra std output from flink shell.
    @Test
    public void testWordCount() throws IOException, InterpreterException {
        interpreter.interpret("val text = benv.fromElements(\"To be or not to be\")", getInterpreterContext());
        interpreter.interpret(("val counts = text.flatMap { _.toLowerCase.split(\" \") }" + ".map { (_, 1) }.groupBy(0).sum(1)"), getInterpreterContext());
        InterpreterResult result = interpreter.interpret("counts.print()", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        String[] expectedCounts = new String[]{ "(to,2)", "(be,2)", "(or,1)", "(not,1)" };
        Arrays.sort(expectedCounts);
        String[] counts = output.split("\n");
        Arrays.sort(counts);
        Assert.assertArrayEquals(expectedCounts, counts);
    }
}

