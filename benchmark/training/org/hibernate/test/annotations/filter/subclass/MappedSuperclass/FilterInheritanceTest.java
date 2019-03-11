/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.filter.subclass.MappedSuperclass;


import java.util.List;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class FilterInheritanceTest extends BaseCoreFunctionalTestCase {
    private Transaction transaction;

    @Test
    @TestForIssue(jiraKey = "HHH-8895")
    public void testSelectFromHuman() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("nameFilter").setParameter("name", "unimportant");
            List humans = session.createQuery("SELECT h FROM Human h").list();
            assertThat(humans.size(), is(1));
            Human human = ((Human) (humans.get(0)));
            assertThat(human.getName(), is("unimportant"));
        });
    }
}

