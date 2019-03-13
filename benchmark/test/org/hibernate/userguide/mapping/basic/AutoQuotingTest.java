/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-auto-quoting-example[]
public class AutoQuotingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::basic-auto-quoting-persistence-example[]
            org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1L);
            product.setName("Mobile phone");
            product.setNumber("123-456-7890");
            entityManager.persist(product);
            // end::basic-auto-quoting-persistence-example[]
        });
    }

    // tag::basic-auto-quoting-example[]
    // tag::basic-auto-quoting-example[]
    @Entity(name = "Product")
    public static class Product {
        @Id
        private Long id;

        private String name;

        private String number;

        // Getters and setters are omitted for brevity
        // end::basic-auto-quoting-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}

