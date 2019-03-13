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
package org.pentaho.di.core.database;


import ValueMetaInterface.TYPE_STRING;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaNone;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class DatabaseMetaTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static final String TABLE_NAME = "tableName";

    private static final String DROP_STATEMENT = "dropStatement";

    private static final String DROP_STATEMENT_FALLBACK = "DROP TABLE IF EXISTS " + (DatabaseMetaTest.TABLE_NAME);

    private DatabaseMeta databaseMeta;

    private DatabaseInterface databaseInterface;

    @Test
    public void testGetDatabaseInterfacesMapWontReturnNullIfCalledSimultaneouslyWithClear() throws InterruptedException, ExecutionException {
        final AtomicBoolean done = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (!(done.get())) {
                    DatabaseMeta.clearDatabaseInterfacesMap();
                } 
            }
        });
        Future<Exception> getFuture = executorService.submit(new Callable<Exception>() {
            @Override
            public Exception call() throws Exception {
                int i = 0;
                while (!(done.get())) {
                    Assert.assertNotNull(("Got null on try: " + (i++)), DatabaseMeta.getDatabaseInterfacesMap());
                    if (i > 30000) {
                        done.set(true);
                    }
                } 
                return null;
            }
        });
        getFuture.get();
    }

    @Test
    public void testDatabaseAccessTypeCode() throws Exception {
        String expectedJndi = "JNDI";
        String access = DatabaseMeta.getAccessTypeDesc(DatabaseMeta.getAccessType(expectedJndi));
        Assert.assertEquals(expectedJndi, access);
    }

    @Test
    public void testApplyingDefaultOptions() throws Exception {
        HashMap<String, String> existingOptions = new HashMap<String, String>();
        existingOptions.put("type1.extra", "extraValue");
        existingOptions.put("type1.existing", "existingValue");
        existingOptions.put("type2.extra", "extraValue2");
        HashMap<String, String> newOptions = new HashMap<String, String>();
        newOptions.put("type1.new", "newValue");
        newOptions.put("type1.existing", "existingDefault");
        Mockito.when(databaseInterface.getExtraOptions()).thenReturn(existingOptions);
        Mockito.when(databaseInterface.getDefaultOptions()).thenReturn(newOptions);
        databaseMeta.applyDefaultOptions(databaseInterface);
        Mockito.verify(databaseInterface).addExtraOption("type1", "new", "newValue");
        Mockito.verify(databaseInterface, Mockito.never()).addExtraOption("type1", "existing", "existingDefault");
    }

    @Test
    public void testQuoteReservedWords() {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.doCallRealMethod().when(databaseMeta).quoteReservedWords(ArgumentMatchers.any(RowMetaInterface.class));
        Mockito.doCallRealMethod().when(databaseMeta).quoteField(ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(databaseMeta).setDatabaseInterface(ArgumentMatchers.any(DatabaseInterface.class));
        Mockito.doReturn("\"").when(databaseMeta).getStartQuote();
        Mockito.doReturn("\"").when(databaseMeta).getEndQuote();
        final DatabaseInterface databaseInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.doReturn(true).when(databaseInterface).isQuoteAllFields();
        databaseMeta.setDatabaseInterface(databaseInterface);
        final RowMeta fields = new RowMeta();
        for (int i = 0; i < 10; i++) {
            final ValueMetaInterface valueMeta = new ValueMetaNone(("test_" + i));
            fields.addValueMeta(valueMeta);
        }
        for (int i = 0; i < 10; i++) {
            databaseMeta.quoteReservedWords(fields);
        }
        for (int i = 0; i < 10; i++) {
            databaseMeta.quoteReservedWords(fields);
            final String name = fields.getValueMeta(i).getName();
            // check valueMeta index in list
            Assert.assertTrue(name.contains(("test_" + i)));
            // check valueMeta is found by quoted name
            Assert.assertNotNull(fields.searchValueMeta(name));
        }
    }

    @Test
    public void testModifyingName() throws Exception {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        OracleDatabaseMeta odbm = new OracleDatabaseMeta();
        Mockito.doCallRealMethod().when(databaseMeta).setDatabaseInterface(ArgumentMatchers.any(DatabaseInterface.class));
        Mockito.doCallRealMethod().when(databaseMeta).setName(ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(databaseMeta).getName();
        Mockito.doCallRealMethod().when(databaseMeta).getDisplayName();
        databaseMeta.setDatabaseInterface(odbm);
        databaseMeta.setName("test");
        List<DatabaseMeta> list = new ArrayList<DatabaseMeta>();
        list.add(databaseMeta);
        DatabaseMeta databaseMeta2 = Mockito.mock(DatabaseMeta.class);
        OracleDatabaseMeta odbm2 = new OracleDatabaseMeta();
        Mockito.doCallRealMethod().when(databaseMeta2).setDatabaseInterface(ArgumentMatchers.any(DatabaseInterface.class));
        Mockito.doCallRealMethod().when(databaseMeta2).setName(ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(databaseMeta2).getName();
        Mockito.doCallRealMethod().when(databaseMeta2).setDisplayName(ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(databaseMeta2).getDisplayName();
        Mockito.doCallRealMethod().when(databaseMeta2).verifyAndModifyDatabaseName(ArgumentMatchers.any(ArrayList.class), ArgumentMatchers.anyString());
        databaseMeta2.setDatabaseInterface(odbm2);
        databaseMeta2.setName("test");
        databaseMeta2.verifyAndModifyDatabaseName(list, null);
        Assert.assertTrue((!(databaseMeta.getDisplayName().equals(databaseMeta2.getDisplayName()))));
    }

    @Test
    public void testGetFeatureSummary() throws Exception {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        OracleDatabaseMeta odbm = new OracleDatabaseMeta();
        Mockito.doCallRealMethod().when(databaseMeta).setDatabaseInterface(ArgumentMatchers.any(DatabaseInterface.class));
        Mockito.doCallRealMethod().when(databaseMeta).getFeatureSummary();
        Mockito.doCallRealMethod().when(databaseMeta).getAttributes();
        databaseMeta.setDatabaseInterface(odbm);
        List<RowMetaAndData> result = databaseMeta.getFeatureSummary();
        Assert.assertNotNull(result);
        for (RowMetaAndData rmd : result) {
            Assert.assertEquals(2, rmd.getRowMeta().size());
            Assert.assertEquals("Parameter", rmd.getRowMeta().getValueMeta(0).getName());
            Assert.assertEquals(TYPE_STRING, rmd.getRowMeta().getValueMeta(0).getType());
            Assert.assertEquals("Value", rmd.getRowMeta().getValueMeta(1).getName());
            Assert.assertEquals(TYPE_STRING, rmd.getRowMeta().getValueMeta(1).getType());
        }
    }

    @Test
    public void indexOfName_NullArray() {
        Assert.assertEquals((-1), DatabaseMeta.indexOfName(null, ""));
    }

    @Test
    public void indexOfName_NullName() {
        Assert.assertEquals((-1), DatabaseMeta.indexOfName(new String[]{ "1" }, null));
    }

    @Test
    public void indexOfName_ExactMatch() {
        Assert.assertEquals(1, DatabaseMeta.indexOfName(new String[]{ "a", "b", "c" }, "b"));
    }

    @Test
    public void indexOfName_NonExactMatch() {
        Assert.assertEquals(1, DatabaseMeta.indexOfName(new String[]{ "a", "b", "c" }, "B"));
    }

    /**
     * Given that the {@link DatabaseInterface} object is of a new extended type.
     * <br/>
     * When {@link DatabaseMeta#getDropTableIfExistsStatement(String)} is called,
     * then the underlying new method of {@link DatabaseInterfaceExtended} should be used.
     */
    @Test
    public void shouldCallNewMethodWhenDatabaseInterfaceIsOfANewType() {
        DatabaseInterfaceExtended databaseInterfaceNew = Mockito.mock(DatabaseInterfaceExtended.class);
        databaseMeta.setDatabaseInterface(databaseInterfaceNew);
        Mockito.when(databaseInterfaceNew.getDropTableIfExistsStatement(DatabaseMetaTest.TABLE_NAME)).thenReturn(DatabaseMetaTest.DROP_STATEMENT);
        String statement = databaseMeta.getDropTableIfExistsStatement(DatabaseMetaTest.TABLE_NAME);
        Assert.assertEquals(DatabaseMetaTest.DROP_STATEMENT, statement);
    }

    /**
     * Given that the {@link DatabaseInterface} object is of an old type.
     * <br/>
     * When {@link DatabaseMeta#getDropTableIfExistsStatement(String)} is called,
     * then a fallback statement should be returned.
     */
    @Test
    public void shouldFallBackWhenDatabaseInterfaceIsOfAnOldType() {
        String statement = databaseMeta.getDropTableIfExistsStatement(DatabaseMetaTest.TABLE_NAME);
        Assert.assertEquals(DatabaseMetaTest.DROP_STATEMENT_FALLBACK, statement);
    }

    @Test
    public void databases_WithSameDbConnTypes_AreTheSame() {
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId("MSSQL");
        Assert.assertTrue(databaseMeta.databaseForBothDbInterfacesIsTheSame(mssqlServerDatabaseMeta, mssqlServerDatabaseMeta));
    }

    @Test
    public void databases_WithSameDbConnTypes_AreNotSame_IfPluginIdIsNull() {
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId(null);
        Assert.assertFalse(databaseMeta.databaseForBothDbInterfacesIsTheSame(mssqlServerDatabaseMeta, mssqlServerDatabaseMeta));
    }

    @Test
    public void databases_WithDifferentDbConnTypes_AreDifferent_IfNonOfThemIsSubsetOfAnother() {
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId("MSSQL");
        DatabaseInterface oracleDatabaseMeta = new OracleDatabaseMeta();
        oracleDatabaseMeta.setPluginId("ORACLE");
        Assert.assertFalse(databaseMeta.databaseForBothDbInterfacesIsTheSame(mssqlServerDatabaseMeta, oracleDatabaseMeta));
    }

    @Test
    public void databases_WithDifferentDbConnTypes_AreTheSame_IfOneConnTypeIsSubsetOfAnother_2LevelHierarchy() {
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId("MSSQL");
        DatabaseInterface mssqlServerNativeDatabaseMeta = new MSSQLServerNativeDatabaseMeta();
        mssqlServerNativeDatabaseMeta.setPluginId("MSSQLNATIVE");
        Assert.assertTrue(databaseMeta.databaseForBothDbInterfacesIsTheSame(mssqlServerDatabaseMeta, mssqlServerNativeDatabaseMeta));
    }

    @Test
    public void databases_WithDifferentDbConnTypes_AreTheSame_IfOneConnTypeIsSubsetOfAnother_3LevelHierarchy() {
        class MSSQLServerNativeDatabaseMetaChild extends MSSQLServerDatabaseMeta {
            @Override
            public String getPluginId() {
                return "MSSQLNATIVE_CHILD";
            }
        }
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId("MSSQL");
        DatabaseInterface mssqlServerNativeDatabaseMetaChild = new MSSQLServerNativeDatabaseMetaChild();
        Assert.assertTrue(databaseMeta.databaseForBothDbInterfacesIsTheSame(mssqlServerDatabaseMeta, mssqlServerNativeDatabaseMetaChild));
    }

    @Test
    public void testCheckParameters() {
        DatabaseMeta meta = Mockito.mock(DatabaseMeta.class);
        BaseDatabaseMeta databaseInterface = Mockito.mock(BaseDatabaseMeta.class);
        Mockito.when(databaseInterface.requiresName()).thenReturn(true);
        Mockito.when(meta.getDatabaseInterface()).thenReturn(databaseInterface);
        Mockito.when(meta.getName()).thenReturn(null);
        Mockito.when(meta.isPartitioned()).thenReturn(false);
        Mockito.when(meta.checkParameters()).thenCallRealMethod();
        Assert.assertEquals(2, meta.checkParameters().length);
    }

    @Test
    public void setSQLServerInstanceTest() {
        DatabaseMeta dbmeta = new DatabaseMeta();
        DatabaseInterface mssqlServerDatabaseMeta = new MSSQLServerDatabaseMeta();
        mssqlServerDatabaseMeta.setPluginId("MSSQL");
        DatabaseInterface mssqlServerNativeDatabaseMeta = new MSSQLServerNativeDatabaseMeta();
        mssqlServerNativeDatabaseMeta.setPluginId("MSSQLNATIVE");
        dbmeta.setDatabaseInterface(mssqlServerDatabaseMeta);
        dbmeta.setSQLServerInstance("");
        Assert.assertEquals(dbmeta.getSQLServerInstance(), null);
        dbmeta.setSQLServerInstance("instance1");
        Assert.assertEquals(dbmeta.getSQLServerInstance(), "instance1");
        dbmeta.setDatabaseInterface(mssqlServerNativeDatabaseMeta);
        dbmeta.setSQLServerInstance("");
        Assert.assertEquals(dbmeta.getSQLServerInstance(), null);
        dbmeta.setSQLServerInstance("instance1");
        Assert.assertEquals(dbmeta.getSQLServerInstance(), "instance1");
    }

    @Test
    public void testAddOptionsMysql() {
        DatabaseMeta databaseMeta = new DatabaseMeta("", "Mysql", "JDBC", null, "stub:stub", null, null, null);
        Map<String, String> options = databaseMeta.getExtraOptions();
        if (!(options.keySet().contains("MYSQL.defaultFetchSize"))) {
            Assert.fail();
        }
    }

    @Test
    public void testAddOptionsMariaDB() {
        DatabaseMeta databaseMeta = new DatabaseMeta("", "MariaDB", "JDBC", null, "stub:stub", null, null, null);
        Map<String, String> options = databaseMeta.getExtraOptions();
        if (!(options.keySet().contains("MARIADB.defaultFetchSize"))) {
            Assert.fail();
        }
    }

    @Test
    public void testAddOptionsInfobright() {
        DatabaseMeta databaseMeta = new DatabaseMeta("", "Infobright", "JDBC", null, "stub:stub", null, null, null);
        Map<String, String> options = databaseMeta.getExtraOptions();
        if (!(options.keySet().contains("INFOBRIGHT.characterEncoding"))) {
            Assert.fail();
        }
    }

    @Test
    public void testAttributesVariable() throws KettleDatabaseException {
        DatabaseMeta dbmeta = new DatabaseMeta("", "Infobright", "JDBC", null, "stub:stub", null, null, null);
        dbmeta.setVariable("someVar", "someValue");
        dbmeta.setAttributes(new Properties());
        Properties props = dbmeta.getAttributes();
        props.setProperty("EXTRA_OPTION_Infobright.additional_param", "${someVar}");
        dbmeta.getURL();
        Assert.assertTrue(dbmeta.getURL().contains("someValue"));
    }

    @Test
    public void testfindDatabase() throws KettleDatabaseException {
        List<DatabaseMeta> databases = new ArrayList<DatabaseMeta>();
        databases.add(new DatabaseMeta("  1", "Infobright", "JDBC", null, "stub:stub", null, null, null));
        databases.add(new DatabaseMeta("  1  ", "Infobright", "JDBC", null, "stub:stub", null, null, null));
        databases.add(new DatabaseMeta("1  ", "Infobright", "JDBC", null, "stub:stub", null, null, null));
        Assert.assertNotNull(DatabaseMeta.findDatabase(databases, "1"));
        Assert.assertNotNull(DatabaseMeta.findDatabase(databases, "1 "));
        Assert.assertNotNull(DatabaseMeta.findDatabase(databases, " 1"));
        Assert.assertNotNull(DatabaseMeta.findDatabase(databases, " 1 "));
    }
}

