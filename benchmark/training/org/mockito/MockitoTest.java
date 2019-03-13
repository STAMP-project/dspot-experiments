/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;


import java.util.List;
import org.junit.Test;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.progress.ThreadSafeMockingProgress;


@SuppressWarnings("unchecked")
public class MockitoTest {
    @Test
    public void shouldRemoveStubbableFromProgressAfterStubbing() {
        List mock = Mockito.mock(List.class);
        Mockito.when(mock.add("test")).thenReturn(true);
        // TODO Consider to move to separate test
        assertThat(ThreadSafeMockingProgress.mockingProgress().pullOngoingStubbing()).isNull();
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test(expected = NotAMockException.class)
    public void shouldValidateMockWhenVerifying() {
        Mockito.verify("notMock");
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test(expected = NotAMockException.class)
    public void shouldValidateMockWhenVerifyingWithExpectedNumberOfInvocations() {
        Mockito.verify("notMock", Mockito.times(19));
    }

    @Test(expected = NotAMockException.class)
    public void shouldValidateMockWhenVerifyingNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions("notMock");
    }

    @Test(expected = NotAMockException.class)
    public void shouldValidateMockWhenVerifyingZeroInteractions() {
        Mockito.verifyZeroInteractions("notMock");
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test(expected = NotAMockException.class)
    public void shouldValidateMockWhenCreatingInOrderObject() {
        Mockito.inOrder("notMock");
    }

    @Test
    public void shouldStartingMockSettingsContainDefaultBehavior() {
        // when
        MockSettingsImpl<?> settings = ((MockSettingsImpl<?>) (Mockito.withSettings()));
        // then
        assertThat(Mockito.RETURNS_DEFAULTS).isEqualTo(settings.getDefaultAnswer());
    }
}

