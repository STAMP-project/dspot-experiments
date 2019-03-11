/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.function;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(HSQLDialect.class)
public class HSQLTruncFunctionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTruncateAndTruncFunctions() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.dialect.function.Person person = new org.hibernate.test.dialect.function.Person();
            person.setId(1L);
            person.setHighestScore(99.56);
            entityManager.persist(person);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Double score = entityManager.createQuery(("select truncate(p.highestScore, 1) " + ("from Person p " + "where p.id = :id")), .class).setParameter("id", 1L).getSingleResult();
            assertEquals(99.5, score, 0.01);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Double score = entityManager.createQuery(("select trunc(p.highestScore, 1) " + ("from Person p " + "where p.id = :id")), .class).setParameter("id", 1L).getSingleResult();
            assertEquals(99.5, score, 0.01);
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private Double highestScore;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Double getHighestScore() {
            return highestScore;
        }

        public void setHighestScore(Double highestScore) {
            this.highestScore = highestScore;
        }
    }
}

