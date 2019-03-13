/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11996")
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class InsertOrderingWithMultipleManyToOne extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);

    @Test
    public void testBatching() throws SQLException {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.Parent parent = new org.hibernate.test.insertordering.Parent();
            session.persist(parent);
            org.hibernate.test.insertordering.ChildA childA = new org.hibernate.test.insertordering.ChildA();
            childA.setParent(parent);
            session.persist(childA);
            org.hibernate.test.insertordering.ChildB childB = new org.hibernate.test.insertordering.ChildB();
            childB.setParent(parent);
            session.persist(childB);
            connectionProvider.clear();
        });
        Assert.assertEquals(3, connectionProvider.getPreparedStatements().size());
        /* PreparedStatement addressPreparedStatement = connectionProvider.getPreparedStatement(
        "insert into Address (ID) values (?)" );
        verify( addressPreparedStatement, times( 2 ) ).addBatch();
        verify( addressPreparedStatement, times( 1 ) ).executeBatch();
        PreparedStatement personPreparedStatement = connectionProvider.getPreparedStatement(
        "insert into Person (ID) values (?)" );
        verify( personPreparedStatement, times( 4 ) ).addBatch();
        verify( personPreparedStatement, times( 1 ) ).executeBatch();
         */
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "ChildA")
    public static class ChildA {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        private InsertOrderingWithMultipleManyToOne.Parent parent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public InsertOrderingWithMultipleManyToOne.Parent getParent() {
            return parent;
        }

        public void setParent(InsertOrderingWithMultipleManyToOne.Parent parent) {
            this.parent = parent;
        }
    }

    @Entity(name = "ChildB")
    public static class ChildB {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        private InsertOrderingWithMultipleManyToOne.Parent parent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public InsertOrderingWithMultipleManyToOne.Parent getParent() {
            return parent;
        }

        public void setParent(InsertOrderingWithMultipleManyToOne.Parent parent) {
            this.parent = parent;
        }
    }
}

