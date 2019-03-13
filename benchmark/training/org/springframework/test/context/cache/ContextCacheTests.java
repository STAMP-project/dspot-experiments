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
package org.springframework.test.context.cache;


import HierarchyMode.CURRENT_LEVEL;
import HierarchyMode.EXHAUSTIVE;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextTestUtils;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


/**
 * Integration tests for verifying proper behavior of the {@link ContextCache} in
 * conjunction with cache keys used in {@link TestContext}.
 *
 * @author Sam Brannen
 * @author Michail Nikolaev
 * @since 3.1
 * @see LruContextCacheTests
 * @see SpringRunnerContextCacheTests
 */
public class ContextCacheTests {
    private ContextCache contextCache = new DefaultContextCache();

    @Test
    public void verifyCacheKeyIsBasedOnContextLoader() {
        loadCtxAndAssertStats(ContextCacheTests.AnnotationConfigContextLoaderTestCase.class, 1, 0, 1);
        loadCtxAndAssertStats(ContextCacheTests.AnnotationConfigContextLoaderTestCase.class, 1, 1, 1);
        loadCtxAndAssertStats(ContextCacheTests.CustomAnnotationConfigContextLoaderTestCase.class, 2, 1, 2);
        loadCtxAndAssertStats(ContextCacheTests.CustomAnnotationConfigContextLoaderTestCase.class, 2, 2, 2);
        loadCtxAndAssertStats(ContextCacheTests.AnnotationConfigContextLoaderTestCase.class, 2, 3, 2);
        loadCtxAndAssertStats(ContextCacheTests.CustomAnnotationConfigContextLoaderTestCase.class, 2, 4, 2);
    }

    @Test
    public void verifyCacheKeyIsBasedOnActiveProfiles() {
        int size = 0;
        int hit = 0;
        int miss = 0;
        loadCtxAndAssertStats(ContextCacheTests.FooBarProfilesTestCase.class, (++size), hit, (++miss));
        loadCtxAndAssertStats(ContextCacheTests.FooBarProfilesTestCase.class, size, (++hit), miss);
        // Profiles {foo, bar} should not hash to the same as {bar,foo}
        loadCtxAndAssertStats(ContextCacheTests.BarFooProfilesTestCase.class, (++size), hit, (++miss));
        loadCtxAndAssertStats(ContextCacheTests.FooBarProfilesTestCase.class, size, (++hit), miss);
        loadCtxAndAssertStats(ContextCacheTests.FooBarProfilesTestCase.class, size, (++hit), miss);
        loadCtxAndAssertStats(ContextCacheTests.BarFooProfilesTestCase.class, size, (++hit), miss);
        loadCtxAndAssertStats(ContextCacheTests.FooBarActiveProfilesResolverTestCase.class, size, (++hit), miss);
    }

