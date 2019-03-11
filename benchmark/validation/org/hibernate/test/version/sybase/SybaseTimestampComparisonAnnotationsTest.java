/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.version.sybase;


import RowVersionType.INSTANCE;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.Session;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.VersionType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@RequiresDialect(SybaseASE15Dialect.class)
public class SybaseTimestampComparisonAnnotationsTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10413")
    public void testComparableTimestamps() {
        final VersionType versionType = sessionFactory().getEntityPersister(SybaseTimestampComparisonAnnotationsTest.Thing.class.getName()).getVersionType();
        Assert.assertSame(INSTANCE, versionType);
        Session s = openSession();
        s.getTransaction().begin();
        SybaseTimestampComparisonAnnotationsTest.Thing thing = new SybaseTimestampComparisonAnnotationsTest.Thing();
        thing.name = "n";
        s.persist(thing);
        s.getTransaction().commit();
        s.close();
        byte[] previousVersion = thing.version;
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);// 1000 milliseconds is one second.

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            s = openSession();
            s.getTransaction().begin();
            thing.name = "n" + i;
            thing = ((SybaseTimestampComparisonAnnotationsTest.Thing) (s.merge(thing)));
            s.getTransaction().commit();
            s.close();
            Assert.assertTrue(((versionType.compare(previousVersion, thing.version)) < 0));
            previousVersion = thing.version;
        }
        s = openSession();
        s.getTransaction().begin();
        s.delete(thing);
        s.getTransaction().commit();
        s.close();
    }

    @Entity
    @Table(name = "thing")
    public static class Thing {
        @Id
        private long id;

        @Version
        @Generated(GenerationTime.ALWAYS)
        @Column(name = "ver", columnDefinition = "timestamp")
        private byte[] version;

        private String name;
    }
}

