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
package org.pentaho.di.trans.steps.dimensionlookup;


import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.SQLStatement;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.PrimitiveIntArrayLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;


public class DimensionLookupMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<DimensionLookupMeta> testMetaClass = DimensionLookupMeta.class;

    private ThreadLocal<DimensionLookupMeta> holdTestingMeta = new ThreadLocal<DimensionLookupMeta>();

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public static final String databaseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((("<connection>" + "<name>lookup</name>") + "<server>127.0.0.1</server>") + "<type>H2</type>") + "<access>Native</access>") + "<database>mem:db</database>") + "<port></port>") + "<username>sa</username>") + "<password></password>") + "</connection>");

    @Test
    public void testGetFields() throws Exception {
        RowMeta extraFields = new RowMeta();
        extraFields.addValueMeta(new ValueMetaString("field1"));
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        DimensionLookupMeta meta = Mockito.spy(new DimensionLookupMeta());
        meta.setUpdate(false);
        meta.setKeyField(null);
        meta.setFieldLookup(new String[]{ "field1" });
        meta.setFieldStream(new String[]{ "" });
        meta.setDatabaseMeta(dbMeta);
        Mockito.doReturn(extraFields).when(meta).getDatabaseTableFields(((Database) (ArgumentMatchers.anyObject())), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(Mockito.mock(LogChannelInterface.class)).when(meta).getLog();
        RowMeta row = new RowMeta();
        try {
            meta.getFields(row, "DimensionLookupMetaTest", new RowMeta[]{ row }, null, null, null, null);
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage().contains(BaseMessages.getString(DimensionLookupMeta.class, "DimensionLookupMeta.Error.NoTechnicalKeySpecified")));
        }
    }

    @Test
    public void testUseDefaultSchemaName() throws Exception {
        String schemaName = "";
        String tableName = "tableName";
        String schemaTable = "default.tableName";
        String keyField = "keyField";
        DatabaseMeta databaseMeta = Mockito.spy(new DatabaseMeta(DimensionLookupMetaTest.databaseXML) {
            @Override
            public String getFieldDefinition(ValueMetaInterface v, String tk, String pk, boolean use_autoinc) {
                return "someValue";
            }
        });
        Mockito.when(databaseMeta.getQuotedSchemaTableCombination(schemaName, tableName)).thenReturn(schemaTable);
        DimensionLookupMeta dlm = new DimensionLookupMeta();
        dlm.setUpdate(true);
        dlm.setDatabaseMeta(databaseMeta);
        dlm.setTableName(tableName);
        dlm.setSchemaName(schemaName);
        dlm.setKeyLookup(new String[]{ "keyLookup1", "keyLookup2" });
        dlm.setKeyStream(new String[]{ "keyStream1", "keyStream2" });
        dlm.setFieldLookup(new String[]{ "fieldLookup1", "fieldLookup2" });
        dlm.setFieldStream(new String[]{ "FieldStream1", "FieldStream2" });
        dlm.setFieldUpdate(new int[]{ 1, 2 });
        dlm.setKeyField(keyField);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        RowMetaInterface rowMetaInterface = Mockito.mock(RowMetaInterface.class);
        Mockito.when(rowMetaInterface.size()).thenReturn(1);
        Repository repository = Mockito.mock(Repository.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        SQLStatement sqlStatement = dlm.getSQLStatements(new TransMeta(), stepMeta, rowMetaInterface, repository, metaStore);
        String sql = sqlStatement.getSQL();
        Assert.assertEquals(3, StringUtils.countMatches(sql, schemaTable));
    }

    @Test
    public void testProvidesModelerMeta() throws Exception {
        final RowMeta rowMeta = Mockito.mock(RowMeta.class);
        final DimensionLookupMeta dimensionLookupMeta = new DimensionLookupMeta() {
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
        dimensionLookupMeta.setFieldLookup(new String[]{ "f1", "f2", "f3" });
        dimensionLookupMeta.setKeyLookup(new String[]{ "k1" });
        dimensionLookupMeta.setFieldStream(new String[]{ "s4", "s5", "s6" });
        dimensionLookupMeta.setKeyStream(new String[]{ "ks1" });
        dimensionLookupMeta.setSchemaName("aSchema");
        dimensionLookupMeta.setTableName("aDimTable");
        final DimensionLookupData dimensionLookupData = new DimensionLookupData();
        Assert.assertEquals(rowMeta, dimensionLookupMeta.getRowMeta(dimensionLookupData));
        Assert.assertEquals(4, dimensionLookupMeta.getDatabaseFields().size());
        Assert.assertEquals("f1", dimensionLookupMeta.getDatabaseFields().get(0));
        Assert.assertEquals("f2", dimensionLookupMeta.getDatabaseFields().get(1));
        Assert.assertEquals("f3", dimensionLookupMeta.getDatabaseFields().get(2));
        Assert.assertEquals("k1", dimensionLookupMeta.getDatabaseFields().get(3));
        Assert.assertEquals(4, dimensionLookupMeta.getStreamFields().size());
        Assert.assertEquals("s4", dimensionLookupMeta.getStreamFields().get(0));
        Assert.assertEquals("s5", dimensionLookupMeta.getStreamFields().get(1));
        Assert.assertEquals("s6", dimensionLookupMeta.getStreamFields().get(2));
        Assert.assertEquals("ks1", dimensionLookupMeta.getStreamFields().get(3));
    }

    // Note - Removed cloneTest since it's covered by the load/save tester
    // Doing this as a work-around for sequenceName validation.
    // Apparently, sequenceName will always be written (getXml),
    // but will only be read if the value of "update" is true (readData).
    // While testing the load/save behavior, there is no sane way
    // to test dependent variables like this (that I could see). So,
    // I'm holding onto the meta in a threadlocal, and have to have
    // this special load/save handler for sequenceName.
    // MB - 5/2016
    public class SequenceNameLoadSaveValidator implements FieldLoadSaveValidator<String> {
        final Random rand = new Random();

        @Override
        public String getTestObject() {
            DimensionLookupMeta dlm = holdTestingMeta.get();// get the currently-being tested meta

            if (dlm.isUpdate()) {
                // value returned here is dependant on isUpdate()
                return UUID.randomUUID().toString();// return a string

            } else {
                return null;// Return null if !isUpdate ...

            }
        }

        @Override
        public boolean validateTestObject(String testObject, Object actual) {
            String another = ((String) (actual));
            DimensionLookupMeta dlm = holdTestingMeta.get();
            if (dlm.isUpdate()) {
                return testObject.equals(another);// if isUpdate, compare strings

            } else {
                return another == null;// If !isUpdate, another should be null

            }
        }
    }

    public class FieldUpdateIntArrayLoadSaveValidator extends PrimitiveIntArrayLoadSaveValidator {
        public FieldUpdateIntArrayLoadSaveValidator(FieldLoadSaveValidator<Integer> fieldValidator) {
            this(fieldValidator, null);
        }

        public FieldUpdateIntArrayLoadSaveValidator(FieldLoadSaveValidator<Integer> fieldValidator, Integer elements) {
            super(fieldValidator, elements);
        }

        @Override
        public int[] getTestObject() {
            DimensionLookupMeta dlm = holdTestingMeta.get();
            int[] testObject = super.getTestObject();
            if (!(dlm.isUpdate())) {
                dlm.setReturnType(testObject);
            }
            return testObject;
        }
    }

    @Test
    public void testPDI16559() throws Exception {
        DimensionLookupMeta dimensionLookup = new DimensionLookupMeta();
        dimensionLookup.setKeyStream(new String[]{ "test_field" });
        dimensionLookup.setKeyLookup(new String[]{  });
        dimensionLookup.setCacheSize(15);
        dimensionLookup.setSchemaName("test_schema");
        dimensionLookup.setFieldStream(new String[]{ "123", "abc", "def" });
        dimensionLookup.setFieldLookup(new String[]{ "wibble" });
        dimensionLookup.setFieldUpdate(new int[]{ 11, 12 });
        try {
            String badXml = dimensionLookup.getXML();
            Assert.fail("Before calling afterInjectionSynchronization, should have thrown an ArrayIndexOOB");
        } catch (Exception expected) {
            // Do Nothing
        }
        dimensionLookup.afterInjectionSynchronization();
        // run without a exception
        String ktrXml = dimensionLookup.getXML();
        Assert.assertEquals(dimensionLookup.getKeyStream().length, dimensionLookup.getKeyLookup().length);
        Assert.assertEquals(dimensionLookup.getFieldStream().length, dimensionLookup.getFieldLookup().length);
        Assert.assertEquals(dimensionLookup.getFieldUpdate().length, dimensionLookup.getFieldUpdate().length);
    }
}