    @Test
    public void verifyCacheBehaviorForContextHierarchies() {
        int size = 0;
        int hits = 0;
        int misses = 0;
        // Level 1
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel1TestCase.class, (++size), hits, (++misses));
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel1TestCase.class, size, (++hits), misses);
        // Level 2
        /* L2 */
        /* L1 */
        /* L2 */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel2TestCase.class, (++size), (++hits), (++misses));
        /* L2 */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel2TestCase.class, size, (++hits), misses);
        /* L2 */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel2TestCase.class, size, (++hits), misses);
        // Level 3-A
        /* L3A */
        /* L2 */
        /* L3A */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, (++size), (++hits), (++misses));
        /* L3A */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, size, (++hits), misses);
        // Level 3-B
        /* L3B */
        /* L2 */
        /* L3B */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, (++size), (++hits), (++misses));
        /* L3B */
        loadCtxAndAssertStats(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, size, (++hits), misses);
    }

    @Test
    public void removeContextHierarchyCacheLevel1() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 1
        // Should also remove Levels 2, 3-A, and 3-B, leaving nothing.
        contextCache.remove(getMergedContextConfiguration(testContext3a).getParent().getParent(), CURRENT_LEVEL);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 1", 0, 1, 4);
        assertParentContextCount(0);
    }

    @Test
    public void removeContextHierarchyCacheLevel1WithExhaustiveMode() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 1
        // Should also remove Levels 2, 3-A, and 3-B, leaving nothing.
        contextCache.remove(getMergedContextConfiguration(testContext3a).getParent().getParent(), EXHAUSTIVE);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 1", 0, 1, 4);
        assertParentContextCount(0);
    }

    @Test
    public void removeContextHierarchyCacheLevel2() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 2
        // Should also remove Levels 3-A and 3-B, leaving only Level 1 as a context in the
        // cache but also removing the Level 1 hierarchy since all children have been
        // removed.
        contextCache.remove(getMergedContextConfiguration(testContext3a).getParent(), CURRENT_LEVEL);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 2", 1, 1, 4);
        assertParentContextCount(0);
    }

    @Test
    public void removeContextHierarchyCacheLevel2WithExhaustiveMode() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 2
        // Should wipe the cache
        contextCache.remove(getMergedContextConfiguration(testContext3a).getParent(), EXHAUSTIVE);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 2", 0, 1, 4);
        assertParentContextCount(0);
    }

    @Test
    public void removeContextHierarchyCacheLevel3Then2() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 3-A
        contextCache.remove(getMergedContextConfiguration(testContext3a), CURRENT_LEVEL);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 3-A", 3, 1, 4);
        assertParentContextCount(2);
        // Remove Level 2
        // Should also remove Level 3-B, leaving only Level 1.
        contextCache.remove(getMergedContextConfiguration(testContext3b).getParent(), CURRENT_LEVEL);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 2", 1, 1, 4);
        assertParentContextCount(0);
    }

    @Test
    public void removeContextHierarchyCacheLevel3Then2WithExhaustiveMode() {
        // Load Level 3-A
        TestContext testContext3a = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3aTestCase.class, contextCache);
        testContext3a.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A", 3, 0, 3);
        assertParentContextCount(2);
        // Load Level 3-B
        TestContext testContext3b = TestContextTestUtils.buildTestContext(ContextCacheTests.ClassHierarchyContextHierarchyLevel3bTestCase.class, contextCache);
        testContext3b.getApplicationContext();
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "level 3, A and B", 4, 1, 4);
        assertParentContextCount(2);
        // Remove Level 3-A
        // Should wipe the cache.
        contextCache.remove(getMergedContextConfiguration(testContext3a), EXHAUSTIVE);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 3-A", 0, 1, 4);
        assertParentContextCount(0);
        // Remove Level 2
        // Should not actually do anything since the cache was cleared in the
        // previous step. So the stats should remain the same.
        contextCache.remove(getMergedContextConfiguration(testContext3b).getParent(), EXHAUSTIVE);
        ContextCacheTestUtils.assertContextCacheStatistics(contextCache, "removed level 2", 0, 1, 4);
        assertParentContextCount(0);
    }

    @Configuration
    static class Config {}

    @ContextConfiguration(classes = ContextCacheTests.Config.class, loader = AnnotationConfigContextLoader.class)
    private static class AnnotationConfigContextLoaderTestCase {}

    @ContextConfiguration(classes = ContextCacheTests.Config.class, loader = ContextCacheTests.CustomAnnotationConfigContextLoader.class)
    private static class CustomAnnotationConfigContextLoaderTestCase {}

    private static class CustomAnnotationConfigContextLoader extends AnnotationConfigContextLoader {}

    @ActiveProfiles({ "foo", "bar" })
    @ContextConfiguration(classes = ContextCacheTests.Config.class, loader = AnnotationConfigContextLoader.class)
    private static class FooBarProfilesTestCase {}

    @ActiveProfiles({ "bar", "foo" })
    @ContextConfiguration(classes = ContextCacheTests.Config.class, loader = AnnotationConfigContextLoader.class)
    private static class BarFooProfilesTestCase {}

    private static class FooBarActiveProfilesResolver implements ActiveProfilesResolver {
        @Override
        public String[] resolve(Class<?> testClass) {
            return new String[]{ "foo", "bar" };
        }
    }

    @ActiveProfiles(resolver = ContextCacheTests.FooBarActiveProfilesResolver.class)
    @ContextConfiguration(classes = ContextCacheTests.Config.class, loader = AnnotationConfigContextLoader.class)
    private static class FooBarActiveProfilesResolverTestCase {}

    @ContextHierarchy({ @ContextConfiguration })
    private static class ClassHierarchyContextHierarchyLevel1TestCase {
        @Configuration
        static class Level1Config {}
    }

    @ContextHierarchy({ @ContextConfiguration })
    private static class ClassHierarchyContextHierarchyLevel2TestCase extends ContextCacheTests.ClassHierarchyContextHierarchyLevel1TestCase {
        @Configuration
        static class Level2Config {}
    }

    @ContextHierarchy({ @ContextConfiguration })
    private static class ClassHierarchyContextHierarchyLevel3aTestCase extends ContextCacheTests.ClassHierarchyContextHierarchyLevel2TestCase {
        @Configuration
        static class Level3aConfig {}
    }

    @ContextHierarchy({ @ContextConfiguration })
    private static class ClassHierarchyContextHierarchyLevel3bTestCase extends ContextCacheTests.ClassHierarchyContextHierarchyLevel2TestCase {
        @Configuration
        static class Level3bConfig {}
    }
}

