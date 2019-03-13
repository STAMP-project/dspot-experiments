/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * used driver hibernate.connection.driver_class com.microsoft.sqlserver.jdbc.SQLServerDriver
 *
 * @author Guenther Demetz
 */
@RequiresDialect({ SQLServer2005Dialect.class })
public class SQLServerDialectCollationTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7198")
    public void testMaxResultsSqlServerWithCaseSensitiveCollation() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (int i = 1; i <= 20; i++) {
                session.persist(new org.hibernate.test.dialect.functional.CustomProduct(i, ("Kit" + i)));
            }
            session.flush();
            session.clear();
            List list = session.createQuery("from CustomProduct where description like 'Kit%'").setFirstResult(2).setMaxResults(2).list();
            assertEquals(2, list.size());
        });
    }

    @Entity(name = "CustomProduct")
    @Table(catalog = "hibernate_orm_test_collation", schema = "dbo")
    public static class CustomProduct implements Serializable {
        @Id
        public Integer id;

        @Column(name = "description", nullable = false)
        public String description;

        public CustomProduct() {
        }

        public CustomProduct(Integer id, String description) {
            this.id = id;
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SQLServerDialectCollationTest.CustomProduct that = ((SQLServerDialectCollationTest.CustomProduct) (o));
            return Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(description);
        }

        @Override
        public String toString() {
            return ((((("CustomProduct{" + "id=") + (id)) + ", description='") + (description)) + '\'') + '}';
        }
    }
}

