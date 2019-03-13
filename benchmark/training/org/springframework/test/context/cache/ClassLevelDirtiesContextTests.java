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


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * JUnit 4 based integration test which verifies correct {@linkplain ContextCache
 * application context caching} in conjunction with the {@link SpringRunner} and
 * {@link DirtiesContext @DirtiesContext} at the class level.
 *
 * @author Sam Brannen
 * @since 3.0
 */
@RunWith(JUnit4.class)
public class ClassLevelDirtiesContextTests {
    private static final AtomicInteger cacheHits = new AtomicInteger(0);

    private static final AtomicInteger cacheMisses = new AtomicInteger(0);

    @Test
    public void verifyDirtiesContextBehavior() throws Exception {
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithCleanMethodsAndDefaultModeTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with clean test method and default class mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithCleanMethodsAndDefaultModeTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with clean test method and default class mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with clean test method and AFTER_CLASS mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with clean test method and AFTER_CLASS mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithAfterEachTestMethodModeTestCase.class, 3);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with clean test method and AFTER_EACH_TEST_METHOD mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.addAndGet(2));
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithAfterEachTestMethodModeTestCase.class, 3);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with clean test method and AFTER_EACH_TEST_METHOD mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.addAndGet(2));
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.get(), ClassLevelDirtiesContextTests.cacheMisses.incrementAndGet());
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.get(), ClassLevelDirtiesContextTests.cacheMisses.incrementAndGet());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.get(), ClassLevelDirtiesContextTests.cacheMisses.incrementAndGet());
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.InheritedClassLevelDirtiesContextWithDirtyMethodsTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after inherited class-level @DirtiesContext with dirty test method", 0, ClassLevelDirtiesContextTests.cacheHits.get(), ClassLevelDirtiesContextTests.cacheMisses.incrementAndGet());
        assertBehaviorForCleanTestCase();
        runTestClassAndAssertStats(ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase.class, 1);
        ContextCacheTestUtils.assertContextCacheStatistics("after class-level @DirtiesContext with clean test method and AFTER_CLASS mode", 0, ClassLevelDirtiesContextTests.cacheHits.incrementAndGet(), ClassLevelDirtiesContextTests.cacheMisses.get());
    }

    // -------------------------------------------------------------------
    @RunWith(SpringRunner.class)
    @ContextConfiguration
    abstract static class BaseTestCase {
        /* no beans */
        @Configuration
        static class Config {}

        @Autowired
        protected ApplicationContext applicationContext;

        protected void assertApplicationContextWasAutowired() {
            Assert.assertNotNull("The application context should have been autowired.", this.applicationContext);
        }
    }

    public static final class CleanTestCase extends ClassLevelDirtiesContextTests.BaseTestCase {
        @Test
        public void verifyContextWasAutowired() {
            assertApplicationContextWasAutowired();
        }
    }

    @DirtiesContext
    public static class ClassLevelDirtiesContextWithCleanMethodsAndDefaultModeTestCase extends ClassLevelDirtiesContextTests.BaseTestCase {
        @Test
        public void verifyContextWasAutowired() {
            assertApplicationContextWasAutowired();
        }
    }

    public static class InheritedClassLevelDirtiesContextWithCleanMethodsAndDefaultModeTestCase extends ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithCleanMethodsAndDefaultModeTestCase {}

    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    public static class ClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase extends ClassLevelDirtiesContextTests.BaseTestCase {
        @Test
        public void verifyContextWasAutowired() {
            assertApplicationContextWasAutowired();
        }
    }

    public static class InheritedClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase extends ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithCleanMethodsAndAfterClassModeTestCase {}

    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    public static class ClassLevelDirtiesContextWithAfterEachTestMethodModeTestCase extends ClassLevelDirtiesContextTests.BaseTestCase {
        @Test
        public void verifyContextWasAutowired1() {
            assertApplicationContextWasAutowired();
        }

        @Test
        public void verifyContextWasAutowired2() {
            assertApplicationContextWasAutowired();
        }

        @Test
        public void verifyContextWasAutowired3() {
            assertApplicationContextWasAutowired();
        }
    }

    public static class InheritedClassLevelDirtiesContextWithAfterEachTestMethodModeTestCase extends ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithAfterEachTestMethodModeTestCase {}

    @DirtiesContext
    public static class ClassLevelDirtiesContextWithDirtyMethodsTestCase extends ClassLevelDirtiesContextTests.BaseTestCase {
        @Test
        @DirtiesContext
        public void dirtyContext() {
            assertApplicationContextWasAutowired();
        }
    }

    public static class InheritedClassLevelDirtiesContextWithDirtyMethodsTestCase extends ClassLevelDirtiesContextTests.ClassLevelDirtiesContextWithDirtyMethodsTestCase {}
}

