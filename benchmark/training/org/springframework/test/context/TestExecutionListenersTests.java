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
package org.springframework.test.context;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;


/**
 * Unit tests for the {@link TestExecutionListeners @TestExecutionListeners}
 * annotation, which verify:
 * <ul>
 * <li>Proper registering of {@linkplain TestExecutionListener listeners} in
 * conjunction with a {@link TestContextManager}</li>
 * <li><em>Inherited</em> functionality proposed in
 * <a href="https://jira.spring.io/browse/SPR-3896" target="_blank">SPR-3896</a></li>
 * </ul>
 *
 * @author Sam Brannen
 * @since 2.5
 */
public class TestExecutionListenersTests {
    @Test
    public void defaultListeners() {
        List<Class<?>> expected = Arrays.asList(ServletTestExecutionListener.class, DirtiesContextBeforeModesTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class);
        assertRegisteredListeners(TestExecutionListenersTests.DefaultListenersTestCase.class, expected);
    }

    /**
     *
     *
     * @since 4.1
     */
    @Test
    public void defaultListenersMergedWithCustomListenerPrepended() {
        List<Class<?>> expected = Arrays.asList(TestExecutionListenersTests.QuuxTestExecutionListener.class, ServletTestExecutionListener.class, DirtiesContextBeforeModesTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class);
        assertRegisteredListeners(TestExecutionListenersTests.MergedDefaultListenersWithCustomListenerPrependedTestCase.class, expected);
    }

    /**
     *
     *
     * @since 4.1
     */
    @Test
    public void defaultListenersMergedWithCustomListenerAppended() {
        List<Class<?>> expected = Arrays.asList(ServletTestExecutionListener.class, DirtiesContextBeforeModesTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class, TestExecutionListenersTests.BazTestExecutionListener.class);
        assertRegisteredListeners(TestExecutionListenersTests.MergedDefaultListenersWithCustomListenerAppendedTestCase.class, expected);
    }

    /**
     *
     *
     * @since 4.1
     */
    @Test
    public void defaultListenersMergedWithCustomListenerInserted() {
        List<Class<?>> expected = Arrays.asList(ServletTestExecutionListener.class, DirtiesContextBeforeModesTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, SqlScriptsTestExecutionListener.class);
        assertRegisteredListeners(TestExecutionListenersTests.MergedDefaultListenersWithCustomListenerInsertedTestCase.class, expected);
    }

    @Test
    public void nonInheritedDefaultListeners() {
        assertRegisteredListeners(TestExecutionListenersTests.NonInheritedDefaultListenersTestCase.class, Arrays.asList(TestExecutionListenersTests.QuuxTestExecutionListener.class));
    }

    @Test
    public void inheritedDefaultListeners() {
        assertRegisteredListeners(TestExecutionListenersTests.InheritedDefaultListenersTestCase.class, Arrays.asList(TestExecutionListenersTests.QuuxTestExecutionListener.class));
        assertRegisteredListeners(TestExecutionListenersTests.SubInheritedDefaultListenersTestCase.class, Arrays.asList(TestExecutionListenersTests.QuuxTestExecutionListener.class));
        assertRegisteredListeners(TestExecutionListenersTests.SubSubInheritedDefaultListenersTestCase.class, Arrays.asList(TestExecutionListenersTests.QuuxTestExecutionListener.class, TestExecutionListenersTests.EnigmaTestExecutionListener.class));
    }

    @Test
    public void customListeners() {
        assertNumRegisteredListeners(TestExecutionListenersTests.ExplicitListenersTestCase.class, 3);
    }

    @Test
    public void customListenersDeclaredOnInterface() {
        assertRegisteredListeners(TestExecutionListenersTests.ExplicitListenersOnTestInterfaceTestCase.class, Arrays.asList(TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class));
    }

    @Test
    public void nonInheritedListeners() {
        assertNumRegisteredListeners(TestExecutionListenersTests.NonInheritedListenersTestCase.class, 1);
    }

    @Test
    public void inheritedListeners() {
        assertNumRegisteredListeners(TestExecutionListenersTests.InheritedListenersTestCase.class, 4);
    }

