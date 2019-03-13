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
package org.springframework.boot.autoconfigure.jooq;


import SQLDialect.DEFAULT;
import SQLDialect.DERBY;
import SQLDialect.H2;
import SQLDialect.HSQLDB;
import SQLDialect.MYSQL;
import SQLDialect.POSTGRES;
import org.junit.Test;


/**
 * Tests for {@link SqlDialectLookup}.
 *
 * @author Michael Simons
 * @author Stephane Nicoll
 */
public class SqlDialectLookupTests {
    @Test
    public void getSqlDialectWhenDataSourceIsNullShouldReturnDefault() {
        assertThat(SqlDialectLookup.getDialect(null)).isEqualTo(DEFAULT);
    }

    @Test
    public void getSqlDialectWhenDataSourceIsUnknownShouldReturnDefault() throws Exception {
        testGetSqlDialect("jdbc:idontexist:", DEFAULT);
    }

    @Test
    public void getSqlDialectWhenDerbyShouldReturnDerby() throws Exception {
        testGetSqlDialect("jdbc:derby:", DERBY);
    }

    @Test
    public void getSqlDialectWhenH2ShouldReturnH2() throws Exception {
        testGetSqlDialect("jdbc:h2:", H2);
    }

    @Test
    public void getSqlDialectWhenHsqldbShouldReturnHsqldb() throws Exception {
        testGetSqlDialect("jdbc:hsqldb:", HSQLDB);
    }

    @Test
    public void getSqlDialectWhenMysqlShouldReturnMysql() throws Exception {
        testGetSqlDialect("jdbc:mysql:", MYSQL);
    }

    @Test
    public void getSqlDialectWhenOracleShouldReturnDefault() throws Exception {
        testGetSqlDialect("jdbc:oracle:", DEFAULT);
    }

    @Test
    public void getSqlDialectWhenPostgresShouldReturnPostgres() throws Exception {
        testGetSqlDialect("jdbc:postgresql:", POSTGRES);
    }

    @Test
    public void getSqlDialectWhenSqlserverShouldReturnDefault() throws Exception {
        testGetSqlDialect("jdbc:sqlserver:", DEFAULT);
    }

    @Test
    public void getSqlDialectWhenDb2ShouldReturnDefault() throws Exception {
        testGetSqlDialect("jdbc:db2:", DEFAULT);
    }

    @Test
    public void getSqlDialectWhenInformixShouldReturnDefault() throws Exception {
        testGetSqlDialect("jdbc:informix-sqli:", DEFAULT);
    }
}

