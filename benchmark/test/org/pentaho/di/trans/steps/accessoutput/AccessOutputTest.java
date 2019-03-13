/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.accessoutput;


import TransTestFactory.DUMMY_STEPNAME;
import TransTestFactory.INJECTOR_STEPNAME;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestFactory;


public class AccessOutputTest {
    private static final String TABLE_NAME = "Users";

    private static final String FIELD_NAME = "UserName";

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testProcessRows() throws IOException, KettleException {
        final String stepName = "My Access Output Step";
        File dbFile = File.createTempFile("AccessOutputTestProcessRows", ".mdb");
        dbFile.delete();
        AccessOutputMeta stepMeta = new AccessOutputMeta();
        stepMeta.setDefault();
        stepMeta.setFilename(dbFile.getAbsolutePath());
        stepMeta.setTablename(AccessOutputTest.TABLE_NAME);
        TransMeta transMeta = TransTestFactory.generateTestTransformation(null, stepMeta, stepName);
        List<RowMetaAndData> inputList = getTestRowMetaAndData(new String[]{ "Alice" });
        TransTestFactory.executeTestTransformation(transMeta, INJECTOR_STEPNAME, stepName, DUMMY_STEPNAME, inputList);
        dbFile.deleteOnExit();
        checkResult(dbFile.getAbsolutePath(), AccessOutputTest.TABLE_NAME, inputList);
    }

    @Test
    public void testTruncateTable() throws IOException, KettleException {
        final String stepName = "My Access Output Step";
        File dbFile = File.createTempFile("AccessOutputTestTruncateTable", ".mdb");
        dbFile.delete();
        AccessOutputMeta stepMeta = new AccessOutputMeta();
        stepMeta.setDefault();
        stepMeta.setFilename(dbFile.getAbsolutePath());
        stepMeta.setTablename(AccessOutputTest.TABLE_NAME);
        stepMeta.setTableTruncated(true);
        TransMeta transMeta = TransTestFactory.generateTestTransformation(null, stepMeta, stepName);
        List<RowMetaAndData> inputList = getTestRowMetaAndData(new String[]{ "Alice" });
        for (int i = 0; i < 3; i++) {
            TransTestFactory.executeTestTransformation(transMeta, INJECTOR_STEPNAME, stepName, DUMMY_STEPNAME, inputList);
        }
        dbFile.deleteOnExit();
        checkResult(dbFile.getAbsolutePath(), AccessOutputTest.TABLE_NAME, inputList);
    }

    @Test
    public void testNoTruncateTable() throws IOException, KettleException {
        final String stepName = "My Access Output Step";
        File dbFile = File.createTempFile("AccessOutputTestNoTruncateTable", ".mdb");
        dbFile.delete();
        AccessOutputMeta stepMeta = new AccessOutputMeta();
        stepMeta.setDefault();
        stepMeta.setFilename(dbFile.getAbsolutePath());
        stepMeta.setTablename(AccessOutputTest.TABLE_NAME);
        stepMeta.setTableTruncated(false);
        TransMeta transMeta = TransTestFactory.generateTestTransformation(null, stepMeta, stepName);
        List<RowMetaAndData> inputList = getTestRowMetaAndData(new String[]{ "Alice" });
        List<RowMetaAndData> expected = new ArrayList<RowMetaAndData>();
        // Execute the transformation 3 times, we should have more rows than just a single execution
        for (int i = 0; i < 3; i++) {
            TransTestFactory.executeTestTransformation(transMeta, INJECTOR_STEPNAME, stepName, DUMMY_STEPNAME, inputList);
            expected.add(inputList.get(0).clone());
        }
        dbFile.deleteOnExit();
        checkResult(dbFile.getAbsolutePath(), AccessOutputTest.TABLE_NAME, expected);
    }
}

