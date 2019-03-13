/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.strategy;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * A {@link ValidityAuditStrategy} test that verifies that the {@code REVEND} field
 * for embedded component collection entries is updated when the component contains
 * {@code null} properties and is removed from the component collection.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11214")
public class ValidityAuditStrategyComponentCollectionRevEndTest extends BaseEnversJPAFunctionalTestCase {
    private Integer productId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        this.productId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.strategy.Product product = new org.hibernate.envers.test.integration.strategy.Product(1, "Test");
            product.getItems().add(new org.hibernate.envers.test.integration.strategy.Item("bread", null));
            entityManager.persist(product);
            return product.getId();
        });
        // Revision 2
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.strategy.Product product = entityManager.find(.class, productId);
            product.getItems().add(new org.hibernate.envers.test.integration.strategy.Item("bread2", 2));
            entityManager.merge(product);
        });
        // Revision 3
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.strategy.Product product = entityManager.find(.class, productId);
            product.getItems().remove(0);
            entityManager.merge(product);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(ValidityAuditStrategyComponentCollectionRevEndTest.Product.class, productId));
    }

    @Test
    public void testRevision1() {
        final ValidityAuditStrategyComponentCollectionRevEndTest.Product product = getAuditReader().find(ValidityAuditStrategyComponentCollectionRevEndTest.Product.class, productId, 1);
        Assert.assertEquals(1, product.getItems().size());
        Assert.assertEquals("bread", product.getItems().get(0).getName());
    }

    @Test
    public void testRevision2() {
        final ValidityAuditStrategyComponentCollectionRevEndTest.Product product = getAuditReader().find(ValidityAuditStrategyComponentCollectionRevEndTest.Product.class, productId, 2);
        Assert.assertEquals(2, product.getItems().size());
        Assert.assertEquals("bread", product.getItems().get(0).getName());
        Assert.assertEquals("bread2", product.getItems().get(1).getName());
    }

    @Test
    public void testRevision3() {
        final ValidityAuditStrategyComponentCollectionRevEndTest.Product product = getAuditReader().find(ValidityAuditStrategyComponentCollectionRevEndTest.Product.class, productId, 3);
        Assert.assertEquals(1, product.getItems().size());
        Assert.assertEquals("bread2", product.getItems().get(0).getName());
    }

    @Entity(name = "Product")
    @Audited
    public static class Product {
        @Id
        private Integer id;

        private String name;

        @ElementCollection
        @CollectionTable(name = "items", joinColumns = @JoinColumn(name = "productId"))
        @OrderColumn(name = "position")
        private List<ValidityAuditStrategyComponentCollectionRevEndTest.Item> items = new ArrayList<ValidityAuditStrategyComponentCollectionRevEndTest.Item>();

        Product() {
        }

        Product(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

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

        public List<ValidityAuditStrategyComponentCollectionRevEndTest.Item> getItems() {
            return items;
        }

        public void setItems(List<ValidityAuditStrategyComponentCollectionRevEndTest.Item> items) {
            this.items = items;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            result = (31 * result) + ((items) != null ? items.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object object) {
            if ((this) == object) {
                return true;
            }
            if ((object == null) | ((getClass()) != (object.getClass()))) {
                return false;
            }
            ValidityAuditStrategyComponentCollectionRevEndTest.Product that = ((ValidityAuditStrategyComponentCollectionRevEndTest.Product) (object));
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            if ((name) != null ? !(name.equals(that.name)) : (that.name) != null) {
                return false;
            }
            return !((items) != null ? !(items.equals(that.items)) : (that.items) != null);
        }
    }

    @Embeddable
    @Audited
    public static class Item {
        private String name;

        private Integer value;

        Item() {
        }

        Item(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + ((value) != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object object) {
            if ((this) == object) {
                return true;
            }
            if ((object == null) || ((getClass()) != (object.getClass()))) {
                return false;
            }
            ValidityAuditStrategyComponentCollectionRevEndTest.Item that = ((ValidityAuditStrategyComponentCollectionRevEndTest.Item) (object));
            if ((name) != null ? !(name.equals(that.name)) : (that.name) != null) {
                return false;
            }
            return !((value) != null ? !(value.equals(that.value)) : (that.value) != null);
        }
    }
}

