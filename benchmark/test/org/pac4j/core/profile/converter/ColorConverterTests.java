package org.pac4j.core.profile.converter;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.profile.Color;


/**
 * This class tests the {@link ColorConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ColorConverterTests {
    private static final String BAD_LENGTH_COLOR = "12345";

    private static final String BAD_COLOR = "zzzzzz";

    private static final String GOOD_COLOR = "FF0005";

    private final ColorConverter converter = new ColorConverter();

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testStringBadLength() {
        Assert.assertNull(this.converter.convert(ColorConverterTests.BAD_LENGTH_COLOR));
    }

    @Test
    public void testBadString() {
        Assert.assertNull(this.converter.convert(ColorConverterTests.BAD_COLOR));
    }

    @Test
    public void testGoodString() {
        final Color color = this.converter.convert(ColorConverterTests.GOOD_COLOR);
        Assert.assertEquals(255, color.getRed());
        Assert.assertEquals(0, color.getGreen());
        Assert.assertEquals(5, color.getBlue());
    }

    @Test
    public void testColorToString() {
        final Color color = new Color(10, 20, 30);
        final Color color2 = this.converter.convert(color.toString());
        Assert.assertEquals(color.getRed(), color2.getRed());
        Assert.assertEquals(color.getGreen(), color2.getGreen());
        Assert.assertEquals(color.getBlue(), color2.getBlue());
    }
}

