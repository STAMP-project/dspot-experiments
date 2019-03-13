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
package com.iluwatar.adapter;


import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Test class
 */
public class AdapterPatternTest {
    private Map<String, Object> beans;

    private static final String FISHING_BEAN = "fisher";

    private static final String ROWING_BEAN = "captain";

    /**
     * This test asserts that when we use the row() method on a captain bean(client), it is
     * internally calling sail method on the fishing boat object. The Adapter ({@link FishingBoatAdapter}
     * ) converts the interface of the target class ( {@link FishingBoat}) into a suitable one
     * expected by the client ({@link Captain} ).
     */
    @Test
    public void testAdapter() {
        Captain captain = ((Captain) (beans.get(AdapterPatternTest.ROWING_BEAN)));
        // when captain moves
        captain.row();
        // the captain internally calls the battleship object to move
        RowingBoat adapter = ((RowingBoat) (beans.get(AdapterPatternTest.FISHING_BEAN)));
        Mockito.verify(adapter).row();
    }
}

