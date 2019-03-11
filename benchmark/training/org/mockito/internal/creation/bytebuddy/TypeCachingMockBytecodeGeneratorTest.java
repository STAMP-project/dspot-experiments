/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;


import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.WeakHashMap;
import org.junit.Test;
import org.mockito.mock.SerializableMode;
import org.mockitoutil.ClassLoaders;
import org.mockitoutil.SimpleClassGenerator;


public class TypeCachingMockBytecodeGeneratorTest {
    @Test
    public void ensure_cache_is_cleared_if_no_reference_to_classloader_and_classes() throws Exception {
        // given
        ClassLoader classloader_with_life_shorter_than_cache = ClassLoaders.inMemoryClassLoader().withClassDefinition("foo.Bar", SimpleClassGenerator.makeMarkerInterface("foo.Bar")).build();
        TypeCachingBytecodeGenerator cachingMockBytecodeGenerator = new TypeCachingBytecodeGenerator(new SubclassBytecodeGenerator(), true);
        Class<?> the_mock_type = cachingMockBytecodeGenerator.mockClass(MockFeatures.withMockFeatures(classloader_with_life_shorter_than_cache.loadClass("foo.Bar"), Collections.<Class<?>>emptySet(), SerializableMode.NONE, false));
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        Reference<Object> typeReference = new PhantomReference<Object>(the_mock_type, referenceQueue);
        // when
        classloader_with_life_shorter_than_cache = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        the_mock_type = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        System.gc();
        TypeCachingMockBytecodeGeneratorTest.ensure_gc_happened();
        // then
        assertThat(referenceQueue.poll()).isEqualTo(typeReference);
    }

    @Test
    public void ensure_cache_returns_same_instance() throws Exception {
        // given
        ClassLoader classloader_with_life_shorter_than_cache = ClassLoaders.inMemoryClassLoader().withClassDefinition("foo.Bar", SimpleClassGenerator.makeMarkerInterface("foo.Bar")).build();
        TypeCachingBytecodeGenerator cachingMockBytecodeGenerator = new TypeCachingBytecodeGenerator(new SubclassBytecodeGenerator(), true);
        Class<?> the_mock_type = cachingMockBytecodeGenerator.mockClass(MockFeatures.withMockFeatures(classloader_with_life_shorter_than_cache.loadClass("foo.Bar"), Collections.<Class<?>>emptySet(), SerializableMode.NONE, false));
        Class<?> other_mock_type = cachingMockBytecodeGenerator.mockClass(MockFeatures.withMockFeatures(classloader_with_life_shorter_than_cache.loadClass("foo.Bar"), Collections.<Class<?>>emptySet(), SerializableMode.NONE, false));
        assertThat(other_mock_type).isSameAs(the_mock_type);
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        Reference<Object> typeReference = new PhantomReference<Object>(the_mock_type, referenceQueue);
        // when
        classloader_with_life_shorter_than_cache = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        the_mock_type = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        other_mock_type = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        System.gc();
        TypeCachingMockBytecodeGeneratorTest.ensure_gc_happened();
        // then
        assertThat(referenceQueue.poll()).isEqualTo(typeReference);
    }

    @Test
    public void ensure_cache_returns_different_instance_serializableMode() throws Exception {
        // given
        ClassLoader classloader_with_life_shorter_than_cache = ClassLoaders.inMemoryClassLoader().withClassDefinition("foo.Bar", SimpleClassGenerator.makeMarkerInterface("foo.Bar")).build();
        TypeCachingBytecodeGenerator cachingMockBytecodeGenerator = new TypeCachingBytecodeGenerator(new SubclassBytecodeGenerator(), true);
        Class<?> the_mock_type = cachingMockBytecodeGenerator.mockClass(MockFeatures.withMockFeatures(classloader_with_life_shorter_than_cache.loadClass("foo.Bar"), Collections.<Class<?>>emptySet(), SerializableMode.NONE, false));
        Class<?> other_mock_type = cachingMockBytecodeGenerator.mockClass(MockFeatures.withMockFeatures(classloader_with_life_shorter_than_cache.loadClass("foo.Bar"), Collections.<Class<?>>emptySet(), SerializableMode.BASIC, false));
        assertThat(other_mock_type).isNotSameAs(the_mock_type);
    }

    @Test
    public void validate_simple_code_idea_where_weakhashmap_with_classloader_as_key_get_GCed_when_no_more_references() throws Exception {
        // given
        WeakHashMap<ClassLoader, Object> cache = new WeakHashMap<ClassLoader, Object>();
        ClassLoader short_lived_classloader = ClassLoaders.inMemoryClassLoader().withClassDefinition("foo.Bar", SimpleClassGenerator.makeMarkerInterface("foo.Bar")).build();
        cache.put(short_lived_classloader, new TypeCachingMockBytecodeGeneratorTest.HoldingAReference(new WeakReference<Class<?>>(short_lived_classloader.loadClass("foo.Bar"))));
        assertThat(cache).hasSize(1);
        // when
        short_lived_classloader = TypeCachingMockBytecodeGeneratorTest.is_no_more_referenced();
        System.gc();
        TypeCachingMockBytecodeGeneratorTest.ensure_gc_happened();
        // then
        assertThat(cache).isEmpty();
    }

    static class HoldingAReference {
        final WeakReference<Class<?>> a;

        HoldingAReference(WeakReference<Class<?>> a) {
            this.a = a;
        }
    }
}

