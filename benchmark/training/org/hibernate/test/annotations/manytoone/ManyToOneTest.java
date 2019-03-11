/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.manytoone;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.Company;
import org.hibernate.test.annotations.Customer;
import org.hibernate.test.annotations.Discount;
import org.hibernate.test.annotations.Flight;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class ManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEager() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Color c = new Color();
        c.setName("Yellow");
        s.persist(c);
        Car car = new Car();
        car.setBodyColor(c);
        s.persist(car);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        car = ((Car) (s.get(Car.class, car.getId())));
        tx.commit();
        s.close();
        Assert.assertNotNull(car);
        Assert.assertNotNull(car.getBodyColor());
        Assert.assertEquals("Yellow", car.getBodyColor().getName());
    }

    @Test
    public void testDefaultMetadata() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Color c = new Color();
        c.setName("Blue");
        s.persist(c);
        Car car = new Car();
        car.setBodyColor(c);
        s.persist(car);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        car = ((Car) (s.get(Car.class, car.getId())));
        Assert.assertNotNull(car);
        Assert.assertNotNull(car.getBodyColor());
        Assert.assertEquals(c.getId(), car.getBodyColor().getId());
        tx.rollback();
        s.close();
    }

    @Test
    public void testCreate() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Flight firstOne = new Flight();
        firstOne.setId(new Long(1));
        firstOne.setName("AF0101");
        firstOne.setDuration(new Long(1000));
        Company frenchOne = new Company();
        frenchOne.setName("Air France");
        firstOne.setCompany(frenchOne);
        s.persist(firstOne);
        tx.commit();
        s.close();
        Assert.assertNotNull("identity id should work", frenchOne.getId());
        s = openSession();
        tx = s.beginTransaction();
        firstOne = ((Flight) (s.get(Flight.class, new Long(1))));
        Assert.assertNotNull(firstOne.getCompany());
        Assert.assertEquals(frenchOne.getName(), firstOne.getCompany().getName());
        tx.commit();
        s.close();
    }

    @Test
    public void testCascade() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Discount discount = new Discount();
        discount.setDiscount(20.12);
        Customer customer = new Customer();
        Collection discounts = new ArrayList();
        discounts.add(discount);
        customer.setName("Quentin Tarantino");
        discount.setOwner(customer);
        customer.setDiscountTickets(discounts);
        s.persist(discount);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        discount = ((Discount) (s.get(Discount.class, discount.getId())));
        Assert.assertNotNull(discount);
        Assert.assertEquals(20.12, discount.getDiscount(), 0.01);
        Assert.assertNotNull(discount.getOwner());
        customer = new Customer();
        customer.setName("Clooney");
        discount.setOwner(customer);
        discounts = new ArrayList();
        discounts.add(discount);
        customer.setDiscountTickets(discounts);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        discount = ((Discount) (s.get(Discount.class, discount.getId())));
        Assert.assertNotNull(discount);
        Assert.assertNotNull(discount.getOwner());
        Assert.assertEquals("Clooney", discount.getOwner().getName());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        customer = ((Customer) (s.get(Customer.class, customer.getId())));
        s.delete(customer);
        tx.commit();
        s.close();
    }

    @Test
    public void testFetch() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Discount discount = new Discount();
        discount.setDiscount(20);
        Customer customer = new Customer();
        Collection discounts = new ArrayList();
        discounts.add(discount);
        customer.setName("Quentin Tarantino");
        discount.setOwner(customer);
        customer.setDiscountTickets(discounts);
        s.persist(discount);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        discount = ((Discount) (s.get(Discount.class, discount.getId())));
        Assert.assertNotNull(discount);
        Assert.assertFalse(Hibernate.isInitialized(discount.getOwner()));
        tx.commit();
        s = openSession();
        tx = s.beginTransaction();
        discount = ((Discount) (s.load(Discount.class, discount.getId())));
        Assert.assertNotNull(discount);
        Assert.assertFalse(Hibernate.isInitialized(discount.getOwner()));
        tx.commit();
        s = openSession();
        tx = s.beginTransaction();
        s.delete(s.get(Discount.class, discount.getId()));
        tx.commit();
        s.close();
    }

    @Test
    public void testCompositeFK() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        ParentPk ppk = new ParentPk();
        ppk.firstName = "John";
        ppk.lastName = "Doe";
        Parent p = new Parent();
        p.age = 45;
        p.id = ppk;
        s.persist(p);
        Child c = new Child();
        c.parent = p;
        s.persist(c);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        // FIXME: fix this when the small parser bug will be fixed
        Query q = s.createQuery(("from " + (Child.class.getName())));// + " c where c.parent.id.lastName = :lastName");

        // q.setString("lastName", p.id.lastName);
        List result = q.list();
        Assert.assertEquals(1, result.size());
        Child c2 = ((Child) (result.get(0)));
        Assert.assertEquals(c2.id, c.id);
        tx.commit();
        s.close();
    }

    @Test
    public void testImplicitCompositeFk() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Node n1 = new Node();
        n1.setDescription("Parent");
        NodePk n1pk = new NodePk();
        n1pk.setLevel(1);
        n1pk.setName("Root");
        n1.setId(n1pk);
        Node n2 = new Node();
        NodePk n2pk = new NodePk();
        n2pk.setLevel(2);
        n2pk.setName("Level 1: A");
        n2.setParent(n1);
        n2.setId(n2pk);
        s.persist(n2);
        tx.commit();
        s = openSession();
        tx = s.beginTransaction();
        n2 = ((Node) (s.get(Node.class, n2pk)));
        Assert.assertNotNull(n2);
        Assert.assertNotNull(n2.getParent());
        Assert.assertEquals(1, n2.getParent().getId().getLevel());
        tx.commit();
        s.close();
    }

    @Test
    public void testManyToOneNonPk() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Order order = new Order();
        order.setOrderNbr("123");
        s.persist(order);
        OrderLine ol = new OrderLine();
        ol.setItem("Mouse");
        ol.setOrder(order);
        s.persist(ol);
        s.flush();
        s.clear();
        ol = ((OrderLine) (s.get(OrderLine.class, ol.getId())));
        Assert.assertNotNull(ol.getOrder());
        Assert.assertEquals("123", ol.getOrder().getOrderNbr());
        Assert.assertTrue(ol.getOrder().getOrderLines().contains(ol));
        tx.rollback();
        s.close();
    }

    @Test
    public void testManyToOneNonPkSecondaryTable() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Order order = new Order();
        order.setOrderNbr("123");
        s.persist(order);
        OrderLine ol = new OrderLine();
        ol.setItem("Mouse");
        ol.setReplacementOrder(order);
        s.persist(ol);
        s.flush();
        s.clear();
        ol = ((OrderLine) (s.get(OrderLine.class, ol.getId())));
        Assert.assertNotNull(ol.getReplacementOrder());
        Assert.assertEquals("123", ol.getReplacementOrder().getOrderNbr());
        Assert.assertFalse(ol.getReplacementOrder().getOrderLines().contains(ol));
        tx.rollback();
        s.close();
    }

    @Test
    public void testTwoManyToOneNonPk() throws Exception {
        // 2 many to one non pk pointing to the same referencedColumnName should not fail
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Customer customer = new Customer();
        customer.userId = "123";
        Customer customer2 = new Customer();
        customer2.userId = "124";
        s.persist(customer2);
        s.persist(customer);
        Deal deal = new Deal();
        deal.from = customer;
        deal.to = customer2;
        s.persist(deal);
        s.flush();
        s.clear();
        deal = ((Deal) (s.get(Deal.class, deal.id)));
        Assert.assertNotNull(deal.from);
        Assert.assertNotNull(deal.to);
        tx.rollback();
        s.close();
    }

    @Test
    public void testFormulaOnOtherSide() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Frame frame = new Frame();
        frame.setName("Prada");
        s.persist(frame);
        Lens l = new Lens();
        l.setFocal(2.5F);
        l.setFrame(frame);
        s.persist(l);
        Lens r = new Lens();
        r.setFocal(1.2F);
        r.setFrame(frame);
        s.persist(r);
        s.flush();
        s.clear();
        frame = ((Frame) (s.get(Frame.class, frame.getId())));
        Assert.assertEquals(2, frame.getLenses().size());
        Assert.assertTrue(((frame.getLenses().iterator().next().getLength()) <= (1 / 1.2F)));
        Assert.assertTrue(((frame.getLenses().iterator().next().getLength()) >= (1 / 2.5F)));
        tx.rollback();
        s.close();
    }
}

