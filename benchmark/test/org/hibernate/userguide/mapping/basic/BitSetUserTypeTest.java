/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.util.BitSet;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import org.hibernate.annotations.Type;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-custom-type-BitSetUserType-mapping-example[]
public class BitSetUserTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
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
    }

    @Test
    public void testNativeQuery() {
        BitSet bitSet = BitSet.valueOf(new long[]{ 1, 2, 3 });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1);
            product.setBitSet(bitSet);
            session.persist(product);
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.basic.Product product = ((org.hibernate.userguide.mapping.basic.Product) (session.getNamedNativeQuery("find_person_by_bitset").setParameter("id", 1L).getSingleResult()));
            assertEquals(bitSet, product.getBitSet());
        });
    }

    // tag::basic-custom-type-BitSetUserType-mapping-example[]
    // tag::basic-custom-type-BitSetUserType-mapping-example[]
    @NamedNativeQuery(name = "find_person_by_bitset", query = "SELECT " + ((("   pr.id AS \"pr.id\", " + "   pr.bitset AS \"pr.bitset\" ") + "FROM Product pr ") + "WHERE pr.id = :id"), resultSetMapping = "Person")
    @SqlResultSetMapping(name = "Person", classes = @ConstructorResult(targetClass = BitSetUserTypeTest.Product.class, columns = { @ColumnResult(name = "pr.id"), @ColumnResult(name = "pr.bitset", type = BitSetUserType.class) }))
    @Entity(name = "Product")
    public static class Product {
        @Id
        private Integer id;

        @Type(type = "bitset")
        private BitSet bitSet;

        // Constructors, getters, and setters are omitted for brevity
        // end::basic-custom-type-BitSetUserType-mapping-example[]
        public Product() {
        }

        public Product(Number id, BitSet bitSet) {
            this.id = id.intValue();
            this.bitSet = bitSet;
        }

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

