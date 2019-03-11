/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.registry.classloading.internal;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static TcclLookupPrecedence.AFTER;
import static TcclLookupPrecedence.BEFORE;
import static TcclLookupPrecedence.NEVER;


/**
 *
 *
 * @author C?dric Tabin
 */
public class ClassLoaderServiceImplTest {
    @Test
    public void testNullTCCL() {
        Thread.currentThread().setContextClassLoader(null);
        ClassLoaderServiceImpl csi1 = new ClassLoaderServiceImpl(null, BEFORE);
        Class<ClassLoaderServiceImplTest> clazz1 = csi1.classForName(ClassLoaderServiceImplTest.class.getName());
        Assert.assertEquals(ClassLoaderServiceImplTest.class, clazz1);
        csi1.stop();
        ClassLoaderServiceImpl csi2 = new ClassLoaderServiceImpl(null, AFTER);
        Class<ClassLoaderServiceImplTest> clazz2 = csi2.classForName(ClassLoaderServiceImplTest.class.getName());
        Assert.assertEquals(ClassLoaderServiceImplTest.class, clazz2);
        csi2.stop();
        ClassLoaderServiceImpl csi3 = new ClassLoaderServiceImpl(null, NEVER);
        Class<ClassLoaderServiceImplTest> clazz3 = csi3.classForName(ClassLoaderServiceImplTest.class.getName());
        Assert.assertEquals(ClassLoaderServiceImplTest.class, clazz3);
        csi3.stop();
    }

    @Test
    public void testLookupBefore() {
        ClassLoaderServiceImplTest.InternalClassLoader icl = new ClassLoaderServiceImplTest.InternalClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        ClassLoaderServiceImpl csi = new ClassLoaderServiceImpl(null, BEFORE);
        Class<ClassLoaderServiceImplTest> clazz = csi.classForName(ClassLoaderServiceImplTest.class.getName());
        Assert.assertEquals(ClassLoaderServiceImplTest.class, clazz);
        Assert.assertEquals(1, icl.getAccessCount());
        csi.stop();
    }

    @Test
    public void testLookupAfterAvoided() {
        ClassLoaderServiceImplTest.InternalClassLoader icl = new ClassLoaderServiceImplTest.InternalClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        ClassLoaderServiceImpl csi = new ClassLoaderServiceImpl(null, AFTER);
        Class<ClassLoaderServiceImplTest> clazz = csi.classForName(ClassLoaderServiceImplTest.class.getName());
        Assert.assertEquals(ClassLoaderServiceImplTest.class, clazz);
        Assert.assertEquals(0, icl.getAccessCount());
        csi.stop();
    }

    @Test
    public void testLookupAfter() {
        ClassLoaderServiceImplTest.InternalClassLoader icl = new ClassLoaderServiceImplTest.InternalClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        ClassLoaderServiceImpl csi = new ClassLoaderServiceImpl(null, AFTER);
        try {
            csi.classForName("test.class.name");
            Assert.assertTrue(false);
        } catch (Exception e) {
        }
        Assert.assertEquals(0, icl.getAccessCount());
        csi.stop();
    }

    @Test
    public void testLookupAfterNotFound() {
        ClassLoaderServiceImplTest.InternalClassLoader icl = new ClassLoaderServiceImplTest.InternalClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        ClassLoaderServiceImpl csi = new ClassLoaderServiceImpl(null, BEFORE);
        try {
            csi.classForName("test.class.not.found");
            Assert.assertTrue(false);
        } catch (Exception e) {
        }
        Assert.assertEquals(0, icl.getAccessCount());
        csi.stop();
    }

    @Test
    public void testLookupNever() {
        ClassLoaderServiceImplTest.InternalClassLoader icl = new ClassLoaderServiceImplTest.InternalClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        ClassLoaderServiceImpl csi = new ClassLoaderServiceImpl(null, NEVER);
        try {
            csi.classForName("test.class.name");
            Assert.assertTrue(false);
        } catch (Exception e) {
        }
        Assert.assertEquals(0, icl.getAccessCount());
        csi.stop();
    }

    private static class InternalClassLoader extends ClassLoader {
        private List<String> names = new ArrayList<>();

        public InternalClassLoader() {
            super(null);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith("org.hibernate")) {
                names.add(name);
            }
            return super.loadClass(name);
        }

        @Override
        protected URL findResource(String name) {
            if (name.startsWith("org.hibernate")) {
                names.add(name);
            }
            return super.findResource(name);
        }

        public int getAccessCount() {
            return names.size();
        }
    }
}

