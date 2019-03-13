/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idprops;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class IdPropertyInTablePerClassSubclassTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-13114")
    public void testHql() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertEquals(2, session.createQuery("from Genius g where g.id = :id", .class).setParameter("id", 1L).list().size());
            assertEquals(1, session.createQuery("from Genius g where g.id is null", .class).list().size());
            assertEquals(3L, session.createQuery("select count( g ) from Genius g").uniqueResult());
            assertEquals(2, session.createQuery("from Human h where h.id = :id", .class).setParameter("id", 1L).list().size());
            assertEquals(1, session.createQuery("from Human h where h.id is null", .class).list().size());
            assertEquals(3L, session.createQuery("select count( h ) from Human h").uniqueResult());
        });
    }

    @Entity(name = "Human")
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public static class Human {
        private Long realId;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "realId")
        public Long getRealId() {
            return realId;
        }

        public void setRealId(Long realId) {
            this.realId = realId;
        }
    }

    @Entity(name = "Genius")
    public static class Genius extends IdPropertyInTablePerClassSubclassTest.Human {
        private Long id;

        public Genius() {
        }

        public Genius(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

