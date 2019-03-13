/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.list;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToStdout;
import org.junit.Assert;
import org.junit.Test;


/**
 * Originally developed to help diagnose HHH-10099 which reports a problem with @OrderColumn
 * not being bound
 *
 * @author Steve Ebersole
 */
public class ListMappingTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    public void testOrderColumnInNormalBiDirectonalModel() {
        Metadata metadata = addAnnotatedClass(ListMappingTest.LineItem.class).buildMetadata();
        Collection lineItemsBinding = metadata.getCollectionBindings().iterator().next();
        // make sure it was interpreted as a List (aka, as having an OrderColumn at all)
        MatcherAssert.assertThat(lineItemsBinding, CoreMatchers.instanceOf(List.class));
        org.hibernate.mapping.List asList = ((org.hibernate.mapping.List) (lineItemsBinding));
        // assert the OrderColumn details
        final Column positionColumn = ((Column) (asList.getIndex().getColumnIterator().next()));
        MatcherAssert.assertThat(positionColumn.getName(), CoreMatchers.equalTo("position"));
        // make sure the OrderColumn is part of the collection table
        Assert.assertTrue(asList.getCollectionTable().containsColumn(positionColumn));
        class TargetImpl extends GenerationTargetToStdout {
            boolean found = false;

            @Override
            public void accept(String action) {
                super.accept(action);
                if (action.matches("^create( (column|row))? table t_line_item.+")) {
                    if (action.contains("position")) {
                        found = true;
                    }
                }
            }
        }
        TargetImpl target = new TargetImpl();
        new org.hibernate.tool.schema.internal.SchemaCreatorImpl(ssr).doCreation(metadata, true, target);
        Assert.assertTrue(target.found);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Bi-directional model
    @Entity(name = "Order")
    @Table(name = "t_order")
    public static class Order {
        private Integer id;

        private List<ListMappingTest.LineItem> lineItems = new ArrayList<ListMappingTest.LineItem>();

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @OneToMany(mappedBy = "order")
        @OrderColumn(name = "position")
        public List<ListMappingTest.LineItem> getLineItems() {
            return lineItems;
        }

        public void setLineItems(List<ListMappingTest.LineItem> lineItems) {
            this.lineItems = lineItems;
        }
    }

    @Entity(name = "LineItem")
    @Table(name = "t_line_item")
    public static class LineItem {
        private Integer id;

        private ListMappingTest.Order order;

        private String product;

        private int quantity;

        private String discountCode;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ManyToOne(optional = false)
        @JoinColumn(name = "order_id")
        public ListMappingTest.Order getOrder() {
            return order;
        }

        public void setOrder(ListMappingTest.Order order) {
            this.order = order;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getDiscountCode() {
            return discountCode;
        }

        public void setDiscountCode(String discountCode) {
            this.discountCode = discountCode;
        }
    }
}

