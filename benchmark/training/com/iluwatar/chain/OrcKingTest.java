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
package com.iluwatar.chain;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static RequestType.COLLECT_TAX;
import static RequestType.DEFEND_CASTLE;
import static RequestType.TORTURE_PRISONER;


/**
 * Date: 12/6/15 - 9:29 PM
 *
 * @author Jeroen Meulemeester
 */
public class OrcKingTest {
    /**
     * All possible requests
     */
    private static final Request[] REQUESTS = new Request[]{ new Request(DEFEND_CASTLE, "Don't let the barbarians enter my castle!!"), new Request(TORTURE_PRISONER, "Don't just stand there, tickle him!"), new Request(COLLECT_TAX, "Don't steal, the King hates competition ...") };

    @Test
    public void testMakeRequest() {
        final OrcKing king = new OrcKing();
        for (final Request request : OrcKingTest.REQUESTS) {
            king.makeRequest(request);
            Assertions.assertTrue(request.isHandled(), (("Expected all requests from King to be handled, but [" + request) + "] was not!"));
        }
    }
}

