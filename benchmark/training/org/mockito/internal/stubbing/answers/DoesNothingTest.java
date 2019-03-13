/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;


import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;


public class DoesNothingTest {
    private IMethods mock;

    private Invocation invocation_Void;

    private Invocation invocation_void;

    private Invocation invocation_String;

    @Test
    public void answer_returnsNull() throws Throwable {
        assertThat(DoesNothing.doesNothing().answer(invocation_Void)).isNull();
        assertThat(DoesNothing.doesNothing().answer(invocation_void)).isNull();
        assertThat(DoesNothing.doesNothing().answer(invocation_String)).isNull();
    }

    @Test(expected = MockitoException.class)
    public void validateFor_nonVoidReturnType_shouldFail() {
        DoesNothing.doesNothing().validateFor(invocation_String);
    }

    @Test
    public void validateFor_voidReturnType_shouldPass() {
        DoesNothing.doesNothing().validateFor(invocation_void);
    }

    @Test
    public void validateFor_voidObjectReturnType() throws Throwable {
        DoesNothing.doesNothing().validateFor(invocation_Void);
    }
}

