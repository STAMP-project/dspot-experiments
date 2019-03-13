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
package com.iluwatar.templatemethod;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/30/15 - 18:12 PM
 *
 * @param <M>
 * 		Type of StealingMethod
 * @author Jeroen Meulemeester
 */
public abstract class StealingMethodTest<M extends StealingMethod> {
    private StealingMethodTest<M>.InMemoryAppender appender;

    /**
     * The tested stealing method
     */
    private final M method;

    /**
     * The expected target
     */
    private final String expectedTarget;

    /**
     * The expected target picking result
     */
    private final String expectedTargetResult;

    /**
     * The expected confusion method
     */
    private final String expectedConfuseMethod;

    /**
     * The expected stealing method
     */
    private final String expectedStealMethod;

    /**
     * Create a new test for the given stealing method, together with the expected results
     *
     * @param method
     * 		The tested stealing method
     * @param expectedTarget
     * 		The expected target name
     * @param expectedTargetResult
     * 		The expected target picking result
     * @param expectedConfuseMethod
     * 		The expected confusion method
     * @param expectedStealMethod
     * 		The expected stealing method
     */
    public StealingMethodTest(final M method, String expectedTarget, final String expectedTargetResult, final String expectedConfuseMethod, final String expectedStealMethod) {
        this.method = method;
        this.expectedTarget = expectedTarget;
        this.expectedTargetResult = expectedTargetResult;
        this.expectedConfuseMethod = expectedConfuseMethod;
        this.expectedStealMethod = expectedStealMethod;
    }

    /**
     * Verify if the thief picks the correct target
     */
    @Test
    public void testPickTarget() {
        Assertions.assertEquals(expectedTarget, pickTarget());
    }

    /**
     * Verify if the target confusing step goes as planned
     */
    @Test
    public void testConfuseTarget() {
        Assertions.assertEquals(0, appender.getLogSize());
        confuseTarget(this.expectedTarget);
        Assertions.assertEquals(this.expectedConfuseMethod, appender.getLastMessage());
        Assertions.assertEquals(1, appender.getLogSize());
    }

    /**
     * Verify if the stealing step goes as planned
     */
    @Test
    public void testStealTheItem() {
        Assertions.assertEquals(0, appender.getLogSize());
        stealTheItem(this.expectedTarget);
        Assertions.assertEquals(this.expectedStealMethod, appender.getLastMessage());
        Assertions.assertEquals(1, appender.getLogSize());
    }

    /**
     * Verify if the complete steal process goes as planned
     */
    @Test
    public void testSteal() {
        steal();
        Assertions.assertTrue(appender.logContains(this.expectedTargetResult));
        Assertions.assertTrue(appender.logContains(this.expectedConfuseMethod));
        Assertions.assertTrue(appender.logContains(this.expectedStealMethod));
        Assertions.assertEquals(3, appender.getLogSize());
    }

    private class InMemoryAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> log = new LinkedList<>();

        public InMemoryAppender() {
            addAppender(this);
            StealingMethodTest.InMemoryAppender.start();
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

        public boolean logContains(String message) {
            return log.stream().anyMatch(( event) -> event.getFormattedMessage().equals(message));
        }
    }
}

