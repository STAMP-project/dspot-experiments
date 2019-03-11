/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.util.Set;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;


public class GenericArrayReturnTypeTest {
    @Test
    public void toArrayTypedDoesNotWork() throws Exception {
        GenericArrayReturnTypeTest.Container container = Mockito.mock(GenericArrayReturnTypeTest.Container.class, Answers.RETURNS_DEEP_STUBS);
        container.getInnerContainer().getTheProblem().toArray(new String[]{  });
    }

    class Container {
        private GenericArrayReturnTypeTest.InnerContainer innerContainer;

        public GenericArrayReturnTypeTest.InnerContainer getInnerContainer() {
            return innerContainer;
        }
    }

    class InnerContainer {
        private Set<String> theProblem;

        public Set<String> getTheProblem() {
            return theProblem;
        }
    }
}

