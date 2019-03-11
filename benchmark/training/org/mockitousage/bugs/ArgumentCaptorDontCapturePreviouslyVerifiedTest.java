/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockitousage.IMethods;


public class ArgumentCaptorDontCapturePreviouslyVerifiedTest {
    @Test
    public void previous_verified_invocation_should_still_capture_args() {
        IMethods mock = Mockito.mock(IMethods.class);
        mock.oneArg("first");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mock, Mockito.times(1)).oneArg(argument.capture());
        assertThat(argument.getAllValues()).hasSize(1);
        // additional interactions
        mock.oneArg("second");
        argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mock, Mockito.times(2)).oneArg(argument.capture());
        assertThat(argument.getAllValues()).hasSize(2);
    }
}

