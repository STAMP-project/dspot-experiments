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
package com.iluwatar.composite;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/11/15 - 8:12 PM
 *
 * @author Jeroen Meulemeester
 */
public class MessengerTest {
    /**
     * The buffer used to capture every write to {@link System#out}
     */
    private ByteArrayOutputStream stdOutBuffer = new ByteArrayOutputStream();

    /**
     * Keep the original std-out so it can be restored after the test
     */
    private final PrintStream realStdOut = System.out;

    /**
     * Test the message from the orcs
     */
    @Test
    public void testMessageFromOrcs() {
        final Messenger messenger = new Messenger();
        testMessage(messenger.messageFromOrcs(), "Where there is a whip there is a way.");
    }

    /**
     * Test the message from the elves
     */
    @Test
    public void testMessageFromElves() {
        final Messenger messenger = new Messenger();
        testMessage(messenger.messageFromElves(), "Much wind pours from your mouth.");
    }
}

