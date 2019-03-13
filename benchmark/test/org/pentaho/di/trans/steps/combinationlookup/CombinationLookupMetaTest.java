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
package org.pentaho.di.trans.steps.combinationlookup;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class CombinationLookupMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<CombinationLookupMeta> testMetaClass = CombinationLookupMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testProvidesModelerMeta() throws Exception {
        final RowMeta rowMeta = Mockito.mock(RowMeta.class);
        final CombinationLookupMeta combinationLookupMeta = new CombinationLookupMeta() {
            @Override
            Database createDatabaseObject() {
                return Mockito.mock(Database.class);
            }

            @Override
            protected RowMetaInterface getDatabaseTableFields(Database db, String schemaName, String tableName) throws KettleDatabaseException {
                Assert.assertEquals("aSchema", schemaName);
                Assert.assertEquals("aDimTable", tableName);
                return rowMeta;
            }
        };
        combinationLookupMeta.setKeyLookup(new String[]{ "f1", "f2", "f3" });
        combinationLookupMeta.setKeyField(new String[]{ "s4", "s5", "s6" });
        combinationLookupMeta.setSchemaName("aSchema");
        combinationLookupMeta.setTablename("aDimTable");
        final CombinationLookupData dimensionLookupData = new CombinationLookupData();
        Assert.assertEquals(rowMeta, combinationLookupMeta.getRowMeta(dimensionLookupData));
        Assert.assertEquals(3, combinationLookupMeta.getDatabaseFields().size());
        Assert.assertEquals("f1", combinationLookupMeta.getDatabaseFields().get(0));
        Assert.assertEquals("f2", combinationLookupMeta.getDatabaseFields().get(1));
        Assert.assertEquals("f3", combinationLookupMeta.getDatabaseFields().get(2));
        Assert.assertEquals(3, combinationLookupMeta.getStreamFields().size());
        Assert.assertEquals("s4", combinationLookupMeta.getStreamFields().get(0));
        Assert.assertEquals("s5", combinationLookupMeta.getStreamFields().get(1));
        Assert.assertEquals("s6", combinationLookupMeta.getStreamFields().get(2));
    }

    @Test
    public void testPDI16559() throws Exception {
        CombinationLookupMeta combinationLookup = new CombinationLookupMeta();
        combinationLookup.setKeyField(new String[]{ "test_field" });
        combinationLookup.setKeyLookup(new String[]{  });
        combinationLookup.setCacheSize(15);
        combinationLookup.setSchemaName("test_schema");
        combinationLookup.setTablename("test_table");
        combinationLookup.setReplaceFields(true);
        combinationLookup.setPreloadCache(false);
        try {
            String badXml = combinationLookup.getXML();
            Assert.fail("Before calling afterInjectionSynchronization, should have thrown an ArrayIndexOOB");
        } catch (Exception expected) {
            // Do Nothing
        }
        combinationLookup.afterInjectionSynchronization();
        // run without a exception
        String ktrXml = combinationLookup.getXML();
        Assert.assertEquals(combinationLookup.getKeyField().length, combinationLookup.getKeyLookup().length);
    }
}

