/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.gcal.internal.util;


import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 */
public class ExecuteCommandJobTest {
    ExecuteCommandJob commandJob;

    @Test
    public void testParseCommand() {
        String[] content;
        content = commandJob.parseCommand("send ItemName value");
        Assert.assertEquals("send", content[0]);
        Assert.assertEquals("ItemName", content[1]);
        Assert.assertEquals("value", content[2]);
        content = commandJob.parseCommand("send ItemName \"value value\"");
        Assert.assertEquals("send", content[0]);
        Assert.assertEquals("ItemName", content[1]);
        Assert.assertEquals("value value", content[2]);
        content = commandJob.parseCommand("send ItemName \"125\"");
        Assert.assertEquals("send", content[0]);
        Assert.assertEquals("ItemName", content[1]);
        Assert.assertEquals("125", content[2]);
        content = commandJob.parseCommand("send ItemName 125");
        Assert.assertEquals("send", content[0]);
        Assert.assertEquals("ItemName", content[1]);
        Assert.assertEquals("125", content[2]);
        content = commandJob.parseCommand("> say(\"Hello\")");
        Assert.assertEquals(">", content[0]);
        Assert.assertEquals("say(\"Hello\")", content[1]);
    }
}

