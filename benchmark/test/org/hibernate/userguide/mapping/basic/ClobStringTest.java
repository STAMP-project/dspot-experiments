/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-clob-string-example[]
public class ClobStringTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        Integer productId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1);
            product.setName("Mobile phone");
            product.setWarranty("My product warranty");
            entityManager.persist(product);
            return product.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Product product = entityManager.find(.class, productId);
            assertEquals("My product warranty", product.getWarranty());
        });
    }

    // tag::basic-clob-string-example[]
    // tag::basic-clob-string-example[]
    @Entity(name = "Product")
    public static class Product {
        @Id
        private Integer id;

        private String name;

        @Lob
        private String warranty;

        // Getters and setters are omitted for brevity
        // end::basic-clob-string-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWarranty() {
            return warranty;
        }

        public void setWarranty(String warranty) {
            this.warranty = warranty;
        }
    }
}

