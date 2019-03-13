/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.nationalized;


import DialectChecks.SupportsNClob;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.annotations.Nationalized;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10364")
@RequiresDialectFeature(SupportsNClob.class)
public class NationalizedLobFieldTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNationalization() {
        Session s = openSession();
        s.getTransaction().begin();
        try {
            NationalizedLobFieldTest.MyEntity e = new NationalizedLobFieldTest.MyEntity(1L);
            e.setState("UK");
            s.save(e);
            s.getTransaction().commit();
        } catch (Exception e) {
            if ((s.getTransaction().getStatus()) != (TransactionStatus.FAILED_COMMIT)) {
                s.getTransaction().rollback();
            }
            TestCase.fail(e.getMessage());
        } finally {
            s.close();
        }
        s = openSession();
        try {
            NationalizedLobFieldTest.MyEntity myEntity = s.get(NationalizedLobFieldTest.MyEntity.class, 1L);
            TestCase.assertNotNull(myEntity);
            Assert.assertThat(myEntity.getState(), Is.is("UK"));
        } finally {
            s.close();
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity")
    public static class MyEntity {
        @Id
        private long id;

        @Lob
        @Nationalized
        private String state;

        public MyEntity() {
        }

        public MyEntity(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}

