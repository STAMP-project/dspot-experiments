/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.mock.mockito;


import DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE;
import java.io.InputStream;
import java.lang.reflect.Field;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;


/**
 * Tests for {@link MockitoTestExecutionListener}.
 *
 * @author Phillip Webb
 */
public class MockitoTestExecutionListenerTests {
    private MockitoTestExecutionListener listener = new MockitoTestExecutionListener();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MockitoPostProcessor postProcessor;

    @Captor
    private ArgumentCaptor<Field> fieldCaptor;

    @Test
    public void prepareTestInstanceShouldInitMockitoAnnotations() throws Exception {
        MockitoTestExecutionListenerTests.WithMockitoAnnotations instance = new MockitoTestExecutionListenerTests.WithMockitoAnnotations();
        this.listener.prepareTestInstance(mockTestContext(instance));
        assertThat(instance.mock).isNotNull();
        assertThat(instance.captor).isNotNull();
    }

    @Test
    public void prepareTestInstanceShouldInjectMockBean() throws Exception {
        MockitoTestExecutionListenerTests.WithMockBean instance = new MockitoTestExecutionListenerTests.WithMockBean();
        this.listener.prepareTestInstance(mockTestContext(instance));
        Mockito.verify(this.postProcessor).inject(this.fieldCaptor.capture(), ArgumentMatchers.eq(instance), ArgumentMatchers.any(MockDefinition.class));
        assertThat(this.fieldCaptor.getValue().getName()).isEqualTo("mockBean");
    }

    @Test
    public void beforeTestMethodShouldDoNothingWhenDirtiesContextAttributeIsNotSet() throws Exception {
        MockitoTestExecutionListenerTests.WithMockBean instance = new MockitoTestExecutionListenerTests.WithMockBean();
        this.listener.beforeTestMethod(mockTestContext(instance));
        Mockito.verifyNoMoreInteractions(this.postProcessor);
    }

    @Test
    public void beforeTestMethodShouldInjectMockBeanWhenDirtiesContextAttributeIsSet() throws Exception {
        MockitoTestExecutionListenerTests.WithMockBean instance = new MockitoTestExecutionListenerTests.WithMockBean();
        TestContext mockTestContext = mockTestContext(instance);
        BDDMockito.given(mockTestContext.getAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE)).willReturn(Boolean.TRUE);
        this.listener.beforeTestMethod(mockTestContext);
        Mockito.verify(this.postProcessor).inject(this.fieldCaptor.capture(), ArgumentMatchers.eq(instance), ((MockDefinition) (ArgumentMatchers.any())));
        assertThat(this.fieldCaptor.getValue().getName()).isEqualTo("mockBean");
    }

    static class WithMockitoAnnotations {
        @Mock
        InputStream mock;

        @Captor
        ArgumentCaptor<InputStream> captor;
    }

    static class WithMockBean {
        @MockBean
        InputStream mockBean;
    }
}

