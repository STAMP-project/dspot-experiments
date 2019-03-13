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
package org.jivesoftware.smackx.reference;


import ReferenceElement.Type;
import ReferenceElement.Type.data;
import ReferenceElement.Type.mention;
import ReferenceProvider.TEST_PROVIDER;
import java.net.URI;
import java.net.URISyntaxException;
import junit.framework.TestCase;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.reference.element.ReferenceElement;
import org.junit.Test;


/* TODO: Later maybe remove this test in case the uri attribute becomes optional.
@Test(expected = NullPointerException.class)
public void uriArgumentNullTest() {
new ReferenceElement(1, 2, ReferenceElement.Type.mention, null, null);
}
 */
public class ReferenceTest extends SmackTestSuite {
    @Test
    public void providerMentionTest() throws Exception {
        String xml = "<reference xmlns='urn:xmpp:reference:0' " + ((("begin='72' " + "end='78' ") + "type='mention' ") + "uri='xmpp:juliet@capulet.lit' />");
        URI uri = new URI("xmpp:juliet@capulet.lit");
        ReferenceElement element = new ReferenceElement(72, 78, Type.mention, null, uri);
        assertXMLEqual(xml, element.toXML().toString());
        TestCase.assertEquals(72, ((int) (element.getBegin())));
        TestCase.assertEquals(78, ((int) (element.getEnd())));
        TestCase.assertEquals(mention, element.getType());
        TestCase.assertNull(element.getAnchor());
        TestCase.assertEquals(uri, element.getUri());
        ReferenceElement parsed = TEST_PROVIDER.parse(TestUtils.getParser(xml));
        assertXMLEqual(xml, parsed.toXML().toString());
    }

    /**
     * TODO: The uri might not be following the XMPP schema.
     * That shouldn't matter though.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void providerDataTest() throws Exception {
        String xml = "<reference xmlns='urn:xmpp:reference:0' " + ("type='data' " + "uri='xmpp:fdp.shakespeare.lit?;node=fdp/submitted/stan.isode.net/accidentreport;item=ndina872be' />");
        URI uri = new URI("xmpp:fdp.shakespeare.lit?;node=fdp/submitted/stan.isode.net/accidentreport;item=ndina872be");
        ReferenceElement element = new ReferenceElement(null, null, Type.data, null, uri);
        assertXMLEqual(xml, element.toXML().toString());
        TestCase.assertNull(element.getBegin());
        TestCase.assertNull(element.getEnd());
        TestCase.assertNull(element.getAnchor());
        TestCase.assertEquals(data, element.getType());
        TestCase.assertEquals(uri, element.getUri());
        ReferenceElement parsed = TEST_PROVIDER.parse(TestUtils.getParser(xml));
        assertXMLEqual(xml, parsed.toXML().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void beginGreaterEndIllegalTest() throws URISyntaxException {
        new ReferenceElement(100, 10, Type.mention, null, new URI("xmpp:test@test.test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void beginSmallerZeroTest() throws URISyntaxException {
        new ReferenceElement((-1), 12, Type.data, null, new URI("xmpp:test@test.test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void endSmallerZeroTest() throws URISyntaxException {
        new ReferenceElement(12, (-2), Type.mention, null, new URI("xmpp:test@test.test"));
    }

    @Test(expected = NullPointerException.class)
    public void typeArgumentNullTest() throws URISyntaxException {
        new ReferenceElement(1, 2, null, null, new URI("xmpp:test@test.test"));
    }
}

