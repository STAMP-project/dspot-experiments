/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.util.BitSet;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.TypeDef;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-custom-type-BitSetTypeDef-mapping-example[]
public class BitSetTypeDefTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        // tag::basic-custom-type-BitSetTypeDef-persistence-example[]
        BitSet bitSet = BitSet.valueOf(new long[]{ 1, 2, 3 });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1);
            product.setBitSet(bitSet);
            session.persist(product);
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.basic.Product product = session.get(.class, 1);
            assertEquals(bitSet, product.getBitSet());
        });
        // end::basic-custom-type-BitSetTypeDef-persistence-example[]
    }

    // tag::basic-custom-type-BitSetTypeDef-mapping-example[]
    // tag::basic-custom-type-BitSetTypeDef-mapping-example[]
    @Entity(name = "Product")
    @TypeDef(name = "bitset", defaultForType = BitSet.class, typeClass = BitSetType.class)
    public static class Product {
        @Id
        private Integer id;

        private BitSet bitSet;

        // Getters and setters are omitted for brevity
        // end::basic-custom-type-BitSetTypeDef-mapping-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public BitSet getBitSet() {
            return bitSet;
        }

        public void setBitSet(BitSet bitSet) {
            this.bitSet = bitSet;
        }
    }
}

