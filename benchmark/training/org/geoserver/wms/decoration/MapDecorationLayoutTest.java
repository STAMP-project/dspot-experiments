/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.decoration;


import MapDecorationLayout.Block;
import MapDecorationLayout.Block.Position;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geoserver.wms.WMSMapContent;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MapDecorationLayoutTest {
    // public static TestSuite suite() { return new TestSuite(MapDecorationLayout.class); }
    private class MockMapDecoration implements MapDecoration {
        Dimension request;

        Rectangle expect;

        public MockMapDecoration(Dimension toRequest, Rectangle toExpect) {
            this.request = toRequest;
            this.expect = toExpect;
        }

        public void loadOptions(Map<String, String> options) {
        }

        public Dimension findOptimalSize(Graphics2D g2d, WMSMapContent mapContent) {
            return this.request;
        }

        public void paint(Graphics2D g2d, Rectangle paintArea, WMSMapContent mapContent) throws Exception {
            Assert.assertEquals("Calculated width matches expected", expect.width, paintArea.width);
            Assert.assertEquals("Calculated height matches expected", expect.height, paintArea.height);
            Assert.assertEquals("Calculated x matches expected", expect.x, paintArea.x);
            Assert.assertEquals("Calculated y matches expected", expect.y, paintArea.y);
        }
    }

    @Test
    public void testStaticSize() throws Exception {
        Graphics2D g2d = createMockGraphics(256, 256);
        MapDecorationLayout dl = new MapDecorationLayout();
        // if we have a component that calculates a size which fits in the image,
        // does it get that size?
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(100, 100), new Rectangle(156, 78, 100, 100)), Position.CR, null, new Point(0, 0)));
        // if we have a component with a user-mandated size which fits in the image,
        // does it get that size?
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(100, 100), new Rectangle(206, 103, 50, 50)), Position.CR, new Dimension(50, 50), new Point(0, 0)));
        dl.paint(g2d, new Rectangle(0, 0, 256, 256), null);
    }

    @Test
    public void testSquished() throws Exception {
        Graphics2D g2d = createMockGraphics(100, 100);
        MapDecorationLayout dl = new MapDecorationLayout();
        // for dynamically sized components, expect to be scaled to fit in view
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(150, 100), new Rectangle(0, 0, 100, 100)), Position.CR, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(100, 150), new Rectangle(0, 0, 100, 100)), Position.CR, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(150, 150), new Rectangle(0, 0, 100, 100)), Position.CR, null, new Point(0, 0)));
        // for components with specified sizes, let them run off the edge.
        // TODO: Should we preserve the aspect ratio for sized components that don't fit?
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(150, 150), new Rectangle(0, 0, 100, 100)), Position.CR, new Dimension(100, 150), new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(150, 150), new Rectangle(0, 0, 100, 100)), Position.CR, new Dimension(150, 100), new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(150, 150), new Rectangle(0, 0, 100, 100)), Position.CR, new Dimension(150, 150), new Point(0, 0)));
        dl.paint(g2d, new Rectangle(0, 0, 100, 100), null);
    }

    @Test
    public void testPosition() {
        Graphics2D g2d = createMockGraphics(100, 100);
        MapDecorationLayout dl = new MapDecorationLayout();
        // try all the position variants to verify that they actually put the block in the right
        // position
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(0, 0, 10, 10)), Position.UL, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 0, 10, 10)), Position.UC, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(90, 0, 10, 10)), Position.UR, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(0, 45, 10, 10)), Position.CL, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 45, 10, 10)), Position.CC, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(90, 45, 10, 10)), Position.CR, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(0, 90, 10, 10)), Position.LL, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 90, 10, 10)), Position.LC, null, new Point(0, 0)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(90, 90, 10, 10)), Position.LR, null, new Point(0, 0)));
        dl.paint(g2d, new Rectangle(0, 0, 100, 100), null);
    }

    @Test
    public void testOffset() {
        Graphics2D g2d = createMockGraphics(100, 100);
        MapDecorationLayout dl = new MapDecorationLayout();
        // try offsets with differing positions
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(10, 10, 10, 10)), Position.UL, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 10, 10, 10)), Position.UC, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(80, 10, 10, 10)), Position.UR, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(10, 45, 10, 10)), Position.CL, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 45, 10, 10)), Position.CC, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(80, 45, 10, 10)), Position.CR, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(10, 80, 10, 10)), Position.LL, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(45, 80, 10, 10)), Position.LC, null, new Point(10, 10)));
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(10, 10), new Rectangle(80, 80, 10, 10)), Position.LR, null, new Point(10, 10)));
        dl.paint(g2d, new Rectangle(0, 0, 100, 100), null);
    }

    @Test
    public void testTopCenter() {
        Graphics2D g2d = createMockGraphics(100, 100);
        MapDecorationLayout dl = new MapDecorationLayout();
        // try offsets with differing positions
        dl.addBlock(new MapDecorationLayout.Block(new MapDecorationLayoutTest.MockMapDecoration(new Dimension(50, 10), new Rectangle(25, 10, 50, 10)), Position.UC, null, new Point(0, 10)));
        dl.paint(g2d, new Rectangle(0, 0, 100, 100), null);
    }

    @Test
    public void testResetGraphics() {
        Graphics2D g2d = createMockGraphics(100, 100);
        MapDecorationLayout dl = new MapDecorationLayout();
        // try offsets with differing positions
        dl.addBlock(new MapDecorationLayout.Block(new MapDecoration() {
            @Override
            public void paint(Graphics2D g2d, Rectangle paintArea, WMSMapContent context) throws Exception {
            }

            @Override
            public void loadOptions(Map<String, String> options) throws Exception {
                // nothing to do
            }

            @Override
            public Dimension findOptimalSize(Graphics2D g2d, WMSMapContent mapContent) throws Exception {
                return new Dimension(10, 10);
            }
        }, Position.UC, null, new Point(0, 10)));
        dl.paint(g2d, new Rectangle(0, 0, 100, 100), null);
    }

    @Test
    public void testLoadLargeValue() throws Exception {
        String messageTemplate = "<#setting datetime_format=\"yyyy-MM-dd\'T\'HH:mm:ss.SSSX\">\n" + ((("<#setting locale=\"en_US\">\n" + "<#if time??>\n") + "${time?datetime?string[\"dd-MM-yyyy\"]}\n") + "</#if>");
        String definition = ((((((("<layout>\n" + ("  <decoration type=\"text\" affinity=\"bottom,right\" offset=\"6,6\" size=\"auto\">\n" + "    <option name=\"message\"><![CDATA[")) + messageTemplate) + "]]></option>\n") + "    <option name=\"font-family\" value=\"Bitstream Vera Sans\"/>\n") + "    <option name=\"font-size\" value=\"12\"/>\n") + "    <option name=\"halo-radius\" value=\"2\"/>\n") + "  </decoration>\n") + "</layout>\n";
        GeoServerExtensionsHelper.singleton("text", new TextDecoration());
        MapDecorationLayout layout = MapDecorationLayout.fromString(definition, false);
        List<MapDecorationLayout.Block> blocks = layout.blocks;
        Assert.assertEquals(1, blocks.size());
        Assert.assertThat(blocks.get(0).decoration, CoreMatchers.instanceOf(TextDecoration.class));
        TextDecoration text = ((TextDecoration) (blocks.get(0).decoration));
        Assert.assertEquals(messageTemplate, text.messageTemplate);
    }
}

