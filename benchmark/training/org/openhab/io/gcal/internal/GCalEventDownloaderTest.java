/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.gcal.internal;


import junit.framework.Assert;
import org.junit.Test;
import org.openhab.io.gcal.internal.GCalEventDownloader.CalendarEventContent;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 */
public class GCalEventDownloaderTest {
    GCalEventDownloader downloader;

    @Test
    public void testParseCommand() {
        CalendarEventContent content;
        content = downloader.parseEventContent("normalContent", false);
        Assert.assertEquals("normalContent", content.startCommands);
        Assert.assertEquals("", content.endCommands);
        Assert.assertEquals("", content.modifiedByEvent);
        content = downloader.parseEventContent("normalContent\nmodified by {\n\nholidays\n}", false);
        Assert.assertEquals("normalContent", content.startCommands);
        Assert.assertEquals("", content.endCommands);
        Assert.assertEquals("holidays", content.modifiedByEvent);
        content = downloader.parseEventContent("start{startCommand  }\nend\n{  endCommand\n}", false);
        Assert.assertEquals("startCommand", content.startCommands);
        Assert.assertEquals("endCommand", content.endCommands);
        Assert.assertEquals("", content.modifiedByEvent);
        content = downloader.parseEventContent("start{startCommand  }\nend\n{  endCommand\n}\nmodified by {\n\nholidays\n}", false);
        Assert.assertEquals("startCommand", content.startCommands);
        Assert.assertEquals("endCommand", content.endCommands);
        Assert.assertEquals("holidays", content.modifiedByEvent);
        content = downloader.parseEventContent("normalContent", true);
        Assert.assertEquals(("[PresenceSimulation]" + ("\n" + "normalContent")), content.startCommands);
        Assert.assertEquals("", content.endCommands);
        Assert.assertEquals("", content.modifiedByEvent);
    }
}

