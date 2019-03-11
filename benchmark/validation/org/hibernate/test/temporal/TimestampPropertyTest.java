/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.temporal;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;


/**
 * Tests that persisted timestamp properties have the expected format to milliseconds
 * and tests that entities can be queried by timestamp value.
 *
 * See Mysql57TimestampFspTest for tests using MySQL 5.7. MySQL 5.7 is tested separately
 * because it requires CURRENT_TIMESTAMP(6) or NOW(6) as a default.
 *
 * @author Gail Badner
 */
@SkipForDialect(value = { SybaseDialect.class, MySQLDialect.class }, comment = "CURRENT_TIMESTAMP not supported as default value in Sybase or MySQL")
public class TimestampPropertyTest extends BaseCoreFunctionalTestCase {
    private final DateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Test
    public void testTime() {
        final TimestampPropertyTest.Entity eOrig = new TimestampPropertyTest.Entity();
        eOrig.ts = new Date();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final TimestampPropertyTest.Entity eGotten = ((TimestampPropertyTest.Entity) (s.get(TimestampPropertyTest.Entity.class, eOrig.id)));
        final String tsOrigFormatted = timestampFormat.format(eOrig.ts);
        final String tsGottenFormatted = timestampFormat.format(eGotten.ts);
        Assert.assertEquals(tsOrigFormatted, tsGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithParameter = s.createQuery("from TimestampPropertyTest$Entity where ts=?1").setParameter(1, eOrig.ts);
        final TimestampPropertyTest.Entity eQueriedWithParameter = ((TimestampPropertyTest.Entity) (queryWithParameter.uniqueResult()));
        assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithTimestamp = s.createQuery("from TimestampPropertyTest$Entity where ts=?1").setTimestamp(1, eOrig.ts);
        final TimestampPropertyTest.Entity eQueriedWithTimestamp = ((TimestampPropertyTest.Entity) (queryWithTimestamp.uniqueResult()));
        assertNotNull(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testTimeGeneratedByColumnDefault() {
        final TimestampPropertyTest.Entity eOrig = new TimestampPropertyTest.Entity();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        assertNotNull(eOrig.tsColumnDefault);
        s = openSession();
        s.getTransaction().begin();
        final TimestampPropertyTest.Entity eGotten = ((TimestampPropertyTest.Entity) (s.get(TimestampPropertyTest.Entity.class, eOrig.id)));
        final String tsColumnDefaultOrigFormatted = timestampFormat.format(eOrig.tsColumnDefault);
        final String tsColumnDefaultGottenFormatted = timestampFormat.format(eGotten.tsColumnDefault);
        Assert.assertEquals(tsColumnDefaultOrigFormatted, tsColumnDefaultGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithParameter = s.createQuery("from TimestampPropertyTest$Entity where tsColumnDefault=?1").setParameter(1, eOrig.tsColumnDefault);
        final TimestampPropertyTest.Entity eQueriedWithParameter = ((TimestampPropertyTest.Entity) (queryWithParameter.uniqueResult()));
        assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithTimestamp = s.createQuery("from TimestampPropertyTest$Entity where tsColumnDefault=?1").setTimestamp(1, eOrig.tsColumnDefault);
        final TimestampPropertyTest.Entity eQueriedWithTimestamp = ((TimestampPropertyTest.Entity) (queryWithTimestamp.uniqueResult()));
        assertNotNull(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
    }

    @javax.persistence.Entity
    @Table(name = "MyEntity")
    public static class Entity {
        @GeneratedValue
        @Id
        private long id;

        @Temporal(TemporalType.TIMESTAMP)
        private Date ts;

        @Temporal(TemporalType.TIMESTAMP)
        @Generated(GenerationTime.INSERT)
        @ColumnDefault("CURRENT_TIMESTAMP")
        private Date tsColumnDefault;
    }
}

