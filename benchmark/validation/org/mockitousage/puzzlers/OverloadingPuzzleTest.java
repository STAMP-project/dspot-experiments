/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.puzzlers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitoutil.TestBase;


public class OverloadingPuzzleTest extends TestBase {
    private OverloadingPuzzleTest.Super mock;

    private interface Super {
        void say(Object message);
    }

    private interface Sub extends OverloadingPuzzleTest.Super {
        void say(String message);
    }

    @Test
    public void shouldUseArgumentTypeWhenOverloadingPuzzleDetected() throws Exception {
        OverloadingPuzzleTest.Sub sub = Mockito.mock(OverloadingPuzzleTest.Sub.class);
        setMockWithDowncast(sub);
        say("Hello");
        try {
            Mockito.verify(sub).say("Hello");
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }
}

