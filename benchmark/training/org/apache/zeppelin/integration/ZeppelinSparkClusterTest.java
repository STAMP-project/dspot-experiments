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
package org.apache.zeppelin.integration;


import AuthenticationInfo.ANONYMOUS;
import InterpreterResult.Type.TABLE;
import Status.ABORT;
import Status.ERROR;
import Status.FINISHED;
import Status.READY;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.zeppelin.display.AngularObject;
import org.apache.zeppelin.interpreter.InterpreterNotFoundException;
import org.apache.zeppelin.interpreter.thrift.InterpreterCompletion;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.rest.AbstractTestRestApi;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test against spark cluster.
 */
public abstract class ZeppelinSparkClusterTest extends AbstractTestRestApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeppelinSparkClusterTest.class);

    // This is for only run setupSparkInterpreter one time for each spark version, otherwise
    // each test method will run setupSparkInterpreter which will cost a long time and may cause travis
    // ci timeout.
    // TODO(zjffdu) remove this after we upgrade it to junit 4.13 (ZEPPELIN-3341)
    private static Set<String> verifiedSparkVersions = new HashSet<>();

    private String sparkVersion;

    private AuthenticationInfo anonymous = new AuthenticationInfo("anonymous");

    public ZeppelinSparkClusterTest(String sparkVersion) throws Exception {
        this.sparkVersion = sparkVersion;
        ZeppelinSparkClusterTest.LOGGER.info(("Testing SparkVersion: " + sparkVersion));
        String sparkHome = DownloadUtils.downloadSpark(sparkVersion);
        if (!(ZeppelinSparkClusterTest.verifiedSparkVersions.contains(sparkVersion))) {
            ZeppelinSparkClusterTest.verifiedSparkVersions.add(sparkVersion);
            setupSparkInterpreter(sparkHome);
            verifySparkVersionNumber();
        }
    }

    @Test
    public void scalaOutputTest() throws IOException, InterruptedException {
        // create new note
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        p.setText(("%spark import java.util.Date\n" + ("import java.net.URL\n" + "println(\"hello\")\n")));
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertEquals(("hello\n" + ("import java.util.Date\n" + "import java.net.URL\n")), p.getReturn().message().get(0).getData());
        p.setText("%spark invalid_code");
        note.run(p.getId(), true);
        Assert.assertEquals(ERROR, p.getStatus());
        Assert.assertTrue(p.getReturn().message().get(0).getData().contains("error: "));
        // test local properties
        p.setText("%spark(p1=v1,p2=v2) print(z.getInterpreterContext().getLocalProperties().size())");
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertEquals("2", p.getReturn().message().get(0).getData());
        // test code completion
        List<InterpreterCompletion> completions = note.completion(p.getId(), "sc.", 2, ANONYMOUS);
        Assert.assertTrue(((completions.size()) > 0));
        // test cancel
        p.setText("%spark sc.range(1,10).map(e=>{Thread.sleep(1000); e}).collect()");
        note.run(p.getId(), false);
        waitForRunning(p);
        p.abort();
        waitForFinish(p);
        Assert.assertEquals(ABORT, p.getStatus());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void basicRDDTransformationAndActionTest() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        p.setText("%spark print(sc.parallelize(1 to 10).reduce(_ + _))");
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertEquals("55", p.getReturn().message().get(0).getData());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void sparkReadJSONTest() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        File tmpJsonFile = File.createTempFile("test", ".json");
        FileWriter jsonFileWriter = new FileWriter(tmpJsonFile);
        IOUtils.copy(new StringReader("{\"metadata\": { \"key\": 84896, \"value\": 54 }}\n"), jsonFileWriter);
        jsonFileWriter.close();
        if (isSpark2()) {
            p.setText((("%spark spark.read.json(\"file://" + (tmpJsonFile.getAbsolutePath())) + "\")"));
        } else {
            p.setText((("%spark sqlContext.read.json(\"file://" + (tmpJsonFile.getAbsolutePath())) + "\")"));
        }
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        if (isSpark2()) {
            Assert.assertTrue(p.getReturn().message().get(0).getData().contains("org.apache.spark.sql.DataFrame = [metadata: struct<key: bigint, value: bigint>]"));
        } else {
            Assert.assertTrue(p.getReturn().message().get(0).getData().contains("org.apache.spark.sql.DataFrame = [metadata: struct<key:bigint,value:bigint>]"));
        }
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void sparkReadCSVTest() throws IOException {
        if (!(isSpark2())) {
            // csv if not supported in spark 1.x natively
            return;
        }
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        File tmpCSVFile = File.createTempFile("test", ".csv");
        FileWriter csvFileWriter = new FileWriter(tmpCSVFile);
        IOUtils.copy(new StringReader("84896,54"), csvFileWriter);
        csvFileWriter.close();
        p.setText((("%spark spark.read.csv(\"file://" + (tmpCSVFile.getAbsolutePath())) + "\")"));
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertTrue(p.getReturn().message().get(0).getData().contains("org.apache.spark.sql.DataFrame = [_c0: string, _c1: string]\n"));
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void sparkSQLTest() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // test basic dataframe api
        Paragraph p = note.addNewParagraph(anonymous);
        p.setText(("%spark val df=sqlContext.createDataFrame(Seq((\"hello\",20)))\n" + "df.collect()"));
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertTrue(p.getReturn().message().get(0).getData().contains("Array[org.apache.spark.sql.Row] = Array([hello,20])"));
        // test display DataFrame
        p = note.addNewParagraph(anonymous);
        p.setText(("%spark val df=sqlContext.createDataFrame(Seq((\"hello\",20)))\n" + "z.show(df)"));
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertEquals(TABLE, p.getReturn().message().get(0).getType());
        Assert.assertEquals("_1\t_2\nhello\t20\n", p.getReturn().message().get(0).getData());
        // test display DataSet
        if (isSpark2()) {
            p = note.addNewParagraph(anonymous);
            p.setText(("%spark val ds=spark.createDataset(Seq((\"hello\",20)))\n" + "z.show(ds)"));
            note.run(p.getId(), true);
            Assert.assertEquals(FINISHED, p.getStatus());
            Assert.assertEquals(TABLE, p.getReturn().message().get(0).getType());
            Assert.assertEquals("_1\t_2\nhello\t20\n", p.getReturn().message().get(0).getData());
        }
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void sparkRTest() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        String sqlContextName = "sqlContext";
        if (isSpark2()) {
            sqlContextName = "spark";
        }
        Paragraph p = note.addNewParagraph(anonymous);
        p.setText((((("%spark.r localDF <- data.frame(name=c(\"a\", \"b\", \"c\"), age=c(19, 23, 18))\n" + "df <- createDataFrame(") + sqlContextName) + ", localDF)\n") + "count(df)"));
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Assert.assertEquals("[1] 3", p.getReturn().message().get(0).getData().trim());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void zRunTest() throws IOException {
        // create new note
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p0 = note.addNewParagraph(anonymous);
        // z.run(paragraphIndex)
        p0.setText("%spark z.run(1)");
        Paragraph p1 = note.addNewParagraph(anonymous);
        p1.setText("%spark val a=10");
        Paragraph p2 = note.addNewParagraph(anonymous);
        p2.setText("%spark print(a)");
        note.run(p0.getId(), true);
        Assert.assertEquals(FINISHED, p0.getStatus());
        // z.run is not blocking call. So p1 may not be finished when p0 is done.
        waitForFinish(p1);
        Assert.assertEquals(FINISHED, p1.getStatus());
        note.run(p2.getId(), true);
        Assert.assertEquals(FINISHED, p2.getStatus());
        Assert.assertEquals("10", p2.getReturn().message().get(0).getData());
        Paragraph p3 = note.addNewParagraph(anonymous);
        p3.setText("%spark println(new java.util.Date())");
        // run current Node, z.runNote(noteId)
        p0.setText(String.format("%%spark z.runNote(\"%s\")", note.getId()));
        note.run(p0.getId());
        waitForFinish(p0);
        waitForFinish(p1);
        waitForFinish(p2);
        waitForFinish(p3);
        Assert.assertEquals(FINISHED, p3.getStatus());
        String p3result = p3.getReturn().message().get(0).getData();
        Assert.assertTrue(((p3result.length()) > 0));
        // z.run(noteId, paragraphId)
        p0.setText(String.format("%%spark z.run(\"%s\", \"%s\")", note.getId(), p3.getId()));
        p3.setText("%spark println(\"END\")");
        note.run(p0.getId(), true);
        waitForFinish(p3);
        Assert.assertEquals(FINISHED, p3.getStatus());
        Assert.assertEquals("END\n", p3.getReturn().message().get(0).getData());
        // run paragraph in note2 via paragraph in note1
        Note note2 = TestUtils.getInstance(Notebook.class).createNote("note2", anonymous);
        Paragraph p20 = note2.addNewParagraph(anonymous);
        p20.setText("%spark val a = 1");
        Paragraph p21 = note2.addNewParagraph(anonymous);
        p21.setText("%spark print(a)");
        // run p20 of note2 via paragraph in note1
        p0.setText(String.format("%%spark z.run(\"%s\", \"%s\")", note2.getId(), p20.getId()));
        note.run(p0.getId(), true);
        waitForFinish(p20);
        Assert.assertEquals(FINISHED, p20.getStatus());
        Assert.assertEquals(READY, p21.getStatus());
        p0.setText(String.format("%%spark z.runNote(\"%s\")", note2.getId()));
        note.run(p0.getId(), true);
        waitForFinish(p20);
        waitForFinish(p21);
        Assert.assertEquals(FINISHED, p20.getStatus());
        Assert.assertEquals(FINISHED, p21.getStatus());
        Assert.assertEquals("1", p21.getReturn().message().get(0).getData());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
        TestUtils.getInstance(Notebook.class).removeNote(note2.getId(), anonymous);
    }

    @Test
    public void testZeppelinContextResource() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(anonymous);
        p1.setText("%spark z.put(\"var_1\", \"hello world\")");
        Paragraph p2 = note.addNewParagraph(anonymous);
        p2.setText("%spark println(z.get(\"var_1\"))");
        Paragraph p3 = note.addNewParagraph(anonymous);
        p3.setText("%spark.pyspark print(z.get(\"var_1\"))");
        // resources across interpreter processes (via DistributedResourcePool)
        Paragraph p4 = note.addNewParagraph(anonymous);
        p4.setText("%python print(z.get('var_1'))");
        note.run(p1.getId(), true);
        note.run(p2.getId(), true);
        note.run(p3.getId(), true);
        note.run(p4.getId(), true);
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(FINISHED, p2.getStatus());
        Assert.assertEquals("hello world\n", p2.getReturn().message().get(0).getData());
        Assert.assertEquals(FINISHED, p3.getStatus());
        Assert.assertEquals("hello world\n", p3.getReturn().message().get(0).getData());
        Assert.assertEquals(FINISHED, p4.getStatus());
        Assert.assertEquals("hello world\n", p4.getReturn().message().get(0).getData());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testZeppelinContextHook() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // register global hook & note1 hook
        Paragraph p1 = note.addNewParagraph(anonymous);
        p1.setText((((((("%python from __future__ import print_function\n" + (("z.registerHook(\'pre_exec\', \'print(1)\')\n" + "z.registerHook(\'post_exec\', \'print(2)\')\n") + "z.registerNoteHook('pre_exec', 'print(3)', '")) + (note.getId())) + "\')\n") + "z.registerNoteHook('post_exec', 'print(4)', '") + (note.getId())) + "\')\n"));
        Paragraph p2 = note.addNewParagraph(anonymous);
        p2.setText("%python print(5)");
        note.run(p1.getId(), true);
        note.run(p2.getId(), true);
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals(FINISHED, p2.getStatus());
        Assert.assertEquals("1\n3\n5\n4\n2\n", p2.getReturn().message().get(0).getData());
        Note note2 = TestUtils.getInstance(Notebook.class).createNote("note2", anonymous);
        Paragraph p3 = note2.addNewParagraph(anonymous);
        p3.setText("%python print(6)");
        note2.run(p3.getId(), true);
        Assert.assertEquals("1\n6\n2\n", p3.getReturn().message().get(0).getData());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
        TestUtils.getInstance(Notebook.class).removeNote(note2.getId(), anonymous);
    }

    @Test
    public void pySparkDepLoaderTest() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        // restart spark interpreter to make dep loader work
        TestUtils.getInstance(Notebook.class).getInterpreterSettingManager().close();
        // load dep
        Paragraph p0 = note.addNewParagraph(anonymous);
        p0.setText("%dep z.load(\"com.databricks:spark-csv_2.11:1.2.0\")");
        note.run(p0.getId(), true);
        Assert.assertEquals(FINISHED, p0.getStatus());
        // write test csv file
        File tmpFile = File.createTempFile("test", "csv");
        FileUtils.write(tmpFile, "a,b\n1,2");
        // load data using libraries from dep loader
        Paragraph p1 = note.addNewParagraph(anonymous);
        String sqlContextName = "sqlContext";
        if (isSpark2()) {
            sqlContextName = "spark";
        }
        p1.setText((((((("%pyspark\n" + ("from pyspark.sql import SQLContext\n" + "print(")) + sqlContextName) + ".read.format('com.databricks.spark.csv')") + ".load('file://") + (tmpFile.getAbsolutePath())) + "').count())"));
        note.run(p1.getId(), true);
        Assert.assertEquals(FINISHED, p1.getStatus());
        Assert.assertEquals("2\n", p1.getReturn().message().get(0).getData());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testSparkZeppelinContextDynamicForms() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        String code = "%spark.spark println(z.textbox(\"my_input\", \"default_name\"))\n" + ((((("println(z.password(\"my_pwd\"))\n" + "println(z.select(\"my_select\", \"1\",") + "Seq((\"1\", \"select_1\"), (\"2\", \"select_2\"))))\n") + "val items=z.checkbox(\"my_checkbox\", Seq(\"2\"), ") + "Seq((\"1\", \"check_1\"), (\"2\", \"check_2\")))\n") + "println(items(0))");
        p.setText(code);
        note.run(p.getId());
        waitForFinish(p);
        Assert.assertEquals(FINISHED, p.getStatus());
        Iterator<String> formIter = p.settings.getForms().keySet().iterator();
        Assert.assertEquals("my_input", formIter.next());
        Assert.assertEquals("my_pwd", formIter.next());
        Assert.assertEquals("my_select", formIter.next());
        Assert.assertEquals("my_checkbox", formIter.next());
        // check dynamic forms values
        String[] result = p.getReturn().message().get(0).getData().split("\n");
        Assert.assertEquals(5, result.length);
        Assert.assertEquals("default_name", result[0]);
        Assert.assertEquals("null", result[1]);
        Assert.assertEquals("1", result[2]);
        Assert.assertEquals("2", result[3]);
        Assert.assertEquals("items: Seq[Any] = Buffer(2)", result[4]);
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testPySparkZeppelinContextDynamicForms() throws IOException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        String code = "%spark.pyspark print(z.input(\'my_input\', \'default_name\'))\n" + ((((("print(z.password(\'my_pwd\'))\n" + "print(z.select('my_select', ") + "[(\'1\', \'select_1\'), (\'2\', \'select_2\')], defaultValue=\'1\'))\n") + "items=z.checkbox('my_checkbox', ") + "[(\'1\', \'check_1\'), (\'2\', \'check_2\')], defaultChecked=[\'2\'])\n") + "print(items[0])");
        p.setText(code);
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Iterator<String> formIter = p.settings.getForms().keySet().iterator();
        Assert.assertEquals("my_input", formIter.next());
        Assert.assertEquals("my_pwd", formIter.next());
        Assert.assertEquals("my_select", formIter.next());
        Assert.assertEquals("my_checkbox", formIter.next());
        // check dynamic forms values
        String[] result = p.getReturn().message().get(0).getData().split("\n");
        Assert.assertEquals(4, result.length);
        Assert.assertEquals("default_name", result[0]);
        Assert.assertEquals("None", result[1]);
        Assert.assertEquals("1", result[2]);
        Assert.assertEquals("2", result[3]);
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testAngularObjects() throws IOException, InterpreterNotFoundException {
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p1 = note.addNewParagraph(anonymous);
        // add local angular object
        p1.setText("%spark z.angularBind(\"name\", \"world\")");
        note.run(p1.getId(), true);
        Assert.assertEquals(FINISHED, p1.getStatus());
        List<AngularObject> angularObjects = p1.getBindedInterpreter().getInterpreterGroup().getAngularObjectRegistry().getAll(note.getId(), null);
        Assert.assertEquals(1, angularObjects.size());
        Assert.assertEquals("name", angularObjects.get(0).getName());
        Assert.assertEquals("world", angularObjects.get(0).get());
        // remove local angular object
        Paragraph p2 = note.addNewParagraph(anonymous);
        p2.setText("%spark z.angularUnbind(\"name\")");
        note.run(p2.getId(), true);
        Assert.assertEquals(FINISHED, p2.getStatus());
        angularObjects = p1.getBindedInterpreter().getInterpreterGroup().getAngularObjectRegistry().getAll(note.getId(), null);
        Assert.assertEquals(0, angularObjects.size());
        // add global angular object
        Paragraph p3 = note.addNewParagraph(anonymous);
        p3.setText("%spark z.angularBindGlobal(\"name2\", \"world2\")");
        note.run(p3.getId(), true);
        Assert.assertEquals(FINISHED, p3.getStatus());
        List<AngularObject> globalAngularObjects = p3.getBindedInterpreter().getInterpreterGroup().getAngularObjectRegistry().getAll(null, null);
        Assert.assertEquals(1, globalAngularObjects.size());
        Assert.assertEquals("name2", globalAngularObjects.get(0).getName());
        Assert.assertEquals("world2", globalAngularObjects.get(0).get());
        // remove global angular object
        Paragraph p4 = note.addNewParagraph(anonymous);
        p4.setText("%spark z.angularUnbindGlobal(\"name2\")");
        note.run(p4.getId(), true);
        Assert.assertEquals(FINISHED, p4.getStatus());
        globalAngularObjects = p4.getBindedInterpreter().getInterpreterGroup().getAngularObjectRegistry().getAll(note.getId(), null);
        Assert.assertEquals(0, globalAngularObjects.size());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }

    @Test
    public void testConfInterpreter() throws IOException {
        TestUtils.getInstance(Notebook.class).getInterpreterSettingManager().close();
        Note note = TestUtils.getInstance(Notebook.class).createNote("note1", anonymous);
        Paragraph p = note.addNewParagraph(anonymous);
        p.setText("%spark.conf spark.jars.packages\tcom.databricks:spark-csv_2.11:1.2.0");
        note.run(p.getId(), true);
        Assert.assertEquals(FINISHED, p.getStatus());
        Paragraph p1 = note.addNewParagraph(anonymous);
        p1.setText("%spark\nimport com.databricks.spark.csv._");
        note.run(p1.getId(), true);
        Assert.assertEquals(FINISHED, p1.getStatus());
        TestUtils.getInstance(Notebook.class).removeNote(note.getId(), anonymous);
    }
}

