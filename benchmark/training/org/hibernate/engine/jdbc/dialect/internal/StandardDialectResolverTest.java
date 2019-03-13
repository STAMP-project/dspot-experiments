/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.jdbc.dialect.internal;


import java.sql.SQLException;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Unit test of the {@link StandardDialectResolver} class.
 *
 * @author Bryan Turner
 */
public class StandardDialectResolverTest extends BaseUnitTestCase {
    @Test
    public void testResolveDialectInternalForSQLServer2000() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(8, SQLServerDialect.class);
    }

    @Test
    public void testResolveDialectInternalForSQLServer2005() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(9, SQLServer2005Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForSQLServer2008() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(10, SQLServer2008Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForSQLServer2012() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(11, SQLServer2008Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForSQLServer2014() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(12, SQLServer2012Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForUnknownSQLServerVersion() throws SQLException {
        StandardDialectResolverTest.runSQLServerDialectTest(7, SQLServerDialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres81() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(8, 1, PostgreSQL81Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres82() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(8, 2, PostgreSQL82Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres83() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(8, 3, PostgreSQL82Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres84() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(8, 4, PostgreSQL82Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres9() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(9, 0, PostgreSQL9Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres91() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(9, 1, PostgreSQL9Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForPostgres92() throws SQLException {
        StandardDialectResolverTest.runPostgresDialectTest(9, 2, PostgreSQL9Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB103() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(10, 3, MariaDB103Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB102() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(10, 2, MariaDB102Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB101() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(10, 1, MariaDB10Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB100() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(10, 0, MariaDB10Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB55() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(5, 5, MariaDB53Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMariaDB52() throws SQLException {
        StandardDialectResolverTest.runMariaDBDialectTest(5, 2, MariaDBDialect.class);
    }

    @Test
    public void testResolveDialectInternalForMySQL57() throws SQLException {
        StandardDialectResolverTest.runMySQLDialectTest(5, 7, MySQL57Dialect.class);
    }

    @Test
    public void testResolveDialectInternalForMySQL8() throws SQLException {
        StandardDialectResolverTest.runMySQLDialectTest(8, 0, MySQL8Dialect.class);
    }
}

