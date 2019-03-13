/**
 * Copyright the original author or authors
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
package org.jivesoftware.smackx.vcardtemp;


import java.util.Arrays;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.junit.Assert;
import org.junit.Test;


public class VCardTest extends InitExtensions {
    @Test
    public void testParseFullVCardIQStanza() throws Throwable {
        // @formatter:off
        final String request = (((((("<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + (((((((((((((((((((((((((((((((((((((((((((((((("<vCard xmlns='vcard-temp'>" + "<FN>User Name</FN>") + "<N>") + "<FAMILY>Name</FAMILY>") + "<GIVEN>User</GIVEN>") + "<MIDDLE>PJ</MIDDLE>") + "<PREFIX>Mr.</PREFIX>") + "<SUFFIX>III</SUFFIX>") + "</N>") + "<NICKNAME>User dude</NICKNAME>") + "<URL>http://www.igniterealtime.org</URL>") + "<BDAY>1970-17-03</BDAY>") + "<ORG>") + "<ORGNAME>Ignite Realtime</ORGNAME>") + "<ORGUNIT>Smack</ORGUNIT>") + "</ORG>") + "<TITLE>Programmer &amp; tester</TITLE>") + "<ROLE>Bug fixer</ROLE>") + "<TEL><WORK/><VOICE/><NUMBER>123-456-7890</NUMBER></TEL>") + "<TEL><WORK/><FAX/><NUMBER/></TEL>") + "<TEL><WORK/><MSG/><NUMBER/></TEL>") + "<ADR>") + "<WORK/>") + "<EXTADD></EXTADD>") + "<STREET>Work Street</STREET>") + "<LOCALITY>Work Locality</LOCALITY>") + "<REGION>Work Region</REGION>") + "<PCODE>Work Post Code</PCODE>") + "<CTRY>Work Country</CTRY>") + "</ADR>") + "<TEL><HOME/><VOICE/><NUMBER>123-098-7654</NUMBER></TEL>") + "<TEL><HOME/><FAX/><NUMBER/></TEL>") + "<TEL><HOME/><MSG/><NUMBER/></TEL>") + "<ADR>") + "<HOME/>") + "<EXTADD/>") + "<STREET/>") + "<LOCALITY>Home Locality</LOCALITY>") + "<REGION>Home Region</REGION>") + "<PCODE>Home Post Code</PCODE>") + "<CTRY>Home Country</CTRY>") + "</ADR>") + "<EMAIL><INTERNET/><PREF/><USERID>user@igniterealtime.org</USERID></EMAIL>") + "<EMAIL><INTERNET/><WORK/><USERID>work@igniterealtime.org</USERID></EMAIL>") + "<JABBERID>user@igniterealtime.org</JABBERID>") + "<DESC>") + "&lt;Check out our website: http://www.igniterealtime.org&gt;") + "</DESC>") + "<PHOTO><BINVAL>")) + (VCardTest.getAvatarEncoded())) + "</BINVAL><TYPE>") + (VCardTest.MIME_TYPE)) + "</TYPE></PHOTO>") + "</vCard>") + "</iq>";
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("User", vCard.getFirstName());
        Assert.assertEquals("Name", vCard.getLastName());
        Assert.assertEquals("PJ", vCard.getMiddleName());
        Assert.assertEquals("User dude", vCard.getNickName());
        Assert.assertEquals("Mr.", vCard.getPrefix());
        Assert.assertEquals("III", vCard.getSuffix());
        Assert.assertEquals("Programmer & tester", vCard.getField("TITLE"));
        Assert.assertEquals("Bug fixer", vCard.getField("ROLE"));
        Assert.assertEquals("<Check out our website: http://www.igniterealtime.org>", vCard.getField("DESC"));
        Assert.assertEquals("http://www.igniterealtime.org", vCard.getField("URL"));
        Assert.assertEquals("user@igniterealtime.org", vCard.getEmailHome());
        Assert.assertEquals("work@igniterealtime.org", vCard.getEmailWork());
        Assert.assertEquals("user@igniterealtime.org", vCard.getJabberId());
        Assert.assertEquals("Ignite Realtime", vCard.getOrganization());
        Assert.assertEquals("Smack", vCard.getOrganizationUnit());
        Assert.assertEquals("123-098-7654", vCard.getPhoneHome("VOICE"));
        Assert.assertEquals("123-456-7890", vCard.getPhoneWork("VOICE"));
        Assert.assertEquals("Work Locality", vCard.getAddressFieldWork("LOCALITY"));
        Assert.assertEquals("Work Region", vCard.getAddressFieldWork("REGION"));
        Assert.assertEquals("Work Post Code", vCard.getAddressFieldWork("PCODE"));
        Assert.assertEquals("Work Country", vCard.getAddressFieldWork("CTRY"));
        Assert.assertEquals("Home Locality", vCard.getAddressFieldHome("LOCALITY"));
        Assert.assertEquals("Home Region", vCard.getAddressFieldHome("REGION"));
        Assert.assertEquals("Home Post Code", vCard.getAddressFieldHome("PCODE"));
        Assert.assertEquals("Home Country", vCard.getAddressFieldHome("CTRY"));
        byte[] expectedAvatar = VCardTest.getAvatarBinary();
        Assert.assertTrue(Arrays.equals(vCard.getAvatar(), expectedAvatar));
        Assert.assertEquals(VCardTest.MIME_TYPE, vCard.getAvatarMimeType());
    }

    @Test
    public void testNoWorkHomeSpecifier_EMAIL() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><EMAIL><USERID>foo@fee.www.bar</USERID></EMAIL></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("foo@fee.www.bar", vCard.getEmailHome());
    }

    @Test
    public void testNoWorkHomeSpecifier_TEL() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><TEL><FAX/><NUMBER>3443233</NUMBER></TEL></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("3443233", vCard.getPhoneWork("FAX"));
    }

    @Test
    public void testUnknownTopLevelElementAdded() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><UNKNOWN>1234</UNKNOWN></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("1234", vCard.getField("UNKNOWN"));
    }

    @Test
    public void testUnknownComplexTopLevelElementNotAdded() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><UNKNOWN><FOO/></UNKNOWN></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals(null, vCard.getField("UNKNOWN"));
    }

    @Test
    public void testUnknownAddressElementNotAdded() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><ADR><UNKNOWN>1234</UNKNOWN></ADR></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals(null, vCard.getField("UNKNOWN"));
    }

    @Test
    public void testUnknownDeepElementNotAdded() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><UNKNOWN><UNKNOWN>1234</UNKNOWN></UNKNOWN></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals(null, vCard.getField("UNKNOWN"));
    }

    @Test
    public void testNoWorkHomeSpecifier_ADDR() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><ADR><STREET>Some street</STREET><FF>ddss</FF></ADR></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("Some street", vCard.getAddressFieldWork("STREET"));
        Assert.assertEquals("ddss", vCard.getAddressFieldWork("FF"));
    }

    @Test
    public void testFN() throws Throwable {
        // @formatter:off
        final String request = "<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + ("<vCard xmlns='vcard-temp'><FN>kir max</FN></vCard>" + "</iq>");
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        Assert.assertEquals("kir max", vCard.getField("FN"));
    }

    private static final String MIME_TYPE = "testtype";

    private static final String VCARD_XML = ((("<vCard xmlns='vcard-temp'><PHOTO><BINVAL>" + (VCardTest.getAvatarEncoded())) + "</BINVAL><TYPE>") + (VCardTest.MIME_TYPE)) + "</TYPE></PHOTO></vCard>";

    @Test
    public void testPhoto() throws Throwable {
        // @formatter:off
        final String request = ("<iq id='v1' to='user@igniterealtime.org/mobile' type='result'>" + (VCardTest.VCARD_XML)) + "</iq>";
        // @formatter:on
        VCard vCard = PacketParserUtils.parseStanza(request);
        byte[] avatar = vCard.getAvatar();
        String mimeType = vCard.getAvatarMimeType();
        Assert.assertEquals(mimeType, VCardTest.MIME_TYPE);
        byte[] expectedAvatar = VCardTest.getAvatarBinary();
        Assert.assertTrue(Arrays.equals(avatar, expectedAvatar));
    }
}

