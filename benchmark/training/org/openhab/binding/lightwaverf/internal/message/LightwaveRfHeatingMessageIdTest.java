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


public class LightwaveRfHeatingMessageIdTest {
    @Test
    public void testGetMessageIdString() {
        LightwaveRfMessageId messageId001 = new LightwaveRfJsonMessageId(1);
        Assert.assertEquals("1", messageId001.getMessageIdString());
        LightwaveRfMessageId messageId012 = new LightwaveRfJsonMessageId(12);
        Assert.assertEquals("12", messageId012.getMessageIdString());
        LightwaveRfMessageId messageId = new LightwaveRfJsonMessageId(123);
        Assert.assertEquals("123", messageId.getMessageIdString());
    }

    @Test
    public void testEqualsObject() {
        LightwaveRfMessageId messageId = new LightwaveRfJsonMessageId(123);
        LightwaveRfMessageId otherMessageId = new LightwaveRfJsonMessageId(123);
        Assert.assertTrue(otherMessageId.equals(messageId));
        Assert.assertTrue(((messageId.hashCode()) == (otherMessageId.hashCode())));
    }
}

