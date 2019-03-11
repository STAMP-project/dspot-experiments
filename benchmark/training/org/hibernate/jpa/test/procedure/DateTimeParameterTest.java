/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import ParameterMode.IN;
import TemporalType.DATE;
import TemporalType.TIME;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DateTimeParameterTest extends BaseUnitTestCase {
    HibernateEntityManagerFactory entityManagerFactory;

    private static GregorianCalendar nowCal = new GregorianCalendar();

    private static Date now = new Date(DateTimeParameterTest.nowCal.getTime().getTime());

    @Test
    public void testBindingCalendarAsDate() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findMessagesByDate");
            query.registerStoredProcedureParameter(1, Calendar.class, IN);
            query.setParameter(1, DateTimeParameterTest.nowCal, DATE);
            List list = query.getResultList();
            Assert.assertEquals(1, list.size());
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    public void testBindingCalendarAsTime() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("findMessagesByTime");
            query.registerStoredProcedureParameter(1, Calendar.class, IN);
            query.setParameter(1, DateTimeParameterTest.nowCal, TIME);
            List list = query.getResultList();
            Assert.assertEquals(1, list.size());
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Entity(name = "Message")
    @Table(name = "MSG")
    public static class Message {
        @Id
        private Integer id;

        private String body;

        @Column(name = "POST_DATE")
        @Temporal(TemporalType.DATE)
        private Date postDate;

        @Column(name = "POST_TIME")
        @Temporal(TemporalType.TIME)
        private Date postTime;

        @Column(name = "TS")
        @Temporal(TemporalType.TIMESTAMP)
        private Date ts;

        public Message() {
        }

        public Message(Integer id, String body, Date postDate, Date postTime, Date ts) {
            this.id = id;
            this.body = body;
            this.postDate = postDate;
            this.postTime = postTime;
            this.ts = ts;
        }
    }
}

