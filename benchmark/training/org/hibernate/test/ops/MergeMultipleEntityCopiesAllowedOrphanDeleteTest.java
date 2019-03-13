/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests merging multiple detached representations of the same entity (allowed)
 * where some associations include cascade="delete-orphan"
 *
 * @author Gail Badner
 */
public class MergeMultipleEntityCopiesAllowedOrphanDeleteTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-9240")
    public void testTopLevelUnidirOneToManyBackrefWithNewElement() {
        Item item1 = new Item();
        item1.setName("item1 name");
        SubItem subItem1 = new SubItem();
        subItem1.setName("subItem1 name");
        item1.getSubItemsBackref().add(subItem1);
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
        Assert.assertFalse(Hibernate.isInitialized(item1_1.getSubItemsBackref()));
        Category category = new Category();
        category.setName("category");
        SubItem subItem2 = new SubItem();
        subItem2.setName("subItem2 name");
        item1.getSubItemsBackref().add(subItem2);
        item1.setCategory(category);
        category.setExampleItem(item1_1);
        s = openSession();
        tx = s.beginTransaction();
        // The following will fail due to PropertyValueException because item1  will
        // be removed from the inverted merge map when the operation cascades to item1_1.
        Item item1Merged = ((Item) (s.merge(item1)));
        // top-level collection should win
        Assert.assertEquals(2, item1.getSubItemsBackref().size());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        item1 = ((Item) (s.get(Item.class, item1.getId())));
        Assert.assertEquals(2, item1.getSubItemsBackref().size());
        tx.commit();
        s.close();
        cleanup();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9239")
    public void testNestedUnidirOneToManyBackrefWithNewElement() {
        Item item1 = new Item();
        item1.setName("item1 name");
        SubItem subItem1 = new SubItem();
        subItem1.setName("subItem1 name");
        item1.getSubItemsBackref().add(subItem1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(item1);
        tx.commit();
        s.close();
        // get another representation of item1
        s = openSession();
        tx = s.beginTransaction();
        Item item1_1 = ((Item) (s.get(Item.class, item1.getId())));
        Hibernate.initialize(item1_1.getSubItemsBackref());
        tx.commit();
        s.close();
        Category category = new Category();
        category.setName("category");
        item1.setCategory(category);
        // Add a new SubItem to the Item representation that will be in a nested association.
        SubItem subItem2 = new SubItem();
        subItem2.setName("subItem2 name");
        item1_1.getSubItemsBackref().add(subItem2);
        category.setExampleItem(item1_1);
        s = openSession();
        tx = s.beginTransaction();
        Item item1Merged = ((Item) (s.merge(item1)));
        // The resulting collection should contain the added element
        Assert.assertEquals(2, item1Merged.getSubItemsBackref().size());
        Assert.assertEquals("subItem1 name", item1Merged.getSubItemsBackref().get(0).getName());
        Assert.assertEquals("subItem2 name", item1Merged.getSubItemsBackref().get(1).getName());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        item1 = ((Item) (s.get(Item.class, item1.getId())));
        Assert.assertEquals(2, item1.getSubItemsBackref().size());
        Assert.assertEquals("subItem1 name", item1.getSubItemsBackref().get(0).getName());
        Assert.assertEquals("subItem2 name", item1.getSubItemsBackref().get(1).getName());
        tx.commit();
        s.close();
        cleanup();
    }

    // @FailureExpected( jiraKey = "HHH-9106" )
    @Test
    public void testTopLevelUnidirOneToManyBackrefWithRemovedElement() {
        Item item1 = new Item();
        item1.setName("item1 name");
        SubItem subItem1 = new SubItem();
        subItem1.setName("subItem1 name");
        item1.getSubItemsBackref().add(subItem1);
        SubItem subItem2 = new SubItem();
        subItem2.setName("subItem2 name");
        item1.getSubItemsBackref().add(subItem2);
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
        Assert.assertFalse(Hibernate.isInitialized(item1_1.getSubItemsBackref()));
        Category category = new Category();
        category.setName("category");
        item1.setCategory(category);
        category.setExampleItem(item1_1);
        // remove subItem1 from top-level Item
        item1.getSubItemsBackref().remove(subItem1);
        s = openSession();
        tx = s.beginTransaction();
        Item item1Merged = ((Item) (s.merge(item1)));
        // element should be removed
        Assert.assertEquals(1, item1Merged.getSubItemsBackref().size());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        item1 = ((Item) (s.get(Item.class, item1.getId())));
        Assert.assertEquals(1, item1.getSubItemsBackref().size());
        // because cascade includes "delete-orphan" the removed SubItem should have been deleted.
        subItem1 = ((SubItem) (s.get(SubItem.class, subItem1.getId())));
        Assert.assertNull(subItem1);
        tx.commit();
        cleanup();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9239")
    public void testNestedUnidirOneToManyBackrefWithRemovedElement() {
        Item item1 = new Item();
        item1.setName("item1 name");
        SubItem subItem1 = new SubItem();
        subItem1.setName("subItem1 name");
        item1.getSubItemsBackref().add(subItem1);
        SubItem subItem2 = new SubItem();
        subItem2.setName("subItem2 name");
        item1.getSubItemsBackref().add(subItem2);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(item1);
        tx.commit();
        s.close();
        // get another representation of item1
        s = openSession();
        tx = s.beginTransaction();
        Item item1_1 = ((Item) (s.get(Item.class, item1.getId())));
        Hibernate.initialize(item1_1.getSubItemsBackref());
        tx.commit();
        s.close();
        // remove subItem1 from the nested Item
        item1_1.getSubItemsBackref().remove(subItem1);
        Category category = new Category();
        category.setName("category");
        item1.setCategory(category);
        category.setExampleItem(item1_1);
        s = openSession();
        tx = s.beginTransaction();
        Item item1Merged = ((Item) (s.merge(item1)));
        // the element should have been removed
        Assert.assertEquals(1, item1Merged.getSubItemsBackref().size());
        Assert.assertTrue(item1Merged.getSubItemsBackref().contains(subItem2));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        item1 = ((Item) (s.get(Item.class, item1.getId())));
        Assert.assertEquals(1, item1.getSubItemsBackref().size());
        Assert.assertTrue(item1.getSubItemsBackref().contains(subItem2));
        // because cascade includes "delete-orphan" the removed SubItem should have been deleted.
        subItem1 = ((SubItem) (s.get(SubItem.class, subItem1.getId())));
        Assert.assertNull(subItem1);
        tx.commit();
        s.close();
        cleanup();
    }

    // @FailureExpected( jiraKey = "HHH-9106" )
    @Test
    public void testTopLevelUnidirOneToManyNoBackrefWithNewElement() {
        Category category1 = new Category();
        category1.setName("category1 name");
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setName("subCategory1 name");
        category1.getSubCategories().add(subCategory1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(category1);
        tx.commit();
        s.close();
        // get another representation of category1
        s = openSession();
        tx = s.beginTransaction();
        Category category1_1 = ((Category) (s.get(Category.class, category1.getId())));
        tx.commit();
        s.close();
        Assert.assertFalse(Hibernate.isInitialized(category1_1.getSubCategories()));
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setName("subCategory2 name");
        category1.getSubCategories().add(subCategory2);
        Item item = new Item();
        item.setName("item");
        category1.setExampleItem(item);
        item.setCategory(category1_1);
        s = openSession();
        tx = s.beginTransaction();
        Category category1Merged = ((Category) (s.merge(category1)));
        Assert.assertEquals(2, category1Merged.getSubCategories().size());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        category1 = ((Category) (s.get(Category.class, category1.getId())));
        Assert.assertEquals(2, category1.getSubCategories().size());
        tx.commit();
        s.close();
        cleanup();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9239")
    public void testNestedUnidirOneToManyNoBackrefWithNewElement() {
        Category category1 = new Category();
        category1.setName("category1 name");
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setName("subCategory1 name");
        category1.getSubCategories().add(subCategory1);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(category1);
        tx.commit();
        s.close();
        // get another representation of category1
        s = openSession();
        tx = s.beginTransaction();
        Category category1_1 = ((Category) (s.get(Category.class, category1.getId())));
        Hibernate.initialize(category1_1.getSubCategories());
        tx.commit();
        s.close();
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setName("subCategory2 name");
        category1_1.getSubCategories().add(subCategory2);
        Item item = new Item();
        item.setName("item");
        category1.setExampleItem(item);
        item.setCategory(category1_1);
        s = openSession();
        tx = s.beginTransaction();
        Category category1Merged = ((Category) (s.merge(category1)));
        // new element should be there
        Assert.assertEquals(2, category1Merged.getSubCategories().size());
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        category1 = ((Category) (s.get(Category.class, category1.getId())));
        Assert.assertEquals(2, category1.getSubCategories().size());
        tx.commit();
        s.close();
        cleanup();
    }

    // @FailureExpected( jiraKey = "HHH-9106" )
    @Test
    public void testTopLevelUnidirOneToManyNoBackrefWithRemovedElement() {
        Category category1 = new Category();
        category1.setName("category1 name");
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setName("subCategory1 name");
        category1.getSubCategories().add(subCategory1);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setName("subCategory2 name");
        category1.getSubCategories().add(subCategory2);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(category1);
        tx.commit();
        s.close();
        // get another representation of category1
        s = openSession();
        tx = s.beginTransaction();
        Category category1_1 = ((Category) (s.get(Category.class, category1.getId())));
        tx.commit();
        s.close();
        Assert.assertFalse(Hibernate.isInitialized(category1_1.getSubCategories()));
        Item item = new Item();
        item.setName("item");
        category1.setExampleItem(item);
        item.setCategory(category1_1);
        category1.getSubCategories().remove(subCategory1);
        s = openSession();
        tx = s.beginTransaction();
        Category category1Merged = ((Category) (s.merge(category1)));
        Assert.assertEquals(1, category1Merged.getSubCategories().size());
        Assert.assertTrue(category1Merged.getSubCategories().contains(subCategory2));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        category1 = ((Category) (s.get(Category.class, category1.getId())));
        Assert.assertEquals(1, category1.getSubCategories().size());
        Assert.assertTrue(category1.getSubCategories().contains(subCategory2));
        subCategory1 = ((SubCategory) (s.get(SubCategory.class, subCategory1.getId())));
        Assert.assertNull(subCategory1);
        tx.commit();
        s.close();
        cleanup();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-9239")
    public void testNestedUnidirOneToManyNoBackrefWithRemovedElement() {
        Category category1 = new Category();
        category1.setName("category1 name");
        SubCategory subCategory1 = new SubCategory();
        subCategory1.setName("subCategory1 name");
        category1.getSubCategories().add(subCategory1);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setName("subCategory2 name");
        category1.getSubCategories().add(subCategory2);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(category1);
        tx.commit();
        s.close();
        // get another representation of category1
        s = openSession();
        tx = s.beginTransaction();
        Category category1_1 = ((Category) (s.get(Category.class, category1.getId())));
        Hibernate.initialize(category1_1.getSubCategories());
        tx.commit();
        s.close();
        category1_1.getSubCategories().remove(subCategory2);
        Item item = new Item();
        item.setName("item");
        category1.setExampleItem(item);
        item.setCategory(category1_1);
        s = openSession();
        tx = s.beginTransaction();
        Category category1Merged = ((Category) (s.merge(category1)));
        Assert.assertEquals(1, category1Merged.getSubCategories().size());
        Assert.assertTrue(category1Merged.getSubCategories().contains(subCategory2));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        category1 = ((Category) (s.get(Category.class, category1.getId())));
        Assert.assertEquals(1, category1.getSubCategories().size());
        Assert.assertTrue(category1.getSubCategories().contains(subCategory2));
        subCategory1 = ((SubCategory) (s.get(SubCategory.class, subCategory1.getId())));
        Assert.assertNull(subCategory1);
        tx.commit();
        s.close();
        cleanup();
    }
}

