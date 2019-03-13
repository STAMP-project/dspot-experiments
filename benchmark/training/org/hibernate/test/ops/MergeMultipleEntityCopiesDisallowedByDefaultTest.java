/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests merging multiple detached representations of the same entity using
 * a the default (that does not allow this).
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9106")
public class MergeMultipleEntityCopiesDisallowedByDefaultTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCascadeFromDetachedToNonDirtyRepresentations() {
        Item item1 = new Item();
        item1.setName("item1");
        Hoarder hoarder = new Hoarder();
        hoarder.setName("joe");
        Session s = openSession();
        Transaction tx = session.beginTransaction();
        s.persist(item1);
        s.persist(hoarder);
        tx.commit();
        s.close();
        // Get another representation of the same Item from a different session.
        s = openSession();
        Item item1_1 = ((Item) (s.get(Item.class, item1.getId())));
        s.close();
        // item1_1 and item1_2 are unmodified representations of the same persistent entity.
        Assert.assertFalse((item1 == item1_1));
        Assert.assertTrue(item1.equals(item1_1));
        // Update hoarder (detached) to references both representations.
        hoarder.getItems().add(item1);
        hoarder.setFavoriteItem(item1_1);
        s = openSession();
        tx = s.beginTransaction();
        try {
            hoarder = ((Hoarder) (s.merge(hoarder)));
            TestCase.fail("should have failed due IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            tx.rollback();
            s.close();
        }
        cleanup();
    }

    @Test
    public void testTopLevelManyToOneManagedNestedIsDetached() {
        Item item1 = new Item();
        item1.setName("item1 name");
        Category category = new Category();
        category.setName("category");
        item1.setCategory(category);
        category.setExampleItem(item1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(item1);
        tx.commit();
        s.close();
        // get another representation of item1
        s = openSession();
        tx = s.beginTransaction();
        Item item1_1 = ((Item) (s.get(Item.class, item1.getId())));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Item item1Merged = ((Item) (s.merge(item1)));
        item1Merged.setCategory(category);
        category.setExampleItem(item1_1);
        // now item1Merged is managed and it has a nested detached item
        try {
            s.merge(item1Merged);
            TestCase.fail("should have failed due IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            tx.rollback();
            s.close();
        }
        cleanup();
    }
}

