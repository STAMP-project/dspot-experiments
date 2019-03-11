/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.test.jpa.AbstractJPATest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ClosedManagerTests extends AbstractJPATest {
    @Test
    public void testQuerySetMaxResults() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i");
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.setMaxResults(1);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testQuerySetFirstResult() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i");
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.setFirstResult(1);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testQuerySetPositionalParameter() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i where i.id = ?1");
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.setParameter(1, 1);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testQuerySetNamedParameter() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i where i.id = :id");
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.setParameter("id", 1);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testQueryGetPositionalParameter() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i where i.id = ?1");
        qry.setParameter(1, 1);
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.getParameter(1);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
        try {
            qry.getParameter(1, Integer.class);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testQueryGetNamedParameter() {
        final Session session = sessionFactory().openSession();
        final Query qry = session.createQuery("select i from Item i where i.id = :id");
        qry.setParameter("id", 1);
        session.close();
        MatcherAssert.assertThat(session.isOpen(), CoreMatchers.is(false));
        try {
            qry.getParameter("id");
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
        try {
            qry.getParameter("id", Long.class);
            Assert.fail("Expecting call to fail");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testClose() {
        final Session session = sessionFactory().openSession();
        // 1st call - should be ok
        session.close();
        try {
            // 2nd should fail (JPA compliance enabled)
            session.close();
            Assert.fail();
        } catch (IllegalStateException expected) {
            // expected outcome
        }
    }
}

