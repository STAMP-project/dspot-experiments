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
package org.apache.zeppelin.scalding;


import Code.ERROR;
import InterpreterResult.Code.INCOMPLETE;
import InterpreterResult.Code.SUCCESS;
import java.io.File;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Tests for the Scalding interpreter for Zeppelin.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScaldingInterpreterTest {
    public static ScaldingInterpreter repl;

    private InterpreterContext context;

    private File tmpDir;

    @Test
    public void testNextLineComments() {
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret("\"123\"\n/*comment here\n*/.toInt", context).code());
    }

    @Test
    public void testNextLineCompanionObject() {
        String code = "class Counter {\nvar value: Long = 0\n}\n // comment\n\n object Counter " + "{\n def apply(x: Long) = new Counter()\n}";
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret(code, context).code());
    }

    @Test
    public void testBasicIntp() {
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret("val a = 1\nval b = 2", context).code());
        // when interpret incomplete expression
        InterpreterResult incomplete = ScaldingInterpreterTest.repl.interpret("val a = \"\"\"", context);
        Assert.assertEquals(INCOMPLETE, incomplete.code());
        Assert.assertTrue(((incomplete.message().get(0).getData().length()) > 0));// expecting some error

        // message
    }

    @Test
    public void testBasicScalding() {
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret(("case class Sale(state: String, name: String, sale: Int)\n" + ((((("val salesList = List(Sale(\"CA\", \"A\", 60), Sale(\"CA\", \"A\", 20), " + "Sale(\"VA\", \"B\", 15))\n") + "val salesPipe = TypedPipe.from(salesList)\n") + "val results = salesPipe.map{x => (1, Set(x.state), x.sale)}.\n") + "    groupAll.sum.values.map{ case(count, set, sum) => (count, set.size, sum) }\n") + "results.dump")), context).code());
    }

    @Test
    public void testNextLineInvocation() {
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret("\"123\"\n.toInt", context).code());
    }

    @Test
    public void testEndWithComment() {
        Assert.assertEquals(SUCCESS, ScaldingInterpreterTest.repl.interpret("val c=1\n//comment", context).code());
    }

    @Test
    public void testReferencingUndefinedVal() {
        InterpreterResult result = ScaldingInterpreterTest.repl.interpret(("def category(min: Int) = {" + ("    if (0 <= value) \"error\"" + "}")), context);
        Assert.assertEquals(ERROR, result.code());
    }
}

