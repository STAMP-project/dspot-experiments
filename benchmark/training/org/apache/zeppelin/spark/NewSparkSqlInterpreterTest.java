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
import org.junit.Test;


public class NewSparkSqlInterpreterTest {
    private static SparkSqlInterpreter sqlInterpreter;

    private static SparkInterpreter sparkInterpreter;

    private static InterpreterContext context;

    private static InterpreterGroup intpGroup;

    @Test
    public void test() throws InterpreterException {
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("case class Test(name:String, age:Int)", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("val test = sc.parallelize(Seq(Test(\"moon\", 33), Test(\"jobs\", 51), Test(\"gates\", 51), Test(\"park\", 34)))", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("test.toDF.registerTempTable(\"test\")", NewSparkSqlInterpreterTest.context);
        InterpreterResult ret = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select name, age from test where age < 40", NewSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals(TABLE, ret.message().get(0).getType());
        Assert.assertEquals("name\tage\nmoon\t33\npark\t34\n", ret.message().get(0).getData());
        ret = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select wrong syntax", NewSparkSqlInterpreterTest.context);
        Assert.assertEquals(ERROR, ret.code());
        Assert.assertTrue(((ret.message().get(0).getData().length()) > 0));
        Assert.assertEquals(SUCCESS, NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select case when name='aa' then name else name end from test", NewSparkSqlInterpreterTest.context).code());
    }

    @Test
    public void testStruct() throws InterpreterException {
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("case class Person(name:String, age:Int)", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("case class People(group:String, person:Person)", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("val gr = sc.parallelize(Seq(People(\"g1\", Person(\"moon\",33)), People(\"g2\", Person(\"sun\",11))))", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("gr.toDF.registerTempTable(\"gr\")", NewSparkSqlInterpreterTest.context);
        InterpreterResult ret = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select * from gr", NewSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
    }

    @Test
    public void testMaxResults() throws InterpreterException {
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("case class P(age:Int)", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("val gr = sc.parallelize(Seq(P(1),P(2),P(3),P(4),P(5),P(6),P(7),P(8),P(9),P(10),P(11)))", NewSparkSqlInterpreterTest.context);
        NewSparkSqlInterpreterTest.sparkInterpreter.interpret("gr.toDF.registerTempTable(\"gr\")", NewSparkSqlInterpreterTest.context);
        InterpreterResult ret = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select * from gr", NewSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
        // the number of rows is 10+1, 1 is the head of table
        Assert.assertEquals(11, ret.message().get(0).getData().split("\n").length);
        Assert.assertTrue(ret.message().get(1).getData().contains("alert-warning"));
        // test limit local property
        NewSparkSqlInterpreterTest.context.getLocalProperties().put("limit", "5");
        ret = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select * from gr", NewSparkSqlInterpreterTest.context);
        Assert.assertEquals(SUCCESS, ret.code());
        // the number of rows is 5+1, 1 is the head of table
        Assert.assertEquals(6, ret.message().get(0).getData().split("\n").length);
    }

    @Test
    public void testConcurrentSQL() throws InterruptedException, InterpreterException {
        if (NewSparkSqlInterpreterTest.sparkInterpreter.getSparkVersion().isSpark2()) {
            NewSparkSqlInterpreterTest.sparkInterpreter.interpret("spark.udf.register(\"sleep\", (e:Int) => {Thread.sleep(e*1000); e})", NewSparkSqlInterpreterTest.context);
        } else {
            NewSparkSqlInterpreterTest.sparkInterpreter.interpret("sqlContext.udf.register(\"sleep\", (e:Int) => {Thread.sleep(e*1000); e})", NewSparkSqlInterpreterTest.context);
        }
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    InterpreterResult result = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select sleep(10)", NewSparkSqlInterpreterTest.context);
                    Assert.assertEquals(SUCCESS, result.code());
                } catch (InterpreterException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    InterpreterResult result = NewSparkSqlInterpreterTest.sqlInterpreter.interpret("select sleep(10)", NewSparkSqlInterpreterTest.context);
                    Assert.assertEquals(SUCCESS, result.code());
                } catch (InterpreterException e) {
                    e.printStackTrace();
                }
            }
        };
        // start running 2 spark sql, each would sleep 10 seconds, the totally running time should
        // be less than 20 seconds, which means they run concurrently.
        long start = System.currentTimeMillis();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        long end = System.currentTimeMillis();
        Assert.assertTrue("running time must be less than 20 seconds", (((end - start) / 1000) < 20));
    }
}

