/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag;


import DialectChecks.SupportsNoColumnInsert;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related to operations on a PersistentBag.
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class PersistentBagTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testWriteMethodDirtying() {
        BagOwner parent = new BagOwner("root");
        BagOwner child = new BagOwner("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        BagOwner otherChild = new BagOwner("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.flush();
        // at this point, the list on parent has now been replaced with a PersistentBag...
        PersistentBag children = ((PersistentBag) (parent.getChildren()));
        Assert.assertFalse(children.remove(otherChild));
        Assert.assertFalse(children.isDirty());
        ArrayList otherCollection = new ArrayList();
        otherCollection.add(child);
        Assert.assertFalse(children.retainAll(otherCollection));
        Assert.assertFalse(children.isDirty());
        otherCollection = new ArrayList();
        otherCollection.add(otherChild);
        Assert.assertFalse(children.removeAll(otherCollection));
        Assert.assertFalse(children.isDirty());
        children.clear();
        session.delete(child);
        Assert.assertTrue(children.isDirty());
        session.flush();
        children.clear();
        Assert.assertFalse(children.isDirty());
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testMergePersistentEntityWithNewOneToManyElements() {
        Order order = new Order();
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(order);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        order = s.get(Order.class, order.getId());
        Item item1 = new Item();
        item1.setName("i1");
        Item item2 = new Item();
        item2.setName("i2");
        order.addItem(item1);
        order.addItem(item2);
        order = ((Order) (s.merge(order)));
        // s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        order = s.get(Order.class, order.getId());
        Assert.assertEquals(2, order.getItems().size());
        s.delete(order);
        s.getTransaction().commit();
        s.close();
    }
}

