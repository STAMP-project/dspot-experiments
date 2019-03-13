/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.references;


import com.facebook.common.internal.Closeables;
import java.io.Closeable;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Basic tests for shared references
 */
@RunWith(RobolectricTestRunner.class)
public class SharedReferenceTest {
    /**
     * Tests out the basic operations (isn't everything a basic operation?)
     */
    @Test
    public void testBasic() {
        // ref count = 1 after creation
        SharedReference<SharedReferenceTest.Thing> tRef = new SharedReference<SharedReferenceTest.Thing>(new SharedReferenceTest.Thing("abc"), THING_RELEASER);
        Assert.assertTrue(SharedReference.isValid(tRef));
        Assert.assertEquals(1, tRef.getRefCountTestOnly());
        SharedReferenceTest.Thing t = tRef.get();
        Assert.assertEquals("abc", t.get());
        // adding a reference increases the ref count
        tRef.addReference();
        Assert.assertTrue(SharedReference.isValid(tRef));
        Assert.assertEquals(2, tRef.getRefCountTestOnly());
        Assert.assertEquals(t, tRef.get());
        Assert.assertEquals("abc", t.get());
        // deleting a reference drops the reference count
        tRef.deleteReference();
        Assert.assertTrue(SharedReference.isValid(tRef));
        Assert.assertEquals(1, tRef.getRefCountTestOnly());
        Assert.assertEquals(t, tRef.get());
        Assert.assertEquals("abc", t.get());
        // when the last reference is gone, the underlying object is disposed
        tRef.deleteReference();
        Assert.assertFalse(SharedReference.isValid(tRef));
        Assert.assertEquals(0, tRef.getRefCountTestOnly());
        // adding a reference now should fail
        try {
            tRef.addReference();
            Assert.fail();
        } catch (SharedReference e) {
            // do nothing
        }
        // so should deleting a reference
        try {
            tRef.deleteReference();
            Assert.fail();
        } catch (SharedReference e) {
            // do nothing
        }
        // null shared references are not 'valid'
        Assert.assertFalse(SharedReference.isValid(null));
        // test out exceptions during a close
        SharedReference<SharedReferenceTest.Thing> t2Ref = new SharedReference<SharedReferenceTest.Thing>(new SharedReferenceTest.Thing2("abc"), THING_RELEASER);
        // this should not throw
        t2Ref.deleteReference();
    }

    @Test
    public void testNewSharedReference() {
        final SharedReferenceTest.Thing thing = new SharedReferenceTest.Thing("abc");
        Assert.assertSame(thing, get());
    }

    @Test
    public void testCustomReleaser() {
        final SharedReferenceTest.Thing thing = new SharedReferenceTest.Thing("abc");
        final ResourceReleaser releaser = Mockito.mock(ResourceReleaser.class);
        final SharedReference<SharedReferenceTest.Thing> tRef = new SharedReference<SharedReferenceTest.Thing>(thing, releaser);
        tRef.deleteReference();
        Mockito.verify(releaser, Mockito.times(1)).release(thing);
    }

    public static class Thing implements Closeable {
        private String mValue;

        public Thing(String value) {
            mValue = value;
        }

        public String get() {
            return mValue;
        }

        public void close() throws IOException {
            mValue = null;
        }
    }

    /**
     * A subclass of Thing that throws an exception on close
     */
    public static class Thing2 extends SharedReferenceTest.Thing {
        private String mValue;

        public Thing2(String value) {
            super(value);
        }

        public void close() throws IOException {
            throw new IOException("");
        }
    }

    public final ResourceReleaser<SharedReferenceTest.Thing> THING_RELEASER = new ResourceReleaser<SharedReferenceTest.Thing>() {
        @Override
        public void release(SharedReferenceTest.Thing value) {
            try {
                Closeables.close(value, true);
            } catch (IOException ioe) {
                // this should not happen
                Assert.fail();
            }
        }
    };
}

