/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.event.internal;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EventSource;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * 2011/10/20 Unit test for code added in MergeContext for performance improvement.
 *
 * @author Wim Ockerman @ CISCO
 */
public class MergeContextTest extends BaseCoreFunctionalTestCase {
    private EventSource session = null;

    @Test
    public void testMergeToManagedEntityFillFollowedByInvertMapping() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity);
        checkCacheConsistency(cache, 1);
        Assert.assertTrue(cache.containsKey(mergeEntity));
        Assert.assertFalse(cache.containsKey(managedEntity));
        Assert.assertTrue(cache.containsValue(managedEntity));
        Assert.assertTrue(cache.invertMap().containsKey(managedEntity));
        Assert.assertFalse(cache.invertMap().containsKey(mergeEntity));
        Assert.assertTrue(cache.invertMap().containsValue(mergeEntity));
        cache.clear();
        checkCacheConsistency(cache, 0);
        Assert.assertFalse(cache.containsKey(mergeEntity));
        Assert.assertFalse(cache.invertMap().containsKey(managedEntity));
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByInvert() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity);
        checkCacheConsistency(cache, 1);
        Assert.assertTrue(cache.containsKey(mergeEntity));
        Assert.assertFalse(cache.containsKey(managedEntity));
        Assert.assertTrue(cache.invertMap().containsKey(managedEntity));
        Assert.assertFalse(cache.invertMap().containsKey(mergeEntity));
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByInvertUsingPutAll() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Map<Object, Object> input = new HashMap<Object, Object>();
        Object mergeEntity1 = new MergeContextTest.Simple(1);
        // 
        Object managedEntity1 = 1;
        input.put(mergeEntity1, managedEntity1);
        Object mergeEntity2 = new MergeContextTest.Simple(3);
        Object managedEntity2 = 2;
        input.put(mergeEntity2, managedEntity2);
        cache.putAll(input);
        checkCacheConsistency(cache, 2);
        Assert.assertTrue(cache.containsKey(mergeEntity1));
        Assert.assertFalse(cache.containsKey(managedEntity1));
        Assert.assertTrue(cache.containsKey(mergeEntity2));
        Assert.assertFalse(cache.containsKey(managedEntity2));
        Assert.assertTrue(cache.invertMap().containsKey(managedEntity1));
        Assert.assertFalse(cache.invertMap().containsKey(mergeEntity1));
        Assert.assertTrue(cache.invertMap().containsKey(managedEntity2));
        Assert.assertFalse(cache.invertMap().containsKey(mergeEntity2));
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByInvertUsingPutWithSetOperatedOnArg() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity, true);
        checkCacheConsistency(cache, 1);
        Assert.assertTrue(cache.containsKey(mergeEntity));
        Assert.assertFalse(cache.containsKey(managedEntity));
        Assert.assertTrue(cache.invertMap().containsKey(managedEntity));
        Assert.assertFalse(cache.invertMap().containsKey(mergeEntity));
        cache.clear();
        checkCacheConsistency(cache, 0);
        cache.put(mergeEntity, managedEntity, false);
        Assert.assertFalse(cache.isOperatedOn(mergeEntity));
        checkCacheConsistency(cache, 1);
        Assert.assertTrue(cache.containsKey(mergeEntity));
        Assert.assertFalse(cache.containsKey(managedEntity));
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByIterateEntrySet() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity, true);
        checkCacheConsistency(cache, 1);
        Iterator it = cache.entrySet().iterator();
        Assert.assertTrue(it.hasNext());
        Map.Entry entry = ((Map.Entry) (it.next()));
        Assert.assertSame(mergeEntity, entry.getKey());
        Assert.assertSame(managedEntity, entry.getValue());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByModifyEntrySet() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity, true);
        Iterator it = cache.entrySet().iterator();
        try {
            it.remove();
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Map.Entry entry = ((Map.Entry) (cache.entrySet().iterator().next()));
        try {
            cache.entrySet().remove(entry);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Map.Entry anotherEntry = new Map.Entry() {
            private Object key = new MergeContextTest.Simple(3);

            private Object value = 4;

            @Override
            public Object getKey() {
                return key;
            }

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public Object setValue(Object value) {
                Object oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        };
        try {
            cache.entrySet().add(anotherEntry);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByModifyKeys() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity, true);
        Iterator it = cache.keySet().iterator();
        try {
            it.remove();
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            cache.keySet().remove(mergeEntity);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Object newmanagedEntity = new MergeContextTest.Simple(3);
        try {
            cache.keySet().add(newmanagedEntity);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByModifyValues() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        Object mergeEntity = new MergeContextTest.Simple(1);
        Object managedEntity = new MergeContextTest.Simple(2);
        cache.put(mergeEntity, managedEntity, true);
        Iterator it = cache.values().iterator();
        try {
            it.remove();
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            cache.values().remove(managedEntity);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        Object newmanagedEntity = new MergeContextTest.Simple(3);
        try {
            cache.values().add(newmanagedEntity);
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByModifyKeyOfEntrySetElement() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        MergeContextTest.Simple mergeEntity = new MergeContextTest.Simple(1);
        MergeContextTest.Simple managedEntity = new MergeContextTest.Simple(0);
        cache.put(mergeEntity, managedEntity, true);
        Map.Entry entry = ((Map.Entry) (cache.entrySet().iterator().next()));
        ((MergeContextTest.Simple) (entry.getKey())).setValue(2);
        Assert.assertEquals(2, mergeEntity.getValue());
        checkCacheConsistency(cache, 1);
        entry = ((Map.Entry) (cache.entrySet().iterator().next()));
        Assert.assertSame(mergeEntity, entry.getKey());
        Assert.assertSame(managedEntity, entry.getValue());
    }

    @Test
    public void testMergeToManagedEntityFillFollowedByModifyValueOfEntrySetElement() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        MergeContextTest.Simple mergeEntity = new MergeContextTest.Simple(1);
        MergeContextTest.Simple managedEntity = new MergeContextTest.Simple(0);
        cache.put(mergeEntity, managedEntity, true);
        Map.Entry entry = ((Map.Entry) (cache.entrySet().iterator().next()));
        ((MergeContextTest.Simple) (entry.getValue())).setValue(2);
        Assert.assertEquals(2, managedEntity.getValue());
        checkCacheConsistency(cache, 1);
        entry = ((Map.Entry) (cache.entrySet().iterator().next()));
        Assert.assertSame(mergeEntity, entry.getKey());
        Assert.assertSame(managedEntity, entry.getValue());
    }

    @Test
    public void testReplaceManagedEntity() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        MergeContextTest.Simple mergeEntity = new MergeContextTest.Simple(1);
        MergeContextTest.Simple managedEntity = new MergeContextTest.Simple(0);
        cache.put(mergeEntity, managedEntity);
        MergeContextTest.Simple managedEntityNew = new MergeContextTest.Simple(0);
        try {
            cache.put(mergeEntity, managedEntityNew);
        } catch (IllegalArgumentException ex) {
            // expected; cannot replace the managed entity result for a particular merge entity.
        }
    }

    @Test
    public void testManagedEntityAssociatedWithNewAndExistingMergeEntities() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        session.getTransaction().begin();
        MergeContextTest.Simple mergeEntity = new MergeContextTest.Simple(1);
        MergeContextTest.Simple managedEntity = new MergeContextTest.Simple(0);
        cache.put(mergeEntity, managedEntity);
        cache.put(new MergeContextTest.Simple(1), managedEntity);
    }

    @Test
    public void testManagedAssociatedWith2ExistingMergeEntities() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        session.getTransaction().begin();
        MergeContextTest.Simple mergeEntity1 = new MergeContextTest.Simple(1);
        session.persist(mergeEntity1);
        MergeContextTest.Simple managedEntity1 = new MergeContextTest.Simple(1);
        cache.put(mergeEntity1, managedEntity1);
        MergeContextTest.Simple managedEntity2 = new MergeContextTest.Simple(2);
        try {
            cache.put(mergeEntity1, managedEntity2);
            Assert.fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected; cannot change managed entity associated with a merge entity
        } finally {
            session.getTransaction().rollback();
        }
    }

    @Test
    public void testRemoveNonExistingEntity() {
        MergeContext cache = new MergeContext(session, new MergeContextTest.DoNothingEntityCopyObserver());
        try {
            cache.remove(new MergeContextTest.Simple(1));
        } catch (UnsupportedOperationException ex) {
            // expected; remove is not supported.
        }
    }

    @Entity
    private static class Simple {
        @Id
        private int value;

        public Simple(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return (("Simple{" + "value=") + (value)) + '}';
        }
    }

    private class DoNothingEntityCopyObserver implements EntityCopyObserver {
        @Override
        public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
        }

        @Override
        public void topLevelMergeComplete(EventSource session) {
        }

        @Override
        public void clear() {
        }
    }

    private class ExceptionThrowingEntityCopyObserver implements EntityCopyObserver {
        @Override
        public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
            throw new IllegalStateException("Entity copies not allowed.");
        }

        @Override
        public void topLevelMergeComplete(EventSource session) {
        }

        @Override
        public void clear() {
        }
    }
}

