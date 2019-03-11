package org.bukkit;


import Color.WHITE;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class ColorTest {
    static class TestColor {
        static int id = 0;

        final String name;

        final int rgb;

        final int bgr;

        final int r;

        final int g;

        final int b;

        TestColor(int rgb, int bgr, int r, int g, int b) {
            this.rgb = rgb;
            this.bgr = bgr;
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = ((((((((((ColorTest.TestColor.id) + ":") + (Integer.toHexString(rgb).toUpperCase())) + "_") + (Integer.toHexString(bgr).toUpperCase())) + "-r") + (Integer.toHexString(r).toUpperCase())) + "-g") + (Integer.toHexString(g).toUpperCase())) + "-b") + (Integer.toHexString(b).toUpperCase());
        }
    }

    static ColorTest.TestColor[] examples = new ColorTest.TestColor[]{ /* 0xRRGGBB, 0xBBGGRR, 0xRR, 0xGG, 0xBB */
    new ColorTest.TestColor(16777215, 16777215, 255, 255, 255), new ColorTest.TestColor(16777130, 11206655, 255, 255, 170), new ColorTest.TestColor(16711935, 16711935, 255, 0, 255), new ColorTest.TestColor(6815522, 2293607, 103, 255, 34), new ColorTest.TestColor(0, 0, 0, 0, 0) };

    @Test
    public void testSerialization() throws Throwable {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Color base = Color.fromRGB(testColor.rgb);
            YamlConfiguration toSerialize = new YamlConfiguration();
            toSerialize.set("color", base);
            String serialized = toSerialize.saveToString();
            YamlConfiguration deserialized = new YamlConfiguration();
            deserialized.loadFromString(serialized);
            Assert.assertThat((((testColor.name) + " on ") + serialized), base, is(deserialized.getColor("color")));
        }
    }

    // Equality tests
    @Test
    public void testEqualities() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Color fromRGB = Color.fromRGB(testColor.rgb);
            Color fromBGR = Color.fromBGR(testColor.bgr);
            Color fromRGBs = Color.fromRGB(testColor.r, testColor.g, testColor.b);
            Color fromBGRs = Color.fromBGR(testColor.b, testColor.g, testColor.r);
            Assert.assertThat(testColor.name, fromRGB, is(fromRGBs));
            Assert.assertThat(testColor.name, fromRGB, is(fromBGR));
            Assert.assertThat(testColor.name, fromRGB, is(fromBGRs));
            Assert.assertThat(testColor.name, fromRGBs, is(fromBGR));
            Assert.assertThat(testColor.name, fromRGBs, is(fromBGRs));
            Assert.assertThat(testColor.name, fromBGR, is(fromBGRs));
        }
    }

    @Test
    public void testInequalities() {
        for (int i = 1; i < (ColorTest.examples.length); i++) {
            ColorTest.TestColor testFrom = ColorTest.examples[i];
            Color from = Color.fromRGB(testFrom.rgb);
            for (int j = i - 1; j >= 0; j--) {
                ColorTest.TestColor testTo = ColorTest.examples[j];
                Color to = Color.fromRGB(testTo.rgb);
                String name = ((testFrom.name) + " to ") + (testTo.name);
                Assert.assertThat(name, from, is(not(to)));
                Color transform = from.setRed(testTo.r).setBlue(testTo.b).setGreen(testTo.g);
                Assert.assertThat(name, transform, is(not(sameInstance(from))));
                Assert.assertThat(name, transform, is(to));
            }
        }
    }

    // RGB tests
    @Test
    public void testRGB() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.rgb).asRGB(), is(testColor.rgb));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.bgr).asRGB(), is(testColor.rgb));
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).asRGB(), is(testColor.rgb));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).asRGB(), is(testColor.rgb));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRGB1() {
        Color.fromRGB(16777216);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRGB2() {
        Color.fromRGB(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRGB3() {
        Color.fromRGB(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRGB4() {
        Color.fromRGB((-1));
    }

    // BGR tests
    @Test
    public void testBGR() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.rgb).asBGR(), is(testColor.bgr));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.bgr).asBGR(), is(testColor.bgr));
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).asBGR(), is(testColor.bgr));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).asBGR(), is(testColor.bgr));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBGR1() {
        Color.fromBGR(16777216);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBGR2() {
        Color.fromBGR(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBGR3() {
        Color.fromBGR(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBGR4() {
        Color.fromBGR((-1));
    }

    // Red tests
    @Test
    public void testRed() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.rgb).getRed(), is(testColor.r));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.bgr).getRed(), is(testColor.r));
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getRed(), is(testColor.r));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getRed(), is(testColor.r));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR01() {
        Color.fromRGB((-1), 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR02() {
        Color.fromRGB(Integer.MAX_VALUE, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR03() {
        Color.fromRGB(Integer.MIN_VALUE, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR04() {
        Color.fromRGB(256, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR05() {
        Color.fromBGR(0, 0, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR06() {
        Color.fromBGR(0, 0, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR07() {
        Color.fromBGR(0, 0, Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR08() {
        Color.fromBGR(0, 0, 256);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR09() {
        WHITE.setRed((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR10() {
        WHITE.setRed(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR11() {
        WHITE.setRed(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidR12() {
        WHITE.setRed(256);
    }

    // Blue tests
    @Test
    public void testBlue() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.rgb).getBlue(), is(testColor.b));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.bgr).getBlue(), is(testColor.b));
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getBlue(), is(testColor.b));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getBlue(), is(testColor.b));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB01() {
        Color.fromRGB(0, 0, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB02() {
        Color.fromRGB(0, 0, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB03() {
        Color.fromRGB(0, 0, Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB04() {
        Color.fromRGB(0, 0, 256);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB05() {
        Color.fromBGR((-1), 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB06() {
        Color.fromBGR(Integer.MAX_VALUE, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB07() {
        Color.fromBGR(Integer.MIN_VALUE, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB08() {
        Color.fromBGR(256, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB09() {
        WHITE.setBlue((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB10() {
        WHITE.setBlue(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB11() {
        WHITE.setBlue(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidB12() {
        WHITE.setBlue(256);
    }

    // Green tests
    @Test
    public void testGreen() {
        for (ColorTest.TestColor testColor : ColorTest.examples) {
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.rgb).getGreen(), is(testColor.g));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.bgr).getGreen(), is(testColor.g));
            Assert.assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getGreen(), is(testColor.g));
            Assert.assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getGreen(), is(testColor.g));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG01() {
        Color.fromRGB(0, (-1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG02() {
        Color.fromRGB(0, Integer.MAX_VALUE, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG03() {
        Color.fromRGB(0, Integer.MIN_VALUE, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG04() {
        Color.fromRGB(0, 256, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG05() {
        Color.fromBGR(0, (-1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG06() {
        Color.fromBGR(0, Integer.MAX_VALUE, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG07() {
        Color.fromBGR(0, Integer.MIN_VALUE, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG08() {
        Color.fromBGR(0, 256, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG09() {
        WHITE.setGreen((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG10() {
        WHITE.setGreen(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG11() {
        WHITE.setGreen(Integer.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidG12() {
        WHITE.setGreen(256);
    }
}

