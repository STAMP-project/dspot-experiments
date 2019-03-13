/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.test.context.support;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.context.MergedContextConfiguration;


/**
 * Unit tests for {@link AnnotationConfigContextLoader}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
public class AnnotationConfigContextLoaderTests {
    private final AnnotationConfigContextLoader contextLoader = new AnnotationConfigContextLoader();

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     *
     *
     * @since 4.0.4
     */
    @Test
    public void configMustNotContainLocations() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.containsString("does not support resource locations"));
        MergedContextConfiguration mergedConfig = new MergedContextConfiguration(getClass(), new String[]{ "config.xml" }, AnnotationConfigContextLoaderTests.EMPTY_CLASS_ARRAY, AnnotationConfigContextLoaderTests.EMPTY_STRING_ARRAY, contextLoader);
        contextLoader.loadContext(mergedConfig);
    }

    @Test
    public void detectDefaultConfigurationClassesForAnnotatedInnerClass() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(ContextConfigurationInnerClassTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("annotated static ContextConfiguration should be considered.", 1, configClasses.length);
        configClasses = contextLoader.detectDefaultConfigurationClasses(AnnotatedFooConfigInnerClassTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("annotated static FooConfig should be considered.", 1, configClasses.length);
    }

    @Test
    public void detectDefaultConfigurationClassesForMultipleAnnotatedInnerClasses() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(MultipleStaticConfigurationClassesTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("multiple annotated static classes should be considered.", 2, configClasses.length);
    }

    @Test
    public void detectDefaultConfigurationClassesForNonAnnotatedInnerClass() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(PlainVanillaFooConfigInnerClassTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("non-annotated static FooConfig should NOT be considered.", 0, configClasses.length);
    }

    @Test
    public void detectDefaultConfigurationClassesForFinalAnnotatedInnerClass() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(FinalConfigInnerClassTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("final annotated static Config should NOT be considered.", 0, configClasses.length);
    }

    @Test
    public void detectDefaultConfigurationClassesForPrivateAnnotatedInnerClass() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(PrivateConfigInnerClassTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("private annotated inner classes should NOT be considered.", 0, configClasses.length);
    }

    @Test
    public void detectDefaultConfigurationClassesForNonStaticAnnotatedInnerClass() {
        Class<?>[] configClasses = contextLoader.detectDefaultConfigurationClasses(NonStaticConfigInnerClassesTestCase.class);
        Assert.assertNotNull(configClasses);
        Assert.assertEquals("non-static annotated inner classes should NOT be considered.", 0, configClasses.length);
    }
}

