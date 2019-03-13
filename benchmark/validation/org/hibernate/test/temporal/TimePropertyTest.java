/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.temporal;


import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import junit.framework.Assert;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class TimePropertyTest extends BaseCoreFunctionalTestCase {
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Test
    public void testTimeAsDate() {
        final TimePropertyTest.Entity eOrig = new TimePropertyTest.Entity();
        eOrig.tAsDate = new Time(new Date().getTime());
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final TimePropertyTest.Entity eGotten = ((TimePropertyTest.Entity) (s.get(TimePropertyTest.Entity.class, eOrig.id)));
        // Some databases retain the milliseconds when being inserted and some don't;
        final String tAsDateOrigFormatted = timeFormat.format(eOrig.tAsDate);
        final String tAsDateGottenFormatted = timeFormat.format(eGotten.tAsDate);
        Assert.assertEquals(tAsDateOrigFormatted, tAsDateGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        String queryString = "from TimePropertyTest$Entity where tAsDate = ?1";
        if (SQLServerDialect.class.isAssignableFrom(getDialect().getClass())) {
            queryString = "from TimePropertyTest$Entity where tAsDate = cast ( ?1 as time )";
        }
        final Query queryWithParameter = s.createQuery(queryString).setParameter(1, eGotten.tAsDate);
        final TimePropertyTest.Entity eQueriedWithParameter = ((TimePropertyTest.Entity) (queryWithParameter.uniqueResult()));
        Assert.assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query query = s.createQuery(queryString).setTime(1, eGotten.tAsDate);
        final TimePropertyTest.Entity eQueried = ((TimePropertyTest.Entity) (query.uniqueResult()));
        Assert.assertNotNull(eQueried);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(eQueried);
        s.getTransaction().commit();
        s.close();
    }

    @javax.persistence.Entity
    @Table(name = "entity")
    public static class Entity {
        @GeneratedValue
        @Id
        private long id;

        @Temporal(TemporalType.TIME)
        private Date tAsDate;
    }
}

