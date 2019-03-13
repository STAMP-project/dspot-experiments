/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.io.InputStream;
import java.sql.Blob;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-blob-example[]
public class BlobTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        Integer productId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::basic-blob-persist-example[]
            byte[] image = new byte[]{ 1, 2, 3 };
            final org.hibernate.userguide.mapping.basic.Product product = new org.hibernate.userguide.mapping.basic.Product();
            product.setId(1);
            product.setName("Mobile phone");
            product.setImage(BlobProxy.generateProxy(image));
            entityManager.persist(product);
            // end::basic-blob-persist-example[]
            return product.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                // tag::basic-blob-find-example[]
                org.hibernate.userguide.mapping.basic.Product product = entityManager.find(.class, productId);
                try (InputStream inputStream = product.getImage().getBinaryStream()) {
                    assertArrayEquals(new byte[]{ 1, 2, 3 }, toBytes(inputStream));
                }
                // end::basic-blob-find-example[]
            } catch ( e) {
                fail(e.getMessage());
            }
        });
    }

    // tag::basic-blob-example[]
    // tag::basic-blob-example[]
    @Entity(name = "Product")
    public static class Product {
        @Id
        private Integer id;

        private String name;

        @Lob
        private Blob image;

        // Getters and setters are omitted for brevity
        // end::basic-blob-example[]
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

        public Blob getImage() {
            return image;
        }

        public void setImage(Blob image) {
            this.image = image;
        }
    }
}

