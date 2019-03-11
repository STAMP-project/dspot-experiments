/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class ActualInvocationHasNullArgumentNPEBugTest extends TestBase {
    public interface Fun {
        String doFun(String something);
    }

    @Test
    public void shouldAllowPassingNullArgument() {
        // given
        ActualInvocationHasNullArgumentNPEBugTest.Fun mockFun = Mockito.mock(ActualInvocationHasNullArgumentNPEBugTest.Fun.class);
        Mockito.when(mockFun.doFun(((String) (ArgumentMatchers.anyObject())))).thenReturn("value");
        // when
        mockFun.doFun(null);
        // then
        try {
            Mockito.verify(mockFun).doFun("hello");
        } catch (AssertionError r) {
            // it's ok, we just want to reproduce the bug
            return;
        }
        Assert.fail();
    }
}

