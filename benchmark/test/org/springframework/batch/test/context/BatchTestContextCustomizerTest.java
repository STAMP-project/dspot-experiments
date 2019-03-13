/**
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.test.context;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;


/**
 *
 *
 * @author Mahmoud Ben Hassine
 */
public class BatchTestContextCustomizerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private BatchTestContextCustomizer contextCustomizer = new BatchTestContextCustomizer();

    @Test
    public void testCustomizeContext() {
        // given
        ConfigurableApplicationContext context = new GenericApplicationContext();
        MergedContextConfiguration mergedConfig = Mockito.mock(MergedContextConfiguration.class);
        // when
        this.contextCustomizer.customizeContext(context, mergedConfig);
        // then
        Assert.assertTrue(context.containsBean("jobLauncherTestUtils"));
        Assert.assertTrue(context.containsBean("jobRepositoryTestUtils"));
    }

    @Test
    public void testCustomizeContext_whenBeanFactoryIsNotAnInstanceOfBeanDefinitionRegistry() {
        // given
        ConfigurableApplicationContext context = Mockito.mock(ConfigurableApplicationContext.class);
        MergedContextConfiguration mergedConfig = Mockito.mock(MergedContextConfiguration.class);
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("The bean factory must be an instance of BeanDefinitionRegistry");
        // when
        this.contextCustomizer.customizeContext(context, mergedConfig);
        // then
        // expected exception
    }
}

