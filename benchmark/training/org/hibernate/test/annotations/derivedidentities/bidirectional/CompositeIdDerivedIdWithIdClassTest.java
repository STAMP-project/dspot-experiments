/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.bidirectional;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class CompositeIdDerivedIdWithIdClassTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11328")
    public void testMergeTransientIdManyToOne() throws Exception {
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart transientCart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart1");
        transientCart.addLineItem(new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "description2", transientCart));
        // assertion for HHH-11274 - checking for exception
        final Object identifier = getIdentifier(transientCart.getLineItems().get(0));
        // merge ID with transient many-to-one
        Session s = openSession();
        s.getTransaction().begin();
        s.merge(transientCart);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart updatedCart = s.get(CompositeIdDerivedIdWithIdClassTest.ShoppingCart.class, "cart1");
        // assertion for HHH-11274 - checking for exception
        new org.hibernate.jpa.internal.PersistenceUnitUtilImpl(sessionFactory()).getIdentifier(transientCart.getLineItems().get(0));
        Assert.assertEquals(1, updatedCart.getLineItems().size());
        Assert.assertEquals("description2", updatedCart.getLineItems().get(0).getDescription());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10623")
    public void testMergeDetachedIdManyToOne() throws Exception {
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart1");
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(cart);
        s.getTransaction().commit();
        s.close();
        // cart is detached now
        CompositeIdDerivedIdWithIdClassTest.LineItem lineItem = new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "description2", cart);
        cart.addLineItem(lineItem);
        // merge lineItem with an ID with detached many-to-one
        s = openSession();
        s.getTransaction().begin();
        s.merge(lineItem);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart updatedCart = s.get(CompositeIdDerivedIdWithIdClassTest.ShoppingCart.class, "cart1");
        Assert.assertEquals(1, updatedCart.getLineItems().size());
        Assert.assertEquals("description2", updatedCart.getLineItems().get(0).getDescription());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12007")
    public void testBindTransientEntityWithTransientKeyManyToOne() {
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart");
        CompositeIdDerivedIdWithIdClassTest.LineItem item = new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "desc", cart);
        Session s = openSession();
        s.getTransaction().begin();
        String cartId = s.createQuery("select c.id from Cart c left join c.lineItems i where i = :item", String.class).setParameter("item", item).uniqueResult();
        Assert.assertNull(cartId);
        Assert.assertFalse(s.contains(item));
        Assert.assertFalse(s.contains(cart));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12007")
    public void testBindTransientEntityWithPersistentKeyManyToOne() {
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart");
        CompositeIdDerivedIdWithIdClassTest.LineItem item = new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "desc", cart);
        Session s = openSession();
        s.getTransaction().begin();
        session.persist(cart);
        String cartId = s.createQuery("select c.id from Cart c left join c.lineItems i where i = :item", String.class).setParameter("item", item).uniqueResult();
        Assert.assertNull(cartId);
        Assert.assertFalse(s.contains(item));
        Assert.assertTrue(s.contains(cart));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12007")
    public void testBindTransientEntityWithDetachedKeyManyToOne() {
        Session s = openSession();
        s.getTransaction().begin();
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart");
        s.getTransaction().commit();
        s.close();
        CompositeIdDerivedIdWithIdClassTest.LineItem item = new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "desc", cart);
        s = openSession();
        s.getTransaction().begin();
        String cartId = s.createQuery("select c.id from Cart c left join c.lineItems i where i = :item", String.class).setParameter("item", item).uniqueResult();
        Assert.assertNull(cartId);
        Assert.assertFalse(s.contains(item));
        Assert.assertFalse(s.contains(cart));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12007")
    public void testBindTransientEntityWithCopiedKeyManyToOne() {
        Session s = openSession();
        s.getTransaction().begin();
        CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart = new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart");
        s.getTransaction().commit();
        s.close();
        CompositeIdDerivedIdWithIdClassTest.LineItem item = new CompositeIdDerivedIdWithIdClassTest.LineItem(0, "desc", new CompositeIdDerivedIdWithIdClassTest.ShoppingCart("cart"));
        s = openSession();
        s.getTransaction().begin();
        String cartId = s.createQuery("select c.id from Cart c left join c.lineItems i where i = :item", String.class).setParameter("item", item).uniqueResult();
        Assert.assertNull(cartId);
        Assert.assertFalse(s.contains(item));
        Assert.assertFalse(s.contains(cart));
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "Cart")
    public static class ShoppingCart implements Serializable {
        @Id
        @Column(name = "id", nullable = false)
        private String id;

        @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<CompositeIdDerivedIdWithIdClassTest.LineItem> lineItems = new ArrayList<>();

        protected ShoppingCart() {
        }

        public ShoppingCart(String id) {
            this.id = id;
        }

        public List<CompositeIdDerivedIdWithIdClassTest.LineItem> getLineItems() {
            return lineItems;
        }

        public void addLineItem(CompositeIdDerivedIdWithIdClassTest.LineItem lineItem) {
            lineItems.add(lineItem);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            CompositeIdDerivedIdWithIdClassTest.ShoppingCart that = ((CompositeIdDerivedIdWithIdClassTest.ShoppingCart) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Entity(name = "LineItem")
    @IdClass(CompositeIdDerivedIdWithIdClassTest.LineItem.Pk.class)
    public static class LineItem implements Serializable {
        @Id
        @Column(name = "item_seq_number", nullable = false)
        private Integer sequenceNumber;

        @Column(name = "description")
        private String description;

        @Id
        @ManyToOne
        @JoinColumn(name = "cart_id")
        private CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart;

        protected LineItem() {
        }

        public LineItem(Integer sequenceNumber, String description, CompositeIdDerivedIdWithIdClassTest.ShoppingCart cart) {
            this.sequenceNumber = sequenceNumber;
            this.description = description;
            this.cart = cart;
        }

        public Integer getSequenceNumber() {
            return sequenceNumber;
        }

        public CompositeIdDerivedIdWithIdClassTest.ShoppingCart getCart() {
            return cart;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CompositeIdDerivedIdWithIdClassTest.LineItem))
                return false;

            CompositeIdDerivedIdWithIdClassTest.LineItem lineItem = ((CompositeIdDerivedIdWithIdClassTest.LineItem) (o));
            return (Objects.equals(getSequenceNumber(), lineItem.getSequenceNumber())) && (Objects.equals(getCart(), lineItem.getCart()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSequenceNumber(), getCart());
        }

        public String getDescription() {
            return description;
        }

        public static class Pk implements Serializable {
            public Integer sequenceNumber;

            public String cart;

            @Override
            public boolean equals(Object o) {
                if ((this) == o)
                    return true;

                if (!(o instanceof CompositeIdDerivedIdWithIdClassTest.LineItem.Pk))
                    return false;

                CompositeIdDerivedIdWithIdClassTest.LineItem.Pk pk = ((CompositeIdDerivedIdWithIdClassTest.LineItem.Pk) (o));
                return (Objects.equals(sequenceNumber, pk.sequenceNumber)) && (Objects.equals(cart, pk.cart));
            }

            @Override
            public int hashCode() {
                return Objects.hash(sequenceNumber, cart);
            }
        }
    }
}

