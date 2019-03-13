/**
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.support;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.support.MetaDataAccessException;


/**
 *
 *
 * @author Lucas Ward
 * @author Will Schipp
 */
public class DatabaseTypeTests {
    @Test
    public void testFromProductName() {
        Assert.assertEquals(DatabaseType.DERBY, DatabaseType.fromProductName("Apache Derby"));
        Assert.assertEquals(DatabaseType.DB2, DatabaseType.fromProductName("DB2"));
        Assert.assertEquals(DatabaseType.DB2VSE, DatabaseType.fromProductName("DB2VSE"));
        Assert.assertEquals(DatabaseType.DB2ZOS, DatabaseType.fromProductName("DB2ZOS"));
        Assert.assertEquals(DatabaseType.DB2AS400, DatabaseType.fromProductName("DB2AS400"));
        Assert.assertEquals(DatabaseType.HSQL, DatabaseType.fromProductName("HSQL Database Engine"));
        Assert.assertEquals(DatabaseType.SQLSERVER, DatabaseType.fromProductName("Microsoft SQL Server"));
        Assert.assertEquals(DatabaseType.MYSQL, DatabaseType.fromProductName("MySQL"));
        Assert.assertEquals(DatabaseType.ORACLE, DatabaseType.fromProductName("Oracle"));
        Assert.assertEquals(DatabaseType.POSTGRES, DatabaseType.fromProductName("PostgreSQL"));
        Assert.assertEquals(DatabaseType.SYBASE, DatabaseType.fromProductName("Sybase"));
        Assert.assertEquals(DatabaseType.SQLITE, DatabaseType.fromProductName("SQLite"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProductName() {
        DatabaseType.fromProductName("bad product name");
    }

    @Test
    public void testFromMetaDataForDerby() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("Apache Derby");
        Assert.assertEquals(DatabaseType.DERBY, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForDB2() throws Exception {
        DataSource oldDs = DatabaseTypeTestUtils.getMockDataSource("DB2/Linux", "SQL0901");
        Assert.assertEquals(DatabaseType.DB2, DatabaseType.fromMetaData(oldDs));
        DataSource newDs = DatabaseTypeTestUtils.getMockDataSource("DB2/NT", "SQL0901");
        Assert.assertEquals(DatabaseType.DB2, DatabaseType.fromMetaData(newDs));
    }

    @Test
    public void testFromMetaDataForDB2VSE() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("DB2 for DB2 for z/OS VUE", "ARI08015");
        Assert.assertEquals(DatabaseType.DB2VSE, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForDB2ZOS() throws Exception {
        DataSource oldDs = DatabaseTypeTestUtils.getMockDataSource("DB2", "DSN08015");
        Assert.assertEquals(DatabaseType.DB2ZOS, DatabaseType.fromMetaData(oldDs));
        DataSource newDs = DatabaseTypeTestUtils.getMockDataSource("DB2 for DB2 UDB for z/OS", "DSN08015");
        Assert.assertEquals(DatabaseType.DB2ZOS, DatabaseType.fromMetaData(newDs));
    }

    @Test
    public void testFromMetaDataForDB2AS400() throws Exception {
        DataSource toolboxDs = DatabaseTypeTestUtils.getMockDataSource("DB2 UDB for AS/400", "07.01.0000 V7R1m0");
        Assert.assertEquals(DatabaseType.DB2AS400, DatabaseType.fromMetaData(toolboxDs));
        DataSource nativeDs = DatabaseTypeTestUtils.getMockDataSource("DB2 UDB for AS/400", "V7R1M0");
        Assert.assertEquals(DatabaseType.DB2AS400, DatabaseType.fromMetaData(nativeDs));
        DataSource prdidDs = DatabaseTypeTestUtils.getMockDataSource("DB2 UDB for AS/400", "QSQ07010");
        Assert.assertEquals(DatabaseType.DB2AS400, DatabaseType.fromMetaData(prdidDs));
    }

    @Test
    public void testFromMetaDataForHsql() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("HSQL Database Engine");
        Assert.assertEquals(DatabaseType.HSQL, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForSqlServer() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("Microsoft SQL Server");
        Assert.assertEquals(DatabaseType.SQLSERVER, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForMySql() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("MySQL");
        Assert.assertEquals(DatabaseType.MYSQL, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForOracle() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("Oracle");
        Assert.assertEquals(DatabaseType.ORACLE, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForPostgres() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("PostgreSQL");
        Assert.assertEquals(DatabaseType.POSTGRES, DatabaseType.fromMetaData(ds));
    }

    @Test
    public void testFromMetaDataForSybase() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource("Adaptive Server Enterprise");
        Assert.assertEquals(DatabaseType.SYBASE, DatabaseType.fromMetaData(ds));
    }

    @Test(expected = MetaDataAccessException.class)
    public void testBadMetaData() throws Exception {
        DataSource ds = DatabaseTypeTestUtils.getMockDataSource(new MetaDataAccessException("Bad!"));
        Assert.assertEquals(DatabaseType.SYBASE, DatabaseType.fromMetaData(ds));
    }
}

