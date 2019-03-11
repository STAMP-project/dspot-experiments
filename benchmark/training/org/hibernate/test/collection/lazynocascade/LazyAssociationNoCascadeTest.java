/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.lazynocascade;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vasily Kochnev
 */
public class LazyAssociationNoCascadeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNoCascadeCache() {
        Parent parent = new Parent();
        BaseChild firstChild = new BaseChild();
        parent.getChildren().add(firstChild);
        Session s = openSession();
        s.beginTransaction();
        s.save(parent);
        s.getTransaction().commit();
        s.clear();
        Child secondChild = new Child();
        secondChild.setName("SecondChildName");
        parent.getChildren().add(secondChild);
        firstChild.setDependency(secondChild);
        s.beginTransaction();
        Parent mergedParent = ((Parent) (s.merge(parent)));
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(mergedParent);
        Assert.assertEquals(mergedParent.getChildren().size(), 2);
    }
}

