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


import ConnectionPoolUtil.defaultInitialNrOfConnections;
import ConnectionPoolUtil.defaultMaximumNrOfConnections;
import DatabaseMeta.CLOB_LENGTH;
import DatabaseMeta.TYPE_ACCESS_JNDI;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;
import org.pentaho.di.repository.LongObjectId;


public class BaseDatabaseMetaTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    BaseDatabaseMeta nativeMeta;

    BaseDatabaseMeta odbcMeta;

    BaseDatabaseMeta jndiMeta;

    @Test
    public void testShowIsTreatedAsAResultsQuery() throws Exception {
        List<SqlScriptStatement> sqlScriptStatements = new H2DatabaseMeta().getSqlScriptStatements("show annotations from service");
        Assert.assertTrue(sqlScriptStatements.get(0).isQuery());
    }

    @Test
    public void testDefaultSettings() throws Exception {
        // Note - this method should only use native or odbc.
        // The jndi meta is used for mutations of the meta, and it would
        // not be threadsafe in a multi-threaded testing environment
        // (each test run in its own thread).
        Assert.assertEquals((-1), nativeMeta.getDefaultDatabasePort());
        Assert.assertTrue(nativeMeta.supportsSetCharacterStream());
        Assert.assertTrue(nativeMeta.supportsAutoInc());
        Assert.assertEquals("", nativeMeta.getLimitClause(5));
        Assert.assertEquals(0, nativeMeta.getNotFoundTK(true));
        Assert.assertEquals("", nativeMeta.getSQLNextSequenceValue("FOO"));
        Assert.assertEquals("", nativeMeta.getSQLCurrentSequenceValue("FOO"));
        Assert.assertEquals("", nativeMeta.getSQLSequenceExists("FOO"));
        Assert.assertTrue(nativeMeta.isFetchSizeSupported());
        Assert.assertFalse(nativeMeta.needsPlaceHolder());
        Assert.assertTrue(nativeMeta.supportsSchemas());
        Assert.assertTrue(nativeMeta.supportsCatalogs());
        Assert.assertTrue(nativeMeta.supportsEmptyTransactions());
        Assert.assertEquals("SUM", nativeMeta.getFunctionSum());
        Assert.assertEquals("AVG", nativeMeta.getFunctionAverage());
        Assert.assertEquals("MIN", nativeMeta.getFunctionMinimum());
        Assert.assertEquals("MAX", nativeMeta.getFunctionMaximum());
        Assert.assertEquals("COUNT", nativeMeta.getFunctionCount());
        Assert.assertEquals("\"", nativeMeta.getStartQuote());
        Assert.assertEquals("\"", nativeMeta.getEndQuote());
        Assert.assertEquals("FOO.BAR", nativeMeta.getSchemaTableCombination("FOO", "BAR"));
        Assert.assertEquals(CLOB_LENGTH, nativeMeta.getMaxTextFieldLength());
        Assert.assertEquals(CLOB_LENGTH, nativeMeta.getMaxVARCHARLength());
        Assert.assertTrue(nativeMeta.supportsTransactions());
        Assert.assertFalse(nativeMeta.supportsSequences());
        Assert.assertTrue(nativeMeta.supportsBitmapIndex());
        Assert.assertTrue(nativeMeta.supportsSetLong());
        Assert.assertArrayEquals(new String[]{  }, nativeMeta.getReservedWords());
        Assert.assertTrue(nativeMeta.quoteReservedWords());
        Assert.assertFalse(nativeMeta.supportsRepository());
        Assert.assertArrayEquals(new String[]{ "TABLE" }, nativeMeta.getTableTypes());
        Assert.assertArrayEquals(new String[]{ "VIEW" }, nativeMeta.getViewTypes());
        Assert.assertArrayEquals(new String[]{ "SYNONYM" }, nativeMeta.getSynonymTypes());
        Assert.assertFalse(nativeMeta.useSchemaNameForTableList());
        Assert.assertTrue(nativeMeta.supportsViews());
        Assert.assertFalse(nativeMeta.supportsSynonyms());
        Assert.assertNull(nativeMeta.getSQLListOfProcedures());
        Assert.assertNull(nativeMeta.getSQLListOfSequences());
        Assert.assertTrue(nativeMeta.supportsFloatRoundingOnUpdate());
        Assert.assertNull(nativeMeta.getSQLLockTables(new String[]{ "FOO" }));
        Assert.assertNull(nativeMeta.getSQLUnlockTables(new String[]{ "FOO" }));
        Assert.assertTrue(nativeMeta.supportsTimeStampToDateConversion());
        Assert.assertTrue(nativeMeta.supportsBatchUpdates());
        Assert.assertFalse(nativeMeta.supportsBooleanDataType());
        Assert.assertFalse(nativeMeta.supportsTimestampDataType());
        Assert.assertTrue(nativeMeta.preserveReservedCase());
        Assert.assertTrue(nativeMeta.isDefaultingToUppercase());
        Map<String, String> emptyMap = new HashMap<String, String>();
        Assert.assertEquals(emptyMap, nativeMeta.getExtraOptions());
        Assert.assertEquals(";", nativeMeta.getExtraOptionSeparator());
        Assert.assertEquals("=", nativeMeta.getExtraOptionValueSeparator());
        Assert.assertEquals(";", nativeMeta.getExtraOptionIndicator());
        Assert.assertTrue(nativeMeta.supportsOptionsInURL());
        Assert.assertNull(nativeMeta.getExtraOptionsHelpText());
        Assert.assertTrue(nativeMeta.supportsGetBlob());
        Assert.assertNull(nativeMeta.getConnectSQL());
        Assert.assertTrue(nativeMeta.supportsSetMaxRows());
        Assert.assertFalse(nativeMeta.isUsingConnectionPool());
        Assert.assertEquals(defaultMaximumNrOfConnections, nativeMeta.getMaximumPoolSize());
        Assert.assertEquals(defaultInitialNrOfConnections, nativeMeta.getInitialPoolSize());
        Assert.assertFalse(nativeMeta.isPartitioned());
        Assert.assertArrayEquals(new PartitionDatabaseMeta[0], nativeMeta.getPartitioningInformation());
        Properties emptyProps = new Properties();
        Assert.assertEquals(emptyProps, nativeMeta.getConnectionPoolingProperties());
        Assert.assertTrue(nativeMeta.needsToLockAllTables());
        Assert.assertTrue(nativeMeta.isStreamingResults());
        Assert.assertFalse(nativeMeta.isQuoteAllFields());
        Assert.assertFalse(nativeMeta.isForcingIdentifiersToLowerCase());
        Assert.assertFalse(nativeMeta.isForcingIdentifiersToUpperCase());
        Assert.assertFalse(nativeMeta.isUsingDoubleDecimalAsSchemaTableSeparator());
        Assert.assertTrue(nativeMeta.isRequiringTransactionsOnQueries());
        Assert.assertEquals("org.pentaho.di.core.database.DatabaseFactory", nativeMeta.getDatabaseFactoryName());
        Assert.assertNull(nativeMeta.getPreferredSchemaName());
        Assert.assertFalse(nativeMeta.supportsSequenceNoMaxValueOption());
        Assert.assertFalse(nativeMeta.requiresCreateTablePrimaryKeyAppend());
        Assert.assertFalse(nativeMeta.requiresCastToVariousForIsNull());
        Assert.assertFalse(nativeMeta.isDisplaySizeTwiceThePrecision());
        Assert.assertTrue(nativeMeta.supportsPreparedStatementMetadataRetrieval());
        Assert.assertFalse(nativeMeta.supportsResultSetMetadataRetrievalOnly());
        Assert.assertFalse(nativeMeta.isSystemTable("FOO"));
        Assert.assertTrue(nativeMeta.supportsNewLinesInSQL());
        Assert.assertNull(nativeMeta.getSQLListOfSchemas());
        Assert.assertEquals(0, nativeMeta.getMaxColumnsInIndex());
        Assert.assertTrue(nativeMeta.supportsErrorHandlingOnBatchUpdates());
        Assert.assertTrue(nativeMeta.isExplorable());
        Assert.assertNull(nativeMeta.getXulOverlayFile());
        Assert.assertTrue(nativeMeta.onlySpaces("   \t   \n  \r   "));
        Assert.assertFalse(nativeMeta.isMySQLVariant());
        Assert.assertTrue(nativeMeta.canTest());
        Assert.assertTrue(nativeMeta.requiresName());
        Assert.assertTrue(nativeMeta.releaseSavepoint());
        Variables v = new Variables();
        v.setVariable("FOOVARIABLE", "FOOVALUE");
        DatabaseMeta dm = new DatabaseMeta();
        dm.setDatabaseInterface(nativeMeta);
        Assert.assertEquals("", nativeMeta.getDataTablespaceDDL(v, dm));
        Assert.assertEquals("", nativeMeta.getIndexTablespaceDDL(v, dm));
        Assert.assertFalse(nativeMeta.useSafePoints());
        Assert.assertTrue(nativeMeta.supportsErrorHandling());
        Assert.assertEquals("'DATA'", nativeMeta.getSQLValue(new ValueMetaString("FOO"), "DATA", null));
        Assert.assertEquals("'15'", nativeMeta.getSQLValue(new ValueMetaString("FOO"), "15", null));
        Assert.assertEquals("_", nativeMeta.getFieldnameProtector());
        Assert.assertEquals("_1ABC_123", nativeMeta.getSafeFieldname("1ABC 123"));
        BaseDatabaseMeta tmpSC = new ConcreteBaseDatabaseMeta() {
            @Override
            public String[] getReservedWords() {
                return new String[]{ "SELECT" };
            }
        };
        Assert.assertEquals("SELECT_", tmpSC.getSafeFieldname("SELECT"));
        Assert.assertEquals("NOMAXVALUE", nativeMeta.getSequenceNoMaxValueOption());
        Assert.assertTrue(nativeMeta.supportsAutoGeneratedKeys());
        Assert.assertNull(nativeMeta.customizeValueFromSQLType(new ValueMetaString("FOO"), null, 0));
        Assert.assertTrue(nativeMeta.fullExceptionLog(new RuntimeException("xxxx")));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecatedItems() throws Exception {
        Assert.assertEquals("'2016-08-11'", nativeMeta.getSQLValue(new ValueMetaDate("FOO"), new Date(116, 7, 11), "YYYY-MM-dd"));
        Assert.assertEquals("\"FOO\".\"BAR\"", nativeMeta.getBackwardsCompatibleSchemaTableCombination("FOO", "BAR"));
        Assert.assertEquals("\"null\".\"BAR\"", nativeMeta.getBackwardsCompatibleSchemaTableCombination(null, "BAR"));// not sure this is right ...

        Assert.assertEquals("FOO\".\"BAR\"", nativeMeta.getBackwardsCompatibleSchemaTableCombination("FOO\"", "BAR"));
        Assert.assertEquals("FOO\".BAR\"", nativeMeta.getBackwardsCompatibleSchemaTableCombination("FOO\"", "BAR\""));
        Assert.assertEquals("\"FOO\"", nativeMeta.getBackwardsCompatibleTable("FOO"));
        Assert.assertEquals("\"null\"", nativeMeta.getBackwardsCompatibleTable(null));// not sure this should happen but it does

        Assert.assertEquals("FOO\"", nativeMeta.getBackwardsCompatibleTable("FOO\""));
        Assert.assertEquals("\"FOO", nativeMeta.getBackwardsCompatibleTable("\"FOO"));
    }

    @Test
    public void testDefaultSQLStatements() {
        // Note - this method should use only native or odbc metas.
        // Use of the jndi meta here could create a race condition
        // when test cases are run by multiple threads
        String lineSep = System.getProperty("line.separator");
        String expected = "ALTER TABLE FOO DROP BAR" + lineSep;
        Assert.assertEquals(expected, odbcMeta.getDropColumnStatement("FOO", new ValueMetaString("BAR"), "", false, "", false));
        Assert.assertEquals("TRUNCATE TABLE FOO", odbcMeta.getTruncateTableStatement("FOO"));
        Assert.assertEquals("SELECT * FROM FOO", odbcMeta.getSQLQueryFields("FOO"));
        Assert.assertEquals("SELECT 1 FROM FOO", odbcMeta.getSQLTableExists("FOO"));
        Assert.assertEquals("SELECT FOO FROM BAR", odbcMeta.getSQLColumnExists("FOO", "BAR"));
        Assert.assertEquals("insert into \"FOO\".\"BAR\"(KEYFIELD, VERSIONFIELD) values (0, 1)", nativeMeta.getSQLInsertAutoIncUnknownDimensionRow("\"FOO\".\"BAR\"", "KEYFIELD", "VERSIONFIELD"));
        Assert.assertEquals("select count(*) FROM FOO", nativeMeta.getSelectCountStatement("FOO"));
        Assert.assertEquals("COL9", nativeMeta.generateColumnAlias(9, "FOO"));
        Assert.assertEquals("[SELECT 1, INSERT INTO FOO VALUES(BAR), DELETE FROM BAR]", nativeMeta.parseStatements("SELECT 1;INSERT INTO FOO VALUES(BAR);DELETE FROM BAR").toString());
        Assert.assertEquals("CREATE TABLE ", nativeMeta.getCreateTableStatement());
        Assert.assertEquals("DROP TABLE IF EXISTS FOO", nativeMeta.getDropTableIfExistsStatement("FOO"));
    }

    @Test
    public void testGettersSetters() {
        // Note - this method should *ONLY* use the jndi meta and not the odbc or native ones.
        // This is the only method in this test class that mutates the meta.
        jndiMeta.setUsername("FOO");
        Assert.assertEquals("FOO", jndiMeta.getUsername());
        jndiMeta.setPassword("BAR");
        Assert.assertEquals("BAR", jndiMeta.getPassword());
        jndiMeta.setAccessType(TYPE_ACCESS_JNDI);
        Assert.assertEquals("", jndiMeta.getUsername());
        Assert.assertEquals("", jndiMeta.getPassword());
        Assert.assertFalse(jndiMeta.isChanged());
        jndiMeta.setChanged(true);
        Assert.assertTrue(jndiMeta.isChanged());
        jndiMeta.setName("FOO");
        Assert.assertEquals("FOO", jndiMeta.getName());
        Assert.assertEquals("FOO", jndiMeta.getDisplayName());
        jndiMeta.setName(null);
        Assert.assertNull(jndiMeta.getName());
        Assert.assertEquals("FOO", jndiMeta.getDisplayName());
        jndiMeta.setDisplayName(null);
        Assert.assertNull(jndiMeta.getDisplayName());
        jndiMeta.setDatabaseName("FOO");
        Assert.assertEquals("FOO", jndiMeta.getDatabaseName());
        Assert.assertEquals("-1", jndiMeta.getDatabasePortNumberString());
        jndiMeta.setDatabasePortNumberString("9876");
        Assert.assertEquals("9876", jndiMeta.getDatabasePortNumberString());
        jndiMeta.setDatabasePortNumberString(null);
        Assert.assertEquals("9876", jndiMeta.getDatabasePortNumberString());// not sure I agree with this behavior

        jndiMeta.setHostname("FOO");
        Assert.assertEquals("FOO", jndiMeta.getHostname());
        LongObjectId id = new LongObjectId(9876);
        jndiMeta.setObjectId(id);
        Assert.assertEquals(id, jndiMeta.getObjectId());
        jndiMeta.setServername("FOO");
        Assert.assertEquals("FOO", jndiMeta.getServername());
        jndiMeta.setDataTablespace("FOO");
        Assert.assertEquals("FOO", jndiMeta.getDataTablespace());
        jndiMeta.setIndexTablespace("FOO");
        Assert.assertEquals("FOO", jndiMeta.getIndexTablespace());
        Properties attrs = jndiMeta.getAttributes();
        Properties testAttrs = new Properties();
        testAttrs.setProperty("FOO", "BAR");
        jndiMeta.setAttributes(testAttrs);
        Assert.assertEquals(testAttrs, jndiMeta.getAttributes());
        jndiMeta.setAttributes(attrs);// reset attributes back to what they were...

        jndiMeta.setSupportsBooleanDataType(true);
        Assert.assertTrue(jndiMeta.supportsBooleanDataType());
        jndiMeta.setSupportsTimestampDataType(true);
        Assert.assertTrue(jndiMeta.supportsTimestampDataType());
        jndiMeta.setPreserveReservedCase(false);
        Assert.assertFalse(jndiMeta.preserveReservedCase());
        jndiMeta.addExtraOption("JNDI", "FOO", "BAR");
        Map<String, String> expectedOptionsMap = new HashMap<String, String>();
        expectedOptionsMap.put("JNDI.FOO", "BAR");
        Assert.assertEquals(expectedOptionsMap, jndiMeta.getExtraOptions());
        jndiMeta.setConnectSQL("SELECT COUNT(*) FROM FOO");
        Assert.assertEquals("SELECT COUNT(*) FROM FOO", jndiMeta.getConnectSQL());
        jndiMeta.setUsingConnectionPool(true);
        Assert.assertTrue(jndiMeta.isUsingConnectionPool());
        jndiMeta.setMaximumPoolSize(15);
        Assert.assertEquals(15, jndiMeta.getMaximumPoolSize());
        jndiMeta.setInitialPoolSize(5);
        Assert.assertEquals(5, jndiMeta.getInitialPoolSize());
        jndiMeta.setPartitioned(true);
        Assert.assertTrue(jndiMeta.isPartitioned());
        PartitionDatabaseMeta[] clusterInfo = new PartitionDatabaseMeta[1];
        PartitionDatabaseMeta aClusterDef = new PartitionDatabaseMeta("FOO", "BAR", "WIBBLE", "NATTIE");
        aClusterDef.setUsername("FOOUSER");
        aClusterDef.setPassword("BARPASSWORD");
        clusterInfo[0] = aClusterDef;
        jndiMeta.setPartitioningInformation(clusterInfo);
        PartitionDatabaseMeta[] gotPartitions = jndiMeta.getPartitioningInformation();
        // MB: Can't use arrayEquals because the PartitionDatabaseMeta doesn't have a toString. :(
        // assertArrayEquals( clusterInfo, gotPartitions );
        Assert.assertTrue((gotPartitions != null));
        if (gotPartitions != null) {
            Assert.assertEquals(1, gotPartitions.length);
            PartitionDatabaseMeta compareWith = gotPartitions[0];
            // MB: Can't use x.equals(y) because PartitionDatabaseMeta doesn't override equals... :(
            Assert.assertEquals(aClusterDef.getClass(), compareWith.getClass());
            Assert.assertEquals(aClusterDef.getDatabaseName(), compareWith.getDatabaseName());
            Assert.assertEquals(aClusterDef.getHostname(), compareWith.getHostname());
            Assert.assertEquals(aClusterDef.getPartitionId(), compareWith.getPartitionId());
            Assert.assertEquals(aClusterDef.getPassword(), compareWith.getPassword());
            Assert.assertEquals(aClusterDef.getPort(), compareWith.getPort());
            Assert.assertEquals(aClusterDef.getUsername(), compareWith.getUsername());
        }
        Properties poolProperties = new Properties();
        poolProperties.put("FOO", "BAR");
        poolProperties.put("BAR", "FOO");
        poolProperties.put("ZZZZZZZZZZZZZZ", "Z.Z.Z.Z.Z.Z.Z.Z.a.a.a.a.a.a.a.a.a");
        poolProperties.put("TOM", "JANE");
        poolProperties.put("AAAAAAAAAAAAA", "BBBBB.BBB.BBBBBBB.BBBBBBBB.BBBBBBBBBBBBBB");
        jndiMeta.setConnectionPoolingProperties(poolProperties);
        Properties compareWithProps = jndiMeta.getConnectionPoolingProperties();
        Assert.assertEquals(poolProperties, compareWithProps);
        jndiMeta.setStreamingResults(false);
        Assert.assertFalse(jndiMeta.isStreamingResults());
        jndiMeta.setQuoteAllFields(true);
        jndiMeta.setForcingIdentifiersToLowerCase(true);
        jndiMeta.setForcingIdentifiersToUpperCase(true);
        Assert.assertTrue(jndiMeta.isQuoteAllFields());
        Assert.assertTrue(jndiMeta.isForcingIdentifiersToLowerCase());
        Assert.assertTrue(jndiMeta.isForcingIdentifiersToUpperCase());
        jndiMeta.setUsingDoubleDecimalAsSchemaTableSeparator(true);
        Assert.assertTrue(jndiMeta.isUsingDoubleDecimalAsSchemaTableSeparator());
        jndiMeta.setPreferredSchemaName("FOO");
        Assert.assertEquals("FOO", jndiMeta.getPreferredSchemaName());
    }

    private int rowCnt = 0;

    @Test
    public void testCheckIndexExists() throws Exception {
        Database db = Mockito.mock(Database.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        DatabaseMetaData dmd = Mockito.mock(DatabaseMetaData.class);
        DatabaseMeta dm = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dm.getQuotedSchemaTableCombination("", "FOO")).thenReturn("FOO");
        Mockito.when(rs.next()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                (rowCnt)++;
                return new Boolean(((rowCnt) < 3));
            }
        });
        Mockito.when(db.getDatabaseMetaData()).thenReturn(dmd);
        Mockito.when(dmd.getIndexInfo(null, null, "FOO", false, true)).thenReturn(rs);
        Mockito.when(rs.getString("COLUMN_NAME")).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if ((rowCnt) == 1) {
                    return "ROW1COL2";
                } else
                    if ((rowCnt) == 2) {
                        return "ROW2COL2";
                    } else {
                        return null;
                    }

            }
        });
        Mockito.when(db.getDatabaseMeta()).thenReturn(dm);
        Assert.assertTrue(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "ROW1COL2", "ROW2COL2" }));
        Assert.assertFalse(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "ROW2COL2", "NOTTHERE" }));
        Assert.assertFalse(odbcMeta.checkIndexExists(db, "", "FOO", new String[]{ "NOTTHERE", "ROW1COL2" }));
    }
}

