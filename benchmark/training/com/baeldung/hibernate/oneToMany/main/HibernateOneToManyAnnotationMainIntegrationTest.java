package com.baeldung.hibernate.oneToMany.main;


import com.baeldung.hibernate.oneToMany.model.Cart;
import com.baeldung.hibernate.oneToMany.model.Items;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class HibernateOneToManyAnnotationMainIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    public HibernateOneToManyAnnotationMainIntegrationTest() {
    }

    @Test
    public void givenSession_checkIfDatabaseIsEmpty() {
        Cart cart = ((Cart) (session.get(Cart.class, new Long(1))));
        Assert.assertNull(cart);
    }

    @Test
    public void givenSession_checkIfDatabaseIsPopulated_afterCommit() {
        Cart cart = new Cart();
        Set<Items> cartItems = new HashSet<>();
        cartItems = cart.getItems();
        Assert.assertNull(cartItems);
        Items item1 = new Items();
        item1.setCart(cart);
        Assert.assertNotNull(item1);
        Set<Items> itemsSet = new HashSet<Items>();
        itemsSet.add(item1);
        Assert.assertNotNull(itemsSet);
        cart.setItems(itemsSet);
        Assert.assertNotNull(cart);
        session.persist(cart);
        session.getTransaction().commit();
        session.close();
        session = HibernateOneToManyAnnotationMainIntegrationTest.sessionFactory.openSession();
        session.beginTransaction();
        cart = ((Cart) (session.get(Cart.class, new Long(1))));
        Assert.assertNotNull(cart);
    }
}

