/**
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.security.web.access.expression;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegatingEvaluationContextTests {
    @Mock
    DelegatingEvaluationContext delegate;

    @InjectMocks
    DelegatingEvaluationContext context;

    @Test
    public void getRootObject() {
        TypedValue expected = Mockito.mock(TypedValue.class);
        Mockito.when(this.delegate.getRootObject()).thenReturn(expected);
        assertThat(this.context.getRootObject()).isEqualTo(expected);
    }

    @Test
    public void getConstructorResolvers() {
        List<ConstructorResolver> expected = new ArrayList<>();
        Mockito.when(this.delegate.getConstructorResolvers()).thenReturn(expected);
        assertThat(this.context.getConstructorResolvers()).isEqualTo(expected);
    }

    @Test
    public void getMethodResolvers() {
        List<MethodResolver> expected = new ArrayList<>();
        Mockito.when(this.delegate.getMethodResolvers()).thenReturn(expected);
        assertThat(this.context.getMethodResolvers()).isEqualTo(expected);
    }

    @Test
    public void getPropertyAccessors() {
        List<PropertyAccessor> expected = new ArrayList<>();
        Mockito.when(this.delegate.getPropertyAccessors()).thenReturn(expected);
        assertThat(this.context.getPropertyAccessors()).isEqualTo(expected);
    }

    @Test
    public void getTypeLocator() {
        TypeLocator expected = Mockito.mock(TypeLocator.class);
        Mockito.when(this.delegate.getTypeLocator()).thenReturn(expected);
        assertThat(this.context.getTypeLocator()).isEqualTo(expected);
    }

    @Test
    public void getTypeConverter() {
        TypeConverter expected = Mockito.mock(TypeConverter.class);
        Mockito.when(this.delegate.getTypeConverter()).thenReturn(expected);
        assertThat(this.context.getTypeConverter()).isEqualTo(expected);
    }

    @Test
    public void getTypeComparator() {
        TypeComparator expected = Mockito.mock(TypeComparator.class);
        Mockito.when(this.delegate.getTypeComparator()).thenReturn(expected);
        assertThat(this.context.getTypeComparator()).isEqualTo(expected);
    }

    @Test
    public void getOperatorOverloader() {
        OperatorOverloader expected = Mockito.mock(OperatorOverloader.class);
        Mockito.when(this.delegate.getOperatorOverloader()).thenReturn(expected);
        assertThat(this.context.getOperatorOverloader()).isEqualTo(expected);
    }

    @Test
    public void getBeanResolver() {
        BeanResolver expected = Mockito.mock(BeanResolver.class);
        Mockito.when(this.delegate.getBeanResolver()).thenReturn(expected);
        assertThat(this.context.getBeanResolver()).isEqualTo(expected);
    }

    @Test
    public void setVariable() {
        String name = "name";
        String value = "value";
        this.context.setVariable(name, value);
        Mockito.verify(this.delegate).setVariable(name, value);
    }

    @Test
    public void lookupVariable() {
        String name = "name";
        String expected = "expected";
        Mockito.when(this.delegate.lookupVariable(name)).thenReturn(expected);
        assertThat(this.context.lookupVariable(name)).isEqualTo(expected);
    }
}

