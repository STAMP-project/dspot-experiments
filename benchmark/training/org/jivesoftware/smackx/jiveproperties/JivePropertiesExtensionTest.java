/**
 * Copyright 2014-2018 Florian Schmaus
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
package org.jivesoftware.smackx.jiveproperties;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.junit.Assert;
import org.junit.Test;


public class JivePropertiesExtensionTest extends InitExtensions {
    @Test
    public void checkProvider() throws Exception {
        // @formatter:off
        String properties = "<message xmlns='jabber:client' from='romeo@example.net/orchard' to='juliet@example.com/balcony'>" + ((((((("<body>Neither, fair saint, if either thee dislike.</body>" + "<properties xmlns='http://www.jivesoftware.com/xmlns/xmpp/properties'>") + "<property>") + "<name>FooBar</name>") + "<value type='integer'>42</value>") + "</property>") + "</properties>") + "</message>");
        // @formatter:on
        Message message = PacketParserUtils.parseStanza(properties);
        JivePropertiesExtension jpe = JivePropertiesExtension.from(message);
        Assert.assertNotNull(jpe);
        Integer integer = ((Integer) (jpe.getProperty("FooBar")));
        Assert.assertNotNull(integer);
        int fourtytwo = integer;
        Assert.assertEquals(42, fourtytwo);
    }
}

