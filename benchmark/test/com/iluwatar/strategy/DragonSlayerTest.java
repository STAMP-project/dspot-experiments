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
package com.iluwatar.strategy;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/29/15 - 10:50 PM
 *
 * @author Jeroen Meulemeester
 */
public class DragonSlayerTest {
    /**
     * Verify if the dragon slayer uses the strategy during battle
     */
    @Test
    public void testGoToBattle() {
        final DragonSlayingStrategy strategy = Mockito.mock(DragonSlayingStrategy.class);
        final DragonSlayer dragonSlayer = new DragonSlayer(strategy);
        dragonSlayer.goToBattle();
        Mockito.verify(strategy).execute();
        Mockito.verifyNoMoreInteractions(strategy);
    }

    /**
     * Verify if the dragon slayer uses the new strategy during battle after a change of strategy
     */
    @Test
    public void testChangeStrategy() {
        final DragonSlayingStrategy initialStrategy = Mockito.mock(DragonSlayingStrategy.class);
        final DragonSlayer dragonSlayer = new DragonSlayer(initialStrategy);
        dragonSlayer.goToBattle();
        Mockito.verify(initialStrategy).execute();
        final DragonSlayingStrategy newStrategy = Mockito.mock(DragonSlayingStrategy.class);
        dragonSlayer.changeStrategy(newStrategy);
        dragonSlayer.goToBattle();
        Mockito.verify(newStrategy).execute();
        Mockito.verifyNoMoreInteractions(initialStrategy, newStrategy);
    }
}

