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
package com.iluwatar.servant;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/28/15 - 10:02 PM
 *
 * @author Jeroen Meulemeester
 */
public class ServantTest {
    @Test
    public void testFeed() {
        final Royalty royalty = Mockito.mock(Royalty.class);
        final Servant servant = new Servant("test");
        servant.feed(royalty);
        Mockito.verify(royalty).getFed();
        Mockito.verifyNoMoreInteractions(royalty);
    }

    @Test
    public void testGiveWine() {
        final Royalty royalty = Mockito.mock(Royalty.class);
        final Servant servant = new Servant("test");
        servant.giveWine(royalty);
        Mockito.verify(royalty).getDrink();
        Mockito.verifyNoMoreInteractions(royalty);
    }

    @Test
    public void testGiveCompliments() {
        final Royalty royalty = Mockito.mock(Royalty.class);
        final Servant servant = new Servant("test");
        servant.giveCompliments(royalty);
        Mockito.verify(royalty).receiveCompliments();
        Mockito.verifyNoMoreInteractions(royalty);
    }

    @Test
    public void testCheckIfYouWillBeHanged() {
        final Royalty goodMoodRoyalty = Mockito.mock(Royalty.class);
        Mockito.when(goodMoodRoyalty.getMood()).thenReturn(true);
        final Royalty badMoodRoyalty = Mockito.mock(Royalty.class);
        Mockito.when(badMoodRoyalty.getMood()).thenReturn(true);
        final List<Royalty> goodCompany = new ArrayList<>();
        goodCompany.add(goodMoodRoyalty);
        goodCompany.add(goodMoodRoyalty);
        goodCompany.add(goodMoodRoyalty);
        final List<Royalty> badCompany = new ArrayList<>();
        goodCompany.add(goodMoodRoyalty);
        goodCompany.add(goodMoodRoyalty);
        goodCompany.add(badMoodRoyalty);
        Assertions.assertTrue(new Servant("test").checkIfYouWillBeHanged(goodCompany));
        Assertions.assertTrue(new Servant("test").checkIfYouWillBeHanged(badCompany));
    }
}

