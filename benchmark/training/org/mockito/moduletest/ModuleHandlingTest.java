/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.moduletest;


import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import org.hamcrest.core.Is;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker;
import org.mockito.stubbing.OngoingStubbing;

import static net.bytebuddy.dynamic.loading.ClassInjector.UsingReflection.isAvailable;


@RunWith(Parameterized.class)
public class ModuleHandlingTest {
    private final boolean namedModules;

    public ModuleHandlingTest(boolean namedModules) {
        this.namedModules = namedModules;
    }

    @Test
    public void can_define_class_in_open_reading_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(true, true, true);
        ModuleLayer layer = layer(jar, true);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            assertThat(mock.getClass().getName()).startsWith("sample.MyCallable$MockitoMock$");
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void can_define_class_in_open_java_util_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(true, true, true);
        ModuleLayer layer = layer(jar, true);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("java.util.concurrent.locks.Lock");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Lock mock = ((Lock) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.tryLock());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenReturn", Object.class).invoke(stubbing, true);
            boolean relocated = (!(Boolean.getBoolean("org.mockito.internal.noUnsafeInjection"))) && (isAvailable());
            String prefix = (relocated) ? "org.mockito.codegen.Lock$MockitoMock$" : "java.util.concurrent.locks.Lock$MockitoMock$";
            assertThat(mock.getClass().getName()).startsWith(prefix);
            assertThat(mock.tryLock()).isEqualTo(true);
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void inline_mock_maker_can_mock_closed_modules() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(true));
        Path jar = ModuleHandlingTest.modularJar(false, false, false);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        Class<?> mockito = loader.loadClass(Mockito.class.getName());
        @SuppressWarnings("unchecked")
        Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
        Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
        loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
        assertThat(mock.getClass().getName()).isEqualTo("sample.MyCallable");
        assertThat(mock.call()).isEqualTo("foo");
    }

    @Test
    public void can_define_class_in_open_reading_private_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(false, true, true);
        ModuleLayer layer = layer(jar, true);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            assertThat(mock.getClass().getName()).startsWith("sample.MyCallable$MockitoMock$");
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void can_define_class_in_open_non_reading_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(true, true, true);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            assertThat(mock.getClass().getName()).startsWith("sample.MyCallable$MockitoMock$");
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void can_define_class_in_open_non_reading_non_exporting_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(true, false, true);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            assertThat(mock.getClass().getName()).startsWith("sample.MyCallable$MockitoMock$");
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void can_define_class_in_closed_module() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(true, true, false);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            boolean relocated = (!(Boolean.getBoolean("org.mockito.internal.noUnsafeInjection"))) && (isAvailable());
            String prefix = (relocated) ? "sample.MyCallable$MockitoMock$" : "org.mockito.codegen.MyCallable$MockitoMock$";
            assertThat(mock.getClass().getName()).startsWith(prefix);
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void cannot_define_class_in_non_opened_non_exported_module_if_lookup_injection() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Assume.assumeThat(((!(Boolean.getBoolean("org.mockito.internal.noUnsafeInjection"))) && (isAvailable())), Is.is(true));
        Path jar = ModuleHandlingTest.modularJar(false, false, false);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            @SuppressWarnings("unchecked")
            Callable<String> mock = ((Callable<String>) (mockito.getMethod("mock", Class.class).invoke(null, type)));
            Object stubbing = mockito.getMethod("when", Object.class).invoke(null, mock.call());
            loader.loadClass(OngoingStubbing.class.getName()).getMethod("thenCallRealMethod").invoke(stubbing);
            assertThat(mock.getClass().getName()).startsWith("sample.MyCallable$MockitoMock$");
            assertThat(mock.call()).isEqualTo("foo");
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }

    @Test
    public void can_define_class_in_non_opened_non_exported_module_if_unsafe_injection() throws Exception {
        Assume.assumeThat(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker), Is.is(false));
        Assume.assumeThat(((!(Boolean.getBoolean("org.mockito.internal.noUnsafeInjection"))) && (isAvailable())), Is.is(false));
        Path jar = ModuleHandlingTest.modularJar(false, false, false);
        ModuleLayer layer = layer(jar, false);
        ClassLoader loader = layer.findLoader("mockito.test");
        Class<?> type = loader.loadClass("sample.MyCallable");
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            Class<?> mockito = loader.loadClass(Mockito.class.getName());
            try {
                mockito.getMethod("mock", Class.class).invoke(null, type);
                fail("Expected mocking to fail");
            } catch (InvocationTargetException e) {
                assertThat(e.getTargetException()).isInstanceOf(loader.loadClass(MockitoException.class.getName()));
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextLoader);
        }
    }
}