    @Test
    public void customListenersRegisteredViaMetaAnnotation() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaTestCase.class, 3);
    }

    @Test
    public void nonInheritedListenersRegisteredViaMetaAnnotation() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaNonInheritedListenersTestCase.class, 1);
    }

    @Test
    public void inheritedListenersRegisteredViaMetaAnnotation() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaInheritedListenersTestCase.class, 4);
    }

    @Test
    public void customListenersRegisteredViaMetaAnnotationWithOverrides() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaWithOverridesTestCase.class, 3);
    }

    @Test
    public void customsListenersRegisteredViaMetaAnnotationWithInheritedListenersWithOverrides() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaInheritedListenersWithOverridesTestCase.class, 5);
    }

    @Test
    public void customListenersRegisteredViaMetaAnnotationWithNonInheritedListenersWithOverrides() {
        assertNumRegisteredListeners(TestExecutionListenersTests.MetaNonInheritedListenersWithOverridesTestCase.class, 8);
    }

    @Test(expected = AnnotationConfigurationException.class)
    public void listenersAndValueAttributesDeclared() {
        new TestContextManager(TestExecutionListenersTests.DuplicateListenersConfigTestCase.class);
    }

    // -------------------------------------------------------------------
    static class DefaultListenersTestCase {}

    @TestExecutionListeners(listeners = { TestExecutionListenersTests.QuuxTestExecutionListener.class, DependencyInjectionTestExecutionListener.class }, mergeMode = MERGE_WITH_DEFAULTS)
    static class MergedDefaultListenersWithCustomListenerPrependedTestCase {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.BazTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
    static class MergedDefaultListenersWithCustomListenerAppendedTestCase {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.BarTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
    static class MergedDefaultListenersWithCustomListenerInsertedTestCase {}

    @TestExecutionListeners(TestExecutionListenersTests.QuuxTestExecutionListener.class)
    static class InheritedDefaultListenersTestCase extends TestExecutionListenersTests.DefaultListenersTestCase {}

    static class SubInheritedDefaultListenersTestCase extends TestExecutionListenersTests.InheritedDefaultListenersTestCase {}

    @TestExecutionListeners(TestExecutionListenersTests.EnigmaTestExecutionListener.class)
    static class SubSubInheritedDefaultListenersTestCase extends TestExecutionListenersTests.SubInheritedDefaultListenersTestCase {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.QuuxTestExecutionListener.class, inheritListeners = false)
    static class NonInheritedDefaultListenersTestCase extends TestExecutionListenersTests.InheritedDefaultListenersTestCase {}

    @TestExecutionListeners({ TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class, TestExecutionListenersTests.BazTestExecutionListener.class })
    static class ExplicitListenersTestCase {}

    @TestExecutionListeners(TestExecutionListenersTests.QuuxTestExecutionListener.class)
    static class InheritedListenersTestCase extends TestExecutionListenersTests.ExplicitListenersTestCase {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.QuuxTestExecutionListener.class, inheritListeners = false)
    static class NonInheritedListenersTestCase extends TestExecutionListenersTests.InheritedListenersTestCase {}

    @TestExecutionListeners({ TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class })
    interface ExplicitListenersTestInterface {}

    static class ExplicitListenersOnTestInterfaceTestCase implements TestExecutionListenersTests.ExplicitListenersTestInterface {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.FooTestExecutionListener.class, value = TestExecutionListenersTests.BarTestExecutionListener.class)
    static class DuplicateListenersConfigTestCase {}

    @TestExecutionListeners({ TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class, TestExecutionListenersTests.BazTestExecutionListener.class })
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaListeners {}

    @TestExecutionListeners(TestExecutionListenersTests.QuuxTestExecutionListener.class)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaInheritedListeners {}

    @TestExecutionListeners(listeners = TestExecutionListenersTests.QuuxTestExecutionListener.class, inheritListeners = false)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaNonInheritedListeners {}

    @TestExecutionListeners
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaListenersWithOverrides {
        Class<? extends TestExecutionListener>[] listeners() default { TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class };
    }

    @TestExecutionListeners
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaInheritedListenersWithOverrides {
        Class<? extends TestExecutionListener>[] listeners() default TestExecutionListenersTests.QuuxTestExecutionListener.class;

        boolean inheritListeners() default true;
    }

    @TestExecutionListeners
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaNonInheritedListenersWithOverrides {
        Class<? extends TestExecutionListener>[] listeners() default TestExecutionListenersTests.QuuxTestExecutionListener.class;

        boolean inheritListeners() default false;
    }

    @TestExecutionListenersTests.MetaListeners
    static class MetaTestCase {}

    @TestExecutionListenersTests.MetaInheritedListeners
    static class MetaInheritedListenersTestCase extends TestExecutionListenersTests.MetaTestCase {}

    @TestExecutionListenersTests.MetaNonInheritedListeners
    static class MetaNonInheritedListenersTestCase extends TestExecutionListenersTests.MetaInheritedListenersTestCase {}

    @TestExecutionListenersTests.MetaListenersWithOverrides(listeners = { TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class, TestExecutionListenersTests.BazTestExecutionListener.class })
    static class MetaWithOverridesTestCase {}

    @TestExecutionListenersTests.MetaInheritedListenersWithOverrides(listeners = { TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class })
    static class MetaInheritedListenersWithOverridesTestCase extends TestExecutionListenersTests.MetaWithOverridesTestCase {}

    @TestExecutionListenersTests.MetaNonInheritedListenersWithOverrides(listeners = { TestExecutionListenersTests.FooTestExecutionListener.class, TestExecutionListenersTests.BarTestExecutionListener.class, TestExecutionListenersTests.BazTestExecutionListener.class }, inheritListeners = true)
    static class MetaNonInheritedListenersWithOverridesTestCase extends TestExecutionListenersTests.MetaInheritedListenersWithOverridesTestCase {}

    static class FooTestExecutionListener extends AbstractTestExecutionListener {}

    static class BarTestExecutionListener extends AbstractTestExecutionListener {
        @Override
        public int getOrder() {
            // 2500 is between DependencyInjectionTestExecutionListener (2000) and
            // DirtiesContextTestExecutionListener (3000)
            return 2500;
        }
    }

    static class BazTestExecutionListener extends AbstractTestExecutionListener {
        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    static class QuuxTestExecutionListener extends AbstractTestExecutionListener {
        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }

    static class EnigmaTestExecutionListener extends AbstractTestExecutionListener {}
}

