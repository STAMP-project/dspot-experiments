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
package org.apache.zeppelin.livy;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.HTML;
import InterpreterResult.Type.IMG;
import InterpreterResult.Type.TABLE;
import InterpreterResult.Type.TEXT;
import LivySparkSQLInterpreter.ZEPPELIN_LIVY_SPARK_SQL_FIELD_TRUNCATE;
import LivyVersion.LIVY_0_3_0;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.livy.test.framework.Cluster;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterOutput;
import org.apache.zeppelin.interpreter.InterpreterOutputListener;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResultMessageOutput;
import org.apache.zeppelin.interpreter.LazyOpenInterpreter;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LivyInterpreterIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LivyInterpreterIT.class);

    private static Cluster cluster;

    private static Properties properties;

    @Test
    public void testSparkInterpreter() throws InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        InterpreterGroup interpreterGroup = new InterpreterGroup("group_1");
        interpreterGroup.put("session_1", new ArrayList<org.apache.zeppelin.interpreter.Interpreter>());
        LivySparkInterpreter sparkInterpreter = new LivySparkInterpreter(LivyInterpreterIT.properties);
        sparkInterpreter.setInterpreterGroup(interpreterGroup);
        interpreterGroup.get("session_1").add(sparkInterpreter);
        AuthenticationInfo authInfo = new AuthenticationInfo("user1");
        LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
        InterpreterOutput output = new InterpreterOutput(outputListener);
        InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
        sparkInterpreter.open();
        LivySparkSQLInterpreter sqlInterpreter = new LivySparkSQLInterpreter(LivyInterpreterIT.properties);
        interpreterGroup.get("session_1").add(sqlInterpreter);
        sqlInterpreter.setInterpreterGroup(interpreterGroup);
        sqlInterpreter.open();
        try {
            // detect spark version
            InterpreterResult result = sparkInterpreter.interpret("sc.version", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            boolean isSpark2 = isSpark2(sparkInterpreter, context);
            testRDD(sparkInterpreter, isSpark2);
            testDataFrame(sparkInterpreter, sqlInterpreter, isSpark2);
        } finally {
            sparkInterpreter.close();
            sqlInterpreter.close();
        }
    }

    @Test
    public void testPySparkInterpreter() throws InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        final LivyPySparkInterpreter pysparkInterpreter = new LivyPySparkInterpreter(LivyInterpreterIT.properties);
        pysparkInterpreter.setInterpreterGroup(Mockito.mock(InterpreterGroup.class));
        AuthenticationInfo authInfo = new AuthenticationInfo("user1");
        LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
        InterpreterOutput output = new InterpreterOutput(outputListener);
        final InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
        pysparkInterpreter.open();
        // test traceback msg
        try {
            pysparkInterpreter.getLivyVersion();
            // for livy version >=0.3 , input some erroneous spark code, check the shown result is more
            // than one line
            InterpreterResult result = pysparkInterpreter.interpret("sc.parallelize(wrongSyntax(1, 2)).count()", context);
            Assert.assertEquals(ERROR, result.code());
            Assert.assertTrue(((result.message().get(0).getData().split("\n").length) > 1));
            Assert.assertTrue(result.message().get(0).getData().contains("Traceback"));
        } catch (APINotFoundException e) {
            // only livy 0.2 can throw this exception since it doesn't have /version endpoint
            // in livy 0.2, most error msg is encapsulated in evalue field, only print(a) in pyspark would
            // return none-empty traceback
            InterpreterResult result = pysparkInterpreter.interpret("print(a)", context);
            Assert.assertEquals(ERROR, result.code());
            Assert.assertTrue(((result.message().get(0).getData().split("\n").length) > 1));
            Assert.assertTrue(result.message().get(0).getData().contains("Traceback"));
        }
        // test utf-8 Encoding
        String utf8Str = "???????";
        InterpreterResult reslt = pysparkInterpreter.interpret((("print(\"" + utf8Str) + "\")"), context);
        Assert.assertEquals(SUCCESS, reslt.code());
        Assert.assertTrue(reslt.message().get(0).getData().contains(utf8Str));
        // test special characters
        String charStr = "a??i????o?";
        InterpreterResult res = pysparkInterpreter.interpret((("print(\"" + charStr) + "\")"), context);
        Assert.assertEquals(SUCCESS, res.code());
        Assert.assertTrue(res.message().get(0).getData().contains(charStr));
        try {
            InterpreterResult result = pysparkInterpreter.interpret("sc.version", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            boolean isSpark2 = isSpark2(pysparkInterpreter, context);
            // test RDD api
            result = pysparkInterpreter.interpret("sc.range(1, 10).sum()", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            Assert.assertTrue(result.message().get(0).getData().contains("45"));
            // test DataFrame api
            if (!isSpark2) {
                pysparkInterpreter.interpret(("from pyspark.sql import SQLContext\n" + "sqlContext = SQLContext(sc)"), context);
                result = pysparkInterpreter.interpret(("df=sqlContext.createDataFrame([(\"hello\",20)])\n" + "df.collect()"), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                // python2 has u and python3 don't have u
                Assert.assertTrue(((result.message().get(0).getData().contains("[Row(_1=u'hello', _2=20)]")) || (result.message().get(0).getData().contains("[Row(_1='hello', _2=20)]"))));
            } else {
                result = pysparkInterpreter.interpret(("df=spark.createDataFrame([(\"hello\",20)])\n" + "df.collect()"), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                // python2 has u and python3 don't have u
                Assert.assertTrue(((result.message().get(0).getData().contains("[Row(_1=u'hello', _2=20)]")) || (result.message().get(0).getData().contains("[Row(_1='hello', _2=20)]"))));
            }
            // test magic api
            pysparkInterpreter.interpret(("t = [{\"name\":\"userA\", \"role\":\"roleA\"}," + "{\"name\":\"userB\", \"role\":\"roleB\"}]"), context);
            result = pysparkInterpreter.interpret("%table t", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            Assert.assertEquals(TABLE, result.message().get(0).getType());
            Assert.assertTrue(result.message().get(0).getData().contains("userA"));
            // error
            result = pysparkInterpreter.interpret("print(a)", context);
            Assert.assertEquals(ERROR, result.code());
            Assert.assertEquals(TEXT, result.message().get(0).getType());
            Assert.assertTrue(result.message().get(0).getData().contains("name 'a' is not defined"));
            // cancel
            if (pysparkInterpreter.livyVersion.newerThanEquals(LIVY_0_3_0)) {
                Thread cancelThread = new Thread() {
                    @Override
                    public void run() {
                        // invoke cancel after 1 millisecond to wait job starting
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pysparkInterpreter.cancel(context);
                    }
                };
                cancelThread.start();
                result = pysparkInterpreter.interpret(("import time\n" + "sc.range(1, 10).foreach(lambda a: time.sleep(10))"), context);
                Assert.assertEquals(ERROR, result.code());
                String message = result.message().get(0).getData();
                // 2 possibilities, sometimes livy doesn't return the real cancel exception
                Assert.assertTrue(((message.contains("cancelled part of cancelled job group")) || (message.contains("Job is cancelled"))));
            }
        } finally {
            pysparkInterpreter.close();
        }
    }

    @Test
    public void testSparkInterpreterWithDisplayAppInfo_StringWithoutTruncation() throws InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        InterpreterGroup interpreterGroup = new InterpreterGroup("group_1");
        interpreterGroup.put("session_1", new ArrayList<org.apache.zeppelin.interpreter.Interpreter>());
        Properties properties2 = new Properties(LivyInterpreterIT.properties);
        properties2.put("zeppelin.livy.displayAppInfo", "true");
        // enable spark ui because it is disabled by livy integration test
        properties2.put("livy.spark.ui.enabled", "true");
        properties2.put(ZEPPELIN_LIVY_SPARK_SQL_FIELD_TRUNCATE, "false");
        LivySparkInterpreter sparkInterpreter = new LivySparkInterpreter(properties2);
        sparkInterpreter.setInterpreterGroup(interpreterGroup);
        interpreterGroup.get("session_1").add(sparkInterpreter);
        AuthenticationInfo authInfo = new AuthenticationInfo("user1");
        LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
        InterpreterOutput output = new InterpreterOutput(outputListener);
        InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
        sparkInterpreter.open();
        LivySparkSQLInterpreter sqlInterpreter = new LivySparkSQLInterpreter(properties2);
        interpreterGroup.get("session_1").add(sqlInterpreter);
        sqlInterpreter.setInterpreterGroup(interpreterGroup);
        sqlInterpreter.open();
        try {
            InterpreterResult result = sparkInterpreter.interpret("sc.version", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(2, result.message().size());
            // check yarn appId and ensure it is not null
            Assert.assertTrue(result.message().get(1).getData().contains("Spark Application Id: application_"));
            // html output
            String htmlCode = "println(\"%html <h1> hello </h1>\")";
            result = sparkInterpreter.interpret(htmlCode, context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(2, result.message().size());
            Assert.assertEquals(HTML, result.message().get(0).getType());
            // detect spark version
            result = sparkInterpreter.interpret("sc.version", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(2, result.message().size());
            boolean isSpark2 = isSpark2(sparkInterpreter, context);
            if (!isSpark2) {
                result = sparkInterpreter.interpret(("val df=sqlContext.createDataFrame(Seq((\"12characters12characters\",20)))" + (".toDF(\"col_1\", \"col_2\")\n" + "df.collect()")), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(2, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("Array[org.apache.spark.sql.Row] = Array([12characters12characters,20])"));
            } else {
                result = sparkInterpreter.interpret(("val df=spark.createDataFrame(Seq((\"12characters12characters\",20)))" + (".toDF(\"col_1\", \"col_2\")\n" + "df.collect()")), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(2, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("Array[org.apache.spark.sql.Row] = Array([12characters12characters,20])"));
            }
            sparkInterpreter.interpret("df.registerTempTable(\"df\")", context);
            // test LivySparkSQLInterpreter which share the same SparkContext with LivySparkInterpreter
            result = sqlInterpreter.interpret("select * from df where col_1='12characters12characters'", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(TABLE, result.message().get(0).getType());
            Assert.assertEquals("col_1\tcol_2\n12characters12characters\t20", result.message().get(0).getData());
        } finally {
            sparkInterpreter.close();
            sqlInterpreter.close();
        }
    }

    @Test
    public void testSparkRInterpreter() throws InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        final LivySparkRInterpreter sparkRInterpreter = new LivySparkRInterpreter(LivyInterpreterIT.properties);
        sparkRInterpreter.setInterpreterGroup(Mockito.mock(InterpreterGroup.class));
        try {
            sparkRInterpreter.getLivyVersion();
        } catch (APINotFoundException e) {
            // don't run sparkR test for livy 0.2 as there's some issues for livy 0.2
            return;
        }
        AuthenticationInfo authInfo = new AuthenticationInfo("user1");
        LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
        InterpreterOutput output = new InterpreterOutput(outputListener);
        final InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
        sparkRInterpreter.open();
        try {
            // only test it in livy newer than 0.2.0
            boolean isSpark2 = isSpark2(sparkRInterpreter, context);
            InterpreterResult result = null;
            // test DataFrame api
            if (isSpark2) {
                result = sparkRInterpreter.interpret("df <- as.DataFrame(faithful)\nhead(df)", context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("eruptions waiting"));
                // cancel
                Thread cancelThread = new Thread() {
                    @Override
                    public void run() {
                        // invoke cancel after 1 millisecond to wait job starting
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sparkRInterpreter.cancel(context);
                    }
                };
                cancelThread.start();
                result = sparkRInterpreter.interpret(("df <- as.DataFrame(faithful)\n" + ("df1 <- dapplyCollect(df, function(x) " + "{ Sys.sleep(10); x <- cbind(x, x$waiting * 60) })")), context);
                Assert.assertEquals(ERROR, result.code());
                String message = result.message().get(0).getData();
                // 2 possibilities, sometimes livy doesn't return the real cancel exception
                Assert.assertTrue(((message.contains("cancelled part of cancelled job group")) || (message.contains("Job is cancelled"))));
            } else {
                result = sparkRInterpreter.interpret(("df <- createDataFrame(sqlContext, faithful)" + "\nhead(df)"), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("eruptions waiting"));
            }
            // error
            result = sparkRInterpreter.interpret("cat(a)", context);
            Assert.assertEquals(ERROR, result.code());
            Assert.assertEquals(TEXT, result.message().get(0).getType());
            Assert.assertTrue(result.message().get(0).getData().contains("object 'a' not found"));
        } finally {
            sparkRInterpreter.close();
        }
    }

    @Test
    public void testLivyTutorialNote() throws IOException, InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        InterpreterGroup interpreterGroup = new InterpreterGroup("group_1");
        interpreterGroup.put("session_1", new ArrayList<org.apache.zeppelin.interpreter.Interpreter>());
        LazyOpenInterpreter sparkInterpreter = new LazyOpenInterpreter(new LivySparkInterpreter(LivyInterpreterIT.properties));
        sparkInterpreter.setInterpreterGroup(interpreterGroup);
        interpreterGroup.get("session_1").add(sparkInterpreter);
        LazyOpenInterpreter sqlInterpreter = new LazyOpenInterpreter(new LivySparkSQLInterpreter(LivyInterpreterIT.properties));
        interpreterGroup.get("session_1").add(sqlInterpreter);
        sqlInterpreter.setInterpreterGroup(interpreterGroup);
        sqlInterpreter.open();
        try {
            AuthenticationInfo authInfo = new AuthenticationInfo("user1");
            LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
            InterpreterOutput output = new InterpreterOutput(outputListener);
            InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
            String p1 = IOUtils.toString(getClass().getResourceAsStream("/livy_tutorial_1.scala"));
            InterpreterResult result = sparkInterpreter.interpret(p1, context);
            Assert.assertEquals(SUCCESS, result.code());
            String p2 = IOUtils.toString(getClass().getResourceAsStream("/livy_tutorial_2.sql"));
            result = sqlInterpreter.interpret(p2, context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(TABLE, result.message().get(0).getType());
        } finally {
            sparkInterpreter.close();
            sqlInterpreter.close();
        }
    }

    @Test
    public void testSharedInterpreter() throws InterpreterException {
        if (!(LivyInterpreterIT.checkPreCondition())) {
            return;
        }
        InterpreterGroup interpreterGroup = new InterpreterGroup("group_1");
        interpreterGroup.put("session_1", new ArrayList<org.apache.zeppelin.interpreter.Interpreter>());
        LazyOpenInterpreter sparkInterpreter = new LazyOpenInterpreter(new LivySparkInterpreter(LivyInterpreterIT.properties));
        sparkInterpreter.setInterpreterGroup(interpreterGroup);
        interpreterGroup.get("session_1").add(sparkInterpreter);
        LazyOpenInterpreter sqlInterpreter = new LazyOpenInterpreter(new LivySparkSQLInterpreter(LivyInterpreterIT.properties));
        interpreterGroup.get("session_1").add(sqlInterpreter);
        sqlInterpreter.setInterpreterGroup(interpreterGroup);
        LazyOpenInterpreter pysparkInterpreter = new LazyOpenInterpreter(new LivyPySparkInterpreter(LivyInterpreterIT.properties));
        interpreterGroup.get("session_1").add(pysparkInterpreter);
        pysparkInterpreter.setInterpreterGroup(interpreterGroup);
        LazyOpenInterpreter sparkRInterpreter = new LazyOpenInterpreter(new LivySparkRInterpreter(LivyInterpreterIT.properties));
        interpreterGroup.get("session_1").add(sparkRInterpreter);
        sparkRInterpreter.setInterpreterGroup(interpreterGroup);
        LazyOpenInterpreter sharedInterpreter = new LazyOpenInterpreter(new LivySharedInterpreter(LivyInterpreterIT.properties));
        interpreterGroup.get("session_1").add(sharedInterpreter);
        sharedInterpreter.setInterpreterGroup(interpreterGroup);
        sparkInterpreter.open();
        sqlInterpreter.open();
        pysparkInterpreter.open();
        sparkRInterpreter.open();
        try {
            AuthenticationInfo authInfo = new AuthenticationInfo("user1");
            LivyInterpreterIT.MyInterpreterOutputListener outputListener = new LivyInterpreterIT.MyInterpreterOutputListener();
            InterpreterOutput output = new InterpreterOutput(outputListener);
            InterpreterContext context = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").setAuthenticationInfo(authInfo).setInterpreterOut(output).build();
            // detect spark version
            InterpreterResult result = sparkInterpreter.interpret("sc.version", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            boolean isSpark2 = isSpark2(((BaseLivyInterpreter) (sparkInterpreter.getInnerInterpreter())), context);
            if (!isSpark2) {
                result = sparkInterpreter.interpret(("val df=sqlContext.createDataFrame(Seq((\"hello\",20))).toDF(\"col_1\", \"col_2\")\n" + "df.collect()"), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("Array[org.apache.spark.sql.Row] = Array([hello,20])"));
                sparkInterpreter.interpret("df.registerTempTable(\"df\")", context);
                // access table from pyspark
                result = pysparkInterpreter.interpret("sqlContext.sql(\"select * from df\").show()", context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains(("+-----+-----+\n" + ((("|col_1|col_2|\n" + "+-----+-----+\n") + "|hello|   20|\n") + "+-----+-----+"))));
                // access table from sparkr
                result = sparkRInterpreter.interpret("head(sql(sqlContext, \"select * from df\"))", context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("col_1 col_2\n1 hello    20"));
            } else {
                result = sparkInterpreter.interpret(("val df=spark.createDataFrame(Seq((\"hello\",20))).toDF(\"col_1\", \"col_2\")\n" + "df.collect()"), context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("Array[org.apache.spark.sql.Row] = Array([hello,20])"));
                sparkInterpreter.interpret("df.registerTempTable(\"df\")", context);
                // access table from pyspark
                result = pysparkInterpreter.interpret("spark.sql(\"select * from df\").show()", context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains(("+-----+-----+\n" + ((("|col_1|col_2|\n" + "+-----+-----+\n") + "|hello|   20|\n") + "+-----+-----+"))));
                // access table from sparkr
                result = sparkRInterpreter.interpret("head(sql(\"select * from df\"))", context);
                Assert.assertEquals(SUCCESS, result.code());
                Assert.assertEquals(1, result.message().size());
                Assert.assertTrue(result.message().get(0).getData().contains("col_1 col_2\n1 hello    20"));
            }
            // test plotting of python
            result = pysparkInterpreter.interpret(("import matplotlib.pyplot as plt\n" + (((("plt.switch_backend(\'agg\')\n" + "data=[1,2,3,4]\n") + "plt.figure()\n") + "plt.plot(data)\n") + "%matplot plt")), context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            Assert.assertEquals(IMG, result.message().get(0).getType());
            // test plotting of R
            result = sparkRInterpreter.interpret("hist(mtcars$mpg)", context);
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertEquals(1, result.message().size());
            Assert.assertEquals(IMG, result.message().get(0).getType());
            // test code completion
            List<InterpreterCompletion> completionResult = sparkInterpreter.completion("df.sho", 6, context);
            Assert.assertEquals(1, completionResult.size());
            Assert.assertEquals("show", completionResult.get(0).name);
        } finally {
            sparkInterpreter.close();
            sqlInterpreter.close();
        }
    }

    public static class MyInterpreterOutputListener implements InterpreterOutputListener {
        @Override
        public void onAppend(int index, InterpreterResultMessageOutput out, byte[] line) {
        }

        @Override
        public void onUpdate(int index, InterpreterResultMessageOutput out) {
        }

        @Override
        public void onUpdateAll(InterpreterOutput out) {
        }
    }
}

