/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.tableinput;


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


/**
 * User: Dzmitry Stsiapanau Date: 2/4/14 Time: 5:47 PM
 */
public class TableInputMetaTest {
    LoadSaveTester loadSaveTester;

    Class<TableInputMeta> testMetaClass = TableInputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public class TableInputMetaHandler extends TableInputMeta {
        public Database database = Mockito.mock(Database.class);

        @Override
        protected Database getDatabase() {
            return database;
        }
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testGetFields() throws Exception {
        TableInputMetaTest.TableInputMetaHandler meta = new TableInputMetaTest.TableInputMetaHandler();
        setLazyConversionActive(true);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        meta.setDatabaseMeta(dbMeta);
        Database mockDB = meta.getDatabase();
        Mockito.when(mockDB.getQueryFields(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(createMockFields());
        RowMetaInterface expectedRowMeta = new RowMeta();
        ValueMetaInterface valueMeta = new ValueMetaString("field1");
        valueMeta.setStorageMetadata(new ValueMetaString("field1"));
        valueMeta.setStorageType(STORAGE_TYPE_BINARY_STRING);
        expectedRowMeta.addValueMeta(valueMeta);
        VariableSpace space = Mockito.mock(VariableSpace.class);
        RowMetaInterface rowMetaInterface = new RowMeta();
        meta.getFields(rowMetaInterface, "TABLE_INPUT_META", null, null, space, null, null);
        Assert.assertEquals(expectedRowMeta.toString(), rowMetaInterface.toString());
    }
}

