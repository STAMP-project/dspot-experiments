/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.creation;


import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 191
public class ShouldAllowInlineMockCreationTest extends TestBase {
    @Mock
    List list;

    @Test
    public void shouldAllowInlineMockCreation() {
        Mockito.when(list.get(0)).thenReturn(Mockito.mock(Set.class));
        Assert.assertTrue(((list.get(0)) instanceof Set));
    }
}

