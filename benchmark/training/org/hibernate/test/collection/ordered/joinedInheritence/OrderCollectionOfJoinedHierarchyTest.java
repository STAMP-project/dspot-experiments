/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.ordered.joinedInheritence;


import org.hibernate.Session;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Brett Meyer
 */
public class OrderCollectionOfJoinedHierarchyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testQuerySyntaxCheck() {
        Session session = openSession();
        session.beginTransaction();
        session.get(Zoo.class, 1L);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testOrdering() {
        Zoo zoo = new Zoo();
        Lion lion1 = new Lion();
        lion1.setWeight(2);
        Lion lion2 = new Lion();
        lion2.setWeight(1);
        zoo.getLions().add(lion1);
        zoo.getLions().add(lion2);
        zoo.getAnimalsById().add(lion1);
        zoo.getAnimalsById().add(lion2);
        Session session = openSession();
        session.beginTransaction();
        session.persist(lion1);
        session.persist(lion2);
        session.persist(zoo);
        session.getTransaction().commit();
        session.clear();
        session.beginTransaction();
        zoo = ((Zoo) (session.get(Zoo.class, zoo.getId())));
        zoo.getLions().size();
        zoo.getLions().size();
        zoo.getAnimalsById().size();
        session.getTransaction().commit();
        session.close();
        Assert.assertNotNull(zoo);
        Assert.assertTrue(((CollectionHelper.isNotEmpty(zoo.getLions())) && ((zoo.getLions().size()) == 2)));
        Assert.assertTrue(((CollectionHelper.isNotEmpty(zoo.getAnimalsById())) && ((zoo.getAnimalsById().size()) == 2)));
        Assert.assertEquals(zoo.getLions().iterator().next().getId(), lion2.getId());
        Assert.assertEquals(zoo.getAnimalsById().iterator().next().getId(), lion1.getId());
    }
}

