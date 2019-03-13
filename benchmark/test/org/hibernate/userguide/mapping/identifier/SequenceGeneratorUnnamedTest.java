/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import DialectChecks.SupportsSequences;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-generators-sequence-mapping-example[]
@RequiresDialectFeature(SupportsSequences.class)
public class SequenceGeneratorUnnamedTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (long i = 1; i <= 5; i++) {
                if ((i % 3) == 0) {
                    entityManager.flush();
                }
                org.hibernate.userguide.mapping.identifier.Product product = new org.hibernate.userguide.mapping.identifier.Product();
                product.setName(String.format("Product %d", i));
                entityManager.persist(product);
            }
        });
    }

    // tag::identifiers-generators-sequence-mapping-example[]
    // tag::identifiers-generators-sequence-mapping-example[]
    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        @Column(name = "product_name")
        private String name;

        // Getters and setters are omitted for brevity
        // end::identifiers-generators-sequence-mapping-example[]
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
    }
}

