/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.event.collection;


import DialectChecks.SupportsNoColumnInsert;
import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * These tests are known to fail. When the functionality is corrected, the
 * corresponding method will be moved into AbstractCollectionEventTest.
 *
 * @author Gail Badner
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class BrokenCollectionEventTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testUpdateDetachedParentNoChildrenToNull() {
        CollectionListeners listeners = new CollectionListeners(sessionFactory());
        ParentWithCollection parent = createParentWithNoChildren("parent");
        listeners.clear();
        Assert.assertEquals(0, parent.getChildren().size());
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Collection oldCollection = parent.getChildren();
        parent.newChildren(null);
        s.update(parent);
        tx.commit();
        s.close();
        int index = 0;
        checkResult(listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, (index++));
        checkResult(listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, (index++));
        // pre- and post- collection recreate events should be created when updating an entity with a "null" collection
        checkResult(listeners, listeners.getPreCollectionRecreateListener(), parent, (index++));
        checkResult(listeners, listeners.getPostCollectionRecreateListener(), parent, (index++));
        checkNumberOfResults(listeners, index);
    }

    // The following fails for the same reason as testUpdateDetachedParentNoChildrenToNullFailureExpected
    // When that issue is fixed, this one should also be fixed and moved into AbstractCollectionEventTest.
    /* public void testUpdateDetachedParentOneChildToNullFailureExpected() {
    CollectionListeners listeners = new CollectionListeners( sessionFactory() );
    ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
    Child oldChild = ( Child ) parent.getChildren().iterator().next();
    assertEquals( 1, parent.getChildren().size() );
    listeners.clear();
    Session s = openSession();
    Transaction tx = s.beginTransaction();
    Collection oldCollection = parent.getChildren();
    parent.newChildren( null );
    s.update( parent );
    tx.commit();
    s.close();
    int index = 0;
    checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
    checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
    if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
    checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
    checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
    }
    // pre- and post- collection recreate events should be created when updating an entity with a "null" collection
    checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
    checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
    checkNumberOfResults( listeners, index );
    }
     */
    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testSaveParentNullChildren() {
        CollectionListeners listeners = new CollectionListeners(sessionFactory());
        ParentWithCollection parent = createParentWithNullChildren("parent");
        Assert.assertNull(parent.getChildren());
        int index = 0;
        // pre- and post- collection recreate events should be created when creating an entity with a "null" collection
        checkResult(listeners, listeners.getPreCollectionRecreateListener(), parent, (index++));
        checkResult(listeners, listeners.getPostCollectionRecreateListener(), parent, (index++));
        checkNumberOfResults(listeners, index);
        listeners.clear();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        parent = ((ParentWithCollection) (s.get(parent.getClass(), parent.getId())));
        tx.commit();
        s.close();
        Assert.assertNotNull(parent.getChildren());
        checkNumberOfResults(listeners, 0);
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testUpdateParentNoChildrenToNull() {
        CollectionListeners listeners = new CollectionListeners(sessionFactory());
        ParentWithCollection parent = createParentWithNoChildren("parent");
        listeners.clear();
        Assert.assertEquals(0, parent.getChildren().size());
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        parent = ((ParentWithCollection) (s.get(parent.getClass(), parent.getId())));
        Collection oldCollection = parent.getChildren();
        parent.newChildren(null);
        tx.commit();
        s.close();
        int index = 0;
        if (wasInitialized()) {
            checkResult(listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, (index++));
        }
        checkResult(listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, (index++));
        checkResult(listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, (index++));
        // pre- and post- collection recreate events should be created when updating an entity with a "null" collection
        checkResult(listeners, listeners.getPreCollectionRecreateListener(), parent, (index++));
        checkResult(listeners, listeners.getPostCollectionRecreateListener(), parent, (index++));
        checkNumberOfResults(listeners, index);
    }
}

