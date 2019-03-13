/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.message;


import org.junit.Assert;
import org.junit.Test;


public class LightwaveRfGeneralMessageIdTest {
    @Test
    public void testGetMessageIdString() {
        LightwaveRfMessageId messageId001 = new LightwaveRfGeneralMessageId(1);
        Assert.assertEquals("001", messageId001.getMessageIdString());
        LightwaveRfMessageId messageId012 = new LightwaveRfGeneralMessageId(12);
        Assert.assertEquals("012", messageId012.getMessageIdString());
        LightwaveRfMessageId messageId = new LightwaveRfGeneralMessageId(123);
        Assert.assertEquals("123", messageId.getMessageIdString());
    }

    @Test
    public void testEqualsObject() {
        LightwaveRfMessageId messageId = new LightwaveRfGeneralMessageId(123);
        LightwaveRfMessageId otherMessageId = new LightwaveRfGeneralMessageId(123);
        Assert.assertTrue(otherMessageId.equals(messageId));
        Assert.assertTrue(((messageId.hashCode()) == (otherMessageId.hashCode())));
    }
}

