/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.configuration.AnnotationEngine;
import org.mockito.configuration.DefaultMockitoConfiguration;
import org.mockito.internal.configuration.ConfigurationAccess;
import org.mockito.internal.configuration.IndependentAnnotationEngine;
import org.mockitoutil.TestBase;


public class DeprecatedAnnotationEngineApiTest extends TestBase {
    class SimpleTestCase {
        @InjectMocks
        DeprecatedAnnotationEngineApiTest.Tested tested = new DeprecatedAnnotationEngineApiTest.Tested();

        @Mock
        DeprecatedAnnotationEngineApiTest.Dependency mock;
    }

    class Tested {
        DeprecatedAnnotationEngineApiTest.Dependency dependency;

        public void setDependency(DeprecatedAnnotationEngineApiTest.Dependency dependency) {
            this.dependency = dependency;
        }
    }

    class Dependency {}

    @Test
    public void shouldInjectMocksIfThereIsNoUserDefinedEngine() throws Exception {
        // given
        AnnotationEngine defaultEngine = new DefaultMockitoConfiguration().getAnnotationEngine();
        ConfigurationAccess.getConfig().overrideAnnotationEngine(defaultEngine);
        DeprecatedAnnotationEngineApiTest.SimpleTestCase test = new DeprecatedAnnotationEngineApiTest.SimpleTestCase();
        // when
        MockitoAnnotations.initMocks(test);
        // then
        Assert.assertNotNull(test.mock);
        Assert.assertNotNull(test.tested.dependency);
        Assert.assertSame(test.mock, test.tested.dependency);
    }

    @Test
    public void shouldRespectUsersEngine() throws Exception {
        // given
        AnnotationEngine customizedEngine = new IndependentAnnotationEngine() {};
        ConfigurationAccess.getConfig().overrideAnnotationEngine(customizedEngine);
        DeprecatedAnnotationEngineApiTest.SimpleTestCase test = new DeprecatedAnnotationEngineApiTest.SimpleTestCase();
        // when
        MockitoAnnotations.initMocks(test);
        // then
        Assert.assertNotNull(test.mock);
        Assert.assertNull(test.tested.dependency);
    }
}

