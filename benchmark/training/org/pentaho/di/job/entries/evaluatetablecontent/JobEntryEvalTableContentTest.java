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
package org.pentaho.di.job.entries.evaluatetablecontent;


import Const.KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES;
import JobEntryEvalTableContent.SUCCESS_CONDITION_ROWS_COUNT_EQUAL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/* tests fix for PDI-1044
Job entry: Evaluate rows number in a table:
PDI Server logs with error from Quartz even though the job finishes successfully.
 */
public class JobEntryEvalTableContentTest {
    private static final Map<Class<?>, String> dbMap = new HashMap<Class<?>, String>();

    JobEntryEvalTableContent entry;

    private static PluginInterface mockDbPlugin;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static class DBMockIface extends BaseDatabaseMeta {
        @Override
        public Object clone() {
            return this;
        }

        @Override
        public String getFieldDefinition(ValueMetaInterface v, String tk, String pk, boolean use_autoinc, boolean add_fieldname, boolean add_cr) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDriverClass() {
            return "org.pentaho.di.job.entries.evaluatetablecontent.MockDriver";
        }

        @Override
        public String getURL(String hostname, String port, String databaseName) throws KettleDatabaseException {
            return "";
        }

        @Override
        public String getAddColumnStatement(String tablename, ValueMetaInterface v, String tk, boolean use_autoinc, String pk, boolean semicolon) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getModifyColumnStatement(String tablename, ValueMetaInterface v, String tk, boolean use_autoinc, String pk, boolean semicolon) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getUsedLibraries() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int[] getAccessTypeList() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    @Test
    public void testNrErrorsFailureNewBehavior() throws Exception {
        entry.setLimit("1");
        entry.setSuccessCondition(SUCCESS_CONDITION_ROWS_COUNT_EQUAL);
        entry.setTablename("table");
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Eval number of rows should fail", res.getResult());
        Assert.assertEquals("No errors should be reported in result object accoding to the new behavior", res.getNrErrors(), 0);
    }

    @Test
    public void testNrErrorsFailureOldBehavior() throws Exception {
        entry.setLimit("1");
        entry.setSuccessCondition(SUCCESS_CONDITION_ROWS_COUNT_EQUAL);
        entry.setTablename("table");
        entry.setVariable(KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES, "Y");
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Eval number of rows should fail", res.getResult());
        Assert.assertEquals("An error should be reported in result object accoding to the old behavior", res.getNrErrors(), 1);
    }

    @Test
    public void testNrErrorsSuccess() throws Exception {
        entry.setLimit("5");
        entry.setSuccessCondition(SUCCESS_CONDITION_ROWS_COUNT_EQUAL);
        entry.setTablename("table");
        Result res = entry.execute(new Result(), 0);
        Assert.assertTrue("Eval number of rows should be suceeded", res.getResult());
        Assert.assertEquals("Apparently there should no error", res.getNrErrors(), 0);
        // that should work regardless of old/new behavior flag
        entry.setVariable(KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES, "Y");
        res = entry.execute(new Result(), 0);
        Assert.assertTrue("Eval number of rows should be suceeded", res.getResult());
        Assert.assertEquals("Apparently there should no error", res.getNrErrors(), 0);
    }

    @Test
    public void testNrErrorsNoCustomSql() throws Exception {
        entry.setLimit("5");
        entry.setSuccessCondition(SUCCESS_CONDITION_ROWS_COUNT_EQUAL);
        entry.setUseCustomSQL(true);
        entry.setCustomSQL(null);
        Result res = entry.execute(new Result(), 0);
        Assert.assertFalse("Eval number of rows should fail", res.getResult());
        Assert.assertEquals("Apparently there should be an error", res.getNrErrors(), 1);
        // that should work regardless of old/new behavior flag
        entry.setVariable(KETTLE_COMPATIBILITY_SET_ERROR_ON_SPECIFIC_JOB_ENTRIES, "Y");
        res = entry.execute(new Result(), 0);
        Assert.assertFalse("Eval number of rows should fail", res.getResult());
        Assert.assertEquals("Apparently there should be an error", res.getNrErrors(), 1);
    }
}

