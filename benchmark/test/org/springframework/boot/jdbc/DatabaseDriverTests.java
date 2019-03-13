/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.jdbc;


import DatabaseDriver.DB2;
import DatabaseDriver.DB2_AS400;
import DatabaseDriver.DERBY;
import DatabaseDriver.FIREBIRD;
import DatabaseDriver.H2;
import DatabaseDriver.HANA;
import DatabaseDriver.HSQLDB;
import DatabaseDriver.INFORMIX;
import DatabaseDriver.JTDS;
import DatabaseDriver.MYSQL;
import DatabaseDriver.ORACLE;
import DatabaseDriver.POSTGRESQL;
import DatabaseDriver.SQLITE;
import DatabaseDriver.SQLSERVER;
import DatabaseDriver.TERADATA;
import DatabaseDriver.UNKNOWN;
import org.junit.Test;


/**
 * Tests for {@link DatabaseDriver}.
 *
 * @author Phillip Webb
 * @author Maciej Walkowiak
 * @author Stephane Nicoll
 */
public class DatabaseDriverTests {
    @Test
    public void classNameForKnownDatabase() {
        String driverClassName = DatabaseDriver.fromJdbcUrl("jdbc:postgresql://hostname/dbname").getDriverClassName();
        assertThat(driverClassName).isEqualTo("org.postgresql.Driver");
    }

    @Test
    public void nullClassNameForUnknownDatabase() {
        String driverClassName = DatabaseDriver.fromJdbcUrl("jdbc:unknowndb://hostname/dbname").getDriverClassName();
        assertThat(driverClassName).isNull();
    }

    @Test
    public void unknownOnNullJdbcUrl() {
        DatabaseDriver actual = DatabaseDriver.fromJdbcUrl(null);
        assertThat(actual).isEqualTo(UNKNOWN);
    }

    @Test
    public void failureOnMalformedJdbcUrl() {
        assertThatIllegalArgumentException().isThrownBy(() -> DatabaseDriver.fromJdbcUrl("malformed:url")).withMessageContaining("URL must start with");
    }

    @Test
    public void unknownOnNullProductName() {
        DatabaseDriver actual = DatabaseDriver.fromProductName(null);
        assertThat(actual).isEqualTo(UNKNOWN);
    }

    @Test
    public void databaseProductNameLookups() {
        assertThat(DatabaseDriver.fromProductName("newone")).isEqualTo(UNKNOWN);
        assertThat(DatabaseDriver.fromProductName("Apache Derby")).isEqualTo(DERBY);
        assertThat(DatabaseDriver.fromProductName("H2")).isEqualTo(H2);
        assertThat(DatabaseDriver.fromProductName("HDB")).isEqualTo(HANA);
        assertThat(DatabaseDriver.fromProductName("HSQL Database Engine")).isEqualTo(HSQLDB);
        assertThat(DatabaseDriver.fromProductName("SQLite")).isEqualTo(SQLITE);
        assertThat(DatabaseDriver.fromProductName("MySQL")).isEqualTo(MYSQL);
        assertThat(DatabaseDriver.fromProductName("Oracle")).isEqualTo(ORACLE);
        assertThat(DatabaseDriver.fromProductName("PostgreSQL")).isEqualTo(POSTGRESQL);
        assertThat(DatabaseDriver.fromProductName("Microsoft SQL Server")).isEqualTo(SQLSERVER);
        assertThat(DatabaseDriver.fromProductName("SQL SERVER")).isEqualTo(SQLSERVER);
        assertThat(DatabaseDriver.fromProductName("DB2")).isEqualTo(DB2);
        assertThat(DatabaseDriver.fromProductName("Firebird 2.5.WI")).isEqualTo(FIREBIRD);
        assertThat(DatabaseDriver.fromProductName("Firebird 2.1.LI")).isEqualTo(FIREBIRD);
        assertThat(DatabaseDriver.fromProductName("DB2/LINUXX8664")).isEqualTo(DB2);
        assertThat(DatabaseDriver.fromProductName("DB2 UDB for AS/400")).isEqualTo(DB2_AS400);
        assertThat(DatabaseDriver.fromProductName("DB3 XDB for AS/400")).isEqualTo(DB2_AS400);
        assertThat(DatabaseDriver.fromProductName("Teradata")).isEqualTo(TERADATA);
        assertThat(DatabaseDriver.fromProductName("Informix Dynamic Server")).isEqualTo(INFORMIX);
    }

    @Test
    public void databaseJdbcUrlLookups() {
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:newone://localhost")).isEqualTo(UNKNOWN);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:derby:sample")).isEqualTo(DERBY);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:h2:~/sample")).isEqualTo(H2);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:hsqldb:hsql://localhost")).isEqualTo(HSQLDB);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:sqlite:sample.db")).isEqualTo(SQLITE);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:mysql://localhost:3306/sample")).isEqualTo(MYSQL);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:oracle:thin:@localhost:1521:orcl")).isEqualTo(ORACLE);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:postgresql://127.0.0.1:5432/sample")).isEqualTo(POSTGRESQL);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:jtds:sqlserver://127.0.0.1:1433/sample")).isEqualTo(JTDS);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:sap:localhost")).isEqualTo(HANA);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:sqlserver://127.0.0.1:1433")).isEqualTo(SQLSERVER);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:firebirdsql://localhost/sample")).isEqualTo(FIREBIRD);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:firebird://localhost/sample")).isEqualTo(FIREBIRD);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:db2://localhost:50000/sample ")).isEqualTo(DB2);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:as400://localhost")).isEqualTo(DB2_AS400);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:teradata://localhost/SAMPLE")).isEqualTo(TERADATA);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:informix-sqli://localhost:1533/sample")).isEqualTo(INFORMIX);
        assertThat(DatabaseDriver.fromJdbcUrl("jdbc:informix-direct://sample")).isEqualTo(INFORMIX);
    }
}

