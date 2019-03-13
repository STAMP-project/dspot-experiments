/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection.embeddable;


import java.util.Arrays;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11215")
public class NullPointerExceptionTest extends BaseEnversJPAFunctionalTestCase {
    private Integer productId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        this.productId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Product product = new Product(1, "Test");
            product.getItems().add(new Item("bread", null));
            entityManager.persist(product);
            return product.getId();
        });
        // Revision 2
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Type type = new Type(2, "T2");
            entityManager.persist(type);
            Product product = entityManager.find(.class, productId);
            product.getItems().add(new Item("bread2", type));
            entityManager.merge(product);
        });
        // Revision 3
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Product product = entityManager.find(.class, productId);
            product.getItems().remove(0);
            entityManager.merge(product);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(Product.class, productId));
        Assert.assertEquals(1, getAuditReader().find(Product.class, productId, 1).getItems().size());
        Assert.assertEquals(2, getAuditReader().find(Product.class, productId, 2).getItems().size());
        Assert.assertEquals(1, getAuditReader().find(Product.class, productId, 3).getItems().size());
    }

    @Test
    public void testRevision1() {
        final Product product = getAuditReader().find(Product.class, productId, 1);
        Assert.assertEquals(1, product.getItems().size());
        Assert.assertEquals("bread", product.getItems().get(0).getName());
    }

    @Test
    public void testRevision2() {
        final Product product = getAuditReader().find(Product.class, productId, 2);
        Assert.assertEquals(2, product.getItems().size());
        Assert.assertEquals("bread", product.getItems().get(0).getName());
        Assert.assertEquals("bread2", product.getItems().get(1).getName());
        Assert.assertEquals(new Type(2, "T2"), product.getItems().get(1).getType());
    }

    @Test
    public void testRevision3() {
        final Product product = getAuditReader().find(Product.class, productId, 3);
        Assert.assertEquals(1, product.getItems().size());
        Assert.assertEquals("bread2", product.getItems().get(0).getName());
        Assert.assertEquals(new Type(2, "T2"), product.getItems().get(0).getType());
    }
}

