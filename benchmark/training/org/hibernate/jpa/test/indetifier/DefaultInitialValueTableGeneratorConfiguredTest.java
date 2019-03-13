/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.indetifier;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class DefaultInitialValueTableGeneratorConfiguredTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTheFirstGeneratedIdIsEqualToTableGeneratorInitialValuePlusOne() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.indetifier.Product product = new org.hibernate.jpa.test.indetifier.Product();
            product.setName("Hibernate");
            entityManager.persist(product);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.indetifier.Product product = entityManager.find(.class, 1L);
            assertThat(product, notNullValue());
        });
    }

    @Test
    public void testTheGeneratedIdValuesAreCorrect() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (long i = 0; i < 3; i++) {
                org.hibernate.jpa.test.indetifier.Product product = new org.hibernate.jpa.test.indetifier.Product();
                product.setName(("Hibernate " + i));
                entityManager.persist(product);
            }
        });
        Session session = getOrCreateEntityManager().unwrap(Session.class);
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                ResultSet resultSet = connection.createStatement().executeQuery("select product_id from table_identifier");
                resultSet.next();
                int productIdValue = resultSet.getInt(1);
                Assert.assertThat(productIdValue, Is.is(10));
            }
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.jpa.test.indetifier.Product> products = entityManager.createQuery("from Product p order by id ").getResultList();
            assertThat(products.size(), is(3));
            assertThat(products.get(0).getId(), is(1L));
            assertThat(products.get(1).getId(), is(2L));
            assertThat(products.get(2).getId(), is(3L));
        });
    }

    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
        @TableGenerator(name = "table-generator", table = "table_identifier", pkColumnName = "table_name", valueColumnName = "product_id", allocationSize = 5)
        private Long id;

        private String name;

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

