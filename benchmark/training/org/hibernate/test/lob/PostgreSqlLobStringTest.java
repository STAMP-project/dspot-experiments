/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import java.sql.Clob;
import java.sql.Statement;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.util.ExceptionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11614")
@RequiresDialect(PostgreSQL81Dialect.class)
public class PostgreSqlLobStringTest extends BaseCoreFunctionalTestCase {
    private final String value1 = "abc";

    private final String value2 = "def";

    private final String value3 = "ghi";

    @Test
    public void testBadClobDataSavedAsStringFails() {
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                final Query query = session.createQuery("from TestEntity");
                final List<org.hibernate.test.lob.TestEntity> results = query.list();
                fail("Exception thrown expected");
            });
        } catch (Exception e) {
            Exception rootException = ((Exception) (ExceptionUtil.rootCause(e)));
            Assert.assertTrue(rootException.getMessage().startsWith("Bad value for type long"));
        }
    }

    @Test
    public void testBadClobDataSavedAsStringworksAfterUpdate() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.doWork(( connection) -> {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(("update test_entity\n" + ((("set \n" + "    clobfield = lo_from_bytea(0, cast(clobfield as bytea)),\n") + "    firstlobfield = lo_from_bytea(0, cast(firstlobfield as bytea)),\n") + "    secondlobfield = lo_from_bytea(0, cast(secondlobfield as bytea))")));
                }
            });
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Query query = session.createQuery("from TestEntity");
            final List<org.hibernate.test.lob.TestEntity> results = query.list();
            assertThat(results.size(), is(1));
            final org.hibernate.test.lob.TestEntity testEntity = results.get(0);
            assertThat(testEntity.getFirstLobField(), is(value1));
            assertThat(testEntity.getSecondLobField(), is(value2));
            final Clob clobField = testEntity.getClobField();
            try {
                assertThat(clobField.getSubString(1, ((int) (clobField.length()))), is(value3));
            } catch ( e) {
                fail(e.getMessage());
            }
        });
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private long id;

        @Lob
        @Column
        String firstLobField;

        @Lob
        @Column
        String secondLobField;

        @Lob
        @Column
        Clob clobField;

        public long getId() {
            return id;
        }

        public String getFirstLobField() {
            return firstLobField;
        }

        public void setFirstLobField(String firstLobField) {
            this.firstLobField = firstLobField;
        }

        public String getSecondLobField() {
            return secondLobField;
        }

        public void setSecondLobField(String secondLobField) {
            this.secondLobField = secondLobField;
        }

        public Clob getClobField() {
            return clobField;
        }

        public void setClobField(Clob clobField) {
            this.clobField = clobField;
        }
    }
}

