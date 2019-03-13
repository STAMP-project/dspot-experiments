/**
 * Copyright 2018 Paul Schaub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.mood;


import Mood.happy;
import Mood.sad;
import MoodElement.ELEMENT;
import MoodElement.NAMESPACE;
import junit.framework.TestCase;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.mood.element.MoodElement;
import org.junit.Test;


public class MoodManagerTest extends SmackTestSuite {
    @Test
    public void addMessageTest() {
        Message message = new Message();
        MoodManager.addMoodToMessage(message, sad);
        TestCase.assertTrue(message.hasExtension(ELEMENT, NAMESPACE));
        TestCase.assertTrue(MoodElement.hasMoodElement(message));
        MoodElement element = MoodElement.fromMessage(message);
        TestCase.assertNotNull(element);
        TestCase.assertEquals(sad, element.getMood());
        TestCase.assertFalse(element.hasConcretisation());
        TestCase.assertFalse(element.hasText());
        message = new Message();
        MoodManager.addMoodToMessage(message, happy, new MoodConcretisationTest.EcstaticMoodConcretisation());
        element = MoodElement.fromMessage(message);
        TestCase.assertTrue(element.hasConcretisation());
    }
}

