/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.plugins.instantiator;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class InstantiatorProviderTest {
    @SuppressWarnings("CheckReturnValue")
    @Test
    public void uses_custom_instantiator_provider() {
        // given mocking works:
        Mockito.mock(List.class);
        // when
        MyInstantiatorProvider2.explosive.set(true);
        // then
        try {
            Mockito.mock(List.class);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(MyInstantiatorProvider2.class.getName(), e.getMessage());
        } finally {
            MyInstantiatorProvider2.explosive.remove();
        }
    }
}

