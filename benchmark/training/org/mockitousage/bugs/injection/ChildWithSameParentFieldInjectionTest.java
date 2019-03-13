/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.injection;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


// issue 289
@RunWith(MockitoJUnitRunner.class)
public class ChildWithSameParentFieldInjectionTest {
    @InjectMocks
    private ChildWithSameParentFieldInjectionTest.System system;

    @Mock
    private ChildWithSameParentFieldInjectionTest.SomeService someService;

    @Test
    public void parent_field_is_not_null() {
        Assert.assertNotNull(((ChildWithSameParentFieldInjectionTest.AbstractSystem) (system)).someService);
    }

    @Test
    public void child_field_is_not_null() {
        Assert.assertNotNull(system.someService);
    }

    public static class System extends ChildWithSameParentFieldInjectionTest.AbstractSystem {
        private ChildWithSameParentFieldInjectionTest.SomeService someService;

        public void doSomethingElse() {
            someService.doSomething();
        }
    }

    public static class AbstractSystem {
        private ChildWithSameParentFieldInjectionTest.SomeService someService;

        public void doSomething() {
            someService.doSomething();
        }
    }

    public static class SomeService {
        public void doSomething() {
        }
    }
}

