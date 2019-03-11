/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.filter.subclass.joined;


import junit.framework.Assert;
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.test.annotations.filter.subclass.SubClassTest;
import org.junit.Test;


@SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
public class JoinedSubClassTest extends SubClassTest {
    @Test
    public void testClub() {
        openSession();
        session.beginTransaction();
        Club club = ((Club) (session.createQuery("from Club").uniqueResult()));
        Assert.assertEquals(3, club.getMembers().size());
        session.clear();
        session.enableFilter("pregnantMembers");
        club = ((Club) (session.createQuery("from Club").uniqueResult()));
        Assert.assertEquals(1, club.getMembers().size());
        session.clear();
        session.enableFilter("iqMin").setParameter("min", 148);
        club = ((Club) (session.createQuery("from Club").uniqueResult()));
        Assert.assertEquals(0, club.getMembers().size());
        session.getTransaction().commit();
        session.close();
    }
}

