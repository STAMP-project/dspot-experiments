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
package com.iluwatar.proxy;


import com.iluwatar.proxy.utils.InMemoryAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link IvoryTower}
 */
public class IvoryTowerTest {
    private InMemoryAppender appender;

    @Test
    public void testEnter() throws Exception {
        final Wizard[] wizards = new Wizard[]{ new Wizard("Gandalf"), new Wizard("Dumbledore"), new Wizard("Oz"), new Wizard("Merlin") };
        IvoryTower tower = new IvoryTower();
        for (Wizard wizard : wizards) {
            tower.enter(wizard);
        }
        Assertions.assertTrue(appender.logContains("Gandalf enters the tower."));
        Assertions.assertTrue(appender.logContains("Dumbledore enters the tower."));
        Assertions.assertTrue(appender.logContains("Oz enters the tower."));
        Assertions.assertTrue(appender.logContains("Merlin enters the tower."));
        Assertions.assertEquals(4, appender.getLogSize());
    }
}

