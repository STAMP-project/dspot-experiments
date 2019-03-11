/**
 * Copyright ? 2017 Paul Schaub
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
package org.jivesoftware.smackx.jingle.transports.jingle_ibb;


import JingleIBBTransport.DEFAULT_BLOCK_SIZE;
import JingleIBBTransport.NAMESPACE_V1;
import junit.framework.TestCase;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.element.JingleIBBTransport;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.provider.JingleIBBTransportProvider;
import org.junit.Test;


/**
 * Test JingleIBBTransport provider and element.
 */
public class JingleIBBTransportTest extends SmackTestSuite {
    @Test
    public void parserTest() throws Exception {
        String sid = StringUtils.randomString(24);
        short size = 8192;
        String xml = ("<transport xmlns='urn:xmpp:jingle:transports:ibb:1' block-size='8192' sid='" + sid) + "'/>";
        JingleIBBTransport transport = new JingleIBBTransport(size, sid);
        TestCase.assertEquals(xml, transport.toXML().toString());
        TestCase.assertEquals(size, transport.getBlockSize());
        TestCase.assertEquals(sid, transport.getSessionId());
        JingleIBBTransport parsed = new JingleIBBTransportProvider().parse(TestUtils.getParser(xml));
        TestCase.assertEquals(transport, parsed);
        TestCase.assertTrue(transport.equals(parsed));
        TestCase.assertEquals(xml, parsed.toXML().toString());
        JingleIBBTransport transport1 = new JingleIBBTransport(((short) (1024)));
        TestCase.assertEquals(((short) (1024)), transport1.getBlockSize());
        TestCase.assertNotSame(transport, transport1);
        TestCase.assertNotSame(transport.getSessionId(), transport1.getSessionId());
        TestCase.assertFalse(transport.equals(null));
        JingleIBBTransport transport2 = new JingleIBBTransport();
        TestCase.assertEquals(DEFAULT_BLOCK_SIZE, transport2.getBlockSize());
        TestCase.assertFalse(transport1.equals(transport2));
        JingleIBBTransport transport3 = new JingleIBBTransport(((short) (-1024)));
        TestCase.assertEquals(DEFAULT_BLOCK_SIZE, transport3.getBlockSize());
        TestCase.assertEquals(transport3.getNamespace(), NAMESPACE_V1);
        TestCase.assertEquals(transport3.getElementName(), "transport");
        JingleIBBTransport transport4 = new JingleIBBTransport("session-id");
        TestCase.assertEquals(DEFAULT_BLOCK_SIZE, transport4.getBlockSize());
    }
}

