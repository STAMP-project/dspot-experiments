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
package org.apache.zeppelin.spark;


import Code.ERROR;
import InterpreterResult.Code.INCOMPLETE;
import InterpreterResult.Code.SUCCESS;
import WellKnownResourceName.ZeppelinReplResult;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.spark.SparkConf;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OldSparkInterpreterTest {
    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    static SparkInterpreter repl;

    static InterpreterGroup intpGroup;

    static InterpreterContext context;

    static Logger LOGGER = LoggerFactory.getLogger(OldSparkInterpreterTest.class);

    @Test
    public void testBasicIntp() throws InterpreterException {
        Assert.assertEquals(SUCCESS, OldSparkInterpreterTest.repl.interpret("val a = 1\nval b = 2", OldSparkInterpreterTest.context).code());
        // when interpret incomplete expression
        InterpreterResult incomplete = OldSparkInterpreterTest.repl.interpret("val a = \"\"\"", OldSparkInterpreterTest.context);
        Assert.assertEquals(INCOMPLETE, incomplete.code());
        Assert.assertTrue(((incomplete.message().get(0).getData().length()) > 0));// expecting some error

        // message
        /* assertEquals(1, repl.getValue("a")); assertEquals(2, repl.getValue("b"));
        repl.interpret("val ver = sc.version");
        assertNotNull(repl.getValue("ver")); assertEquals("HELLO\n",
        repl.interpret("println(\"HELLO\")").message());
         */
    }

    @Test
    public void testNonStandardSparkProperties() throws IOException, InterpreterException {
        // throw NoSuchElementException if no such property is found
        InterpreterResult result = OldSparkInterpreterTest.repl.interpret("sc.getConf.get(\"property_1\")", OldSparkInterpreterTest.context);
        Assert.assertEquals(SUCCESS, result.code());
    }

    @Test
    public void testNextLineInvocation() throws InterpreterException {
        Assert.assertEquals(SUCCESS, OldSparkInterpreterTest.repl.interpret("\"123\"\n.toInt", OldSparkInterpreterTest.context).code());
    }

    @Test
    public void testNextLineComments() throws InterpreterException {
        Assert.assertEquals(SUCCESS, OldSparkInterpreterTest.repl.interpret("\"123\"\n/*comment here\n*/.toInt", OldSparkInterpreterTest.context).code());
    }

    @Test
    public void testNextLineCompanionObject() throws InterpreterException {
        String code = "class Counter {\nvar value: Long = 0\n}\n // comment\n\n object Counter {\n def apply(x: Long) = new Counter()\n}";
        Assert.assertEquals(SUCCESS, OldSparkInterpreterTest.repl.interpret(code, OldSparkInterpreterTest.context).code());
    }

    @Test
    public void testEndWithComment() throws InterpreterException {
        Assert.assertEquals(SUCCESS, OldSparkInterpreterTest.repl.interpret("val c=1\n//comment", OldSparkInterpreterTest.context).code());
    }

    @Test
    public void testCreateDataFrame() throws InterpreterException {
        if ((OldSparkInterpreterTest.getSparkVersionNumber(OldSparkInterpreterTest.repl)) >= 13) {
            OldSparkInterpreterTest.repl.interpret("case class Person(name:String, age:Int)\n", OldSparkInterpreterTest.context);
            OldSparkInterpreterTest.repl.interpret("val people = sc.parallelize(Seq(Person(\"moon\", 33), Person(\"jobs\", 51), Person(\"gates\", 51), Person(\"park\", 34)))\n", OldSparkInterpreterTest.context);
            OldSparkInterpreterTest.repl.interpret("people.toDF.count", OldSparkInterpreterTest.context);
            Assert.assertEquals(new Long(4), OldSparkInterpreterTest.context.getResourcePool().get(OldSparkInterpreterTest.context.getNoteId(), OldSparkInterpreterTest.context.getParagraphId(), ZeppelinReplResult.toString()).get());
        }
    }

    @Test
    public void testZShow() throws InterpreterException {
        String code = "";
        OldSparkInterpreterTest.repl.interpret("case class Person(name:String, age:Int)\n", OldSparkInterpreterTest.context);
        OldSparkInterpreterTest.repl.interpret("val people = sc.parallelize(Seq(Person(\"moon\", 33), Person(\"jobs\", 51), Person(\"gates\", 51), Person(\"park\", 34)))\n", OldSparkInterpreterTest.context);
        if ((OldSparkInterpreterTest.getSparkVersionNumber(OldSparkInterpreterTest.repl)) < 13) {
            OldSparkInterpreterTest.repl.interpret("people.registerTempTable(\"people\")", OldSparkInterpreterTest.context);
            code = "z.show(sqlc.sql(\"select * from people\"))";
        } else {
            code = "z.show(people.toDF)";
        }
        Assert.assertEquals(Code.SUCCESS, OldSparkInterpreterTest.repl.interpret(code, OldSparkInterpreterTest.context).code());
    }

    @Test
    public void testSparkSql() throws IOException, InterpreterException {
        OldSparkInterpreterTest.repl.interpret("case class Person(name:String, age:Int)\n", OldSparkInterpreterTest.context);
        OldSparkInterpreterTest.repl.interpret("val people = sc.parallelize(Seq(Person(\"moon\", 33), Person(\"jobs\", 51), Person(\"gates\", 51), Person(\"park\", 34)))\n", OldSparkInterpreterTest.context);
        Assert.assertEquals(Code.SUCCESS, OldSparkInterpreterTest.repl.interpret("people.take(3)", OldSparkInterpreterTest.context).code());
        if ((OldSparkInterpreterTest.getSparkVersionNumber(OldSparkInterpreterTest.repl)) <= 11) {
            // spark 1.2 or later does not allow create multiple
            // SparkContext in the same jvm by default.
            // create new interpreter
            SparkInterpreter repl2 = new SparkInterpreter(OldSparkInterpreterTest.getSparkTestProperties(OldSparkInterpreterTest.tmpDir));
            repl2.setInterpreterGroup(OldSparkInterpreterTest.intpGroup);
            OldSparkInterpreterTest.intpGroup.get("note").add(repl2);
            repl2.open();
            repl2.interpret("case class Man(name:String, age:Int)", OldSparkInterpreterTest.context);
            repl2.interpret("val man = sc.parallelize(Seq(Man(\"moon\", 33), Man(\"jobs\", 51), Man(\"gates\", 51), Man(\"park\", 34)))", OldSparkInterpreterTest.context);
            Assert.assertEquals(Code.SUCCESS, repl2.interpret("man.take(3)", OldSparkInterpreterTest.context).code());
            repl2.close();
        }
    }

    @Test
    public void testReferencingUndefinedVal() throws InterpreterException {
        InterpreterResult result = OldSparkInterpreterTest.repl.interpret(("def category(min: Int) = {" + ("    if (0 <= value) \"error\"" + "}")), OldSparkInterpreterTest.context);
        Assert.assertEquals(ERROR, result.code());
    }

    @Test
    public void emptyConfigurationVariablesOnlyForNonSparkProperties() {
        Properties intpProperty = OldSparkInterpreterTest.repl.getProperties();
        SparkConf sparkConf = OldSparkInterpreterTest.repl.getSparkContext().getConf();
        for (Object oKey : intpProperty.keySet()) {
            String key = ((String) (oKey));
            String value = ((String) (intpProperty.get(key)));
            OldSparkInterpreterTest.LOGGER.debug(String.format("[%s]: [%s]", key, value));
            if ((key.startsWith("spark.")) && (value.isEmpty())) {
                Assert.assertTrue(String.format("configuration starting from 'spark.' should not be empty. [%s]", key), ((!(sparkConf.contains(key))) || (!(sparkConf.get(key).isEmpty()))));
            }
        }
    }

    @Test
    public void shareSingleSparkContext() throws IOException, InterruptedException, InterpreterException {
        // create another SparkInterpreter
        SparkInterpreter repl2 = new SparkInterpreter(OldSparkInterpreterTest.getSparkTestProperties(OldSparkInterpreterTest.tmpDir));
        repl2.setInterpreterGroup(OldSparkInterpreterTest.intpGroup);
        OldSparkInterpreterTest.intpGroup.get("note").add(repl2);
        repl2.open();
        Assert.assertEquals(Code.SUCCESS, OldSparkInterpreterTest.repl.interpret("print(sc.parallelize(1 to 10).count())", OldSparkInterpreterTest.context).code());
        Assert.assertEquals(Code.SUCCESS, repl2.interpret("print(sc.parallelize(1 to 10).count())", OldSparkInterpreterTest.context).code());
        repl2.close();
    }

    @Test
    public void testEnableImplicitImport() throws IOException, InterpreterException {
        if ((OldSparkInterpreterTest.getSparkVersionNumber(OldSparkInterpreterTest.repl)) >= 13) {
            // Set option of importing implicits to "true", and initialize new Spark repl
            Properties p = OldSparkInterpreterTest.getSparkTestProperties(OldSparkInterpreterTest.tmpDir);
            p.setProperty("zeppelin.spark.importImplicit", "true");
            SparkInterpreter repl2 = new SparkInterpreter(p);
            repl2.setInterpreterGroup(OldSparkInterpreterTest.intpGroup);
            OldSparkInterpreterTest.intpGroup.get("note").add(repl2);
            repl2.open();
            String ddl = "val df = Seq((1, true), (2, false)).toDF(\"num\", \"bool\")";
            Assert.assertEquals(Code.SUCCESS, repl2.interpret(ddl, OldSparkInterpreterTest.context).code());
            repl2.close();
        }
    }

    @Test
    public void testDisableImplicitImport() throws IOException, InterpreterException {
        if ((OldSparkInterpreterTest.getSparkVersionNumber(OldSparkInterpreterTest.repl)) >= 13) {
            // Set option of importing implicits to "false", and initialize new Spark repl
            // this test should return error status when creating DataFrame from sequence
            Properties p = OldSparkInterpreterTest.getSparkTestProperties(OldSparkInterpreterTest.tmpDir);
            p.setProperty("zeppelin.spark.importImplicit", "false");
            SparkInterpreter repl2 = new SparkInterpreter(p);
            repl2.setInterpreterGroup(OldSparkInterpreterTest.intpGroup);
            OldSparkInterpreterTest.intpGroup.get("note").add(repl2);
            repl2.open();
            String ddl = "val df = Seq((1, true), (2, false)).toDF(\"num\", \"bool\")";
            Assert.assertEquals(ERROR, repl2.interpret(ddl, OldSparkInterpreterTest.context).code());
            repl2.close();
        }
    }

    @Test
    public void testCompletion() throws InterpreterException {
        List<InterpreterCompletion> completions = OldSparkInterpreterTest.repl.completion("sc.", "sc.".length(), null);
        Assert.assertTrue(((completions.size()) > 0));
    }

    @Test
    public void testMultilineCompletion() throws InterpreterException {
        String buf = "val x = 1\nsc.";
        List<InterpreterCompletion> completions = OldSparkInterpreterTest.repl.completion(buf, buf.length(), null);
        Assert.assertTrue(((completions.size()) > 0));
    }

    @Test
    public void testMultilineCompletionNewVar() throws InterpreterException {
        Assume.assumeFalse("this feature does not work with scala 2.10", Utils.isScala2_10());
        Assume.assumeTrue("This feature does not work with scala < 2.11.8", Utils.isCompilerAboveScala2_11_7());
        String buf = "val x = sc\nx.";
        List<InterpreterCompletion> completions = OldSparkInterpreterTest.repl.completion(buf, buf.length(), null);
        Assert.assertTrue(((completions.size()) > 0));
    }
}

