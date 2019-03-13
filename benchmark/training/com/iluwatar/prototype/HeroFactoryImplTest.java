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
package com.iluwatar.prototype;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/28/15 - 8:34 PM
 *
 * @author Jeroen Meulemeester
 */
public class HeroFactoryImplTest {
    @Test
    public void testFactory() throws Exception {
        final Mage mage = Mockito.mock(Mage.class);
        final Warlord warlord = Mockito.mock(Warlord.class);
        final Beast beast = Mockito.mock(Beast.class);
        Mockito.when(mage.copy()).thenThrow(CloneNotSupportedException.class);
        Mockito.when(warlord.copy()).thenThrow(CloneNotSupportedException.class);
        Mockito.when(beast.copy()).thenThrow(CloneNotSupportedException.class);
        final HeroFactoryImpl factory = new HeroFactoryImpl(mage, warlord, beast);
        Assertions.assertNull(factory.createMage());
        Assertions.assertNull(factory.createWarlord());
        Assertions.assertNull(factory.createBeast());
        Mockito.verify(mage).copy();
        Mockito.verify(warlord).copy();
        Mockito.verify(beast).copy();
        Mockito.verifyNoMoreInteractions(mage, warlord, beast);
    }
}

