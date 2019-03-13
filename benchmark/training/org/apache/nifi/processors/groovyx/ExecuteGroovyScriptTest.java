/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.groovyx;


import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecuteGroovyScriptTest {
    private static final String DB_LOCATION = "target/db";

    protected TestRunner runner;

    protected static DBCPService dbcp = null;// to make single initialization


    protected ExecuteGroovyScript proc;

    public final String TEST_RESOURCE_LOCATION = "target/test/resources/groovy/";

    private final String TEST_CSV_DATA = "gender,title,first,last\n" + ("female,miss,marlene,shaw\n" + "male,mr,todd,graham");

    /**
     * Tests a script that reads content of the flowfile content and stores the value in an attribute of the outgoing flowfile.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testReadFlowFileContentAndStoreInFlowFileAttribute() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, "def flowFile = session.get(); if(!flowFile)return; flowFile.testAttr = flowFile.read().getText('UTF-8'); REL_SUCCESS << flowFile;");
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue("test content".getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        result.get(0).assertAttributeEquals("testAttr", "test content");
    }

    @Test
    public void test_onTrigger_groovy() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_onTrigger.groovy"));
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        result.get(0).assertAttributeEquals("from-content", "test content");
    }

    @Test
    public void test_onTriggerX_groovy() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_onTriggerX.groovy"));
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        result.get(0).assertAttributeEquals("from-content", "test content");
    }

    @Test
    public void test_onTrigger_changeContent_groovy() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_onTrigger_changeContent.groovy"));
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue(TEST_CSV_DATA.getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertAttributeEquals("selected.columns", "first,last");
        resultFile.assertContentEquals("Marlene Shaw\nTodd Graham\n");
    }

    @Test
    public void test_onTrigger_changeContentX_groovy() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_onTrigger_changeContentX.groovy"));
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue(TEST_CSV_DATA.getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertAttributeEquals("selected.columns", "first,last");
        resultFile.assertContentEquals("Marlene Shaw\nTodd Graham\n");
    }

    @Test
    public void test_no_input_groovy() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_no_input.groovy"));
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertAttributeEquals("filename", "test.txt");
        resultFile.assertContentEquals("Test");
    }

    @Test
    public void test_good_script() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, " def ff = session.get(); if(!ff)return; REL_SUCCESS << ff ");
        runner.assertValid();
    }

    @Test
    public void test_bad_script() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, " { { ");
        runner.assertNotValid();
    }

    // ---------------------------------------------------------
    @Test
    public void test_ctl_01_access() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_ctl_01_access.groovy"));
        runner.setProperty("CTL.mydbcp", "dbcp");// pass dbcp as a service to script

        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertContentEquals("OK", "UTF-8");
    }

    @Test
    public void test_sql_01_select() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_sql_01_select.groovy"));
        runner.setProperty("SQL.mydb", "dbcp");
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertAttributeEquals("filename", "test.txt");
        resultFile.assertContentEquals("Joe Smith\nCarrie Jones\n", "UTF-8");
    }

    @Test
    public void test_sql_02_blob_write() throws Exception {
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_sql_02_blob_write.groovy"));
        runner.setProperty("SQL.mydb", "dbcp");
        // runner.setProperty("ID", "0");
        runner.assertValid();
        runner.enqueue(TEST_CSV_DATA.getBytes(StandardCharsets.UTF_8), map("ID", "0"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertContentEquals(TEST_CSV_DATA.getBytes(StandardCharsets.UTF_8));
        // let's check database content in next text case
    }

    @Test
    public void test_sql_03_blob_read() throws Exception {
        // read blob from database written at previous step and write to flow file
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_sql_03_blob_read.groovy"));
        runner.setProperty("SQL.mydb", "dbcp");
        runner.setProperty("ID", "0");
        runner.setValidateExpressionUsage(false);
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        resultFile.assertContentEquals(TEST_CSV_DATA.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void test_sql_04_insert_and_json() throws Exception {
        // read blob from database written at previous step and write to flow file
        runner.setProperty(proc.SCRIPT_FILE, ((TEST_RESOURCE_LOCATION) + "test_sql_04_insert_and_json.groovy"));
        runner.setProperty("SQL.mydb", "dbcp");
        runner.setValidateExpressionUsage(false);
        runner.assertValid();
        runner.enqueue(new FileInputStream(((TEST_RESOURCE_LOCATION) + "test_sql_04_insert_and_json.json")));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 3);// number of inserted rows

        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        MockFlowFile resultFile = result.get(0);
        List<String> lines = ResourceGroovyMethods.readLines(new File(((TEST_RESOURCE_LOCATION) + "test_sql_04_insert_and_json.json")), "UTF-8");
        // pass through to&from json before compare
        resultFile.assertContentEquals(JsonOutput.toJson(new JsonSlurper().parseText(lines.get(1))), "UTF-8");
    }

    @Test
    public void test_filter_01() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, "def ff = session.get{it.FILTER=='3'}; if(!ff)return; REL_SUCCESS << ff;");
        // runner.setProperty(proc.FAIL_STRATEGY, "rollback");
        runner.assertValid();
        runner.enqueue("01".getBytes("UTF-8"), map("FILTER", "1"));
        runner.enqueue("31".getBytes("UTF-8"), map("FILTER", "3"));
        runner.enqueue("03".getBytes("UTF-8"), map("FILTER", "2"));
        runner.enqueue("32".getBytes("UTF-8"), map("FILTER", "3"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 2);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(proc.REL_SUCCESS.getName());
        result.get(0).assertContentEquals("31", "UTF-8");
        result.get(1).assertContentEquals("32", "UTF-8");
    }

    @Test
    public void test_read_01() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, "def ff = session.get(); if(!ff)return; assert ff.read().getText('UTF-8')=='1234'; REL_SUCCESS << ff; ");
        runner.assertValid();
        runner.enqueue("1234".getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
    }

    @Test
    public void test_read_02() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, "def ff = session.get(); if(!ff)return; ff.read{s-> assert s.getText('UTF-8')=='1234' }; REL_SUCCESS << ff; ");
        runner.assertValid();
        runner.enqueue("1234".getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
    }

    @Test
    public void test_read_03() throws Exception {
        runner.setProperty(proc.SCRIPT_BODY, "def ff = session.get(); if(!ff)return; ff.read('UTF-8'){r-> assert r.getText()=='1234' }; REL_SUCCESS << ff; ");
        runner.assertValid();
        runner.enqueue("1234".getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(proc.REL_SUCCESS.getName(), 1);
    }

    private static class DBCPServiceSimpleImpl extends AbstractControllerService implements DBCPService {
        @Override
        public String getIdentifier() {
            return "dbcp";
        }

        @Override
        public Connection getConnection() throws ProcessException {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                return DriverManager.getConnection((("jdbc:derby:" + (ExecuteGroovyScriptTest.DB_LOCATION)) + ";create=true"));
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }
}

