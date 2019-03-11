/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.knx.internal.channel;


import java.util.Collections;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Simon Kaufmann - initial contribution and API.
 */
public class KNXChannelTypeTest {
    private KNXChannelType ct;

    @Test
    public void testParse_withDPT_multiple_withRead() {
        ChannelConfiguration res = ct.parse("5.001:<1/3/22+0/3/22+<0/8/15");
        Assert.assertEquals("5.001", res.getDPT());
        Assert.assertEquals("1/3/22", res.getMainGA().getGA());
        Assert.assertTrue(res.getMainGA().isRead());
        Assert.assertEquals(3, res.getListenGAs().size());
        Assert.assertEquals(2, res.getReadGAs().size());
    }

    @Test
    public void testParse_withDPT_multiple_withoutRead() {
        ChannelConfiguration res = ct.parse("5.001:1/3/22+0/3/22+0/8/15");
        Assert.assertEquals("5.001", res.getDPT());
        Assert.assertEquals("1/3/22", res.getMainGA().getGA());
        Assert.assertFalse(res.getMainGA().isRead());
        Assert.assertEquals(3, res.getListenGAs().size());
        Assert.assertEquals(0, res.getReadGAs().size());
    }

    @Test
    public void testParse_withoutDPT_single_withoutRead() {
        ChannelConfiguration res = ct.parse("1/3/22");
        Assert.assertNull(res.getDPT());
        Assert.assertEquals("1/3/22", res.getMainGA().getGA());
        Assert.assertFalse(res.getMainGA().isRead());
        Assert.assertEquals(1, res.getListenGAs().size());
        Assert.assertEquals(0, res.getReadGAs().size());
    }

    @Test
    public void testParse_withoutDPT_single_witRead() {
        ChannelConfiguration res = ct.parse("<1/3/22");
        Assert.assertNull(res.getDPT());
        Assert.assertEquals("1/3/22", res.getMainGA().getGA());
        Assert.assertTrue(res.getMainGA().isRead());
        Assert.assertEquals(1, res.getListenGAs().size());
        Assert.assertEquals(1, res.getReadGAs().size());
    }

    @Test
    public void testParse_twoLevel() {
        ChannelConfiguration res = ct.parse("5.001:<3/1024+<4/1025");
        Assert.assertEquals("3/1024", res.getMainGA().getGA());
        Assert.assertEquals(2, res.getListenGAs().size());
        Assert.assertEquals(2, res.getReadGAs().size());
    }

    @Test
    public void testParse_freeLevel() {
        ChannelConfiguration res = ct.parse("5.001:<4610+<4611");
        Assert.assertEquals("4610", res.getMainGA().getGA());
        Assert.assertEquals(2, res.getListenGAs().size());
        Assert.assertEquals(2, res.getReadGAs().size());
    }

    private static class MyKNXChannelType extends KNXChannelType {
        public MyKNXChannelType(String channelTypeID) {
            super(channelTypeID);
        }

        @Override
        @NonNull
        protected Set<@NonNull
        String> getAllGAKeys() {
            return Collections.emptySet();
        }

        @Override
        @NonNull
        protected String getDefaultDPT(@NonNull
        String gaConfigKey) {
            return "";
        }
    }
}

