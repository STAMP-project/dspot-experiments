/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.orm.jpa;


import Database.DB2;
import Database.DEFAULT;
import Database.DERBY;
import Database.H2;
import Database.HANA;
import Database.HSQL;
import Database.INFORMIX;
import Database.MYSQL;
import Database.ORACLE;
import Database.POSTGRESQL;
import Database.SQL_SERVER;
import org.junit.Test;


/**
 * Tests for {@link DatabaseLookup}.
 *
 * @author Edd? Mel?ndez
 * @author Phillip Webb
 */
public class DatabaseLookupTests {
    @Test
    public void getDatabaseWhenDataSourceIsNullShouldReturnDefault() {
        assertThat(DatabaseLookup.getDatabase(null)).isEqualTo(DEFAULT);
    }

    @Test
    public void getDatabaseWhenDataSourceIsUnknownShouldReturnDefault() throws Exception {
        testGetDatabase("jdbc:idontexist:", DEFAULT);
    }

    @Test
    public void getDatabaseWhenDerbyShouldReturnDerby() throws Exception {
        testGetDatabase("jdbc:derby:", DERBY);
    }

    @Test
    public void getDatabaseWhenH2ShouldReturnH2() throws Exception {
        testGetDatabase("jdbc:h2:", H2);
    }

    @Test
    public void getDatabaseWhenHsqldbShouldReturnHsqldb() throws Exception {
        testGetDatabase("jdbc:hsqldb:", HSQL);
    }

    @Test
    public void getDatabaseWhenMysqlShouldReturnMysql() throws Exception {
        testGetDatabase("jdbc:mysql:", MYSQL);
    }

    @Test
    public void getDatabaseWhenOracleShouldReturnOracle() throws Exception {
        testGetDatabase("jdbc:oracle:", ORACLE);
    }

    @Test
    public void getDatabaseWhenPostgresShouldReturnPostgres() throws Exception {
        testGetDatabase("jdbc:postgresql:", POSTGRESQL);
    }

    @Test
    public void getDatabaseWhenSqlserverShouldReturnSqlserver() throws Exception {
        testGetDatabase("jdbc:sqlserver:", SQL_SERVER);
    }

    @Test
    public void getDatabaseWhenDb2ShouldReturnDb2() throws Exception {
        testGetDatabase("jdbc:db2:", DB2);
    }

    @Test
    public void getDatabaseWhenInformixShouldReturnInformix() throws Exception {
        testGetDatabase("jdbc:informix-sqli:", INFORMIX);
    }

    @Test
    public void getDatabaseWhenSapShouldReturnHana() throws Exception {
        testGetDatabase("jdbc:sap:", HANA);
    }
}

