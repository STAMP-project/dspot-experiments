/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.stubbing.OngoingStubbing;


public class IOOBExceptionShouldNotBeThrownWhenNotCodingFluentlyTest {
    @Test
    public void second_stubbing_throws_IndexOutOfBoundsException() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> map = Mockito.mock(Map.class);
        OngoingStubbing<String> mapOngoingStubbing = Mockito.when(map.get(ArgumentMatchers.anyString()));
        mapOngoingStubbing.thenReturn("first stubbing");
        try {
            mapOngoingStubbing.thenReturn("second stubbing");
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e.getMessage()).contains("Incorrect use of API detected here").contains(this.getClass().getSimpleName());
        }
    }
}

