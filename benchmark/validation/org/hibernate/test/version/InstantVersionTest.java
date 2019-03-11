/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.version;


import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-10026")
public class InstantVersionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testInstantUsageAsVersion() {
        Session session = openSession();
        session.getTransaction().begin();
        InstantVersionTest.TheEntity e = new InstantVersionTest.TheEntity(1);
        session.save(e);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        e = session.byId(InstantVersionTest.TheEntity.class).load(1);
        MatcherAssert.assertThat(e.getTs(), CoreMatchers.notNullValue());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        e = session.byId(InstantVersionTest.TheEntity.class).load(1);
        session.delete(e);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "the_entity")
    public static class TheEntity {
        private Integer id;

        private Instant ts;

        public TheEntity() {
        }

        public TheEntity(Integer id) {
            this.id = id;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Version
        public Instant getTs() {
            return ts;
        }

        public void setTs(Instant ts) {
            this.ts = ts;
        }
    }
}

