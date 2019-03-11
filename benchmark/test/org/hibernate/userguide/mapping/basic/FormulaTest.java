/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Formula;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-column-formula-example[]
public class FormulaTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::mapping-column-formula-persistence-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::basic-datetime-temporal-date-persist-example[]
            org.hibernate.userguide.mapping.basic.Account account = new org.hibernate.userguide.mapping.basic.Account();
            account.setId(1L);
            account.setCredit(5000.0);
            account.setRate((1.25 / 100));
            entityManager.persist(account);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Account account = entityManager.find(.class, 1L);
            assertEquals(Double.valueOf(62.5), account.getInterest());
        });
        // end::mapping-column-formula-persistence-example[]
    }

    // tag::mapping-column-formula-example[]
    // tag::mapping-column-formula-example[]
    @Entity(name = "Account")
    public static class Account {
        @Id
        private Long id;

        private Double credit;

        private Double rate;

        @Formula("credit * rate")
        private Double interest;

        // Getters and setters omitted for brevity
        // end::mapping-column-formula-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Double getCredit() {
            return credit;
        }

        public void setCredit(Double credit) {
            this.credit = credit;
        }

        public Double getRate() {
            return rate;
        }

        public void setRate(Double rate) {
            this.rate = rate;
        }

        public Double getInterest() {
            return interest;
        }

        public void setInterest(Double interest) {
            this.interest = interest;
        }
    }
}

