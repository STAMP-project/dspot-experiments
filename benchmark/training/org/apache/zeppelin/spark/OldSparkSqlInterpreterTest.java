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


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import Type.TABLE;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class OldSparkSqlInterpreterTest {
    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    static SparkSqlInterpreter sql;

    static SparkInterpreter repl;

    static InterpreterContext context;

    static InterpreterGroup intpGroup;

    @Test
    public void test() throws InterpreterException {
        OldSparkSqlInterpreterTest.repl.interpret("case class Test(name:String, age:Int)", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val test = sc.parallelize(Seq(Test(\"moon\", 33), Test(\"jobs\", 51), Test(\"gates\", 51), Test(\"park\", 34)))", OldSparkSqlInterpreterTest.context);
        if (isDataFrameSupported()) {
            OldSparkSqlInterpreterTest.repl.interpret("test.toDF.registerTempTable(\"test\")", OldSparkSqlInterpreterTest.context);
        } else {
            OldSparkSqlInterpreterTest.repl.interpret("test.registerTempTable(\"test\")", OldSparkSqlInterpreterTest.context);
        }
        InterpreterResult ret = OldSparkSqlInterpreterTest.sql.interpret("select name, age from test where age < 40", OldSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals(TABLE, ret.message().get(0).getType());
        Assert.assertEquals("name\tage\nmoon\t33\npark\t34\n", ret.message().get(0).getData());
        ret = OldSparkSqlInterpreterTest.sql.interpret("select wrong syntax", OldSparkSqlInterpreterTest.context);
        Assert.assertEquals(ERROR, ret.code());
        Assert.assertTrue(((ret.message().get(0).getData().length()) > 0));
        Assert.assertEquals(SUCCESS, OldSparkSqlInterpreterTest.sql.interpret("select case when name==\"aa\" then name else name end from test", OldSparkSqlInterpreterTest.context).code());
    }

    @Test
    public void testStruct() throws InterpreterException {
        OldSparkSqlInterpreterTest.repl.interpret("case class Person(name:String, age:Int)", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("case class People(group:String, person:Person)", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val gr = sc.parallelize(Seq(People(\"g1\", Person(\"moon\",33)), People(\"g2\", Person(\"sun\",11))))", OldSparkSqlInterpreterTest.context);
        if (isDataFrameSupported()) {
            OldSparkSqlInterpreterTest.repl.interpret("gr.toDF.registerTempTable(\"gr\")", OldSparkSqlInterpreterTest.context);
        } else {
            OldSparkSqlInterpreterTest.repl.interpret("gr.registerTempTable(\"gr\")", OldSparkSqlInterpreterTest.context);
        }
        InterpreterResult ret = OldSparkSqlInterpreterTest.sql.interpret("select * from gr", OldSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
    }

    @Test
    public void test_null_value_in_row() throws InterpreterException {
        OldSparkSqlInterpreterTest.repl.interpret("import org.apache.spark.sql._", OldSparkSqlInterpreterTest.context);
        if (isDataFrameSupported()) {
            OldSparkSqlInterpreterTest.repl.interpret("import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}", OldSparkSqlInterpreterTest.context);
        }
        OldSparkSqlInterpreterTest.repl.interpret("def toInt(s:String): Any = {try { s.trim().toInt} catch {case e:Exception => null}}", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val schema = StructType(Seq(StructField(\"name\", StringType, false),StructField(\"age\" , IntegerType, true),StructField(\"other\" , StringType, false)))", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val csv = sc.parallelize(Seq((\"jobs, 51, apple\"), (\"gates, , microsoft\")))", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val raw = csv.map(_.split(\",\")).map(p => Row(p(0),toInt(p(1)),p(2)))", OldSparkSqlInterpreterTest.context);
        if (isDataFrameSupported()) {
            OldSparkSqlInterpreterTest.repl.interpret("val people = sqlContext.createDataFrame(raw, schema)", OldSparkSqlInterpreterTest.context);
            OldSparkSqlInterpreterTest.repl.interpret("people.toDF.registerTempTable(\"people\")", OldSparkSqlInterpreterTest.context);
        } else {
            OldSparkSqlInterpreterTest.repl.interpret("val people = sqlContext.applySchema(raw, schema)", OldSparkSqlInterpreterTest.context);
            OldSparkSqlInterpreterTest.repl.interpret("people.registerTempTable(\"people\")", OldSparkSqlInterpreterTest.context);
        }
        InterpreterResult ret = OldSparkSqlInterpreterTest.sql.interpret("select name, age from people where name = 'gates'", OldSparkSqlInterpreterTest.context);
        System.err.println(("RET=" + (ret.message())));
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals(TABLE, ret.message().get(0).getType());
        Assert.assertEquals("name\tage\ngates\tnull\n", ret.message().get(0).getData());
    }

    @Test
    public void testMaxResults() throws InterpreterException {
        OldSparkSqlInterpreterTest.repl.interpret("case class P(age:Int)", OldSparkSqlInterpreterTest.context);
        OldSparkSqlInterpreterTest.repl.interpret("val gr = sc.parallelize(Seq(P(1),P(2),P(3),P(4),P(5),P(6),P(7),P(8),P(9),P(10),P(11)))", OldSparkSqlInterpreterTest.context);
        if (isDataFrameSupported()) {
            OldSparkSqlInterpreterTest.repl.interpret("gr.toDF.registerTempTable(\"gr\")", OldSparkSqlInterpreterTest.context);
        } else {
            OldSparkSqlInterpreterTest.repl.interpret("gr.registerTempTable(\"gr\")", OldSparkSqlInterpreterTest.context);
        }
        InterpreterResult ret = OldSparkSqlInterpreterTest.sql.interpret("select * from gr", OldSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertTrue(ret.message().get(1).getData().contains("alert-warning"));
    }
}

