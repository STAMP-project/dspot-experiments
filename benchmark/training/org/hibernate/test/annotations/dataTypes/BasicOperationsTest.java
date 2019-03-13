/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.dataTypes;


import DialectChecks.SupportsExpectedLobUsagePattern;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.Locale;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.testing.DialectCheck;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(value = { SupportsExpectedLobUsagePattern.class, BasicOperationsTest.OracleDialectChecker.class }, jiraKey = "HHH-6834")
public class BasicOperationsTest extends BaseCoreFunctionalTestCase {
    private static final String SOME_ENTITY_TABLE_NAME = "SOMEENTITY";

    private static final String SOME_OTHER_ENTITY_TABLE_NAME = "SOMEOTHERENTITY";

    public static class OracleDialectChecker implements DialectCheck {
        @Override
        public boolean isMatch(Dialect dialect) {
            return !(dialect instanceof Oracle8iDialect);
        }
    }

    @Test
    public void testCreateAndDelete() {
        Date now = new Date();
        Session s = openSession();
        s.doWork(new BasicOperationsTest.ValidateSomeEntityColumns(((SessionImplementor) (s))));
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_ENTITY_TABLE_NAME, 0));
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_OTHER_ENTITY_TABLE_NAME, 0));
        s.beginTransaction();
        SomeEntity someEntity = new SomeEntity(now);
        SomeOtherEntity someOtherEntity = new SomeOtherEntity(1);
        s.save(someEntity);
        s.save(someOtherEntity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_ENTITY_TABLE_NAME, 1));
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_OTHER_ENTITY_TABLE_NAME, 1));
        s.beginTransaction();
        s.delete(someEntity);
        s.delete(someOtherEntity);
        s.getTransaction().commit();
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_ENTITY_TABLE_NAME, 0));
        s.doWork(new BasicOperationsTest.ValidateRowCount(((SessionImplementor) (s)), BasicOperationsTest.SOME_OTHER_ENTITY_TABLE_NAME, 0));
        s.close();
    }

    // verify all the expected columns are created
    class ValidateSomeEntityColumns implements Work {
        private SessionImplementor s;

        public ValidateSomeEntityColumns(SessionImplementor s) {
            this.s = s;
        }

        public void execute(Connection connection) throws SQLException {
            // id -> java.util.Date (DATE - becase of explicit TemporalType)
            validateColumn(connection, "ID", Types.DATE);
            // timeData -> java.sql.Time (TIME)
            validateColumn(connection, "TIMEDATA", Types.TIME);
            // tsData -> java.sql.Timestamp (TIMESTAMP)
            validateColumn(connection, "TSDATA", Types.TIMESTAMP);
        }

        private void validateColumn(Connection connection, String columnName, int expectedJdbcTypeCode) throws SQLException {
            DatabaseMetaData meta = connection.getMetaData();
            // DBs treat the meta information differently, in particular case sensitivity.
            // We need to use the meta information to find out how to treat names
            String tableNamePattern = generateFinalNamePattern(meta, BasicOperationsTest.SOME_ENTITY_TABLE_NAME);
            String columnNamePattern = generateFinalNamePattern(meta, columnName);
            ResultSet columnInfo = meta.getColumns(null, null, tableNamePattern, columnNamePattern);
            s.getJdbcCoordinator().getResourceRegistry().register(columnInfo, columnInfo.getStatement());
            Assert.assertTrue(columnInfo.next());
            int dataType = columnInfo.getInt("DATA_TYPE");
            s.getJdbcCoordinator().getResourceRegistry().release(columnInfo, columnInfo.getStatement());
            Assert.assertEquals(columnName, JdbcTypeNameMapper.getTypeName(expectedJdbcTypeCode), JdbcTypeNameMapper.getTypeName(dataType));
        }

        private String generateFinalNamePattern(DatabaseMetaData meta, String name) throws SQLException {
            if (meta.storesLowerCaseIdentifiers()) {
                return name.toLowerCase(Locale.ROOT);
            } else {
                return name;
            }
        }
    }

    // verify we have the right amount of columns
    class ValidateRowCount implements Work {
        private final int expectedRowCount;

        private final String table;

        private SessionImplementor s;

        public ValidateRowCount(SessionImplementor s, String table, int count) {
            this.s = s;
            this.expectedRowCount = count;
            this.table = table;
        }

        public void execute(Connection connection) throws SQLException {
            Statement st = s.getJdbcCoordinator().getStatementPreparer().createStatement();
            s.getJdbcCoordinator().getResultSetReturn().extract(st, ("SELECT COUNT(*) FROM " + (table)));
            ResultSet result = s.getJdbcCoordinator().getResultSetReturn().extract(st, ("SELECT COUNT(*) FROM " + (table)));
            result.next();
            int rowCount = result.getInt(1);
            Assert.assertEquals("Unexpected row count", expectedRowCount, rowCount);
        }
    }
}

