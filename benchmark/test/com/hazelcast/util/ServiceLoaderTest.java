/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.util;


import ServiceLoader.ServiceDefinition;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.SerializerHook;
import com.hazelcast.spi.impl.SpiPortableHook;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ServiceLoaderTest extends HazelcastTestSupport {
    @Test
    public void selectClassLoaders_whenTCCL_isNull_thenDoNotSelectNullClassloader() {
        Thread.currentThread().setContextClassLoader(null);
        ClassLoader dummyClassLoader = new URLClassLoader(new URL[0]);
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(dummyClassLoader);
        HazelcastTestSupport.assertNotContains(classLoaders, null);
    }

    @Test
    public void selectClassLoaders_whenPassedClassLoaderIsisNull_thenDoNotSelectNullClassloader() {
        Thread.currentThread().setContextClassLoader(null);
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(null);
        HazelcastTestSupport.assertNotContains(classLoaders, null);
    }

    @Test
    public void testMultipleClassloaderLoadsTheSameClass() throws Exception {
        ClassLoader parent = this.getClass().getClassLoader();
        // child classloader will steal bytecode from the parent and will define classes on its own
        ClassLoader childLoader = new ServiceLoaderTest.StealingClassloader(parent);
        Class<?> interfaceClass = childLoader.loadClass(PortableHook.class.getName());
        Iterator<? extends Class<?>> iterator = ServiceLoader.classIterator(interfaceClass, "com.hazelcast.PortableHook", childLoader);
        // make sure some hook were found.
        Assert.assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            Class<?> hook = iterator.next();
            Assert.assertEquals(childLoader, hook.getClassLoader());
        } 
    }

    @Test
    public void testHookDeduplication() {
        Class<?> hook = newClassImplementingInterface("com.hazelcast.internal.serialization.SomeHook", PortableHook.class, PortableHook.class.getClassLoader());
        ClassLoader parentClassloader = hook.getClassLoader();
        // child classloader delegating everything to its parent
        URLClassLoader childClassloader = new URLClassLoader(new URL[]{  }, parentClassloader);
        ServiceLoader.ServiceDefinition definition1 = new ServiceLoader.ServiceDefinition(hook.getName(), parentClassloader);
        // the definition loaded by the child classloader -> it only delegates to the parent -> it's a duplicated
        ServiceLoader.ServiceDefinition definition2 = new ServiceLoader.ServiceDefinition(hook.getName(), childClassloader);
        Set<ServiceLoader.ServiceDefinition> definitions = TestCollectionUtils.setOf(definition1, definition2);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertTrue(iterator.hasNext());
        Class<PortableHook> hookFromIterator = iterator.next();
        Assert.assertEquals(hook, hookFromIterator);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSkipHooksWithImplementingTheExpectedInterfaceButLoadedByDifferentClassloader() {
        Class<?> otherInterface = newInterface(PortableHook.class.getName());
        ClassLoader otherClassloader = otherInterface.getClassLoader();
        Class<?> otherHook = newClassImplementingInterface("com.hazelcast.internal.serialization.DifferentHook", otherInterface, otherClassloader);
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition(otherHook.getName(), otherClassloader);
        Set<ServiceLoader.ServiceDefinition> definitions = Collections.singleton(definition);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testFailFastWhenHookDoesNotImplementExpectedInteface() {
        Class<?> otherInterface = newInterface("com.hazelcast.internal.serialization.DifferentInterface");
        ClassLoader otherClassloader = otherInterface.getClassLoader();
        Class<?> otherHook = newClassImplementingInterface("com.hazelcast.internal.serialization.DifferentHook", otherInterface, otherClassloader);
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition(otherHook.getName(), otherClassloader);
        Set<ServiceLoader.ServiceDefinition> definitions = Collections.singleton(definition);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSkipUnknownClassesStartingFromHazelcastPackage() {
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition("com.hazelcast.DoesNotExist", getClass().getClassLoader());
        Set<ServiceLoader.ServiceDefinition> definitions = Collections.singleton(definition);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = HazelcastException.class)
    public void testFailFastOnUnknownClassesFromNonHazelcastPackage() {
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition("non.a.hazelcast.DoesNotExist", getClass().getClassLoader());
        Set<ServiceLoader.ServiceDefinition> definitions = Collections.singleton(definition);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSkipHookLoadedByDifferentClassloader() {
        Class<?> otherInterface = newInterface(PortableHook.class.getName());
        ClassLoader otherClassloader = otherInterface.getClassLoader();
        Class<?> otherHook = newClassImplementingInterface("com.hazelcast.internal.serialization.DifferentHook", otherInterface, otherClassloader);
        // otherHook is loaded by other classloader -> it should be skipped
        ServiceLoader.ServiceDefinition definition1 = new ServiceLoader.ServiceDefinition(otherHook.getName(), otherClassloader);
        // this hook should be loaded
        ServiceLoader.ServiceDefinition definition2 = new ServiceLoader.ServiceDefinition(SpiPortableHook.class.getName(), SpiPortableHook.class.getClassLoader());
        Set<ServiceLoader.ServiceDefinition> definitions = TestCollectionUtils.setOf(definition1, definition2);
        ServiceLoader.ClassIterator<PortableHook> iterator = new ServiceLoader.ClassIterator<PortableHook>(definitions, .class);
        Assert.assertTrue(iterator.hasNext());
        Class<PortableHook> hook = iterator.next();
        Assert.assertEquals(SpiPortableHook.class, hook);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testPrivatePortableHook() {
        String hookName = ServiceLoaderTest.DummyPrivatePortableHook.class.getName();
        ClassLoader classLoader = ServiceLoaderTest.DummyPrivatePortableHook.class.getClassLoader();
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition(hookName, classLoader);
        ServiceLoader.ClassIterator<PortableHook> classIterator = new ServiceLoader.ClassIterator<PortableHook>(singleton(definition), .class);
        ServiceLoader.NewInstanceIterator<PortableHook> instanceIterator = new ServiceLoader.NewInstanceIterator<PortableHook>(classIterator);
        Assert.assertTrue(instanceIterator.hasNext());
        ServiceLoaderTest.DummyPrivatePortableHook hook = ((ServiceLoaderTest.DummyPrivatePortableHook) (instanceIterator.next()));
        Assert.assertNotNull(hook);
    }

    @Test
    public void testPrivateSerializerHook() {
        String hookName = ServiceLoaderTest.DummyPrivateSerializerHook.class.getName();
        ClassLoader classLoader = ServiceLoaderTest.DummyPrivateSerializerHook.class.getClassLoader();
        ServiceLoader.ServiceDefinition definition = new ServiceLoader.ServiceDefinition(hookName, classLoader);
        ServiceLoader.ClassIterator<SerializerHook> classIterator = new ServiceLoader.ClassIterator<SerializerHook>(singleton(definition), .class);
        ServiceLoader.NewInstanceIterator<SerializerHook> instanceIterator = new ServiceLoader.NewInstanceIterator<SerializerHook>(classIterator);
        Assert.assertTrue(instanceIterator.hasNext());
        ServiceLoaderTest.DummyPrivateSerializerHook hook = ((ServiceLoaderTest.DummyPrivateSerializerHook) (instanceIterator.next()));
        Assert.assertNotNull(hook);
    }

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ServiceLoader.class);
    }

    @Test
    public void selectingSimpleSingleClassLoader() {
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(null);
        Assert.assertEquals(1, classLoaders.size());
    }

    @Test
    public void selectingSimpleGivenClassLoader() {
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(new URLClassLoader(new URL[0]));
        Assert.assertEquals(2, classLoaders.size());
    }

    @Test
    public void selectingSimpleDifferentThreadContextClassLoader() {
        Thread currentThread = Thread.currentThread();
        ClassLoader tccl = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(new URLClassLoader(new URL[0]));
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(null);
        currentThread.setContextClassLoader(tccl);
        Assert.assertEquals(2, classLoaders.size());
    }

    @Test
    public void selectingTcclAndGivenClassLoader() {
        Thread currentThread = Thread.currentThread();
        ClassLoader tccl = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(new URLClassLoader(new URL[0]));
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(new URLClassLoader(new URL[0]));
        currentThread.setContextClassLoader(tccl);
        Assert.assertEquals(3, classLoaders.size());
    }

    @Test
    public void selectingSameTcclAndGivenClassLoader() {
        ClassLoader same = new URLClassLoader(new URL[0]);
        Thread currentThread = Thread.currentThread();
        ClassLoader tccl = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(same);
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(same);
        currentThread.setContextClassLoader(tccl);
        Assert.assertEquals(2, classLoaders.size());
    }

    @Test
    public void loadServicesSingleClassLoader() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderTestInterface> type = ServiceLoaderTest.ServiceLoaderTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderTestInterface";
        Set<ServiceLoaderTest.ServiceLoaderTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderTestInterface> iterator = ServiceLoader.iterator(type, factoryId, null);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        Assert.assertEquals(1, implementations.size());
    }

    @Test
    public void loadServicesSimpleGivenClassLoader() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderTestInterface> type = ServiceLoaderTest.ServiceLoaderTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderTestInterface";
        ClassLoader given = new URLClassLoader(new URL[0]);
        Set<ServiceLoaderTest.ServiceLoaderTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderTestInterface> iterator = ServiceLoader.iterator(type, factoryId, given);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        Assert.assertEquals(1, implementations.size());
    }

    @Test
    public void loadServicesSimpleDifferentThreadContextClassLoader() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderTestInterface> type = ServiceLoaderTest.ServiceLoaderTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderTestInterface";
        Thread current = Thread.currentThread();
        ClassLoader tccl = current.getContextClassLoader();
        current.setContextClassLoader(new URLClassLoader(new URL[0]));
        Set<ServiceLoaderTest.ServiceLoaderTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderTestInterface> iterator = ServiceLoader.iterator(type, factoryId, null);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        current.setContextClassLoader(tccl);
        Assert.assertEquals(1, implementations.size());
    }

    @Test
    public void loadServicesTcclAndGivenClassLoader() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderTestInterface> type = ServiceLoaderTest.ServiceLoaderTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderTestInterface";
        ClassLoader given = new URLClassLoader(new URL[0]);
        Thread current = Thread.currentThread();
        ClassLoader tccl = current.getContextClassLoader();
        current.setContextClassLoader(new URLClassLoader(new URL[0]));
        Set<ServiceLoaderTest.ServiceLoaderTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderTestInterface> iterator = ServiceLoader.iterator(type, factoryId, given);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        current.setContextClassLoader(tccl);
        Assert.assertEquals(1, implementations.size());
    }

    @Test
    public void loadServicesSameTcclAndGivenClassLoader() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderTestInterface> type = ServiceLoaderTest.ServiceLoaderTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderTestInterface";
        ClassLoader same = new URLClassLoader(new URL[0]);
        Thread current = Thread.currentThread();
        ClassLoader tccl = current.getContextClassLoader();
        current.setContextClassLoader(same);
        Set<ServiceLoaderTest.ServiceLoaderTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderTestInterface> iterator = ServiceLoader.iterator(type, factoryId, same);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        current.setContextClassLoader(tccl);
        Assert.assertEquals(1, implementations.size());
    }

    @Test
    public void loadServicesWithSpaceInURL() throws Exception {
        Class<ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface> type = ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface.class;
        String factoryId = "com.hazelcast.ServiceLoaderSpecialCharsTestInterface";
        String externalForm = ClassLoader.getSystemResource("test with special chars^").toExternalForm().replace("%20", " ").replace("%5e", "^");
        URL url = new URL((externalForm + "/"));
        ClassLoader given = new URLClassLoader(new URL[]{ url });
        Set<ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface> implementations = new HashSet<ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface>();
        Iterator<ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface> iterator = ServiceLoader.iterator(type, factoryId, given);
        while (iterator.hasNext()) {
            implementations.add(iterator.next());
        } 
        Assert.assertEquals(1, implementations.size());
    }

    public interface ServiceLoaderTestInterface {}

    public interface ServiceLoaderSpecialCharsTestInterface {}

    public static class ServiceLoaderTestInterfaceImpl implements ServiceLoaderTest.ServiceLoaderTestInterface {}

    public static class ServiceLoaderSpecialCharsTestInterfaceImpl implements ServiceLoaderTest.ServiceLoaderSpecialCharsTestInterface {}

    private static class DummyPrivatePortableHook implements PortableHook {
        @Override
        public int getFactoryId() {
            return 0;
        }

        @Override
        public PortableFactory createFactory() {
            return null;
        }

        @Override
        public Collection<ClassDefinition> getBuiltinDefinitions() {
            return null;
        }
    }

    private static class DummyPrivateSerializerHook implements SerializerHook {
        @Override
        public Class getSerializationType() {
            return null;
        }

        @Override
        public Serializer createSerializer() {
            return null;
        }

        @Override
        public boolean isOverwritable() {
            return false;
        }
    }

    /**
     * Delegates everything to a given parent classloader.
     * When a loaded class is defined by the parent then it "steals"
     * its bytecode and try to define it on its own.
     * <p>
     * It simulates the situation where child and parent defines the same classes.
     */
    private static class StealingClassloader extends ClassLoader {
        private final ClassLoader parent;

        private StealingClassloader(ClassLoader parent) {
            super(parent);
            this.parent = parent;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> loadedByParent = parent.loadClass(name);
            if ((loadedByParent != null) && (parent.equals(loadedByParent.getClassLoader()))) {
                byte[] bytecode = loadBytecodeFromParent(name);
                Class<?> clazz = defineClass(name, bytecode, 0, bytecode.length);
                resolveClass(clazz);
                return clazz;
            } else {
                return loadedByParent;
            }
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return parent.getResources(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return parent.getResourceAsStream(name);
        }

        private byte[] loadBytecodeFromParent(String className) {
            String resource = className.replace('.', '/').concat(".class");
            InputStream is = null;
            try {
                is = parent.getResourceAsStream(resource);
                if (is != null) {
                    try {
                        return IOUtil.toByteArray(is);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                IOUtil.closeResource(is);
            }
            return null;
        }
    }
}

