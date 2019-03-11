/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.emops;


import javax.persistence.EntityManager;
import junit.framework.TestCase;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests merging multiple detached representations of the same entity when
 * not allowed by default.
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9106")
public class MergeMultipleEntityCopiesDisallowedByDefaultTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCascadeFromDetachedToNonDirtyRepresentations() {
        Item item1 = new Item();
        item1.setName("item1");
        Hoarder hoarder = new Hoarder();
        hoarder.setName("joe");
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(item1);
        em.persist(hoarder);
        em.getTransaction().commit();
        em.close();
        // Get another representation of the same Item from a different session.
        em = getOrCreateEntityManager();
        Item item1_1 = em.find(Item.class, item1.getId());
        em.close();
        // item1_1 and item1_2 are unmodified representations of the same persistent entity.
        Assert.assertFalse((item1 == item1_1));
        Assert.assertTrue(item1.equals(item1_1));
        // Update hoarder (detached) to references both representations.
        hoarder.getItems().add(item1);
        hoarder.setFavoriteItem(item1_1);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.merge(hoarder);
            TestCase.fail("should have failed due IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            em.getTransaction().rollback();
            em.close();
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
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(item1);
        em.getTransaction().commit();
        em.close();
        // get another representation of item1
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item item1_1 = em.find(Item.class, item1.getId());
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item item1Merged = em.merge(item1);
        item1Merged.setCategory(category);
        category.setExampleItem(item1_1);
        // now item1Merged is managed and it has a nested detached item
        try {
            em.merge(item1Merged);
            TestCase.fail("should have failed due IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
        cleanup();
    }
}

