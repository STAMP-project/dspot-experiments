/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.scalar;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Nationalized;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-10183")
@RequiresDialect(SQLServer2008Dialect.class)
public class NationalizedScalarQueryTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testScalarResult() {
        NationalizedScalarQueryTest.User user1 = new NationalizedScalarQueryTest.User(1, "Chris");
        NationalizedScalarQueryTest.User user2 = new NationalizedScalarQueryTest.User(2, "Steve");
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(user1);
            session.save(user2);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<Object[]> users = session.createNativeQuery("select * from users").getResultList();
            assertEquals(2, users.size());
        });
    }

    @Entity(name = "User")
    @Table(name = "users")
    public static class User {
        private Integer id;

        private String name;

        public User() {
        }

        public User(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Nationalized
        @Column(nullable = false)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

