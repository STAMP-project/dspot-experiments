/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.test.context;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests that verify proper behavior of {@link DirtiesContext @DirtiesContext}
 * in conjunction with context hierarchies configured via {@link ContextHierarchy @ContextHierarchy}.
 *
 * @author Sam Brannen
 * @author Tadaya Tsuyukubo
 * @since 3.2.2
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContextHierarchyDirtiesContextTests {
    private static ApplicationContext context;

    private static String foo;

    private static String bar;

    private static String baz;

    @Test
    public void classLevelDirtiesContextWithCurrentLevelHierarchyMode() {
        runTestAndVerifyHierarchies(ContextHierarchyDirtiesContextTests.ClassLevelDirtiesContextWithCurrentLevelModeTestCase.class, true, true, false);
    }

    @Test
    public void classLevelDirtiesContextWithExhaustiveHierarchyMode() {
        runTestAndVerifyHierarchies(ContextHierarchyDirtiesContextTests.ClassLevelDirtiesContextWithExhaustiveModeTestCase.class, false, false, false);
    }

    @Test
    public void methodLevelDirtiesContextWithCurrentLevelHierarchyMode() {
        runTestAndVerifyHierarchies(ContextHierarchyDirtiesContextTests.MethodLevelDirtiesContextWithCurrentLevelModeTestCase.class, true, true, false);
    }

    @Test
    public void methodLevelDirtiesContextWithExhaustiveHierarchyMode() {
        runTestAndVerifyHierarchies(ContextHierarchyDirtiesContextTests.MethodLevelDirtiesContextWithExhaustiveModeTestCase.class, false, false, false);
    }

    // -------------------------------------------------------------------------
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextHierarchy(@ContextConfiguration(name = "foo"))
    abstract static class FooTestCase implements ApplicationContextAware {
        @Configuration
        static class Config {
            @Bean
            public String bean() {
                return "foo";
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            ContextHierarchyDirtiesContextTests.context = applicationContext;
            ContextHierarchyDirtiesContextTests.baz = applicationContext.getBean("bean", String.class);
            ContextHierarchyDirtiesContextTests.bar = applicationContext.getParent().getBean("bean", String.class);
            ContextHierarchyDirtiesContextTests.foo = applicationContext.getParent().getParent().getBean("bean", String.class);
        }
    }

    @ContextHierarchy(@ContextConfiguration(name = "bar"))
    abstract static class BarTestCase extends ContextHierarchyDirtiesContextTests.FooTestCase {
        @Configuration
        static class Config {
            @Bean
            public String bean() {
                return "bar";
            }
        }
    }

    @ContextHierarchy(@ContextConfiguration(name = "baz"))
    abstract static class BazTestCase extends ContextHierarchyDirtiesContextTests.BarTestCase {
        @Configuration
        static class Config {
            @Bean
            public String bean() {
                return "baz";
            }
        }
    }

    // -------------------------------------------------------------------------
    /**
     * {@link DirtiesContext} is declared at the class level, without specifying
     * the {@link DirtiesContext.HierarchyMode}.
     * <p>After running this test class, the context cache should be <em>exhaustively</em>
     * cleared beginning from the current context hierarchy, upwards to the highest
     * parent context, and then back down through all subhierarchies of the parent
     * context.
     */
    @DirtiesContext
    public static class ClassLevelDirtiesContextWithExhaustiveModeTestCase extends ContextHierarchyDirtiesContextTests.BazTestCase {
        @Test
        public void test() {
        }
    }

    /**
     * {@link DirtiesContext} is declared at the class level, specifying the
     * {@link DirtiesContext.HierarchyMode#CURRENT_LEVEL CURRENT_LEVEL} hierarchy mode.
     * <p>After running this test class, the context cache should be cleared
     * beginning from the current context hierarchy and down through all subhierarchies.
     */
    @DirtiesContext(hierarchyMode = HierarchyMode.CURRENT_LEVEL)
    public static class ClassLevelDirtiesContextWithCurrentLevelModeTestCase extends ContextHierarchyDirtiesContextTests.BazTestCase {
        @Test
        public void test() {
        }
    }

    /**
     * {@link DirtiesContext} is declared at the method level, without specifying
     * the {@link DirtiesContext.HierarchyMode}.
     * <p>After running this test class, the context cache should be <em>exhaustively</em>
     * cleared beginning from the current context hierarchy, upwards to the highest
     * parent context, and then back down through all subhierarchies of the parent
     * context.
     */
    public static class MethodLevelDirtiesContextWithExhaustiveModeTestCase extends ContextHierarchyDirtiesContextTests.BazTestCase {
        @Test
        @DirtiesContext
        public void test() {
        }
    }

    /**
     * {@link DirtiesContext} is declared at the method level, specifying the
     * {@link DirtiesContext.HierarchyMode#CURRENT_LEVEL CURRENT_LEVEL} hierarchy mode.
     * <p>After running this test class, the context cache should be cleared
     * beginning from the current context hierarchy and down through all subhierarchies.
     */
    public static class MethodLevelDirtiesContextWithCurrentLevelModeTestCase extends ContextHierarchyDirtiesContextTests.BazTestCase {
        @Test
        @DirtiesContext(hierarchyMode = HierarchyMode.CURRENT_LEVEL)
        public void test() {
        }
    }
}

