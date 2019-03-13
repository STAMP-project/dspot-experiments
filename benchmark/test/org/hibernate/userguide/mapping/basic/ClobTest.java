/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.io.Reader;
import java.sql.Clob;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.ClobProxy;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-clob-example[]
public class ClobTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        Integer productId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::basic-clob-persist-example[]
            String warranty = "My product warranty";
            final org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1);
            product.setName("Mobile phone");
            product.setWarranty(ClobProxy.generateProxy(warranty));
            entityManager.persist(product);
            // end::basic-clob-persist-example[]
            return product.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                // tag::basic-clob-find-example[]
                org.hibernate.userguide.mapping.basic.Product product = entityManager.find(.class, productId);
                try (Reader reader = product.getWarranty().getCharacterStream()) {
                    assertEquals("My product warranty", toString(reader));
                }
                // end::basic-clob-find-example[]
            } catch ( e) {
                fail(e.getMessage());
            }
        });
    }

    // tag::basic-clob-example[]
    // tag::basic-clob-example[]
    @Entity(name = "Product")
    public static class Product {
        @Id
        private Integer id;

        private String name;

        @Lob
        private Clob warranty;

        // Getters and setters are omitted for brevity
        // end::basic-clob-example[]
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

        public Clob getWarranty() {
            return warranty;
        }

        public void setWarranty(Clob warranty) {
            this.warranty = warranty;
        }
    }
}

