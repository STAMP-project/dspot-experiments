/**
 * Copyright (C) 2007 Jive Software.
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
package org.jivesoftware.smack.packet;


import Presence.Mode;
import Presence.Mode.away;
import Presence.Mode.chat;
import Presence.Type;
import Presence.Type.available;
import Presence.Type.unavailable;
import StreamOpen.CLIENT_NAMESPACE;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


public class PresenceTest {
    @Test
    public void setPresenceTypeTest() throws IOException, SAXException {
        Presence.Type type = Type.unavailable;
        Presence.Type type2 = Type.subscribe;
        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence").append(" type=\"").append(type).append("\">").append("</presence>");
        String control = controlBuilder.toString();
        Presence presenceTypeInConstructor = new Presence(type);
        presenceTypeInConstructor.setStanzaId(null);
        Assert.assertEquals(type, presenceTypeInConstructor.getType());
        assertXMLEqual(control, presenceTypeInConstructor.toXML(CLIENT_NAMESPACE).toString());
        controlBuilder = new StringBuilder();
        controlBuilder.append("<presence").append(" type=\"").append(type2).append("\">").append("</presence>");
        control = controlBuilder.toString();
        Presence presenceTypeSet = PresenceTest.getNewPresence();
        presenceTypeSet.setType(type2);
        Assert.assertEquals(type2, presenceTypeSet.getType());
        assertXMLEqual(control, presenceTypeSet.toXML(CLIENT_NAMESPACE).toString());
    }

    @Test(expected = NullPointerException.class)
    public void setNullPresenceTypeTest() {
        PresenceTest.getNewPresence().setType(null);
    }

    @Test
    public void isPresenceAvailableTest() {
        Presence presence = PresenceTest.getNewPresence();
        presence.setType(available);
        Assert.assertTrue(presence.isAvailable());
        presence.setType(unavailable);
        Assert.assertFalse(presence.isAvailable());
    }

    @Test
    public void setPresenceStatusTest() throws IOException, SAXException {
        final String status = "This is a test of the emergency broadcast system.";
        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>").append("<status>").append(status).append("</status>").append("</presence>");
        String control = controlBuilder.toString();
        Presence presence = PresenceTest.getNewPresence();
        presence.setStatus(status);
        Assert.assertEquals(status, presence.getStatus());
        assertXMLEqual(control, presence.toXML(CLIENT_NAMESPACE).toString());
    }

    @Test
    public void setPresencePriorityTest() throws IOException, SAXException {
        final int priority = 10;
        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>").append("<priority>").append(priority).append("</priority>").append("</presence>");
        String control = controlBuilder.toString();
        Presence presence = PresenceTest.getNewPresence();
        presence.setPriority(priority);
        Assert.assertEquals(priority, presence.getPriority());
        assertXMLEqual(control, presence.toXML(CLIENT_NAMESPACE).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIllegalPriorityTest() {
        PresenceTest.getNewPresence().setPriority(Integer.MIN_VALUE);
    }

    @Test
    public void setPresenceModeTest() throws IOException, SAXException {
        Presence.Mode mode1 = Mode.dnd;
        final int priority = 10;
        final String status = "This is a test of the emergency broadcast system.";
        Presence.Mode mode2 = Mode.chat;
        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>").append("<status>").append(status).append("</status>").append("<priority>").append(priority).append("</priority>").append("<show>").append(mode1).append("</show>").append("</presence>");
        String control = controlBuilder.toString();
        Presence presenceModeInConstructor = new Presence(Type.available, status, priority, mode1);
        presenceModeInConstructor.setStanzaId(null);
        Assert.assertEquals(mode1, presenceModeInConstructor.getMode());
        assertXMLEqual(control, presenceModeInConstructor.toXML(CLIENT_NAMESPACE).toString());
        controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>").append("<show>").append(mode2).append("</show>").append("</presence>");
        control = controlBuilder.toString();
        Presence presenceModeSet = PresenceTest.getNewPresence();
        presenceModeSet.setMode(mode2);
        Assert.assertEquals(mode2, presenceModeSet.getMode());
        assertXMLEqual(control, presenceModeSet.toXML(CLIENT_NAMESPACE).toString());
    }

    @Test
    public void isModeAwayTest() {
        Presence presence = PresenceTest.getNewPresence();
        presence.setMode(away);
        Assert.assertTrue(presence.isAway());
        presence.setMode(chat);
        Assert.assertFalse(presence.isAway());
    }

    @Test
    public void presenceXmlLangTest() throws IOException, SAXException {
        final String lang = "sp";
        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence").append(" xml:lang=\"").append(lang).append("\">").append("</presence>");
        String control = controlBuilder.toString();
        Presence presence = PresenceTest.getNewPresence();
        presence.setLanguage(lang);
        assertXMLEqual(control, presence.toXML(CLIENT_NAMESPACE).toString());
    }
}

