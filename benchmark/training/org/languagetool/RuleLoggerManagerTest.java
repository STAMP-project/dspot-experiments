/**
 * LanguageTool, a natural language style checker
 *  * Copyright (C) 2018 Fabian Richter
 *  *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 2.1 of the License, or (at your option) any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 *  * USA
 */
package org.languagetool;


import java.util.logging.Level;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RuleLoggerManagerTest {
    @Test
    public void testLogging() {
        RuleLoggerManager manager = RuleLoggerManager.getInstance();
        manager.setLevel(Level.INFO);
        RuleLogger logger = Mockito.spy(new OutputStreamLogger());
        manager.addLogger(logger);
        RuleLoggerMessage msg = new RuleLoggerMessage("TEST_RULE", "XX", "foobar");
        manager.log(msg, Level.FINE);
        Mockito.verify(logger, Mockito.never()).log(ArgumentMatchers.any(), ArgumentMatchers.any());
        manager.log(msg, Level.INFO);
        Mockito.verify(logger).log(msg, Level.INFO);
        manager.removeLogger(logger);
        manager.log(msg, Level.SEVERE);
        Mockito.verify(logger, Mockito.times(1)).log(ArgumentMatchers.eq(msg), ArgumentMatchers.any());
    }
}

