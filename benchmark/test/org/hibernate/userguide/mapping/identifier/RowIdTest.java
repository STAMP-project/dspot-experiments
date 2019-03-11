/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.RowId;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-rowid-mapping[]
@RequiresDialect(Oracle8iDialect.class)
public class RowIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Product product = new org.hibernate.userguide.mapping.identifier.Product();
            product.setId(1L);
            product.setName("Mobile phone");
            product.setNumber("123-456-7890");
            entityManager.persist(product);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::identifiers-rowid-example[]
            org.hibernate.userguide.mapping.identifier.Product product = entityManager.find(.class, 1L);
            product.setName("Smart phone");
            // end::identifiers-rowid-example[]
        });
    }

    // tag::identifiers-rowid-mapping[]
    // tag::identifiers-rowid-mapping[]
    @Entity(name = "Product")
    @RowId("ROWID")
    public static class Product {
        @Id
        private Long id;

        @Column(name = "`name`")
        private String name;

        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::identifiers-rowid-mapping[]
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

