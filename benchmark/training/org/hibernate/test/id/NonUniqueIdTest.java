/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.HibernateException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class NonUniqueIdTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12802")
    public void testLoadEntityWithNonUniqueId() {
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                session.get(.class, 1);
                fail("should have failed because there are 2 entities with id == 1");
            });
        } catch (HibernateException ex) {
            // expected
        }
    }

    @Entity
    @Table(name = "CATEGORY")
    public static class Category {
        @Id
        private int id;

        private String name;
    }
}

