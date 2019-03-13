/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.idbag;


import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentIdentifierBag;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related to operations on a PersistentIdentifierBag
 *
 * @author Steve Ebersole
 */
public class PersistentIdBagTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testWriteMethodDirtying() {
        IdbagOwner parent = new IdbagOwner("root");
        IdbagOwner child = new IdbagOwner("c1");
        parent.getChildren().add(child);
        IdbagOwner otherChild = new IdbagOwner("c2");
        Session session = openSession();
        session.beginTransaction();
        session.save(parent);
        session.flush();
        // at this point, the list on parent has now been replaced with a PersistentBag...
        PersistentIdentifierBag children = ((PersistentIdentifierBag) (parent.getChildren()));
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
}

