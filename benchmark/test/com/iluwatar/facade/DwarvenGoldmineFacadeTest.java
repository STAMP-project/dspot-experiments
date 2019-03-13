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
package com.iluwatar.facade;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/9/15 - 9:40 PM
 *
 * @author Jeroen Meulemeester
 */
public class DwarvenGoldmineFacadeTest {
    private DwarvenGoldmineFacadeTest.InMemoryAppender appender;

    /**
     * Test a complete day cycle in the gold mine by executing all three different steps: {@link DwarvenGoldmineFacade#startNewDay()}, {@link DwarvenGoldmineFacade#digOutGold()} and {@link DwarvenGoldmineFacade#endDay()}.
     *
     * See if the workers are doing what's expected from them on each step.
     */
    @Test
    public void testFullWorkDay() {
        final DwarvenGoldmineFacade goldMine = new DwarvenGoldmineFacade();
        goldMine.startNewDay();
        // On the start of a day, all workers should wake up ...
        Assertions.assertTrue(appender.logContains("Dwarf gold digger wakes up."));
        Assertions.assertTrue(appender.logContains("Dwarf cart operator wakes up."));
        Assertions.assertTrue(appender.logContains("Dwarven tunnel digger wakes up."));
        // ... and go to the mine
        Assertions.assertTrue(appender.logContains("Dwarf gold digger goes to the mine."));
        Assertions.assertTrue(appender.logContains("Dwarf cart operator goes to the mine."));
        Assertions.assertTrue(appender.logContains("Dwarven tunnel digger goes to the mine."));
        // No other actions were invoked, so the workers shouldn't have done (printed) anything else
        Assertions.assertEquals(6, appender.getLogSize());
        // Now do some actual work, start digging gold!
        goldMine.digOutGold();
        // Since we gave the dig command, every worker should be doing it's job ...
        Assertions.assertTrue(appender.logContains("Dwarf gold digger digs for gold."));
        Assertions.assertTrue(appender.logContains("Dwarf cart operator moves gold chunks out of the mine."));
        Assertions.assertTrue(appender.logContains("Dwarven tunnel digger creates another promising tunnel."));
        // Again, they shouldn't be doing anything else.
        Assertions.assertEquals(9, appender.getLogSize());
        // Enough gold, lets end the day.
        goldMine.endDay();
        // Check if the workers go home ...
        Assertions.assertTrue(appender.logContains("Dwarf gold digger goes home."));
        Assertions.assertTrue(appender.logContains("Dwarf cart operator goes home."));
        Assertions.assertTrue(appender.logContains("Dwarven tunnel digger goes home."));
        // ... and go to sleep. We need well rested workers the next day :)
        Assertions.assertTrue(appender.logContains("Dwarf gold digger goes to sleep."));
        Assertions.assertTrue(appender.logContains("Dwarf cart operator goes to sleep."));
        Assertions.assertTrue(appender.logContains("Dwarven tunnel digger goes to sleep."));
        // Every worker should be sleeping now, no other actions allowed
        Assertions.assertEquals(15, appender.getLogSize());
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

        public boolean logContains(String message) {
            return log.stream().anyMatch(( event) -> event.getFormattedMessage().equals(message));
        }
    }
}

