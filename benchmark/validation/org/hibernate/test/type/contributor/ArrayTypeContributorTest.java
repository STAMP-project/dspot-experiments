/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.contributor;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Type;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11409")
public class ArrayTypeContributorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.type.contributor.CorporateUser> users = session.createQuery("select u from CorporateUser u where u.emailAddresses = :address", .class).setParameter("address", new Array(), ArrayType.INSTANCE).getResultList();
            assertTrue(users.isEmpty());
        });
    }

    @Test
    public void testNativeSQL() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<Array> emails = session.createNativeQuery("select u.emailAddresses from CorporateUser u where u.userName = :name").setParameter("name", "Vlad").getResultList();
            assertEquals(1, emails.size());
        });
    }

    @Entity(name = "CorporateUser")
    public static class CorporateUser {
        @Id
        private String userName;

        @Type(type = "comma-separated-array")
        private Array emailAddresses = new Array();

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Array getEmailAddresses() {
            return emailAddresses;
        }
    }
}

