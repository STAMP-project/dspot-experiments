/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class InterfaceOverrideTest {
    public interface CloneableInterface extends Cloneable {
        InterfaceOverrideTest.CloneableInterface clone();
    }

    @Test
    public void inherit_public_method_from_interface() {
        InterfaceOverrideTest.CloneableInterface i = Mockito.mock(InterfaceOverrideTest.CloneableInterface.class);
        Mockito.when(i.clone()).thenReturn(i);
        Assert.assertEquals(i, i.clone());
    }
}

