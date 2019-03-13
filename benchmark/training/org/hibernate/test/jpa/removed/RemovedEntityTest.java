/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.removed;


import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.test.jpa.AbstractJPATest;
import org.hibernate.test.jpa.Item;
import org.hibernate.test.jpa.Part;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class RemovedEntityTest extends AbstractJPATest {
    @Test
    public void testRemoveThenContains() {
        Session s = openSession();
        s.beginTransaction();
        Item item = new Item();
        item.setName("dummy");
        s.persist(item);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(item);
        boolean contains = s.contains(item);
        s.getTransaction().commit();
        s.close();
        Assert.assertFalse("expecting removed entity to not be contained", contains);
    }

    @Test
    public void testRemoveThenGet() {
        Session s = openSession();
        s.beginTransaction();
        Item item = new Item();
        item.setName("dummy");
        s.persist(item);
        s.getTransaction().commit();
        s.close();
        Long id = item.getId();
        s = openSession();
        s.beginTransaction();
        s.delete(item);
        item = ((Item) (s.get(Item.class, id)));
        s.getTransaction().commit();
        s.close();
        Assert.assertNull("expecting removed entity to be returned as null from get()", item);
    }

    @Test
    public void testRemoveThenSave() {
        Session s = openSession();
        s.beginTransaction();
        Item item = new Item();
        item.setName("dummy");
        s.persist(item);
        s.getTransaction().commit();
        s.close();
        Long id = item.getId();
        s = openSession();
        s.beginTransaction();
        item = ((Item) (s.get(Item.class, id)));
        String sessionAsString = s.toString();
        s.delete(item);
        Item item2 = ((Item) (s.get(Item.class, id)));
        Assert.assertNull("expecting removed entity to be returned as null from get()", item2);
        s.persist(item);
        Assert.assertEquals("expecting session to be as it was before", sessionAsString, s.toString());
        item.setName("Rescued");
        item = ((Item) (s.get(Item.class, id)));
        Assert.assertNotNull("expecting rescued entity to be returned from get()", item);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        item = ((Item) (s.get(Item.class, id)));
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull("expecting removed entity to be returned as null from get()", item);
        Assert.assertEquals("Rescued", item.getName());
        // clean up
        s = openSession();
        s.beginTransaction();
        s.delete(item);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveThenSaveWithCascades() {
        Session s = openSession();
        s.beginTransaction();
        Item item = new Item();
        item.setName("dummy");
        Part part = new Part(item, "child", "1234", BigDecimal.ONE);
        // persist cascades to part
        s.persist(item);
        // delete cascades to part also
        s.delete(item);
        Assert.assertFalse("the item is contained in the session after deletion", s.contains(item));
        Assert.assertFalse("the part is contained in the session after deletion", s.contains(part));
        // now try to persist again as a "unschedule removal" operation
        s.persist(item);
        Assert.assertTrue("the item is contained in the session after deletion", s.contains(item));
        Assert.assertTrue("the part is contained in the session after deletion", s.contains(part));
        s.getTransaction().commit();
        s.close();
        // clean up
        s = openSession();
        s.beginTransaction();
        s.delete(item);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveChildThenFlushWithCascadePersist() {
        Session s = openSession();
        s.beginTransaction();
        Item item = new Item();
        item.setName("dummy");
        Part child = new Part(item, "child", "1234", BigDecimal.ONE);
        // persist cascades to part
        s.persist(item);
        // delete the part
        s.delete(child);
        Assert.assertFalse("the child is contained in the session, since it is deleted", s.contains(child));
        // now try to flush, which will attempt to cascade persist again to child.
        s.flush();
        Assert.assertTrue("Now it is consistent again since if was cascade-persisted by the flush()", s.contains(child));
        s.getTransaction().commit();
        s.close();
        // clean up
        s = openSession();
        s.beginTransaction();
        s.delete(item);
        s.getTransaction().commit();
        s.close();
    }
}

