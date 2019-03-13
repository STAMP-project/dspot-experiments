/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-annotation-explicit-example[]
public class ExplicitBasicTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.id = 1;
            entityManager.persist(product);
        });
    }

    // tag::basic-annotation-explicit-example[]
    @Entity(name = "Product")
    public class Product {
        @Id
        @Basic
        private Integer id;

        @Basic
        private String sku;

        @Basic
        private String name;

        @Basic
        private String description;
    }
}

