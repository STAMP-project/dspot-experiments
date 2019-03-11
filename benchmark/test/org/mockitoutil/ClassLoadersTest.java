/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoutil;


import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClassLoadersTest {
    public static final String CLASS_NAME_DEPENDING_ON_INTERFACE = "org.mockitoutil.ClassLoadersTest$ClassUsingInterface1";

    public static final String INTERFACE_NAME = "org.mockitoutil.ClassLoadersTest$Interface1";

    @Test(expected = ClassNotFoundException.class)
    public void isolated_class_loader_cannot_load_classes_when_no_given_prefix() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().build();
        // when
        cl.loadClass("org.mockito.Mockito");
        // then raises CNFE
    }

    @Test
    public void isolated_class_loader_cannot_load_classes_if_no_code_source_path() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withPrivateCopyOf(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE).build();
        // when
        try {
            cl.loadClass(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // then
            assertThat(e).hasMessageContaining(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE);
        }
    }

    @Test
    public void isolated_class_loader_cannot_load_classes_if_dependent_classes_do_not_match_the_prefixes() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE).build();
        // when
        try {
            cl.loadClass(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE);
            Assert.fail();
        } catch (NoClassDefFoundError e) {
            // then
            assertThat(e).hasMessageContaining("org/mockitoutil/ClassLoadersTest$Interface1");
        }
    }

    @Test
    public void isolated_class_loader_can_load_classes_when_dependent_classes_are_matching_the_prefixes() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE).withPrivateCopyOf(ClassLoadersTest.INTERFACE_NAME).build();
        // when
        Class<?> aClass = cl.loadClass(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE);
        // then
        assertThat(aClass).isNotNull();
        assertThat(aClass.getClassLoader()).isEqualTo(cl);
        assertThat(aClass.getInterfaces()[0].getClassLoader()).isEqualTo(cl);
    }

    @Test
    public void isolated_class_loader_can_load_classes_isolated_classes_in_isolation() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.class.getPackage().getName()).build();
        // when
        Class<?> aClass = cl.loadClass(ClassLoadersTest.AClass.class.getName());
        // then
        assertThat(aClass).isNotNull();
        assertThat(aClass).isNotSameAs(ClassLoadersTest.AClass.class);
        assertThat(aClass.getClassLoader()).isEqualTo(cl);
    }

    @Test
    public void isolated_class_loader_cannot_load_classes_if_prefix_excluded() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.class.getPackage().getName()).without(ClassLoadersTest.AClass.class.getName()).build();
        // when
        try {
            cl.loadClass(ClassLoadersTest.AClass.class.getName());
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // then
            assertThat(e).hasMessageContaining("org.mockitoutil").hasMessageContaining(ClassLoadersTest.AClass.class.getName());
        }
    }

    @Test
    public void isolated_class_loader_has_no_parent() throws Exception {
        ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.CLASS_NAME_DEPENDING_ON_INTERFACE).withPrivateCopyOf(ClassLoadersTest.INTERFACE_NAME).build();
        assertThat(cl.getParent()).isNull();
    }

    @Test(expected = ClassNotFoundException.class)
    public void excluding_class_loader_cannot_load_classes_when_no_correct_source_url_set() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.excludingClassLoader().withCodeSourceUrlOf(this.getClass()).build();
        // when
        cl.loadClass("org.mockito.Mockito");
        // then class CNFE
    }

    @Test
    public void excluding_class_loader_can_load_classes_when_correct_source_url_set() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.excludingClassLoader().withCodeSourceUrlOf(Mockito.class).build();
        // when
        cl.loadClass("org.mockito.Mockito");
        // then class successfully loaded
    }

    @Test
    public void excluding_class_loader_cannot_load_class_when_excluded_prefix_match_class_to_load() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.excludingClassLoader().withCodeSourceUrlOf(Mockito.class).without("org.mockito.BDDMockito").build();
        cl.loadClass("org.mockito.Mockito");
        // when
        try {
            cl.loadClass("org.mockito.BDDMockito");
            Assert.fail("should have raise a ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            assertThat(e.getMessage()).contains("org.mockito.BDDMockito");
        }
        // then class successfully loaded
    }

    @Test
    public void can_not_load_a_class_not_previously_registered_in_builder() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.inMemoryClassLoader().withClassDefinition("yop.Dude", SimpleClassGenerator.makeMarkerInterface("yop.Dude")).build();
        // when
        try {
            cl.loadClass("not.Defined");
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // then
            assertThat(e.getMessage()).contains("not.Defined");
        }
    }

    @Test
    public void can_load_a_class_in_memory_from_bytes() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.inMemoryClassLoader().withClassDefinition("yop.Dude", SimpleClassGenerator.makeMarkerInterface("yop.Dude")).build();
        // when
        Class<?> aClass = cl.loadClass("yop.Dude");
        // then
        assertThat(aClass).isNotNull();
        assertThat(aClass.getClassLoader()).isEqualTo(cl);
        assertThat(aClass.getName()).isEqualTo("yop.Dude");
    }

    @Test
    public void cannot_load_a_class_file_not_in_parent() throws Exception {
        // given
        ClassLoader cl = ClassLoaders.inMemoryClassLoader().withParent(ClassLoaders.jdkClassLoader()).build();
        cl.loadClass("java.lang.String");
        try {
            // when
            cl.loadClass("org.mockito.Mockito");
            Assert.fail("should have not found Mockito class");
        } catch (ClassNotFoundException e) {
            // then
            assertThat(e.getMessage()).contains("org.mockito.Mockito");
        }
    }

    @Test
    public void can_list_all_classes_reachable_in_a_classloader() throws Exception {
        ClassLoader classLoader = // .withCodeSourceUrlOf(ClassLoaders.class)
        ClassLoaders.inMemoryClassLoader().withParent(ClassLoaders.jdkClassLoader()).withClassDefinition("a.A", SimpleClassGenerator.makeMarkerInterface("a.A")).withClassDefinition("a.b.B", SimpleClassGenerator.makeMarkerInterface("a.b.B")).withClassDefinition("c.C", SimpleClassGenerator.makeMarkerInterface("c.C")).build();
        assertThat(ClassLoaders.in(classLoader).listOwnedClasses()).containsOnly("a.A", "a.b.B", "c.C");
        assertThat(ClassLoaders.in(classLoader).omit("b", "c").listOwnedClasses()).containsOnly("a.A");
    }

    @Test
    public void return_bootstrap_classloader() throws Exception {
        assertThat(ClassLoaders.jdkClassLoader()).isNotEqualTo(Mockito.class.getClassLoader());
        assertThat(ClassLoaders.jdkClassLoader()).isNotEqualTo(ClassLoaders.class.getClassLoader());
        assertThat(ClassLoaders.jdkClassLoader()).isEqualTo(Number.class.getClassLoader());
        assertThat(ClassLoaders.jdkClassLoader()).isEqualTo(null);
    }

    @Test
    public void return_current_classloader() throws Exception {
        assertThat(ClassLoaders.currentClassLoader()).isEqualTo(this.getClass().getClassLoader());
    }

    @Test
    public void can_run_in_given_classloader() throws Exception {
        // given
        final ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withCodeSourceUrlOf(Assertions.class).withPrivateCopyOf("org.assertj.core").withPrivateCopyOf(ClassLoadersTest.class.getPackage().getName()).without(ClassLoadersTest.AClass.class.getName()).build();
        final AtomicBoolean executed = new AtomicBoolean(false);
        // when
        ClassLoaders.using(cl).execute(new Runnable() {
            @Override
            public void run() {
                assertThat(this.getClass().getClassLoader()).describedAs("runnable is reloaded in given classloader").isEqualTo(cl);
                assertThat(Thread.currentThread().getContextClassLoader()).describedAs("Thread context classloader is using given classloader").isEqualTo(cl);
                try {
                    assertThat(Thread.currentThread().getContextClassLoader().loadClass("java.lang.String")).describedAs("can load JDK type").isNotNull();
                    assertThat(Thread.currentThread().getContextClassLoader().loadClass("org.mockitoutil.ClassLoadersTest$ClassUsingInterface1")).describedAs("can load classloader types").isNotNull();
                } catch (ClassNotFoundException cnfe) {
                    Assertions.fail("should not have raised a CNFE", cnfe);
                }
                executed.set(true);
            }
        });
        // then
        assertThat(executed.get()).isEqualTo(true);
    }

    @Test
    public void cannot_load_runnable_in_given_classloader_if_some_type_cant_be_loaded() throws Exception {
        // given
        final ClassLoader cl = ClassLoaders.isolatedClassLoader().withCurrentCodeSourceUrls().withPrivateCopyOf(ClassLoadersTest.class.getPackage().getName()).without(ClassLoadersTest.AClass.class.getName()).build();
        // when
        try {
            ClassLoaders.using(cl).execute(new Runnable() {
                @Override
                public void run() {
                    ClassLoadersTest.AClass cant_be_found = new ClassLoadersTest.AClass();
                }
            });
            Assertions.fail("should have raised a ClassNotFoundException");
        } catch (IllegalStateException ise) {
            // then
            assertThat(ise).hasCauseInstanceOf(NoClassDefFoundError.class).hasMessageContaining("AClass");
        }
    }

    @SuppressWarnings("unused")
    static class AClass {}

    @SuppressWarnings("unused")
    static class ClassUsingInterface1 implements ClassLoadersTest.Interface1 {}

    @SuppressWarnings("unused")
    interface Interface1 {}
}

