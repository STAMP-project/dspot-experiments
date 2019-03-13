/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazyload;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Oleksander Dukhno
 */
public class JtaLazyLoadingTest extends BaseCoreFunctionalTestCase {
    private static final int CHILDREN_SIZE = 3;

    private Long parentID;

    private Long lastChildID;

    @Test
    @TestForIssue(jiraKey = "HHH-7971")
    public void testLazyCollectionLoadingAfterEndTransaction() {
        Session s = openSession();
        s.beginTransaction();
        Parent loadedParent = ((Parent) (s.load(Parent.class, parentID)));
        s.getTransaction().commit();
        s.close();
        Assert.assertFalse(Hibernate.isInitialized(loadedParent.getChildren()));
        int i = 0;
        for (Child child : loadedParent.getChildren()) {
            i++;
            Assert.assertNotNull(child);
        }
        Assert.assertEquals(JtaLazyLoadingTest.CHILDREN_SIZE, i);
        s = openSession();
        s.beginTransaction();
        Child loadedChild = ((Child) (s.load(Child.class, lastChildID)));
        s.getTransaction().commit();
        s.close();
        Parent p = loadedChild.getParent();
        int j = 0;
        for (Child child : p.getChildren()) {
            j++;
            Assert.assertNotNull(child);
        }
        Assert.assertEquals(JtaLazyLoadingTest.CHILDREN_SIZE, j);
    }
}

