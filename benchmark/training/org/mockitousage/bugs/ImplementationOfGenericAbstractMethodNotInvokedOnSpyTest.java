/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.Mockito;


public class ImplementationOfGenericAbstractMethodNotInvokedOnSpyTest {
    public abstract class GenericAbstract<T> {
        protected abstract String method_to_implement(T value);

        public String public_method(T value) {
            return method_to_implement(value);
        }
    }

    public class ImplementsGenericMethodOfAbstract<T extends Number> extends ImplementationOfGenericAbstractMethodNotInvokedOnSpyTest.GenericAbstract<T> {
        @Override
        protected String method_to_implement(T value) {
            return "concrete value";
        }
    }

    @Test
    public void should_invoke_method_to_implement() {
        ImplementationOfGenericAbstractMethodNotInvokedOnSpyTest.GenericAbstract<Number> spy = Mockito.spy(new ImplementationOfGenericAbstractMethodNotInvokedOnSpyTest.ImplementsGenericMethodOfAbstract<Number>());
        assertThat(spy.public_method(73L)).isEqualTo("concrete value");
    }
}

