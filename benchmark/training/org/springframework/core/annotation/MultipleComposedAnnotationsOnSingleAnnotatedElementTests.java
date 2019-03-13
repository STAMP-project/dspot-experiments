/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests that verify support for finding multiple composed annotations on
 * a single annotated element.
 *
 * <p>See <a href="https://jira.spring.io/browse/SPR-13486">SPR-13486</a>.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see AnnotatedElementUtils
 * @see AnnotatedElementUtilsTests
 * @see ComposedRepeatableAnnotationsTests
 */
public class MultipleComposedAnnotationsOnSingleAnnotatedElementTests {
    @Test
    public void getMultipleComposedAnnotationsOnClass() {
        assertGetAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesClass.class);
    }

    @Test
    public void getMultipleInheritedComposedAnnotationsOnSuperclass() {
        assertGetAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.SubMultipleComposedCachesClass.class);
    }

    @Test
    public void getMultipleNoninheritedComposedAnnotationsOnClass() {
        Class<?> element = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleNoninheritedComposedCachesClass.class;
        Set<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> cacheables = getAllMergedAnnotations(element, MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class);
        Assert.assertNotNull(cacheables);
        Assert.assertEquals(2, cacheables.size());
        Iterator<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> iterator = cacheables.iterator();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable1 = iterator.next();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable2 = iterator.next();
        Assert.assertEquals("noninheritedCache1", cacheable1.value());
        Assert.assertEquals("noninheritedCache2", cacheable2.value());
    }

    @Test
    public void getMultipleNoninheritedComposedAnnotationsOnSuperclass() {
        Class<?> element = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.SubMultipleNoninheritedComposedCachesClass.class;
        Set<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> cacheables = getAllMergedAnnotations(element, MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class);
        Assert.assertNotNull(cacheables);
        Assert.assertEquals(0, cacheables.size());
    }

    @Test
    public void getComposedPlusLocalAnnotationsOnClass() {
        assertGetAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.ComposedPlusLocalCachesClass.class);
    }

    @Test
    public void getMultipleComposedAnnotationsOnInterface() {
        Class<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesOnInterfaceClass> element = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesOnInterfaceClass.class;
        Set<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> cacheables = getAllMergedAnnotations(element, MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class);
        Assert.assertNotNull(cacheables);
        Assert.assertEquals(0, cacheables.size());
    }

    @Test
    public void getMultipleComposedAnnotationsOnMethod() throws Exception {
        AnnotatedElement element = getClass().getDeclaredMethod("multipleComposedCachesMethod");
        assertGetAllMergedAnnotationsBehavior(element);
    }

    @Test
    public void getComposedPlusLocalAnnotationsOnMethod() throws Exception {
        AnnotatedElement element = getClass().getDeclaredMethod("composedPlusLocalCachesMethod");
        assertGetAllMergedAnnotationsBehavior(element);
    }

    @Test
    public void findMultipleComposedAnnotationsOnClass() {
        assertFindAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesClass.class);
    }

    @Test
    public void findMultipleInheritedComposedAnnotationsOnSuperclass() {
        assertFindAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.SubMultipleComposedCachesClass.class);
    }

    @Test
    public void findMultipleNoninheritedComposedAnnotationsOnClass() {
        Class<?> element = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleNoninheritedComposedCachesClass.class;
        Set<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> cacheables = findAllMergedAnnotations(element, MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class);
        Assert.assertNotNull(cacheables);
        Assert.assertEquals(2, cacheables.size());
        Iterator<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> iterator = cacheables.iterator();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable1 = iterator.next();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable2 = iterator.next();
        Assert.assertEquals("noninheritedCache1", cacheable1.value());
        Assert.assertEquals("noninheritedCache2", cacheable2.value());
    }

    @Test
    public void findMultipleNoninheritedComposedAnnotationsOnSuperclass() {
        Class<?> element = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.SubMultipleNoninheritedComposedCachesClass.class;
        Set<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> cacheables = findAllMergedAnnotations(element, MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class);
        Assert.assertNotNull(cacheables);
        Assert.assertEquals(2, cacheables.size());
        Iterator<MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable> iterator = cacheables.iterator();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable1 = iterator.next();
        MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable cacheable2 = iterator.next();
        Assert.assertEquals("noninheritedCache1", cacheable1.value());
        Assert.assertEquals("noninheritedCache2", cacheable2.value());
    }

    @Test
    public void findComposedPlusLocalAnnotationsOnClass() {
        assertFindAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.ComposedPlusLocalCachesClass.class);
    }

    @Test
    public void findMultipleComposedAnnotationsOnInterface() {
        assertFindAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesOnInterfaceClass.class);
    }

    @Test
    public void findComposedCacheOnInterfaceAndLocalCacheOnClass() {
        assertFindAllMergedAnnotationsBehavior(MultipleComposedAnnotationsOnSingleAnnotatedElementTests.ComposedCacheOnInterfaceAndLocalCacheClass.class);
    }

    @Test
    public void findMultipleComposedAnnotationsOnMethod() throws Exception {
        AnnotatedElement element = getClass().getDeclaredMethod("multipleComposedCachesMethod");
        assertFindAllMergedAnnotationsBehavior(element);
    }

    @Test
    public void findComposedPlusLocalAnnotationsOnMethod() throws Exception {
        AnnotatedElement element = getClass().getDeclaredMethod("composedPlusLocalCachesMethod");
        assertFindAllMergedAnnotationsBehavior(element);
    }

    @Test
    public void findMultipleComposedAnnotationsOnBridgeMethod() throws Exception {
        assertFindAllMergedAnnotationsBehavior(getBridgeMethod());
    }

    // -------------------------------------------------------------------------
    /**
     * Mock of {@code org.springframework.cache.annotation.Cacheable}.
     */
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Cacheable {
        @AliasFor("cacheName")
        String value() default "";

        @AliasFor("value")
        String cacheName() default "";

        String key() default "";
    }

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable("fooCache")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface FooCache {
        @AliasFor(annotation = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class)
        String key() default "";
    }

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable("barCache")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface BarCache {
        @AliasFor(annotation = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class)
        String key();
    }

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable("noninheritedCache1")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoninheritedCache1 {
        @AliasFor(annotation = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class)
        String key() default "";
    }

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable("noninheritedCache2")
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoninheritedCache2 {
        @AliasFor(annotation = MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable.class)
        String key() default "";
    }

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.FooCache(key = "fooKey")
    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.BarCache(key = "barKey")
    private static class MultipleComposedCachesClass {}

    private static class SubMultipleComposedCachesClass extends MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesClass {}

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.NoninheritedCache1
    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.NoninheritedCache2
    private static class MultipleNoninheritedComposedCachesClass {}

    private static class SubMultipleNoninheritedComposedCachesClass extends MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleNoninheritedComposedCachesClass {}

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable(cacheName = "fooCache", key = "fooKey")
    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.BarCache(key = "barKey")
    private static class ComposedPlusLocalCachesClass {}

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.FooCache(key = "fooKey")
    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.BarCache(key = "barKey")
    private interface MultipleComposedCachesInterface {}

    private static class MultipleComposedCachesOnInterfaceClass implements MultipleComposedAnnotationsOnSingleAnnotatedElementTests.MultipleComposedCachesInterface {}

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.Cacheable(cacheName = "fooCache", key = "fooKey")
    private interface ComposedCacheInterface {}

    @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.BarCache(key = "barKey")
    private static class ComposedCacheOnInterfaceAndLocalCacheClass implements MultipleComposedAnnotationsOnSingleAnnotatedElementTests.ComposedCacheInterface {}

    public interface GenericParameter<T> {
        T getFor(Class<T> cls);
    }

    @SuppressWarnings("unused")
    private static class StringGenericParameter implements MultipleComposedAnnotationsOnSingleAnnotatedElementTests.GenericParameter<String> {
        @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.FooCache(key = "fooKey")
        @MultipleComposedAnnotationsOnSingleAnnotatedElementTests.BarCache(key = "barKey")
        @Override
        public String getFor(Class<String> cls) {
            return "foo";
        }

        public String getFor(Integer integer) {
            return "foo";
        }
    }
}

