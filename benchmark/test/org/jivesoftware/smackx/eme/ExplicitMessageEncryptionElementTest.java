/**
 * Copyright 2018 Paul Schaub
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
package org.jivesoftware.smackx.eme;


import ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl;
import ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.openpgpV0;
import java.util.List;
import junit.framework.TestCase;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.eme.element.ExplicitMessageEncryptionElement;
import org.junit.Test;


public class ExplicitMessageEncryptionElementTest extends SmackTestSuite {
    @Test
    public void addToMessageTest() {
        Message message = new Message();
        // Check inital state (no elements)
        TestCase.assertNull(ExplicitMessageEncryptionElement.from(message));
        TestCase.assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message, omemoVAxolotl));
        List<ExtensionElement> extensions = message.getExtensions();
        TestCase.assertEquals(0, extensions.size());
        // Add OMEMO
        ExplicitMessageEncryptionElement.set(message, omemoVAxolotl);
        extensions = message.getExtensions();
        TestCase.assertEquals(1, extensions.size());
        TestCase.assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message, omemoVAxolotl));
        TestCase.assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message, omemoVAxolotl.getNamespace()));
        TestCase.assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message, openpgpV0));
        TestCase.assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message, openpgpV0.getNamespace()));
        ExplicitMessageEncryptionElement.set(message, openpgpV0);
        extensions = message.getExtensions();
        TestCase.assertEquals(2, extensions.size());
        TestCase.assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message, openpgpV0));
        TestCase.assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message, omemoVAxolotl));
        // Check, if adding additional OMEMO wont add another element
        ExplicitMessageEncryptionElement.set(message, omemoVAxolotl);
        extensions = message.getExtensions();
        TestCase.assertEquals(2, extensions.size());
    }
}

