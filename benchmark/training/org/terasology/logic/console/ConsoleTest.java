/**
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.console;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.TerasologyTestingEnvironment;


public class ConsoleTest extends TerasologyTestingEnvironment {
    private final String MESSAGE_TEXT = "Test message";

    @Test
    public void testClearCommand() {
        for (int i = 0; i < 10; i++) {
            getConsole().addMessage("Just a message");
        }
        getConsole().clear();
        Iterator<Message> it = getConsole().getMessages().iterator();
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testAddMessage() {
        getConsole().addMessage(MESSAGE_TEXT);
        checkMessage(getConsole().getMessages().iterator(), true);
    }

    @Test
    public void testAddConsoleMessage() {
        getConsole().addMessage(new Message(MESSAGE_TEXT));
        checkMessage(getConsole().getMessages().iterator(), true);
    }

    @Test
    public void testAddInlineMessage() {
        getConsole().addMessage(MESSAGE_TEXT, false);
        checkMessage(getConsole().getMessages().iterator(), false);
    }

    @Test
    public void testAddInlineMessage2() {
        getConsole().addMessage(new Message(MESSAGE_TEXT, false));
        checkMessage(getConsole().getMessages().iterator(), false);
    }
}

