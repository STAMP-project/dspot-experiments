/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import TemporalType.DATE;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DateTimeParameterTest extends BaseEntityManagerFunctionalTestCase {
    private static GregorianCalendar nowCal = new GregorianCalendar();

    private static Date now = new Date(DateTimeParameterTest.nowCal.getTime().getTime());

    @Test
    public void testBindingCalendarAsDate() {
        createTestData();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("from Thing t where t.someDate = :aDate");
            query.setParameter("aDate", DateTimeParameterTest.nowCal, DATE);
            List list = query.getResultList();
            Assert.assertEquals(1, list.size());
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
        deleteTestData();
    }

    @Test
    public void testBindingNulls() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("from Thing t where t.someDate = :aDate or t.someTime = :aTime or t.someTimestamp = :aTimestamp");
            query.setParameter("aDate", ((Date) (null)), DATE);
            query.setParameter("aTime", ((Date) (null)), DATE);
            query.setParameter("aTimestamp", ((Date) (null)), DATE);
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Entity(name = "Thing")
    @Table(name = "THING")
    public static class Thing {
        @Id
        public Integer id;

        public String someString;

        @Temporal(TemporalType.DATE)
        public Date someDate;

        @Temporal(TemporalType.TIME)
        public Date someTime;

        @Temporal(TemporalType.TIMESTAMP)
        public Date someTimestamp;

        public Thing() {
        }

        public Thing(Integer id, String someString, Date someDate, Date someTime, Date someTimestamp) {
            this.id = id;
            this.someString = someString;
            this.someDate = someDate;
            this.someTime = someTime;
            this.someTimestamp = someTimestamp;
        }
    }
}

