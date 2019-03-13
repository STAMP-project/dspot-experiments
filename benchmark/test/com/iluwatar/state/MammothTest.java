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
package com.iluwatar.state;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/29/15 - 8:27 PM
 *
 * @author Jeroen Meulemeester
 */
public class MammothTest {
    private MammothTest.InMemoryAppender appender;

    /**
     * Switch to a complete mammoth 'mood'-cycle and verify if the observed mood matches the expected
     * value.
     */
    @Test
    public void testTimePasses() {
        final Mammoth mammoth = new Mammoth();
        mammoth.observe();
        Assertions.assertEquals("The mammoth is calm and peaceful.", appender.getLastMessage());
        Assertions.assertEquals(1, appender.getLogSize());
        mammoth.timePasses();
        Assertions.assertEquals("The mammoth gets angry!", appender.getLastMessage());
        Assertions.assertEquals(2, appender.getLogSize());
        mammoth.observe();
        Assertions.assertEquals("The mammoth is furious!", appender.getLastMessage());
        Assertions.assertEquals(3, appender.getLogSize());
        mammoth.timePasses();
        Assertions.assertEquals("The mammoth calms down.", appender.getLastMessage());
        Assertions.assertEquals(4, appender.getLogSize());
        mammoth.observe();
        Assertions.assertEquals("The mammoth is calm and peaceful.", appender.getLastMessage());
        Assertions.assertEquals(5, appender.getLogSize());
    }

    /**
     * Verify if {@link Mammoth#toString()} gives the expected value
     */
    @Test
    public void testToString() {
        final String toString = new Mammoth().toString();
        Assertions.assertNotNull(toString);
        Assertions.assertEquals("The mammoth", toString);
    }

    private class InMemoryAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> log = new LinkedList<>();

        public InMemoryAppender() {
            addAppender(this);
            start();
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            log.add(eventObject);
        }

        public int getLogSize() {
            return log.size();
        }

        public String getLastMessage() {
            return log.get(((log.size()) - 1)).getFormattedMessage();
        }
    }
}

