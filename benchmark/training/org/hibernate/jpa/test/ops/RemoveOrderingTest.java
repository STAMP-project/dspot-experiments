/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class RemoveOrderingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8550")
    @FailureExpected(jiraKey = "HHH-8550")
    public void testManyToOne() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            RemoveOrderingTest.Company company = new RemoveOrderingTest.Company(1, "acme");
            RemoveOrderingTest.Person person = new RemoveOrderingTest.Person(1, "joe", company);
            em.persist(person);
            em.flush();
            em.remove(company);
            em.remove(person);
            em.flush();
            em.persist(person);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        em.close();
    }

    @Entity(name = "Company")
    @Table(name = "COMPANY")
    public static class Company {
        @Id
        public Integer id;

        public String name;

        public Company() {
        }

        public Company(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Person")
    @Table(name = "PERSON")
    public static class Person {
        @Id
        public Integer id;

        public String name;

        @ManyToOne(cascade = CascadeType.ALL, optional = false)
        @JoinColumn(name = "EMPLOYER_FK")
        public RemoveOrderingTest.Company employer;

        public Person() {
        }

        public Person(Integer id, String name, RemoveOrderingTest.Company employer) {
            this.id = id;
            this.name = name;
            this.employer = employer;
        }
    }
}

