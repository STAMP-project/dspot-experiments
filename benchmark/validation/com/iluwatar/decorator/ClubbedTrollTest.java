/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.decorator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;


/**
 * Tests for {@link ClubbedTroll}
 */
public class ClubbedTrollTest {
    @Test
    public void testClubbedTroll() {
        // Create a normal troll first, but make sure we can spy on it later on.
        final Troll simpleTroll = Mockito.spy(new SimpleTroll());
        // Now we want to decorate the troll to make it stronger ...
        final Troll clubbed = new ClubbedTroll(simpleTroll);
        Assertions.assertEquals(20, clubbed.getAttackPower());
        Mockito.verify(simpleTroll, VerificationModeFactory.times(1)).getAttackPower();
        // Check if the clubbed troll actions are delegated to the decorated troll
        clubbed.attack();
        Mockito.verify(simpleTroll, VerificationModeFactory.times(1)).attack();
        clubbed.fleeBattle();
        Mockito.verify(simpleTroll, VerificationModeFactory.times(1)).fleeBattle();
        Mockito.verifyNoMoreInteractions(simpleTroll);
    }
}

