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
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;


/**
 *
 *
 * @author Gail Badner
 */
@RequiresDialect(MySQL57Dialect.class)
@TestForIssue(jiraKey = "HHH-8401")
public class MySQL57TimestampPropertyTest extends BaseCoreFunctionalTestCase {
    private final DateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Test
    public void testTime() {
        final MySQL57TimestampPropertyTest.Entity eOrig = new MySQL57TimestampPropertyTest.Entity();
        eOrig.ts = new Date();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final MySQL57TimestampPropertyTest.Entity eGotten = ((MySQL57TimestampPropertyTest.Entity) (s.get(MySQL57TimestampPropertyTest.Entity.class, eOrig.id)));
        final String tsOrigFormatted = timestampFormat.format(eOrig.ts);
        final String tsGottenFormatted = timestampFormat.format(eGotten.ts);
        Assert.assertEquals(tsOrigFormatted, tsGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithParameter = s.createQuery("from Entity where ts= ?1").setParameter(1, eOrig.ts);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithParameter = ((MySQL57TimestampPropertyTest.Entity) (queryWithParameter.uniqueResult()));
        assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithTimestamp = s.createQuery("from Entity where ts= ?1").setTimestamp(1, eOrig.ts);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithTimestamp = ((MySQL57TimestampPropertyTest.Entity) (queryWithTimestamp.uniqueResult()));
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
        final MySQL57TimestampPropertyTest.Entity eOrig = new MySQL57TimestampPropertyTest.Entity();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        assertNotNull(eOrig.tsColumnDefault);
        s = openSession();
        s.getTransaction().begin();
        final MySQL57TimestampPropertyTest.Entity eGotten = ((MySQL57TimestampPropertyTest.Entity) (s.get(MySQL57TimestampPropertyTest.Entity.class, eOrig.id)));
        final String tsColumnDefaultOrigFormatted = timestampFormat.format(eOrig.tsColumnDefault);
        final String tsColumnDefaultGottenFormatted = timestampFormat.format(eGotten.tsColumnDefault);
        Assert.assertEquals(tsColumnDefaultOrigFormatted, tsColumnDefaultGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithParameter = s.createQuery("from Entity where tsColumnDefault= ?1").setParameter(1, eOrig.tsColumnDefault);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithParameter = ((MySQL57TimestampPropertyTest.Entity) (queryWithParameter.uniqueResult()));
        assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithTimestamp = s.createQuery("from Entity where tsColumnDefault= ?1").setTimestamp(1, eOrig.tsColumnDefault);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithTimestamp = ((MySQL57TimestampPropertyTest.Entity) (queryWithTimestamp.uniqueResult()));
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
    public void testTimeGeneratedByColumnDefinition() {
        final MySQL57TimestampPropertyTest.Entity eOrig = new MySQL57TimestampPropertyTest.Entity();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(eOrig);
        s.getTransaction().commit();
        s.close();
        assertNotNull(eOrig.tsColumnDefinition);
        s = openSession();
        s.getTransaction().begin();
        final MySQL57TimestampPropertyTest.Entity eGotten = ((MySQL57TimestampPropertyTest.Entity) (s.get(MySQL57TimestampPropertyTest.Entity.class, eOrig.id)));
        final String tsColumnDefinitionOrigFormatted = timestampFormat.format(eOrig.tsColumnDefinition);
        final String tsColumnDefinitionGottenFormatted = timestampFormat.format(eGotten.tsColumnDefinition);
        Assert.assertEquals(tsColumnDefinitionOrigFormatted, tsColumnDefinitionGottenFormatted);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithParameter = s.createQuery("from Entity where tsColumnDefinition= ?1").setParameter(1, eOrig.tsColumnDefinition);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithParameter = ((MySQL57TimestampPropertyTest.Entity) (queryWithParameter.uniqueResult()));
        assertNotNull(eQueriedWithParameter);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        final Query queryWithTimestamp = s.createQuery("from Entity where tsColumnDefinition= ?1").setTimestamp(1, eOrig.tsColumnDefinition);
        final MySQL57TimestampPropertyTest.Entity eQueriedWithTimestamp = ((MySQL57TimestampPropertyTest.Entity) (queryWithTimestamp.uniqueResult()));
        assertNotNull(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.delete(eQueriedWithTimestamp);
        s.getTransaction().commit();
        s.close();
    }

    @javax.persistence.Entity(name = "Entity")
    public static class Entity {
        @GeneratedValue
        @Id
        private long id;

        @Temporal(TemporalType.TIMESTAMP)
        private Date ts;

        @Temporal(TemporalType.TIMESTAMP)
        @Generated(GenerationTime.INSERT)
        @ColumnDefault("CURRENT_TIMESTAMP(6)")
        private Date tsColumnDefault;

        @Temporal(TemporalType.TIMESTAMP)
        @Generated(GenerationTime.INSERT)
        @Column(columnDefinition = "datetime(6) default NOW(6)")
        private Date tsColumnDefinition;
    }
}

