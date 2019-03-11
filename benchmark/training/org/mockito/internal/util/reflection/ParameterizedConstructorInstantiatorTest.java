/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.io.IOException;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ParameterizedConstructorInstantiatorTest {
    private Set<?> whateverForNow;

    private ParameterizedConstructorInstantiatorTest.OneConstructor withOneConstructor;

    private ParameterizedConstructorInstantiatorTest.MultipleConstructor withMultipleConstructor;

    private ParameterizedConstructorInstantiatorTest.NoArgConstructor withNoArgConstructor;

    private ParameterizedConstructorInstantiatorTest.ThrowingConstructor withThrowingConstructor;

    private ParameterizedConstructorInstantiatorTest.VarargConstructor withVarargConstructor;

    @Mock
    private FieldInitializer.ConstructorArgumentResolver resolver;

    @Test
    public void should_be_created_with_an_argument_resolver() throws Exception {
        new FieldInitializer.ParameterizedConstructorInstantiator(this, field("whateverForNow"), resolver);
    }

    @Test
    public void should_fail_if_no_parameterized_constructor_found___excluding_inner_and_others_kind_of_types() throws Exception {
        try {
            new FieldInitializer.ParameterizedConstructorInstantiator(this, field("withNoArgConstructor"), resolver).instantiate();
            Assert.fail();
        } catch (MockitoException me) {
            assertThat(me.getMessage()).contains("no parameterized constructor").contains("withNoArgConstructor").contains("NoArgConstructor");
        }
    }

    @Test
    public void should_instantiate_type_if_resolver_provide_matching_types() throws Exception {
        Observer observer = Mockito.mock(Observer.class);
        Map map = Mockito.mock(Map.class);
        BDDMockito.given(resolver.resolveTypeInstances(Matchers.<Class<?>[]>anyVararg())).willReturn(new Object[]{ observer, map });
        new FieldInitializer.ParameterizedConstructorInstantiator(this, field("withMultipleConstructor"), resolver).instantiate();
        Assert.assertNotNull(withMultipleConstructor);
        Assert.assertNotNull(withMultipleConstructor.observer);
        Assert.assertNotNull(withMultipleConstructor.map);
    }

    @Test
    public void should_fail_if_an_argument_instance_type_do_not_match_wanted_type() throws Exception {
        Observer observer = Mockito.mock(Observer.class);
        Set<?> wrongArg = Mockito.mock(Set.class);
        BDDMockito.given(resolver.resolveTypeInstances(Matchers.<Class<?>[]>anyVararg())).willReturn(new Object[]{ observer, wrongArg });
        try {
            new FieldInitializer.ParameterizedConstructorInstantiator(this, field("withMultipleConstructor"), resolver).instantiate();
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e.getMessage()).contains("argResolver").contains("incorrect types");
        }
    }

    @Test
    public void should_report_failure_if_constructor_throws_exception() throws Exception {
        BDDMockito.given(resolver.resolveTypeInstances(Matchers.<Class<?>[]>anyVararg())).willReturn(new Object[]{ null });
        try {
            new FieldInitializer.ParameterizedConstructorInstantiator(this, field("withThrowingConstructor"), resolver).instantiate();
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e.getMessage()).contains("constructor").contains("raised an exception");
        }
    }

    @Test
    public void should_instantiate_type_with_vararg_constructor() throws Exception {
        Observer[] vararg = new Observer[]{  };
        BDDMockito.given(resolver.resolveTypeInstances(Matchers.<Class<?>[]>anyVararg())).willReturn(new Object[]{ "", vararg });
        new FieldInitializer.ParameterizedConstructorInstantiator(this, field("withVarargConstructor"), resolver).instantiate();
        Assert.assertNotNull(withVarargConstructor);
    }

    private static class NoArgConstructor {
        NoArgConstructor() {
        }
    }

    private static class OneConstructor {
        public OneConstructor(Observer observer) {
        }
    }

    private static class ThrowingConstructor {
        public ThrowingConstructor(Observer observer) throws IOException {
            throw new IOException();
        }
    }

    private static class MultipleConstructor extends ParameterizedConstructorInstantiatorTest.OneConstructor {
        Observer observer;

        Map map;

        public MultipleConstructor(Observer observer) {
            this(observer, null);
        }

        public MultipleConstructor(Observer observer, Map map) {
            super(observer);
            this.observer = observer;
            this.map = map;
        }
    }

    private static class VarargConstructor {
        VarargConstructor(String whatever, Observer... observers) {
        }
    }
}

