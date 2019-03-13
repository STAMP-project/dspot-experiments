/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.outputs;


import DateTimeZone.UTC;
import GelfMessageLevel.ALERT;
import GelfMessageLevel.INFO;
import GelfOutput.Config;
import Message.FIELD_FULL_MESSAGE;
import org.graylog2.gelfclient.GelfMessage;
import org.graylog2.gelfclient.transport.GelfTransport;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GelfOutputTest {
    @Test
    public void testWrite() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final Message message = Mockito.mock(Message.class);
        final GelfMessage gelfMessage = new GelfMessage("Test");
        final GelfOutput gelfOutput = Mockito.spy(new GelfOutput(transport));
        Mockito.doReturn(gelfMessage).when(gelfOutput).toGELFMessage(message);
        gelfOutput.write(message);
        Mockito.verify(transport).send(ArgumentMatchers.eq(gelfMessage));
    }

    @Test
    public void testGetRequestedConfiguration() throws Exception {
        final GelfOutput.Config gelfOutputConfig = new GelfOutput.Config();
        final ConfigurationRequest request = gelfOutputConfig.getRequestedConfiguration();
        Assert.assertNotNull(request);
        Assert.assertNotNull(request.asList());
    }

    @Test
    public void testToGELFMessageTimestamp() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(gelfMessage.getTimestamp(), ((now.getMillis()) / 1000.0), 0.0);
    }

    @Test
    public void testToGELFMessageFullMessage() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField(FIELD_FULL_MESSAGE, "Full Message");
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals("Full Message", gelfMessage.getFullMessage());
    }

    @Test
    public void testToGELFMessageWithValidNumericLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", 6);
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(INFO, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithInvalidNumericLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", (-1L));
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(ALERT, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithValidStringLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", "6");
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(INFO, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithInvalidStringLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", "BOOM");
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(ALERT, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithInvalidNumericStringLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", "-1");
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(ALERT, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithInvalidTypeLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", new Object());
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(ALERT, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithNullLevel() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("level", null);
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(ALERT, gelfMessage.getLevel());
    }

    @Test
    public void testToGELFMessageWithNonStringFacility() throws Exception {
        final GelfTransport transport = Mockito.mock(GelfTransport.class);
        final GelfOutput gelfOutput = new GelfOutput(transport);
        final DateTime now = DateTime.now(UTC);
        final Message message = new Message("Test", "Source", now);
        message.addField("facility", 42L);
        final GelfMessage gelfMessage = gelfOutput.toGELFMessage(message);
        Assert.assertEquals(42L, gelfMessage.getAdditionalFields().get("facility"));
    }
}

