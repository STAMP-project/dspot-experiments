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
package org.pentaho.di.trans.steps.databaselookup;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class DatabaseLookupMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<DatabaseLookupMeta> testMetaClass = DatabaseLookupMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    private DatabaseLookupMeta databaseLookupMeta = new DatabaseLookupMeta();

    @Test
    public void getFieldWithValueUsedTwice() throws KettleStepException {
        databaseLookupMeta.setReturnValueField(new String[]{ "match", "match", "mismatch" });
        databaseLookupMeta.setReturnValueNewName(new String[]{ "v1", "v2", "v3" });
        ValueMetaInterface v1 = new ValueMetaString("match");
        ValueMetaInterface v2 = new ValueMetaString("match1");
        RowMetaInterface[] info = new RowMetaInterface[1];
        info[0] = new RowMeta();
        info[0].setValueMetaList(Arrays.asList(v1, v2));
        ValueMetaInterface r1 = new ValueMetaString("value");
        RowMetaInterface row = new RowMeta();
        row.setValueMetaList(new ArrayList<ValueMetaInterface>(Arrays.asList(r1)));
        databaseLookupMeta.getFields(row, "", info, null, null, null, null);
        List<ValueMetaInterface> expectedRow = Arrays.asList(new ValueMetaInterface[]{ new ValueMetaString("value"), new ValueMetaString("v1"), new ValueMetaString("v2") });
        Assert.assertEquals(3, row.getValueMetaList().size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expectedRow.get(i).getName(), row.getValueMetaList().get(i).getName());
        }
    }

    @Test
    public void testProvidesModelerMeta() throws Exception {
        DatabaseLookupMeta databaseLookupMeta = new DatabaseLookupMeta();
        databaseLookupMeta.setReturnValueField(new String[]{ "f1", "f2", "f3" });
        databaseLookupMeta.setReturnValueNewName(new String[]{ "s4", "s5", "s6" });
        DatabaseLookupData databaseLookupData = new DatabaseLookupData();
        databaseLookupData.returnMeta = Mockito.mock(RowMeta.class);
        Assert.assertEquals(databaseLookupData.returnMeta, databaseLookupMeta.getRowMeta(databaseLookupData));
        Assert.assertEquals(3, databaseLookupMeta.getDatabaseFields().size());
        Assert.assertEquals("f1", databaseLookupMeta.getDatabaseFields().get(0));
        Assert.assertEquals("f2", databaseLookupMeta.getDatabaseFields().get(1));
        Assert.assertEquals("f3", databaseLookupMeta.getDatabaseFields().get(2));
        Assert.assertEquals(3, databaseLookupMeta.getStreamFields().size());
        Assert.assertEquals("s4", databaseLookupMeta.getStreamFields().get(0));
        Assert.assertEquals("s5", databaseLookupMeta.getStreamFields().get(1));
        Assert.assertEquals("s6", databaseLookupMeta.getStreamFields().get(2));
    }

    @Test
    public void cloneTest() throws Exception {
        DatabaseLookupMeta meta = new DatabaseLookupMeta();
        meta.allocate(2, 2);
        meta.setStreamKeyField1(new String[]{ "aa", "bb" });
        meta.setTableKeyField(new String[]{ "cc", "dd" });
        meta.setKeyCondition(new String[]{ "ee", "ff" });
        meta.setStreamKeyField2(new String[]{ "gg", "hh" });
        meta.setReturnValueField(new String[]{ "ii", "jj" });
        meta.setReturnValueNewName(new String[]{ "kk", "ll" });
        meta.setReturnValueDefault(new String[]{ "mm", "nn" });
        meta.setReturnValueDefaultType(new int[]{ 10, 50 });
        meta.setOrderByClause("FOO DESC");
        DatabaseLookupMeta aClone = ((DatabaseLookupMeta) (meta.clone()));
        Assert.assertFalse((aClone == meta));
        Assert.assertTrue(Arrays.equals(meta.getStreamKeyField1(), aClone.getStreamKeyField1()));
        Assert.assertTrue(Arrays.equals(meta.getTableKeyField(), aClone.getTableKeyField()));
        Assert.assertTrue(Arrays.equals(meta.getKeyCondition(), aClone.getKeyCondition()));
        Assert.assertTrue(Arrays.equals(meta.getStreamKeyField2(), aClone.getStreamKeyField2()));
        Assert.assertTrue(Arrays.equals(meta.getReturnValueField(), aClone.getReturnValueField()));
        Assert.assertTrue(Arrays.equals(meta.getReturnValueNewName(), aClone.getReturnValueNewName()));
        Assert.assertTrue(Arrays.equals(meta.getReturnValueDefault(), aClone.getReturnValueDefault()));
        Assert.assertTrue(Arrays.equals(meta.getReturnValueDefaultType(), aClone.getReturnValueDefaultType()));
        Assert.assertEquals(meta.getOrderByClause(), aClone.getOrderByClause());
        Assert.assertEquals(meta.getXML(), aClone.getXML());
    }
}

