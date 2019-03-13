/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.emops;


import javax.persistence.EntityManager;
import junit.framework.TestCase;
import org.hibernate.Hibernate;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EventSource;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests merging multiple detached representations of the same entity using a custom EntityCopyObserver.
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-9106")
public class MergeMultipleEntityCopiesCustomTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMergeMultipleEntityCopiesAllowed() {
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
        // Get another representation of the same Item from a different EntityManager.
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
        // the merge should succeed because it does not have Category copies.
        // (CustomEntityCopyObserver does not allow Category copies; it does allow Item copies)
        hoarder = em.merge(hoarder);
        Assert.assertEquals(1, hoarder.getItems().size());
        Assert.assertSame(hoarder.getFavoriteItem(), hoarder.getItems().iterator().next());
        Assert.assertEquals(item1.getId(), hoarder.getFavoriteItem().getId());
        Assert.assertEquals(item1.getCategory(), hoarder.getFavoriteItem().getCategory());
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        hoarder = em.find(Hoarder.class, hoarder.getId());
        Assert.assertEquals(1, hoarder.getItems().size());
        Assert.assertSame(hoarder.getFavoriteItem(), hoarder.getItems().iterator().next());
        Assert.assertEquals(item1.getId(), hoarder.getFavoriteItem().getId());
        Assert.assertEquals(item1.getCategory(), hoarder.getFavoriteItem().getCategory());
        em.getTransaction().commit();
        em.close();
        cleanup();
    }

    @Test
    public void testMergeMultipleEntityCopiesAllowedAndDisallowed() {
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
        // make sure item1_1.category is initialized
        Hibernate.initialize(item1_1.getCategory());
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item item1Merged = em.merge(item1);
        // make sure item1Merged.category is also managed
        Hibernate.initialize(item1Merged.getCategory());
        item1Merged.setCategory(category);
        category.setExampleItem(item1_1);
        // now item1Merged is managed and it has a nested detached item
        // and there is  multiple managed/detached Category objects
        try {
            // the following should fail because multiple copies of Category objects is not allowed by
            // CustomEntityCopyObserver
            em.merge(item1Merged);
            TestCase.fail("should have failed because CustomEntityCopyObserver does not allow multiple copies of a Category. ");
        } catch (IllegalStateException ex) {
            // expected
        } finally {
            em.getTransaction().rollback();
        }
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        item1 = em.find(Item.class, item1.getId());
        Assert.assertEquals(category.getName(), item1.getCategory().getName());
        Assert.assertSame(item1, item1.getCategory().getExampleItem());
        em.getTransaction().commit();
        em.close();
        cleanup();
    }

    public static class CustomEntityCopyObserver implements EntityCopyObserver {
        @Override
        public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
            if (Category.class.isInstance(managedEntity)) {
                throw new IllegalStateException(String.format("Entity copies of type [%s] not allowed", Category.class.getName()));
            }
        }

        @Override
        public void topLevelMergeComplete(EventSource session) {
        }

        @Override
        public void clear() {
        }
    }
}

