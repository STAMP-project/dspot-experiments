/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection;


import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConstructorInjectionTest {
    @Mock
    private Observer observer;

    private ConstructorInjectionTest.ArgConstructor whatever;

    private ConstructorInjection underTest;

    @Test
    public void should_do_the_trick_of_instantiating() throws Exception {
        boolean result = underTest.process(field("whatever"), this, newSetOf(observer));
        Assert.assertTrue(result);
        Assert.assertNotNull(whatever);
    }

    private static class ArgConstructor {
        ArgConstructor(Observer observer) {
        }
    }
}

