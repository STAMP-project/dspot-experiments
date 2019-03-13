/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.set;


import CacheMode.IGNORE;
import java.util.HashSet;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.criterion.Restrictions;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class PersistentSetTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testWriteMethodDirtying() {
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        Child otherChild = new Child("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.flush();
        // at this point, the set on parent has now been replaced with a PersistentSet...
        PersistentSet children = ((PersistentSet) (parent.getChildren()));
        Assert.assertFalse(children.add(child));
        Assert.assertFalse(children.isDirty());
        Assert.assertFalse(children.remove(otherChild));
        Assert.assertFalse(children.isDirty());
        HashSet otherSet = new HashSet();
        otherSet.add(child);
        Assert.assertFalse(children.addAll(otherSet));
        Assert.assertFalse(children.isDirty());
        Assert.assertFalse(children.retainAll(otherSet));
        Assert.assertFalse(children.isDirty());
        otherSet = new HashSet();
        otherSet.add(otherChild);
        Assert.assertFalse(children.removeAll(otherSet));
        Assert.assertFalse(children.isDirty());
        Assert.assertTrue(children.retainAll(otherSet));
        Assert.assertTrue(children.isDirty());
        Assert.assertTrue(children.isEmpty());
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
    public void testCollectionMerging() {
        Session session = openSession();
        session.beginTransaction();
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        session.save(parent);
        session.getTransaction().commit();
        session.close();
        CollectionStatistics stats = sessionFactory().getStatistics().getCollectionStatistics(((Parent.class.getName()) + ".children"));
        long recreateCount = stats.getRecreateCount();
        long updateCount = stats.getUpdateCount();
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.merge(parent)));
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(recreateCount, stats.getRecreateCount());
        Assert.assertEquals(updateCount, stats.getUpdateCount());
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, "p1")));
        Assert.assertEquals(1, parent.getChildren().size());
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testCollectiondirtyChecking() {
        Session session = openSession();
        session.beginTransaction();
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        session.save(parent);
        session.getTransaction().commit();
        session.close();
        CollectionStatistics stats = sessionFactory().getStatistics().getCollectionStatistics(((Parent.class.getName()) + ".children"));
        long recreateCount = stats.getRecreateCount();
        long updateCount = stats.getUpdateCount();
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, "p1")));
        Assert.assertEquals(1, parent.getChildren().size());
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(1, parent.getChildren().size());
        Assert.assertEquals(recreateCount, stats.getRecreateCount());
        Assert.assertEquals(updateCount, stats.getUpdateCount());
        session = openSession();
        session.beginTransaction();
        Assert.assertEquals(1, parent.getChildren().size());
        session.delete(parent);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testCompositeElementWriteMethodDirtying() {
        Container container = new Container("p1");
        Container.Content c1 = new Container.Content("c1");
        container.getContents().add(c1);
        Container.Content c2 = new Container.Content("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(container);
        session.flush();
        // at this point, the set on container has now been replaced with a PersistentSet...
        PersistentSet children = ((PersistentSet) (container.getContents()));
        Assert.assertFalse(children.add(c1));
        Assert.assertFalse(children.isDirty());
        Assert.assertFalse(children.remove(c2));
        Assert.assertFalse(children.isDirty());
        HashSet otherSet = new HashSet();
        otherSet.add(c1);
        Assert.assertFalse(children.addAll(otherSet));
        Assert.assertFalse(children.isDirty());
        Assert.assertFalse(children.retainAll(otherSet));
        Assert.assertFalse(children.isDirty());
        otherSet = new HashSet();
        otherSet.add(c2);
        Assert.assertFalse(children.removeAll(otherSet));
        Assert.assertFalse(children.isDirty());
        Assert.assertTrue(children.retainAll(otherSet));
        Assert.assertTrue(children.isDirty());
        Assert.assertTrue(children.isEmpty());
        children.clear();
        Assert.assertTrue(children.isDirty());
        session.flush();
        children.clear();
        Assert.assertFalse(children.isDirty());
        session.delete(container);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-2485")
    public void testCompositeElementMerging() {
        Session session = openSession();
        session.beginTransaction();
        Container container = new Container("p1");
        Container.Content c1 = new Container.Content("c1");
        container.getContents().add(c1);
        session.save(container);
        session.getTransaction().commit();
        session.close();
        CollectionStatistics stats = sessionFactory().getStatistics().getCollectionStatistics(((Container.class.getName()) + ".contents"));
        long recreateCount = stats.getRecreateCount();
        long updateCount = stats.getUpdateCount();
        container.setName("another name");
        session = openSession();
        session.beginTransaction();
        container = ((Container) (session.merge(container)));
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(1, container.getContents().size());
        Assert.assertEquals(recreateCount, stats.getRecreateCount());
        Assert.assertEquals(updateCount, stats.getUpdateCount());
        session = openSession();
        session.beginTransaction();
        container = ((Container) (session.get(Container.class, container.getId())));
        Assert.assertEquals(1, container.getContents().size());
        session.delete(container);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-2485")
    public void testCompositeElementCollectionDirtyChecking() {
        Session session = openSession();
        session.beginTransaction();
        Container container = new Container("p1");
        Container.Content c1 = new Container.Content("c1");
        container.getContents().add(c1);
        session.save(container);
        session.getTransaction().commit();
        session.close();
        CollectionStatistics stats = sessionFactory().getStatistics().getCollectionStatistics(((Container.class.getName()) + ".contents"));
        long recreateCount = stats.getRecreateCount();
        long updateCount = stats.getUpdateCount();
        session = openSession();
        session.beginTransaction();
        container = ((Container) (session.get(Container.class, container.getId())));
        Assert.assertEquals(1, container.getContents().size());
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(1, container.getContents().size());
        Assert.assertEquals(recreateCount, stats.getRecreateCount());
        Assert.assertEquals(updateCount, stats.getUpdateCount());
        session = openSession();
        session.beginTransaction();
        container = ((Container) (session.get(Container.class, container.getId())));
        Assert.assertEquals(1, container.getContents().size());
        session.delete(container);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testLoadChildCheckParentContainsChildCache() {
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        child.setDescription("desc1");
        parent.getChildren().add(child);
        child.setParent(parent);
        Child otherChild = new Child("c2");
        otherChild.setDescription("desc2");
        parent.getChildren().add(otherChild);
        otherChild.setParent(parent);
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.getTransaction().commit();
        session = openSession();
        session.beginTransaction();
        parent = ((Parent) (session.get(Parent.class, parent.getName())));
        Assert.assertTrue(parent.getChildren().contains(child));
        Assert.assertTrue(parent.getChildren().contains(otherChild));
        session.getTransaction().commit();
        session = openSession();
        session.beginTransaction();
        child = ((Child) (session.get(Child.class, child.getName())));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        session.clear();
        child = ((Child) (session.createCriteria(Child.class, child.getName()).setCacheable(true).add(Restrictions.idEq("c1")).uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        Assert.assertTrue(child.getParent().getChildren().contains(otherChild));
        session.clear();
        child = ((Child) (session.createCriteria(Child.class, child.getName()).setCacheable(true).add(Restrictions.idEq("c1")).uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        Assert.assertTrue(child.getParent().getChildren().contains(otherChild));
        session.clear();
        child = ((Child) (session.createQuery("from Child where name = 'c1'").setCacheable(true).uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        child = ((Child) (session.createQuery("from Child where name = 'c1'").setCacheable(true).uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        session.delete(child.getParent());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testLoadChildCheckParentContainsChildNoCache() {
        Parent parent = new Parent("p1");
        Child child = new Child("c1");
        parent.getChildren().add(child);
        child.setParent(parent);
        Child otherChild = new Child("c2");
        parent.getChildren().add(otherChild);
        otherChild.setParent(parent);
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.getTransaction().commit();
        session = openSession();
        session.beginTransaction();
        session.setCacheMode(IGNORE);
        parent = ((Parent) (session.get(Parent.class, parent.getName())));
        Assert.assertTrue(parent.getChildren().contains(child));
        Assert.assertTrue(parent.getChildren().contains(otherChild));
        session.getTransaction().commit();
        session = openSession();
        session.beginTransaction();
        session.setCacheMode(IGNORE);
        child = ((Child) (session.get(Child.class, child.getName())));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        session.clear();
        child = ((Child) (session.createCriteria(Child.class, child.getName()).add(Restrictions.idEq("c1")).uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        Assert.assertTrue(child.getParent().getChildren().contains(otherChild));
        session.clear();
        child = ((Child) (session.createQuery("from Child where name = 'c1'").uniqueResult()));
        Assert.assertTrue(child.getParent().getChildren().contains(child));
        session.delete(child.getParent());
        session.getTransaction().commit();
        session.close();
    }
}

