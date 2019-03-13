/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;


import org.junit.Test;


public class ArgumentCaptorTest {
    @Test
    public void tell_handy_return_values_to_return_value_for() throws Exception {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        assertThat(captor.capture()).isNull();
    }
}

