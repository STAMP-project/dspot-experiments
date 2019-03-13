/**
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.ui.overlay;


import OverlayManager.OVERLAY_COMPARATOR;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static OverlayPosition.DYNAMIC;
import static OverlayPosition.TOOLTIP;
import static OverlayPosition.TOP_LEFT;
import static OverlayPriority.HIGH;
import static OverlayPriority.LOW;


public class OverlayManagerTest {
    class TestOverlay extends Overlay {
        TestOverlay(OverlayPosition position, OverlayPriority priority) {
            setPosition(position);
            setPriority(priority);
        }

        @Override
        public Dimension render(Graphics2D graphics) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class OverlayA extends Overlay {
        @Override
        public Dimension render(Graphics2D graphics) {
            return null;
        }
    }

    private static class OverlayB extends Overlay {
        @Override
        public Dimension render(Graphics2D graphics) {
            return null;
        }
    }

    @Test
    public void testEquality() {
        Overlay a1 = new OverlayManagerTest.OverlayA();
        Overlay a2 = new OverlayManagerTest.OverlayA();
        Overlay b = new OverlayManagerTest.OverlayB();
        // The same instance of the same overlay should be equal
        Assert.assertTrue(a1.equals(a1));
        // A different instance of the same overlay should not be equal by default
        Assert.assertFalse(a1.equals(a2));
        // A different instance of a different overlay should not be equal
        Assert.assertFalse(a1.equals(b));
    }

    @Test
    public void testSort() {
        // High priorities overlays render first
        Overlay tlh = new OverlayManagerTest.TestOverlay(TOP_LEFT, HIGH);
        Overlay tll = new OverlayManagerTest.TestOverlay(TOP_LEFT, LOW);
        List<Overlay> overlays = Arrays.asList(tlh, tll);
        overlays.sort(OVERLAY_COMPARATOR);
        Assert.assertEquals(tlh, overlays.get(0));
        Assert.assertEquals(tll, overlays.get(1));
    }

    @Test
    public void testSortDynamic() {
        // Dynamic overlays render before static overlays
        Overlay tlh = new OverlayManagerTest.TestOverlay(TOP_LEFT, HIGH);
        Overlay dyn = new OverlayManagerTest.TestOverlay(DYNAMIC, HIGH);
        List<Overlay> overlays = Arrays.asList(tlh, dyn);
        overlays.sort(OVERLAY_COMPARATOR);
        Assert.assertEquals(dyn, overlays.get(0));
        Assert.assertEquals(tlh, overlays.get(1));
    }

    @Test
    public void testTooltips() {
        // Tooltip overlay renders after everything
        Overlay t = new OverlayManagerTest.TestOverlay(TOOLTIP, HIGH);
        Overlay dyn = new OverlayManagerTest.TestOverlay(DYNAMIC, HIGH);
        Overlay tlh = new OverlayManagerTest.TestOverlay(TOP_LEFT, HIGH);
        List<Overlay> overlays = Arrays.asList(t, dyn, tlh);
        overlays.sort(OVERLAY_COMPARATOR);
        Assert.assertEquals(dyn, overlays.get(0));
        Assert.assertEquals(tlh, overlays.get(1));
        Assert.assertEquals(t, overlays.get(2));
    }
}

