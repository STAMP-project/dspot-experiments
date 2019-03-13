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
package org.pentaho.di.core.database;


import org.junit.Assert;
import org.junit.Test;


public class SequenceMetaTest {
    @Test
    public void testSupport() {
        DatabaseInterface[] support = new DatabaseInterface[]{ new AS400DatabaseMeta(), new DB2DatabaseMeta(), new GreenplumDatabaseMeta(), new HypersonicDatabaseMeta(), new KingbaseESDatabaseMeta(), new MonetDBDatabaseMeta(), new MSSQLServerDatabaseMeta(), new MSSQLServerNativeDatabaseMeta(), new NetezzaDatabaseMeta(), new OracleDatabaseMeta(), new OracleRDBDatabaseMeta(), new PostgreSQLDatabaseMeta(), new RedshiftDatabaseMeta(), new VerticaDatabaseMeta(), new Vertica5DatabaseMeta() };
        DatabaseInterface[] doNotSupport = new DatabaseInterface[]{ new CacheDatabaseMeta(), new DbaseDatabaseMeta(), new DerbyDatabaseMeta(), new Exasol4DatabaseMeta(), new ExtenDBDatabaseMeta(), new ExtenDBDatabaseMeta(), new FirebirdDatabaseMeta(), new GenericDatabaseMeta(), new GuptaDatabaseMeta(), new H2DatabaseMeta(), new InfiniDbDatabaseMeta(), new InfobrightDatabaseMeta(), new InformixDatabaseMeta(), new IngresDatabaseMeta(), new InterbaseDatabaseMeta(), new LucidDBDatabaseMeta(), new MondrianNativeDatabaseMeta(), new MSAccessDatabaseMeta(), new MySQLDatabaseMeta(), new MariaDBDatabaseMeta(), new NeoviewDatabaseMeta(), new RemedyActionRequestSystemDatabaseMeta(), new SAPDBDatabaseMeta(), new SQLiteDatabaseMeta(), new SybaseDatabaseMeta(), new SybaseIQDatabaseMeta(), new TeradataDatabaseMeta(), new UniVerseDatabaseMeta(), new VectorWiseDatabaseMeta() };
        for (DatabaseInterface db : support) {
            SequenceMetaTest.assertSupports(db, true);
        }
        for (DatabaseInterface db : doNotSupport) {
            SequenceMetaTest.assertSupports(db, false);
        }
    }

    @Test
    public void testSQL() {
        DatabaseInterface databaseInterface;
        final String sequenceName = "sequence_name";
        databaseInterface = new OracleDatabaseMeta();
        Assert.assertEquals("SELECT sequence_name.nextval FROM dual", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT sequence_name.currval FROM DUAL", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new OracleRDBDatabaseMeta();
        Assert.assertEquals("SELECT sequence_name.nextval FROM dual", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT sequence_name.currval FROM DUAL", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new VerticaDatabaseMeta();
        Assert.assertEquals("SELECT nextval('sequence_name')", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT currval('sequence_name')", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new PostgreSQLDatabaseMeta();
        Assert.assertEquals("SELECT nextval('sequence_name')", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT currval('sequence_name')", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        Assert.assertEquals("SELECT relname AS sequence_name FROM pg_catalog.pg_statio_all_sequences", databaseInterface.getSQLListOfSequences());
        Assert.assertEquals("SELECT relname AS sequence_name FROM pg_catalog.pg_statio_all_sequences WHERE relname = 'sequence_name'", databaseInterface.getSQLSequenceExists(sequenceName));
        databaseInterface = new GreenplumDatabaseMeta();
        Assert.assertEquals("SELECT nextval('sequence_name')", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT currval('sequence_name')", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        Assert.assertEquals("SELECT relname AS sequence_name FROM pg_catalog.pg_statio_all_sequences", databaseInterface.getSQLListOfSequences());
        Assert.assertEquals("SELECT relname AS sequence_name FROM pg_catalog.pg_statio_all_sequences WHERE relname = 'sequence_name'", databaseInterface.getSQLSequenceExists(sequenceName));
        databaseInterface = new AS400DatabaseMeta();
        Assert.assertEquals("SELECT NEXT VALUE FOR sequence_name FROM SYSIBM.SYSDUMMY1", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT PREVIOUS VALUE FOR sequence_name FROM SYSIBM.SYSDUMMY1", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new DB2DatabaseMeta();
        Assert.assertEquals("SELECT NEXT VALUE FOR sequence_name FROM SYSIBM.SYSDUMMY1", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT PREVIOUS VALUE FOR sequence_name FROM SYSIBM.SYSDUMMY1", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new HypersonicDatabaseMeta();
        Assert.assertEquals(("SELECT NEXT VALUE FOR sequence_name " + "FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES WHERE SEQUENCE_NAME = 'sequence_name'"), databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals(("SELECT sequence_name.currval " + "FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES WHERE SEQUENCE_NAME = 'sequence_name'"), databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new KingbaseESDatabaseMeta();
        Assert.assertEquals("SELECT nextval('sequence_name')", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT currval('sequence_name')", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new MonetDBDatabaseMeta();
        Assert.assertEquals("SELECT next_value_for( 'sys', 'sequence_name' )", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT get_value_for( 'sys', 'sequence_name' )", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        Assert.assertEquals("SELECT name FROM sys.sequences", databaseInterface.getSQLListOfSequences());
        Assert.assertEquals("SELECT * FROM sys.sequences WHERE name = 'sequence_name'", databaseInterface.getSQLSequenceExists(sequenceName));
        databaseInterface = new MSSQLServerDatabaseMeta();
        Assert.assertEquals("SELECT NEXT VALUE FOR sequence_name", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT current_value FROM sys.sequences WHERE name = 'sequence_name'", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        Assert.assertEquals("SELECT name FROM sys.sequences", databaseInterface.getSQLListOfSequences());
        Assert.assertEquals("SELECT 1 FROM sys.sequences WHERE name = 'sequence_name'", databaseInterface.getSQLSequenceExists(sequenceName));
        databaseInterface = new MSSQLServerNativeDatabaseMeta();
        Assert.assertEquals("SELECT NEXT VALUE FOR sequence_name", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("SELECT current_value FROM sys.sequences WHERE name = 'sequence_name'", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        Assert.assertEquals("SELECT name FROM sys.sequences", databaseInterface.getSQLListOfSequences());
        Assert.assertEquals("SELECT 1 FROM sys.sequences WHERE name = 'sequence_name'", databaseInterface.getSQLSequenceExists(sequenceName));
        databaseInterface = new NetezzaDatabaseMeta();
        Assert.assertEquals("select next value for sequence_name", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("select last_value from sequence_name", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        // the rest of the database metas say they don't support sequences
        databaseInterface = new MySQLDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new InfiniDbDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new InfobrightDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new DbaseDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new DerbyDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new ExtenDBDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new FirebirdDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new GenericDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new GuptaDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new H2DatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new InformixDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new IngresDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new InterbaseDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new LucidDBDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new MSAccessDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new NeoviewDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new RemedyActionRequestSystemDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new SAPDBDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new SQLiteDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new SybaseDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new SybaseIQDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new TeradataDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
        databaseInterface = new UniVerseDatabaseMeta();
        Assert.assertEquals("", databaseInterface.getSQLNextSequenceValue(sequenceName));
        Assert.assertEquals("", databaseInterface.getSQLCurrentSequenceValue(sequenceName));
    }
}

