/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-managed-state-dynamic-update-mapping-example[]
public class DynamicUpdateTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Product book = new org.hibernate.userguide.pc.Product();
            book.setId(1L);
            book.setName("High-Performance Java Persistence");
            book.setDescription("get the most out of your persistence layer");
            book.setPriceCents(2999);
            book.setQuantity(10000);
            entityManager.persist(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Product book = entityManager.find(.class, 1L);
            book.setPriceCents(2499);
        });
    }

    // tag::pc-managed-state-dynamic-update-mapping-example[]
    // tag::pc-managed-state-dynamic-update-mapping-example[]
    @Entity(name = "Product")
    @DynamicUpdate
    public static class Product {
        @Id
        private Long id;

        @Column
        private String name;

        @Column
        private String description;

        @Column(name = "price_cents")
        private Integer priceCents;

        @Column
        private Integer quantity;

        // Getters and setters are omitted for brevity
        // end::pc-managed-state-dynamic-update-mapping-example[]
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getPriceCents() {
            return priceCents;
        }

        public void setPriceCents(Integer priceCents) {
            this.priceCents = priceCents;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}

