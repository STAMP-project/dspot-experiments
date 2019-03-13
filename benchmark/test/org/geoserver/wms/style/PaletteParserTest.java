/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.style;


import PaletteParser.InvalidColorException;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.geotools.styling.StyledLayerDescriptor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opengis.filter.expression.Function;


public class PaletteParserTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    PaletteParser parser = new PaletteParser();

    @Test
    public void testParseBlackAndWhiteHash() throws IOException {
        assertBlackAndWhite("#000000\n#FFFFFF");
    }

    @Test
    public void testParseBlackAndWhiteHashAlpha() throws IOException {
        assertBlackAndWhite("#FF000000\n#FFFFFFFF");
    }

    @Test
    public void testParseBlackAndWhiteSimpleHex() throws IOException {
        assertBlackAndWhite("0x000000\n0xFFFFFF");
    }

    @Test
    public void testParseBlackAndWhiteHexAlpha() throws IOException {
        assertBlackAndWhite("0xFF000000\n0xFFFFFFFF");
    }

    @Test
    public void assertBlackAndWhiteTranslucent() throws IOException {
        assertBlackAndWhiteTranslucent("#64000000\n#64FFFFFF");
    }

    @Test
    public void assertBlackAndWhiteHexTranslucent() throws IOException {
        assertBlackAndWhiteTranslucent("0x64000000\n0x64FFFFFF");
    }

    @Test
    public void testParseBlackAndWhiteSimpleHexComments() throws IOException {
        assertBlackAndWhite("%one\n0x000000\n%two\n0xFFFFFF\n%three\n%four");
    }

    @Test
    public void testErrorMessage() throws IOException {
        // we expect to get a error message pointing at the invalid color
        exception.expect(InvalidColorException.class);
        exception.expectMessage("Invalid color 'abcde', supported syntaxes are #RRGGBB, 0xRRGGBB, #AARRGGBB and 0xAARRGGBB");
        parser.parseColorMap(toReader("#FF0000\nabcde\n#000000"));
    }

    @Test
    public void testParseBlackWhiteToStyle() throws IOException, TransformerException {
        StyledLayerDescriptor sld = parser.parseStyle(toReader("#000000\n#FFFFFF"));
        Function cm = PaletteParserTest.assertDynamicColorColormap(sld);
        Assert.assertEquals("rgb(0,0,0);rgb(255,255,255)", cm.getParameters().get(0).evaluate(null));
    }

    @Test
    public void testParseBlackWhiteTranslucentToStyle() throws IOException, TransformerException {
        StyledLayerDescriptor sld = parser.parseStyle(toReader("#64000000\n#64FFFFFF"));
        Function cm = PaletteParserTest.assertDynamicColorColormap(sld);
        Assert.assertEquals("rgba(0,0,0,0.39);rgba(255,255,255,0.39)", cm.getParameters().get(0).evaluate(null));
    }
}

