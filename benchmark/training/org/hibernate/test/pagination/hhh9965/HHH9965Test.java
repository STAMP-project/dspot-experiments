/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.pagination.hhh9965;


import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 17/12/17.
 *
 * @author Reda.Housni-Alaoui
 */
@TestForIssue(jiraKey = "HHH-9965")
public class HHH9965Test extends BaseCoreFunctionalTestCase {
    @Test
    public void testHHH9965() {
        Session session = openSession();
        session.beginTransaction();
        String hql = "SELECT s FROM Shop s join fetch s.products";
        try {
            session.createQuery(hql).setMaxResults(3).list();
            Assert.fail("Pagination over collection fetch failure was expected");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        session.getTransaction().commit();
        session.close();
    }
}

