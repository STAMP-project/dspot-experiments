/**
 * Copyright 2017 Florian Schmaus
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
package org.jivesoftware.smackx.eme.provider;


import ExplicitMessageEncryptionProtocol.openpgpV0;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.eme.element.ExplicitMessageEncryptionElement;
import org.junit.Assert;
import org.junit.Test;


public class ExplicitMessageEncryptionProviderTest {
    private static final String OX_EME_ELEMENT = "<encryption xmlns='urn:xmpp:eme:0' namespace='urn:xmpp:openpgp:0'/>";

    private static final String UNKNOWN_NAMESPACE = "urn:xmpp:foobar:0";

    private static final String UNKNOWN_NAME = "Foo Bar";

    private static final String UNKNOWN_EME_ELEMENT = ((("<encryption xmlns='urn:xmpp:eme:0' namespace='" + (ExplicitMessageEncryptionProviderTest.UNKNOWN_NAMESPACE)) + "' name='") + (ExplicitMessageEncryptionProviderTest.UNKNOWN_NAME)) + "'/>";

    @Test
    public void testParseOxEmeElement() throws Exception {
        ExplicitMessageEncryptionElement eme = TestUtils.parseExtensionElement(ExplicitMessageEncryptionProviderTest.OX_EME_ELEMENT);
        Assert.assertEquals(openpgpV0, eme.getProtocol());
    }

    @Test
    public void testParseUnknownEmeElement() throws Exception {
        ExplicitMessageEncryptionElement eme = TestUtils.parseExtensionElement(ExplicitMessageEncryptionProviderTest.UNKNOWN_EME_ELEMENT);
        Assert.assertEquals(ExplicitMessageEncryptionProviderTest.UNKNOWN_NAMESPACE, eme.getEncryptionNamespace());
        Assert.assertEquals(ExplicitMessageEncryptionProviderTest.UNKNOWN_NAME, eme.getName());
    }
}

