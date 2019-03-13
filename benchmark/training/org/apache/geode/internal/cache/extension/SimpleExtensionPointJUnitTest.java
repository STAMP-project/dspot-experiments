/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.extension;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.geode.cache.Cache;
import org.apache.geode.internal.cache.xmlcache.XmlGenerator;
import org.apache.geode.test.fake.Fakes;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SimpleExtensionPoint}.
 *
 * @since GemFire 8.1
 */
public class SimpleExtensionPointJUnitTest {
    /**
     * Test method for {@link SimpleExtensionPoint#SimpleExtensionPoint(Extensible, Object)} .
     */
    @Test
    public void testSimpleExtensionPoint() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        Assert.assertSame(m.extensionPoint.extensible, m.extensionPoint.target);
        Assert.assertNotNull(m.extensionPoint.extensions);
        Assert.assertNotNull(m.extensionPoint.iterable);
    }

    /**
     * Test method for {@link SimpleExtensionPoint#getExtensions()} .
     */
    @Test
    public void testGetExtensions() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        Assert.assertEquals(0, m.extensionPoint.extensions.size());
        Assert.assertTrue((!(m.extensionPoint.iterable.iterator().hasNext())));
        final Iterable<Extension<SimpleExtensionPointJUnitTest.MockInterface>> extensions = m.getExtensionPoint().getExtensions();
        Assert.assertNotNull(extensions);
        // extensions should be empty
        final Iterator<Extension<SimpleExtensionPointJUnitTest.MockInterface>> iterator = extensions.iterator();
        Assert.assertTrue((!(iterator.hasNext())));
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    /**
     * Test method for {@link SimpleExtensionPoint#addExtension(Extension)} .
     */
    @Test
    public void testAddExtension() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        final SimpleExtensionPointJUnitTest.MockExtension extension = new SimpleExtensionPointJUnitTest.MockExtension();
        m.getExtensionPoint().addExtension(extension);
        Assert.assertEquals(1, m.extensionPoint.extensions.size());
        final Iterable<Extension<SimpleExtensionPointJUnitTest.MockInterface>> extensions = m.getExtensionPoint().getExtensions();
        Assert.assertNotNull(extensions);
        final Iterator<Extension<SimpleExtensionPointJUnitTest.MockInterface>> iterator = extensions.iterator();
        // first and only entry should be our extension.
        final Extension<SimpleExtensionPointJUnitTest.MockInterface> actual = iterator.next();
        Assert.assertSame(extension, actual);
        // should only be one extension in the iterator.
        try {
            iterator.next();
            Assert.fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    /**
     * Test method for {@link SimpleExtensionPoint#removeExtension(Extension)} .
     */
    @Test
    public void testRemoveExtension() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        final SimpleExtensionPointJUnitTest.MockExtension extension = new SimpleExtensionPointJUnitTest.MockExtension();
        m.getExtensionPoint().addExtension(extension);
        final Iterable<Extension<SimpleExtensionPointJUnitTest.MockInterface>> extensions = m.getExtensionPoint().getExtensions();
        Assert.assertNotNull(extensions);
        final Iterator<Extension<SimpleExtensionPointJUnitTest.MockInterface>> i = extensions.iterator();
        // first and only entry should be our extension.
        final Extension<SimpleExtensionPointJUnitTest.MockInterface> actual = i.next();
        Assert.assertSame(extension, actual);
        // should not be able to remove it via iterator.
        try {
            i.remove();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        m.getExtensionPoint().removeExtension(extension);
        Assert.assertEquals(0, m.extensionPoint.extensions.size());
        // extensions should be empty
        final Iterable<Extension<SimpleExtensionPointJUnitTest.MockInterface>> extensionsRemoved = m.getExtensionPoint().getExtensions();
        try {
            extensionsRemoved.iterator().next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    /**
     * Test method for {@link SimpleExtensionPoint#getTarget()} .
     */
    @Test
    public void testGetTarget() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        final SimpleExtensionPointJUnitTest.MockInterface a = m.getExtensionPoint().getTarget();
        Assert.assertSame(m, a);
    }

    /**
     * Test method for {@link SimpleExtensionPoint#fireCreate(Extensible)} .
     */
    @Test
    public void testFireCreate() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        final AtomicInteger counter = new AtomicInteger(0);
        final SimpleExtensionPointJUnitTest.MockExtension extension = new SimpleExtensionPointJUnitTest.MockExtension() {
            @Override
            public void onCreate(Extensible<SimpleExtensionPointJUnitTest.MockInterface> source, Extensible<SimpleExtensionPointJUnitTest.MockInterface> target) {
                counter.incrementAndGet();
            }
        };
        counter.set(0);
        m.getExtensionPoint().addExtension(extension);
        // fire with itself as the target
        m.extensionPoint.fireCreate(m);
        Assert.assertEquals(1, counter.get());
        counter.set(0);
        m.getExtensionPoint().removeExtension(extension);
        // fire with itself as the target
        m.extensionPoint.fireCreate(m);
        Assert.assertEquals(0, counter.get());
    }

    /**
     * Test method for {@link SimpleExtensionPoint#beforeCreate(Cache)} .
     */
    @Test
    public void testBeforeCreate() {
        final SimpleExtensionPointJUnitTest.MockImpl m = new SimpleExtensionPointJUnitTest.MockImpl();
        final Cache c = Fakes.cache();
        final AtomicInteger counter = new AtomicInteger(0);
        final SimpleExtensionPointJUnitTest.MockExtension extension = new SimpleExtensionPointJUnitTest.MockExtension() {
            @Override
            public void beforeCreate(Extensible<SimpleExtensionPointJUnitTest.MockInterface> source, Cache cache) {
                counter.incrementAndGet();
            }
        };
        counter.set(0);
        m.getExtensionPoint().addExtension(extension);
        // Verify beforeCreate is invoked when the extension is added
        m.extensionPoint.beforeCreate(c);
        Assert.assertEquals(1, counter.get());
        counter.set(0);
        m.getExtensionPoint().removeExtension(extension);
        // Verify beforeCreate is not invoked when the extension is removed
        m.extensionPoint.beforeCreate(c);
        Assert.assertEquals(0, counter.get());
    }

    private interface MockInterface {
        public void method1();
    }

    private static class MockImpl implements Extensible<SimpleExtensionPointJUnitTest.MockInterface> , SimpleExtensionPointJUnitTest.MockInterface {
        private SimpleExtensionPoint<SimpleExtensionPointJUnitTest.MockInterface> extensionPoint = new SimpleExtensionPoint<SimpleExtensionPointJUnitTest.MockInterface>(this, this);

        @Override
        public ExtensionPoint<SimpleExtensionPointJUnitTest.MockInterface> getExtensionPoint() {
            return extensionPoint;
        }

        @Override
        public void method1() {
        }
    }

    private static class MockExtension implements Extension<SimpleExtensionPointJUnitTest.MockInterface> {
        @Override
        public XmlGenerator<SimpleExtensionPointJUnitTest.MockInterface> getXmlGenerator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void beforeCreate(Extensible<SimpleExtensionPointJUnitTest.MockInterface> source, Cache cache) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onCreate(Extensible<SimpleExtensionPointJUnitTest.MockInterface> source, Extensible<SimpleExtensionPointJUnitTest.MockInterface> target) {
            throw new UnsupportedOperationException();
        }
    }
}

